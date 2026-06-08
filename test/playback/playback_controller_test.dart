import 'package:flutter_test/flutter_test.dart';
import 'package:dubstepfm/src/playback/bitrate_preference.dart';
import 'package:dubstepfm/src/playback/playback_controller.dart';
import 'package:dubstepfm/src/playback/playback_state.dart';

import 'fake_radio_player.dart';

PlaybackController _makeController(
  FakeRadioPlayer fake, {
  Duration backoff = Duration.zero,
  int maxAttempts = 10,
  InMemoryBitratePreference? pref,
}) {
  return PlaybackController(
    player: fake,
    preference: pref ?? InMemoryBitratePreference(),
    policy:
        ReconnectPolicy(maxAttempts: maxAttempts, backoff: backoff),
    stationFallback: 'DUBSTEP.FM',
  );
}

void main() {
  group('PlaybackController state machine', () {
    test('initial state is Idle', () async {
      final fake = FakeRadioPlayer();
      final c = _makeController(fake);
      expect(c.state.value, isA<IdleState>());
      await c.dispose();
    });

    test('Idle + play → Loading + setUrlAndPlay(currentUrl)', () async {
      final fake = FakeRadioPlayer();
      final c = _makeController(fake);
      await c.load();
      await c.play();
      expect(c.state.value, isA<LoadingState>());
      expect(fake.setUrlCalls, hasLength(1));
      expect(fake.setUrlCalls.first, c.currentUrl);
      await c.dispose();
    });

    test('Loading + EngineReady → Playing', () async {
      final fake = FakeRadioPlayer();
      final c = _makeController(fake);
      await c.load();
      await c.play();
      fake.signalReady();
      await Future.delayed(Duration.zero);
      expect(c.state.value, isA<PlayingState>());
      await c.dispose();
    });

    test('FR-006 debounce: repeated play() while Loading is a no-op',
        () async {
      final fake = FakeRadioPlayer();
      final c = _makeController(fake);
      await c.load();
      await c.play();
      await c.play();
      await c.play();
      expect(fake.setUrlCalls.length, 1);
      await c.dispose();
    });

    test('FR-006 debounce: repeated play() while Playing is a no-op',
        () async {
      final fake = FakeRadioPlayer();
      final c = _makeController(fake);
      await c.load();
      await c.play();
      fake.signalReady();
      await Future.delayed(Duration.zero);
      await c.play();
      expect(fake.setUrlCalls.length, 1);
      await c.dispose();
    });

    test('Playing + stop → Idle + RadioPlayer.stop()', () async {
      final fake = FakeRadioPlayer();
      final c = _makeController(fake);
      await c.load();
      await c.play();
      fake.signalReady();
      await Future.delayed(Duration.zero);
      await c.stop();
      expect(c.state.value, isA<IdleState>());
      expect(fake.stopCalls, 1);
      await c.dispose();
    });

    test('Idle + stop is a no-op', () async {
      final fake = FakeRadioPlayer();
      final c = _makeController(fake);
      await c.stop();
      expect(c.state.value, isA<IdleState>());
      expect(fake.stopCalls, 0);
      await c.dispose();
    });

    test('FR-007 mid-play setUrl restarts on new URL and resets retry counter',
        () async {
      final fake = FakeRadioPlayer();
      final c = _makeController(fake, maxAttempts: 10);
      await c.load();
      await c.play();
      fake.signalReady();
      await Future.delayed(Duration.zero);
      // Burn 5 errors so the counter is mid-run.
      for (var i = 0; i < 5; i++) {
        fake.signalError();
        await Future.delayed(Duration.zero);
        await Future.delayed(Duration.zero);
      }
      final priorCalls = fake.setUrlCalls.length;
      await c.setUrl('http://50.118.246.51:80/256mp3');
      expect(c.state.value, isA<LoadingState>());
      expect(fake.setUrlCalls.last, 'http://50.118.246.51:80/256mp3');
      expect(fake.setUrlCalls.length, greaterThan(priorCalls));
      // After setUrl the retry counter is back at 0 — 9 fresh errors stay
      // in Loading; the 10th flips to Error.
      for (var i = 0; i < 9; i++) {
        fake.signalError();
        await Future.delayed(Duration.zero);
        await Future.delayed(Duration.zero);
      }
      expect(c.state.value, isA<LoadingState>());
      fake.signalError();
      await Future.delayed(Duration.zero);
      expect(c.state.value, isA<ErrorState>());
      await c.dispose();
    });

    test('setUrl while Idle persists but does not auto-start', () async {
      final fake = FakeRadioPlayer();
      final pref = InMemoryBitratePreference();
      final c = _makeController(fake, pref: pref);
      await c.load();
      await c.setUrl('http://50.118.246.51:80/64mp3');
      expect(c.state.value, isA<IdleState>());
      expect(await pref.read(), 'http://50.118.246.51:80/64mp3');
      expect(fake.setUrlCalls, isEmpty);
      await c.dispose();
    });

    test('Error + play retries with reset counter', () async {
      final fake = FakeRadioPlayer();
      final c = _makeController(fake, maxAttempts: 3);
      await c.load();
      await c.play();
      for (var i = 0; i < 3; i++) {
        fake.signalError();
        await Future.delayed(Duration.zero);
        await Future.delayed(Duration.zero);
      }
      expect(c.state.value, isA<ErrorState>());
      await c.play();
      expect(c.state.value, isA<LoadingState>());
      await c.dispose();
    });

    test('I4: stop pushes a cleared NotificationItem', () async {
      final fake = FakeRadioPlayer();
      final c = _makeController(fake);
      await c.load();
      await c.play();
      fake.signalReady();
      await Future.delayed(Duration.zero);
      fake.signalMetadata('Artist - Title');
      await Future.delayed(Duration.zero);
      expect(c.currentItem.value?.clearedToFallback, isFalse);
      await c.stop();
      expect(c.currentItem.value?.clearedToFallback, isTrue);
      expect(c.currentItem.value?.title, 'DUBSTEP.FM');
      await c.dispose();
    });

    test('I5: dispose cancels and further calls throw', () async {
      final fake = FakeRadioPlayer();
      final c = _makeController(fake);
      await c.dispose();
      expect(() => c.play(), throwsStateError);
    });
  });
}
