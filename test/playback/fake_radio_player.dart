/// Deterministic [RadioPlayer] for unit tests. Exposes `signal*` methods
/// so tests can drive the controller through every state transition
/// without timing or platform-channel surprises.
library;

import 'dart:async';

import 'package:dubstepfm/src/playback/radio_player.dart';

class FakeRadioPlayer implements RadioPlayer {
  final StreamController<RadioEngineEvent> _events =
      StreamController<RadioEngineEvent>.broadcast();
  final StreamController<IcyFrame?> _icy =
      StreamController<IcyFrame?>.broadcast();

  final List<String> setUrlCalls = [];
  int stopCalls = 0;
  bool _disposed = false;

  @override
  Stream<RadioEngineEvent> get events => _events.stream;

  @override
  Stream<IcyFrame?> get icyMetadata => _icy.stream;

  @override
  Future<void> setUrlAndPlay(String url) async {
    _ensureNotDisposed();
    setUrlCalls.add(url);
    _events.add(const RadioEngineLoading());
  }

  @override
  Future<void> stop() async {
    _ensureNotDisposed();
    stopCalls += 1;
    _icy.add(null);
    // Note: per contract, stop() while already stopped is silent — but the
    // controller is the one that decides "already stopped"; this fake
    // always emits so tests can observe call counts.
    _events.add(const RadioEngineStopped());
  }

  @override
  Future<void> dispose() async {
    _disposed = true;
    await _events.close();
    await _icy.close();
  }

  // -------------------------------------------------------------------
  //  Test-driver methods.
  // -------------------------------------------------------------------

  void signalReady() {
    _ensureNotDisposed();
    _events.add(const RadioEngineReady());
  }

  void signalError([Object? cause]) {
    _ensureNotDisposed();
    _events.add(RadioEngineError(cause ?? 'fake-error'));
  }

  void signalStopped() {
    _ensureNotDisposed();
    _events.add(const RadioEngineStopped());
  }

  void signalMetadata(String? streamTitle) {
    _ensureNotDisposed();
    _icy.add(IcyFrame(streamTitle));
  }

  void _ensureNotDisposed() {
    if (_disposed) throw StateError('FakeRadioPlayer used after dispose');
  }
}
