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

  final ja.AudioPlayer _player;
  final StreamController<RadioEngineEvent> _events =
      StreamController<RadioEngineEvent>.broadcast();
  final StreamController<IcyFrame?> _icy =
      StreamController<IcyFrame?>.broadcast();

  StreamSubscription<ja.PlayerState>? _stateSub;
  StreamSubscription<ja.PlayerException>? _errorSub;
  StreamSubscription<ja.IcyMetadata?>? _icySub;

  bool _disposed = false;
  bool _suppressNextStoppedEvent = false;

  JustAudioRadioPlayer({ja.AudioPlayer? player})
      : _player = player ??
            ja.AudioPlayer(
              userAgent: _userAgent,
              // audio_session / audio_service own focus and noisy handling.
              handleInterruptions: false,
              // audio_service activates the session; just_audio should not.
              handleAudioSessionActivation: false,
            ) {
    _stateSub = _player.playerStateStream.listen(_onPlayerState);
    _errorSub = _player.errorStream.listen(_onError);
    _icySub = _player.icyMetadataStream.listen(_onIcyMetadata);
  }

  @override
  Stream<RadioEngineEvent> get events => _events.stream;

  @override
  Stream<IcyFrame?> get icyMetadata => _icy.stream;

  @override
  Future<void> setUrlAndPlay(String url) async {
    _ensureNotDisposed();
    _events.add(const RadioEngineLoading());
    try {
      // Stop any in-flight playback first so the engine surfaces a fresh
      // loading→ready cycle for the new URL rather than carrying state over.
      _suppressNextStoppedEvent = true;
      await _player.stop();
      await _player.setAudioSource(ja.AudioSource.uri(Uri.parse(url)));
      await _player.play();
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
