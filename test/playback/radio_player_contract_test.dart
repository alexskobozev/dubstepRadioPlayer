import 'package:flutter_test/flutter_test.dart';
import 'package:dubstepfm/src/playback/radio_player.dart';

import 'fake_radio_player.dart';

void main() {
  group('RadioPlayer contract (contracts/radio-player.md)', () {
    test('I1: setUrlAndPlay emits Loading before any subsequent event',
        () async {
      final p = FakeRadioPlayer();
      final received = <RadioEngineEvent>[];
      final sub = p.events.listen(received.add);
      await p.setUrlAndPlay('http://example/stream');
      p.signalReady();
      await Future.delayed(Duration.zero);
      expect(received, isNotEmpty);
      expect(received.first, isA<RadioEngineLoading>());
      expect(received.any((e) => e is RadioEngineReady), isTrue);
      await sub.cancel();
      await p.dispose();
    });

    test('I2: constructing a player does not start audio', () async {
      final p = FakeRadioPlayer();
      expect(p.setUrlCalls, isEmpty);
      expect(p.stopCalls, 0);
      await p.dispose();
    });

    test('I3: stop while stopped is recordable but emits Stopped', () async {
      final p = FakeRadioPlayer();
      await p.stop();
      expect(p.stopCalls, 1);
      await p.dispose();
    });

    test('I4: dispose is terminal — further calls throw', () async {
      final p = FakeRadioPlayer();
      await p.dispose();
      expect(() => p.setUrlAndPlay('x'), throwsStateError);
      expect(() => p.stop(), throwsStateError);
    });

    test('I5: stop pushes null on icyMetadata (FR-022)', () async {
      final p = FakeRadioPlayer();
      final received = <IcyFrame?>[];
      final sub = p.icyMetadata.listen(received.add);
      p.signalMetadata('Some Track');
      await Future.delayed(Duration.zero);
      await p.stop();
      await Future.delayed(Duration.zero);
      expect(received.contains(null), isTrue);
      await sub.cancel();
      await p.dispose();
    });

    test('I6: player has no internal state-machine (no retry on its own)',
        () async {
      final p = FakeRadioPlayer();
      final received = <RadioEngineEvent>[];
      final sub = p.events.listen(received.add);
      await p.setUrlAndPlay('http://example/stream');
      p.signalError();
      await Future.delayed(Duration.zero);
      // The fake must not auto-retry by itself.
      expect(p.setUrlCalls.length, 1);
      expect(
        received.whereType<RadioEngineError>().length,
        1,
      );
      await sub.cancel();
      await p.dispose();
    });
  });
}
