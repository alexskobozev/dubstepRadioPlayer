/// Production [RadioPlayer] implementation backed by `package:just_audio`.
///
/// Maps `ProcessingState` / `errorStream` events to [RadioEngineEvent] and
/// `icyMetadataStream` frames to [IcyFrame]. Sets the audio attributes
/// `usage: media, contentType: music` (R5) at construction time. Disables
/// just_audio's built-in interruption handling so that `audio_session` /
/// `audio_service` own audio focus (R5, FR-017).
library;

import 'dart:async';

import 'package:just_audio/just_audio.dart' as ja;

import 'radio_player.dart';

class JustAudioRadioPlayer implements RadioPlayer {
  static const String _userAgent = 'dubstep.fm';

  ja.AudioPlayer _player;
  final bool _ownsPlayer;
  final StreamController<RadioEngineEvent> _events =
      StreamController<RadioEngineEvent>.broadcast();
  final StreamController<IcyFrame?> _icy =
      StreamController<IcyFrame?>.broadcast();

  StreamSubscription<ja.PlayerState>? _stateSub;
  StreamSubscription<ja.PlayerException>? _errorSub;
  StreamSubscription<ja.IcyMetadata?>? _icySub;

  bool _disposed = false;
  bool _suppressNextStoppedEvent = false;

  /// Set when a start attempt times out: the native engine may be wedged
  /// (notably on iOS, where the OS suspends a long-backgrounded, non-playing
  /// app and tears down just_audio's AVPlayer + local proxy sockets). Reusing
  /// it leaves playback "connecting" forever until app kill, so we stand up a
  /// fresh engine before the next attempt.
  bool _needsRecreate = false;

  /// A live-stream start that hasn't begun within this window is treated as a
  /// failure, so the controller's reconnect policy can recover rather than
  /// spinning indefinitely on a dead engine.
  static const Duration _connectTimeout = Duration(seconds: 20);

  JustAudioRadioPlayer({ja.AudioPlayer? player})
      : _player = player ?? _createPlayer(),
        _ownsPlayer = player == null {
    _subscribe();
  }

  static ja.AudioPlayer _createPlayer() => ja.AudioPlayer(
        userAgent: _userAgent,
        // We own interruption *reactions* (audio_session's
        // interruptionEventStream / becomingNoisyEventStream call
        // controller.stop()), so just_audio must not also pause/resume.
        handleInterruptions: false,
        // …but just_audio MUST own audio-session activation. It calls
        // session.setActive(true) before each play (requesting audio focus)
        // and deactivates on stop. Without this nothing ever requests focus,
        // so (a) interruptionEventStream never fires — calls don't pause the
        // radio — and (b) on iOS, after a route change leaves the
        // AVAudioSession inactive, playback stalls "connecting" forever until
        // app restart. Reactivating on every play fixes both. (audio_service
        // does NOT request focus itself when paired with just_audio.)
        handleAudioSessionActivation: true,
      );

  void _subscribe() {
    _stateSub = _player.playerStateStream.listen(_onPlayerState);
    _errorSub = _player.errorStream.listen(_onError);
    _icySub = _player.icyMetadataStream.listen(_onIcyMetadata);
  }

  /// Replace the (possibly wedged) engine with a fresh one. Only valid when we
  /// created the player ourselves; an injected test player is left untouched.
  Future<void> _recreatePlayer() async {
    final old = _player;
    await _stateSub?.cancel();
    await _errorSub?.cancel();
    await _icySub?.cancel();
    _suppressNextStoppedEvent = false;
    _player = _createPlayer();
    _subscribe();
    // Dispose the corpse last; its teardown events are already unhooked. A
    // wedged engine can itself throw on dispose, so swallow it.
    try {
      await old.dispose();
    } catch (_) {}
  }

  @override
  Stream<RadioEngineEvent> get events => _events.stream;

  @override
  Stream<IcyFrame?> get icyMetadata => _icy.stream;

  @override
  Future<void> setUrlAndPlay(String url) async {
    _ensureNotDisposed();
    _events.add(const RadioEngineLoading());
    // A previous attempt timed out against a wedged engine; replace it before
    // retrying rather than reusing the corpse (the iOS "connecting forever").
    if (_needsRecreate && _ownsPlayer) {
      _needsRecreate = false;
      await _recreatePlayer();
    }
    try {
      // Stop any in-flight playback first so the engine surfaces a fresh
      // loading→ready cycle for the new URL rather than carrying state over.
      // Bound only the connect (stop + setAudioSource): a load that never
      // begins (e.g. a suspended-then-resumed engine whose local proxy socket
      // is dead) must surface as an error so the reconnect policy can recover,
      // not hang. play() is deliberately OUTSIDE this — its future completes
      // only when playback *ends*, so a live stream that never ends would
      // otherwise "time out" mid-playback and force an endless reconnect loop.
      await () async {
        _suppressNextStoppedEvent = true;
        await _player.stop();
        await _player.setAudioSource(ja.AudioSource.uri(Uri.parse(url)));
      }()
          .timeout(_connectTimeout);
      // Fire-and-forget; start errors surface via errorStream → _onError.
      unawaited(_player.play());
    } on TimeoutException catch (e) {
      // Don't trust this engine again — recreate it on the next attempt.
      _needsRecreate = true;
      _events.add(RadioEngineError(e));
    } on ja.PlayerException catch (e) {
      _events.add(RadioEngineError(e));
    } catch (e) {
      _events.add(RadioEngineError(e));
    }
  }

  @override
  Future<void> stop() async {
    _ensureNotDisposed();
    await _player.stop();
    _icy.add(null);
    _events.add(const RadioEngineStopped());
  }

  @override
  Future<void> dispose() async {
    if (_disposed) return;
    _disposed = true;
    await _stateSub?.cancel();
    await _errorSub?.cancel();
    await _icySub?.cancel();
    await _player.dispose();
    await _events.close();
    await _icy.close();
  }

  // -------------------------------------------------------------------------

  void _onPlayerState(ja.PlayerState state) {
    switch (state.processingState) {
      case ja.ProcessingState.idle:
        if (_suppressNextStoppedEvent) {
          _suppressNextStoppedEvent = false;
        }
        // Don't translate idle into an event — the controller drives Idle
        // explicitly via stop(). An organic idle (e.g. the engine giving
        // up) will be surfaced separately by errorStream.
        break;
      case ja.ProcessingState.loading:
      case ja.ProcessingState.buffering:
        _events.add(const RadioEngineLoading());
        break;
      case ja.ProcessingState.ready:
        if (state.playing) {
          _events.add(const RadioEngineReady());
        }
        break;
      case ja.ProcessingState.completed:
        // Live streams shouldn't complete; treat as stopped if it happens.
        _events.add(const RadioEngineStopped());
        _icy.add(null);
        break;
    }
  }

  void _onError(ja.PlayerException e) {
    _events.add(RadioEngineError(e));
  }

  void _onIcyMetadata(ja.IcyMetadata? metadata) {
    final title = metadata?.info?.title;
    if (title == null) {
      _icy.add(const IcyFrame(null));
    } else {
      _icy.add(IcyFrame(title));
    }
  }

  void _ensureNotDisposed() {
    if (_disposed) {
      throw StateError('JustAudioRadioPlayer used after dispose()');
    }
  }
}
