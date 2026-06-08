/// Engine-wrapping interface — see
/// `specs/002-stream-playback-migration/contracts/radio-player.md`.
///
/// Decouples the playback state machine from `package:just_audio` so the
/// controller can be unit-tested without an audio device (FR-023).
library;

abstract class RadioPlayer {
  /// Engine-level events emitted in response to setUrlAndPlay/stop calls
  /// and to network/playback events from the underlying engine.
  Stream<RadioEngineEvent> get events;

  /// Latest ICY metadata frame. `null` until first frame received; reset
  /// to `null` when the engine is stopped (FR-022).
  Stream<IcyFrame?> get icyMetadata;

  /// Idempotent: start playing the supplied URL. Buffers, then either
  /// emits [RadioEngineReady] (→ controller transitions to playing) or
  /// [RadioEngineError] (→ controller increments retry counter).
  Future<void> setUrlAndPlay(String url);

  /// Idempotent: stop and release any held audio focus.
  Future<void> stop();

  /// Final teardown. After [dispose], no further events are emitted and
  /// subsequent method calls throw [StateError].
  Future<void> dispose();
}

sealed class RadioEngineEvent {
  const RadioEngineEvent();
}

class RadioEngineLoading extends RadioEngineEvent {
  const RadioEngineLoading();
  @override
  String toString() => 'RadioEngineLoading';
}

class RadioEngineReady extends RadioEngineEvent {
  const RadioEngineReady();
  @override
  String toString() => 'RadioEngineReady';
}

class RadioEngineError extends RadioEngineEvent {
  final Object cause;
  const RadioEngineError(this.cause);
  @override
  String toString() => 'RadioEngineError($cause)';
}

class RadioEngineStopped extends RadioEngineEvent {
  const RadioEngineStopped();
  @override
  String toString() => 'RadioEngineStopped';
}

/// Inline track metadata frame as broadcast by Shoutcast/Icecast streams.
class IcyFrame {
  /// Raw `StreamTitle` (commonly `"Artist - Track"` or just `"Track"`).
  /// `null` when the stream broadcasts no title at this point in time.
  final String? streamTitle;

  const IcyFrame(this.streamTitle);

  @override
  bool operator ==(Object other) =>
      other is IcyFrame && other.streamTitle == streamTitle;

  @override
  int get hashCode => streamTitle.hashCode;

  @override
  String toString() => 'IcyFrame($streamTitle)';
}
