/// Owns the user-visible `PlaybackState`, the `RadioPlayer`, and the
/// bounded reconnect policy. Implements the transition table in
/// `specs/002-stream-playback-migration/contracts/playback-state-machine.md`.
///
/// Deliberately imports neither `package:just_audio` nor `package:flutter`
/// other than `foundation` (for `ValueListenable`/`ValueNotifier`), so the
/// whole state machine is unit-testable against a `FakeRadioPlayer`
/// (FR-023, FR-024).
// ignore_for_file: prefer_initializing_formals
library;

import 'dart:async';

import 'package:flutter/foundation.dart';

import '../analytics/analytics_logger.dart';
import 'endpoint_catalog.dart';
import 'bitrate_preference.dart';
import 'icy_metadata.dart';
import 'playback_state.dart';
import 'radio_player.dart';

class ReconnectPolicy {
  final int maxAttempts;
  final Duration backoff;
  const ReconnectPolicy({
    this.maxAttempts = 10,
    this.backoff = const Duration(seconds: 10),
  });
}

/// Snapshot of "what should be shown in the system notification right now".
/// Pushed to the audio_service handler by [PlaybackController]; the handler
/// translates it into a `MediaItem` (the controller itself doesn't import
/// audio_service so it stays unit-testable).
class NotificationItem {
  final String title;
  final String artist;
  final String url;
  final bool clearedToFallback;

  const NotificationItem({
    required this.title,
    required this.artist,
    required this.url,
    required this.clearedToFallback,
  });

  @override
  bool operator ==(Object other) =>
      other is NotificationItem &&
      other.title == title &&
      other.artist == artist &&
      other.url == url &&
      other.clearedToFallback == clearedToFallback;

  @override
  int get hashCode => Object.hash(title, artist, url, clearedToFallback);

  @override
  String toString() =>
      'NotificationItem(title=$title, artist=$artist, cleared=$clearedToFallback)';
}

class PlaybackController {
  final RadioPlayer _player;
  final BitratePreference _preference;
  final ReconnectPolicy _policy;
  final String _stationFallback;
  final AnalyticsLogger _analytics;

  final ValueNotifier<PlaybackState> _state =
      ValueNotifier<PlaybackState>(kInitialState);
  final ValueNotifier<NotificationItem?> _currentItem =
      ValueNotifier<NotificationItem?>(null);
  final ValueNotifier<bool> _slowLoading = ValueNotifier<bool>(false);
  final ValueNotifier<bool> _buffering = ValueNotifier<bool>(false);

  StreamSubscription<RadioEngineEvent>? _eventSub;
  StreamSubscription<IcyFrame?>? _icySub;

  String _currentUrl = kDefaultEndpoint.url;
  int _retryCount = 0;
  Timer? _retryTimer;
  Timer? _slowLoadTimer;
  static const Duration _slowLoadThreshold = Duration(seconds: 6);
  bool _disposed = false;
  IcyFrame? _lastIcy;

  PlaybackController({
    required RadioPlayer player,
    required BitratePreference preference,
    ReconnectPolicy policy = const ReconnectPolicy(),
    String stationFallback = 'DUBSTEP.FM',
    AnalyticsLogger analytics = const NoOpAnalyticsLogger(),
  })  : _player = player,
        _preference = preference,
        _policy = policy,
        _stationFallback = stationFallback,
        _analytics = analytics {
    _eventSub = _player.events.listen(_onEngineEvent);
    _icySub = _player.icyMetadata.listen(_onIcyFrame);
  }

  ValueListenable<PlaybackState> get state => _state;
  ValueListenable<NotificationItem?> get currentItem => _currentItem;
  ValueListenable<bool> get slowLoading => _slowLoading;
  ValueListenable<bool> get buffering => _buffering;
  String get currentUrl => _currentUrl;
  String get stationFallback => _stationFallback;

  /// Read the persisted URL and adopt it as the current selection. Call
  /// once at app start before the listener taps PLAY.
  Future<void> load() async {
    _ensureNotDisposed();
    _currentUrl = await _preference.read();
  }

  Future<void> play() async {
    _ensureNotDisposed();
    final s = _state.value;
    // FR-006: debounce while loading or playing.
    if (s is LoadingState || s is PlayingState) return;
    _retryCount = 0;
    _cancelRetryTimer();
    _state.value = const LoadingState();
    _armSlowLoadTimer();
    unawaited(_analytics.logEvent('playback_start', {
      'bitrate_kbps': endpointForUrl(_currentUrl).bitrateKbps,
      'url': _currentUrl,
    }));
    await _player.setUrlAndPlay(_currentUrl);
  }

  Future<void> stop() async {
    _ensureNotDisposed();
    final s = _state.value;
    if (s is IdleState || s is ErrorState) return;
    _cancelRetryTimer();
    _cancelSlowLoadTimer();
    _buffering.value = false;
    _retryCount = 0;
    _lastIcy = null;
    _state.value = const IdleState();
    _pushClearedItem();
    await _player.stop();
  }

  Future<void> setUrl(String url) async {
    _ensureNotDisposed();
    if (url == _currentUrl) return;
    _currentUrl = url;
    await _preference.write(url);
    final s = _state.value;
    if (s is LoadingState || s is PlayingState) {
      _retryCount = 0;
      _cancelRetryTimer();
      _state.value = const LoadingState();
      _armSlowLoadTimer();
      unawaited(_analytics.logEvent('playback_start', {
        'bitrate_kbps': endpointForUrl(url).bitrateKbps,
        'url': url,
      }));
      await _player.setUrlAndPlay(url);
    }
    // In Idle / Error: persist but do not auto-start.
  }

