import 'package:flutter_test/flutter_test.dart';
import 'package:dubstepfm/src/playback/icy_metadata.dart';

void main() {
  const fallback = 'DUBSTEP.FM';

  group('parseIcyTitle (data-model.md §E5)', () {
    test('null → station fallback', () {
      final m = parseIcyTitle(null, stationFallback: fallback);
      expect(m.displayTitle, fallback);
      expect(m.displayArtist, isNull);
      expect(m.hasIcyTitle, isFalse);
    });

    test('empty → station fallback', () {
      final m = parseIcyTitle('', stationFallback: fallback);
      expect(m.displayTitle, fallback);
      expect(m.displayArtist, isNull);
    });

    test('whitespace → station fallback', () {
      final m = parseIcyTitle('   ', stationFallback: fallback);
      expect(m.displayTitle, fallback);
      expect(m.displayArtist, isNull);
    });

    test('plain title (no separator) → entire string as title', () {
      final m = parseIcyTitle('LiveMixSet003', stationFallback: fallback);
      expect(m.displayTitle, 'LiveMixSet003');
      expect(m.displayArtist, isNull);
    });

    test('artist - title → split', () {
      final m =
          parseIcyTitle('Skream - Midnight Request Line', stationFallback: fallback);
      expect(m.displayArtist, 'Skream');
      expect(m.displayTitle, 'Midnight Request Line');
    });

    test('A - B - C → first segment is artist, rest is title', () {
      final m = parseIcyTitle('A - B - C', stationFallback: fallback);
      expect(m.displayArtist, 'A');
      expect(m.displayTitle, 'B - C');
    });

    test('leading/trailing whitespace trimmed', () {
      final m =
          parseIcyTitle('  Burial - Archangel  ', stationFallback: fallback);
      expect(m.displayArtist, 'Burial');
      expect(m.displayTitle, 'Archangel');
    });

    test('separator at start (no artist) → whole trimmed as title', () {
      final m =
          parseIcyTitle(' - Just Title', stationFallback: fallback);
      // index <= 0 → treated as plain title
      expect(m.displayArtist, isNull);
      expect(m.displayTitle, '- Just Title');
    });

    test('separator at end (empty title side) → whole as title', () {
      final m =
          parseIcyTitle('Artist - ', stationFallback: fallback);
      expect(m.displayArtist, isNull);
      expect(m.displayTitle, 'Artist -');
    });

    test('hasIcyTitle reflects presence of a real title', () {
      expect(parseIcyTitle(null, stationFallback: fallback).hasIcyTitle,
          isFalse);
      expect(
          parseIcyTitle('Foo', stationFallback: fallback).hasIcyTitle, isTrue);
      expect(
          parseIcyTitle('Foo - Bar', stationFallback: fallback).hasIcyTitle,
          isTrue);
    });
  });
}
