/// The shipping catalog of live-stream endpoints.
///
/// Encodes the findings of the DNS investigation summarised in
/// `specs/002-stream-playback-migration/research.md` (R3) and
/// `data-model.md` (§E3): only the direct IP `50.118.246.51:80` is online
/// at release time, so all four bitrates ship pointing at that IP.
///
/// Hostname-based URLs (`stream.dubstep.fm`) are documented below for
/// restoration when the upstream operator brings the other servers back
/// online — they are deliberately NOT in the active rotation.
library;

import 'l10n_keys.dart' show kLabelKeyByBitrate;

class RadioEndpoint {
  final int bitrateKbps;
  final String url;
  final String labelKey;

  const RadioEndpoint({
    required this.bitrateKbps,
    required this.url,
    required this.labelKey,
  });

  @override
  String toString() => 'RadioEndpoint($bitrateKbps kbps, $url)';
}

/// Active catalog used by the app. Order matters for the bitrate dialog.
const List<RadioEndpoint> kRadioEndpoints = [
  RadioEndpoint(
    bitrateKbps: 24,
    url: 'http://50.118.246.51:80/24mp3',
    labelKey: 'b24kbps',
  ),
  RadioEndpoint(
    bitrateKbps: 64,
    url: 'http://50.118.246.51:80/64mp3',
    labelKey: 'b64kbps',
  ),
  RadioEndpoint(
    bitrateKbps: 128,
    url: 'http://50.118.246.51:80/128mp3',
    labelKey: 'b128kbps',
  ),
  RadioEndpoint(
    bitrateKbps: 256,
    url: 'http://50.118.246.51:80/256mp3',
    labelKey: 'b256kbps',
  ),
];

/// First-launch default. Mirrors legacy `Links.LINK_128`.
const RadioEndpoint kDefaultEndpoint = RadioEndpoint(
  bitrateKbps: 128,
  url: 'http://50.118.246.51:80/128mp3',
  labelKey: 'b128kbps',
);

// ---------------------------------------------------------------------------
// Documented-but-inactive hostnames (do NOT add to `kRadioEndpoints` without
// first verifying with `dig` or equivalent that the operator has restored
// the round-robin and that none of the previously-dead IPs are still listed):
//
//   http://stream.dubstep.fm/24mp3   — round-robin to 3 IPs; 2 dead at release.
//   http://stream.dubstep.fm/64mp3
//   http://stream.dubstep.fm/128mp3
//   http://stream.dubstep.fm/256mp3
//
//   http://shout.dubstep.fm/256mp3   — resolves only to 50.117.1.60 (OFFLINE).
//                                       Root cause of the legacy listen.pls
//                                       playlist abort. NEVER re-enable
//                                       without re-pointing the host first.
// ---------------------------------------------------------------------------

/// Helper: find the endpoint matching a stored URL, or fall back to default.
RadioEndpoint endpointForUrl(String url) {
  for (final e in kRadioEndpoints) {
    if (e.url == url) return e;
  }
  return kDefaultEndpoint;
}

// Internal sanity-check (referenced by tests).
bool catalogReferencesShout() {
  for (final e in kRadioEndpoints) {
    if (e.url.contains('shout.dubstep.fm')) return true;
  }
  return false;
}

// Used by endpoint_catalog_test.dart to verify each label key exists in the
// strings map without forcing the catalog file to depend on Flutter.
const Set<String> kKnownLabelKeys = {
  'b24kbps',
  'b64kbps',
  'b128kbps',
  'b256kbps',
};

// Tie-back to ensure each catalog entry's labelKey is from the known set.
bool labelKeysAreKnown() {
  for (final e in kRadioEndpoints) {
    if (!kKnownLabelKeys.contains(e.labelKey)) return false;
    if (kLabelKeyByBitrate[e.bitrateKbps] != e.labelKey) return false;
  }
  return true;
}