  Future<void> dispose() async {
    if (_disposed) return;
    _disposed = true;
    _cancelRetryTimer();
    _cancelSlowLoadTimer();
    await _eventSub?.cancel();
    await _icySub?.cancel();
    _state.dispose();
    _currentItem.dispose();
    _slowLoading.dispose();
    _buffering.dispose();
    await _player.dispose();
  }

  // --------------------------------------------------------------------
  //  Engine event handling — the heart of the state machine.
  // --------------------------------------------------------------------

  void _onEngineEvent(RadioEngineEvent event) {
    final s = _state.value;
    switch (event) {
      case RadioEngineLoading():
        // Mid-play buffering: keep the PlayingState title/artist visible,
        // just flip the buffering flag so the UI can show a subtitle.
        if (s is PlayingState) _buffering.value = true;
        break;
      case RadioEngineReady():
        if (s is LoadingState) {
          _retryCount = 0;
          _cancelRetryTimer();
          _cancelSlowLoadTimer();
          // Enter Playing with the station fallback as the initial title.
          // ICY frames will update this in-place via _onIcyFrame.
          _state.value = PlayingState(displayTitle: _stationFallback);
          _pushFallbackItem();
          // ExoPlayer commonly parses the first ICY frame during buffering,
          // which lands while we're still in LoadingState. Replay it now so
          // the title/notification don't get stuck on the fallback.
          if (_lastIcy != null) _applyIcyFrame(_lastIcy);
        } else if (s is PlayingState) {
          // Buffer recovered mid-play; keep the current title.
          _buffering.value = false;
        }
        break;
      case RadioEngineError(:final cause):
        if (s is LoadingState || s is PlayingState) {
          _buffering.value = false;
          _retryCount += 1;
          final willRetry = _retryCount < _policy.maxAttempts;
          unawaited(_analytics.logEvent('playback_error', {
            'bitrate_kbps': endpointForUrl(_currentUrl).bitrateKbps,
            'url': _currentUrl,
            'reason': _describeCause(cause),
            'phase': s is LoadingState ? 'loading' : 'playing',
            'retry_count': _retryCount,
            'will_retry': willRetry,
          }));
          if (!willRetry) {
            _retryCount = 0;
            _cancelRetryTimer();
            _cancelSlowLoadTimer();
            _state.value = ErrorState(cause);
            _pushClearedItem();
            unawaited(_analytics.logEvent('playback_failed', {
              'bitrate_kbps': endpointForUrl(_currentUrl).bitrateKbps,
              'url': _currentUrl,
              'reason': _describeCause(cause),
            }));
            // Best-effort: ensure the engine is in a stopped state too.
            unawaited(_player.stop());
          } else {
            _state.value = const LoadingState();
            _armSlowLoadTimer();
            _scheduleRetry();
          }
        }
        break;
      case RadioEngineStopped():
        if (s is PlayingState || s is LoadingState) {
          _cancelRetryTimer();
          _cancelSlowLoadTimer();
          _buffering.value = false;
          _retryCount = 0;
          _state.value = const IdleState();
          _pushClearedItem();
        }
        break;
    }
  }

  void _onIcyFrame(IcyFrame? frame) {
    _lastIcy = frame;
    if (_state.value is! PlayingState) return;
    _applyIcyFrame(frame);
  }

  void _applyIcyFrame(IcyFrame? frame) {
    final parsed = parseIcyTitle(
      frame?.streamTitle,
      stationFallback: _stationFallback,
    );
    _state.value = PlayingState(
      displayTitle: parsed.displayTitle,
      displayArtist: parsed.displayArtist,
    );
    _currentItem.value = NotificationItem(
      title: parsed.displayTitle,
      artist: parsed.displayArtist ?? _stationFallback,
      url: _currentUrl,
      clearedToFallback: !parsed.hasIcyTitle,
    );
  }

  // --------------------------------------------------------------------
  //  Retry + notification helpers.
  // --------------------------------------------------------------------

  void _scheduleRetry() {
    _cancelRetryTimer();
    if (_policy.backoff == Duration.zero) {
      // Test mode: retry on the next microtask so FakeAsync can drive it.
      scheduleMicrotask(() {
        if (_disposed) return;
        if (_state.value is! LoadingState) return;
        unawaited(_player.setUrlAndPlay(_currentUrl));
      });
      return;
    }
    _retryTimer = Timer(_policy.backoff, () {
      if (_disposed) return;
      if (_state.value is! LoadingState) return;
      unawaited(_player.setUrlAndPlay(_currentUrl));
    });
  }

  void _cancelRetryTimer() {
    _retryTimer?.cancel();
    _retryTimer = null;
  }

  void _armSlowLoadTimer() {
    _cancelSlowLoadTimer();
    _slowLoading.value = false;
    _slowLoadTimer = Timer(_slowLoadThreshold, () {
      if (_disposed) return;
      if (_state.value is LoadingState) _slowLoading.value = true;
    });
  }

  void _cancelSlowLoadTimer() {
    _slowLoadTimer?.cancel();
    _slowLoadTimer = null;
    _slowLoading.value = false;
  }

  void _pushFallbackItem() {
    _currentItem.value = NotificationItem(
      title: _stationFallback,
      artist: _stationFallback,
      url: _currentUrl,
      clearedToFallback: true,
    );
  }

  void _pushClearedItem() {
    _currentItem.value = NotificationItem(
      title: _stationFallback,
      artist: _stationFallback,
      url: _currentUrl,
      clearedToFallback: true,
    );
  }

  String _describeCause(Object cause) {
    final s = cause.toString();
    return s.length > 100 ? s.substring(0, 100) : s;
  }

  void _ensureNotDisposed() {
    if (_disposed) {
      throw StateError('PlaybackController used after dispose()');
    }
  }
}
