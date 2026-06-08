/// Thin async wrapper around `shared_preferences` for the
/// `current_bitrate_url` key. Defaults to the 128 kbps endpoint on first
/// launch (FR-005).
library;

import 'package:shared_preferences/shared_preferences.dart';

import 'endpoint_catalog.dart';

abstract class BitratePreference {
  Future<String> read();
  Future<void> write(String url);
}

class SharedPreferencesBitratePreference implements BitratePreference {
  static const String kKey = 'current_bitrate_url';

  @override
  Future<String> read() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(kKey) ?? kDefaultEndpoint.url;
  }

  @override
  Future<void> write(String url) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(kKey, url);
  }
}

/// In-memory implementation for tests.
class InMemoryBitratePreference implements BitratePreference {
  String _value;
  InMemoryBitratePreference([String? initial])
      : _value = initial ?? kDefaultEndpoint.url;

  @override
  Future<String> read() async => _value;

  @override
  Future<void> write(String url) async {
    _value = url;
  }
}
