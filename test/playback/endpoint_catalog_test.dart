import 'package:flutter_test/flutter_test.dart';
import 'package:dubstepfm/src/l10n/strings.dart';
import 'package:dubstepfm/src/playback/endpoint_catalog.dart';
import 'package:dubstepfm/src/playback/l10n_keys.dart';

void main() {
  group('Endpoint catalog invariants (data-model.md §E3)', () {
    test('I1: exactly four active endpoints', () {
      expect(kRadioEndpoints.length, 4);
      final bitrates = kRadioEndpoints.map((e) => e.bitrateKbps).toSet();
      expect(bitrates, {24, 64, 128, 256});
    });

    test('I2: no active endpoint references shout.dubstep.fm (FR-008)', () {
      expect(catalogReferencesShout(), isFalse);
      for (final e in kRadioEndpoints) {
        expect(e.url.contains('shout.dubstep.fm'), isFalse,
            reason: 'Active catalog must not include shout.dubstep.fm');
      }
    });

    test('I3: 128 kbps is the default endpoint (FR-005)', () {
      expect(kDefaultEndpoint.bitrateKbps, 128);
      expect(
        kRadioEndpoints.any((e) => e.url == kDefaultEndpoint.url),
        isTrue,
      );
    });

    test('I4: every labelKey exists in kStrings', () {
      for (final e in kRadioEndpoints) {
        expect(kStrings.containsKey(e.labelKey), isTrue,
            reason: 'kStrings is missing key "${e.labelKey}"');
      }
    });

    test('I5: every URL parses as http or https', () {
      for (final e in kRadioEndpoints) {
        final uri = Uri.tryParse(e.url);
        expect(uri, isNotNull, reason: 'Invalid URL: ${e.url}');
        expect(uri!.scheme == 'http' || uri.scheme == 'https', isTrue,
            reason: 'Unexpected scheme on ${e.url}');
      }
    });

    test('label keys align with bitrate map', () {
      expect(labelKeysAreKnown(), isTrue);
      expect(kLabelKeyByBitrate.length, 4);
    });
  });

  group('endpointForUrl', () {
    test('returns the matching endpoint for a known URL', () {
      final e = endpointForUrl(kRadioEndpoints.last.url);
      expect(e.bitrateKbps, 256);
    });

    test('falls back to default for an unknown URL', () {
      final e = endpointForUrl('http://example.invalid/whatever');
      expect(e.url, kDefaultEndpoint.url);
    });
  });
}
