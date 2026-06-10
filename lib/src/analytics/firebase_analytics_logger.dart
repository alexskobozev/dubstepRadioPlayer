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
      } else if (value is num || value is bool) {
        sanitized[key] = value;
      } else {
        final s = value.toString();
        sanitized[key] = s.length > 100 ? s.substring(0, 100) : s;
      }
    });
    return _analytics.logEvent(name: name, parameters: sanitized);
  }
}
