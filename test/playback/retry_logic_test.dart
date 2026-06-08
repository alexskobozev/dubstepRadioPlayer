import 'package:flutter_test/flutter_test.dart';
import 'package:dubstepfm/src/playback/bitrate_preference.dart';
import 'package:dubstepfm/src/playback/playback_controller.dart';
import 'package:dubstepfm/src/playback/playback_state.dart';

import 'fake_radio_player.dart';

void main() {
  group('Reconnect bound (FR-010 / FR-011 / SC-007)', () {
    test('9 errors stay in Loading; 10th flips to Error (default policy)',
        () async {
      final fake = FakeRadioPlayer();
      final c = PlaybackController(
        player: fake,
        preference: InMemoryBitratePreference(),
        policy: const ReconnectPolicy(
          maxAttempts: 10,
          backoff: Duration.zero,
        ),
      );
      await c.load();
      await c.play();
      for (var i = 0; i < 9; i++) {
        fake.signalError();
        await Future.delayed(Duration.zero);
        await Future.delayed(Duration.zero);
      }
      expect(c.state.value, isA<LoadingState>(),
          reason: '9 errors must NOT yet flip to Error');
      fake.signalError();
      await Future.delayed(Duration.zero);
      expect(c.state.value, isA<ErrorState>(),
          reason: '10th error must transition to Error');
      await c.dispose();
    });

    test('successful Playing resets the counter', () async {
      final fake = FakeRadioPlayer();
      final c = PlaybackController(
        player: fake,
        preference: InMemoryBitratePreference(),
        policy: const ReconnectPolicy(
          maxAttempts: 3,
          backoff: Duration.zero,
        ),
      );
      await c.load();
      await c.play();
      // 2 errors, then recover.
      for (var i = 0; i < 2; i++) {
        fake.signalError();
        await Future.delayed(Duration.zero);
        await Future.delayed(Duration.zero);
      }
      fake.signalReady();
      await Future.delayed(Duration.zero);
      expect(c.state.value, isA<PlayingState>());

      // Counter is back at 0 — another 2 errors must stay in Loading
      // (would hit limit 3 only after the 3rd).
      for (var i = 0; i < 2; i++) {
        fake.signalError();
        await Future.delayed(Duration.zero);
        await Future.delayed(Duration.zero);
      }
      expect(c.state.value, isA<LoadingState>());
      await c.dispose();
    });

    test('error path uses backoff via setUrlAndPlay re-call', () async {
      final fake = FakeRadioPlayer();
      final c = PlaybackController(
        player: fake,
        preference: InMemoryBitratePreference(),
        policy: const ReconnectPolicy(
          maxAttempts: 10,
          backoff: Duration.zero,
        ),
      );
      await c.load();
      await c.play();
      // 1st call from c.play().
      expect(fake.setUrlCalls.length, 1);
      fake.signalError();
      // Allow the scheduled retry microtask to fire.
      await Future.delayed(Duration.zero);
      await Future.delayed(Duration.zero);
      // 2nd call from the scheduled retry.
      expect(fake.setUrlCalls.length, 2);
      await c.dispose();
    });
  });
}
