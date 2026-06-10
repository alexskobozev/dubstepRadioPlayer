/// Engine-agnostic analytics sink used by [PlaybackController].
///
/// Decouples the playback state machine from `firebase_analytics` so the
/// controller stays unit-testable without a Firebase platform channel.
library;

abstract class AnalyticsLogger {
  Future<void> logEvent(String name, Map<String, Object?> params);
}

class NoOpAnalyticsLogger implements AnalyticsLogger {
  const NoOpAnalyticsLogger();

  @override
  Future<void> logEvent(String name, Map<String, Object?> params) async {}
}
