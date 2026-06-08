import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:dubstepfm/src/home_screen.dart';
import 'package:dubstepfm/src/playback/bitrate_preference.dart';
import 'package:dubstepfm/src/playback/playback_controller.dart';
import 'package:dubstepfm/src/theme.dart';

import 'playback/fake_radio_player.dart';

Future<PlaybackController> _pumpHome(WidgetTester tester) async {
  final fake = FakeRadioPlayer();
  final controller = PlaybackController(
    player: fake,
    preference: InMemoryBitratePreference(),
    policy: const ReconnectPolicy(maxAttempts: 10, backoff: Duration.zero),
    stationFallback: 'DUBSTEP.FM',
  );
  await controller.load();
  await tester.pumpWidget(MaterialApp(
    theme: buildDubstepTheme(),
    home: HomeScreen(controller: controller),
  ));
  await tester.pumpAndSettle();
  addTearDown(controller.dispose);
  return controller;
}

void main() {
  group('HomeScreen baseline parity', () {
    testWidgets('renders AppBar, status row, logo, PLAY, STOP, gear',
        (tester) async {
      await _pumpHome(tester);

      expect(find.widgetWithText(AppBar, 'DUBSTEP.FM'), findsOneWidget);
      expect(find.byTooltip('Settings'), findsOneWidget);
      expect(find.text('TAP PLAY TO WUB'), findsOneWidget);
      expect(find.byIcon(Icons.stop), findsOneWidget);
      expect(find.byKey(const Key('home_logo')), findsOneWidget);
      expect(find.widgetWithText(TextButton, 'PLAY'), findsOneWidget);
      expect(find.widgetWithText(TextButton, 'STOP'), findsOneWidget);
    });

    testWidgets('tapping PLAY drives the controller into Loading',
        (tester) async {
      final controller = await _pumpHome(tester);
      await tester.tap(find.widgetWithText(TextButton, 'PLAY'));
      await tester.pump();
      expect(find.textContaining('Loading'), findsOneWidget);
      expect(controller.currentUrl.contains('128mp3'), isTrue);
      await controller.stop();
      await tester.pump();
    });

    testWidgets('PLAY button is disabled while loading or playing',
        (tester) async {
      final controller = await _pumpHome(tester);
      await tester.tap(find.widgetWithText(TextButton, 'PLAY'));
      await tester.pump();
      final playButton = tester
          .widget<TextButton>(find.widgetWithText(TextButton, 'PLAY'));
      expect(playButton.onPressed, isNull);
      // STOP, by contrast, must be enabled.
      final stopButton = tester
          .widget<TextButton>(find.widgetWithText(TextButton, 'STOP'));
      expect(stopButton.onPressed, isNotNull);
      await controller.stop();
      await tester.pump();
    });
  });

  group('Bitrate dialog', () {
    testWidgets('gear opens dialog with four bitrate options', (tester) async {
      await _pumpHome(tester);

      await tester.tap(find.byTooltip('Settings'));
      await tester.pumpAndSettle();

      expect(find.text('Choose bitrate'), findsOneWidget);
      expect(find.text('24 kbps'), findsOneWidget);
      expect(find.text('64 kbps'), findsOneWidget);
      expect(find.text('128 kbps'), findsOneWidget);
      expect(find.text('256 kbps'), findsOneWidget);
    });

    testWidgets('tapping an option dismisses and persists the selection',
        (tester) async {
      final controller = await _pumpHome(tester);

      await tester.tap(find.byTooltip('Settings'));
      await tester.pumpAndSettle();
      await tester.tap(find.text('256 kbps'));
      await tester.pumpAndSettle();

      expect(find.text('Choose bitrate'), findsNothing);
      expect(controller.currentUrl.contains('256mp3'), isTrue);
    });
  });
}
