import 'package:firebase_analytics/firebase_analytics.dart';

import 'analytics_logger.dart';

class FirebaseAnalyticsLogger implements AnalyticsLogger {
  final FirebaseAnalytics _analytics;
  FirebaseAnalyticsLogger([FirebaseAnalytics? analytics])
      : _analytics = analytics ?? FirebaseAnalytics.instance;

  @override
  Future<void> logEvent(String name, Map<String, Object?> params) {
    final sanitized = <String, Object>{};
    params.forEach((key, value) {
      if (value == null) return;
      if (value is String) {
        sanitized[key] = value.length > 100 ? value.substring(0, 100) : value;
      } else if (value is bool) {
        // Firebase Analytics only accepts String or num parameter values;
        // a raw bool trips an assertion. Encode as 1/0.
        sanitized[key] = value ? 1 : 0;
      } else if (value is num) {
        sanitized[key] = value;
      } else {
        final s = value.toString();
        sanitized[key] = s.length > 100 ? s.substring(0, 100) : s;
      }
    });
    return _analytics.logEvent(name: name, parameters: sanitized);
  }
}
