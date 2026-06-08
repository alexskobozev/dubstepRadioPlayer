import 'dart:async';
import 'dart:io';

import 'package:audio_service/audio_service.dart';
import 'package:audio_session/audio_session.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_crashlytics/firebase_crashlytics.dart';
import 'package:flutter/foundation.dart' show PlatformDispatcher, kIsWeb;
import 'package:flutter/material.dart';
import 'package:flutter/services.dart' show rootBundle;
import 'package:path_provider/path_provider.dart';

import 'firebase_options.dart';
import 'src/home_screen.dart';
import 'src/l10n/strings.dart';
import 'src/playback/audio_handler.dart';
import 'src/playback/bitrate_preference.dart';
import 'src/playback/just_audio_radio_player.dart';
import 'src/playback/playback_controller.dart';
import 'src/theme.dart';

Future<void> main() async {
  runZonedGuarded<Future<void>>(() async {
    WidgetsFlutterBinding.ensureInitialized();

    // Crashlytics has no web SDK in firebase_crashlytics; skip on web.
    if (!kIsWeb) {
      await Firebase.initializeApp(
        options: DefaultFirebaseOptions.currentPlatform,
      );
      FlutterError.onError =
          FirebaseCrashlytics.instance.recordFlutterFatalError;
      PlatformDispatcher.instance.onError = (error, stack) {
        FirebaseCrashlytics.instance.recordError(error, stack, fatal: true);
        return true;
      };
    }

    await _bootstrap();
  }, (error, stack) {
    if (!kIsWeb) {
      FirebaseCrashlytics.instance.recordError(error, stack, fatal: true);
    }
  });
}

Future<void> _bootstrap() async {
  final controller = PlaybackController(
    player: JustAudioRadioPlayer(),
    preference: SharedPreferencesBitratePreference(),
    stationFallback: kStrings['station_name']!,
  );
  await controller.load();

  // audio_service / audio_session have no web implementation; awaiting their
  // platform channels on web hangs main() and leaves the engine unpainted.
  if (!kIsWeb) {
    // audio_service's Android side fetches artUri via flutter_cache_manager,
    // which only understands http(s)/file/content URIs — not asset://. Stage
    // the bundled logo into the cache dir and hand back a file:// URI.
    final artUri = await _stageNotificationArt();
    await AudioService.init(
      builder: () => DubstepAudioHandler(controller, artUri: artUri),
      config: AudioServiceConfig(
        androidNotificationChannelId: 'fm.dubstep.audio',
        androidNotificationChannelName:
            kStrings['notification_channel_name']!,
        androidNotificationOngoing: true,
        androidStopForegroundOnPause: true,
        androidNotificationIcon: 'mipmap/ic_launcher',
      ),
    );

    final session = await AudioSession.instance;
    await session.configure(const AudioSessionConfiguration.music());
    session.interruptionEventStream.listen((event) {
      if (event.begin) {
        controller.stop();
      }
    });
    session.becomingNoisyEventStream.listen((_) {
      controller.stop();
    });
  }

  runApp(DubstepFmApp(controller: controller));
}

Future<Uri?> _stageNotificationArt() async {
  try {
    final dir = await getApplicationSupportDirectory();
    final file = File('${dir.path}/notification_art.png');
    if (!await file.exists()) {
      final data = await rootBundle.load('assets/images/img_logo.png');
      await file.writeAsBytes(data.buffer.asUint8List(), flush: true);
    }
    return file.uri;
  } catch (_) {
    return null;
  }
}

class DubstepFmApp extends StatelessWidget {
  final PlaybackController controller;
  const DubstepFmApp({super.key, required this.controller});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: kStrings['app_name']!,
      debugShowCheckedModeBanner: false,
      theme: buildDubstepTheme(),
      home: HomeScreen(controller: controller),
    );
  }
}
