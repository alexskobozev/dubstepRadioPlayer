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
import 'src/analytics/analytics_logger.dart';
import 'src/analytics/firebase_analytics_logger.dart';
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
        _recordError(error, stack);
        return true;
      };
    }

    await _bootstrap();
  }, (error, stack) {
    if (!kIsWeb) {
      _recordError(error, stack);
    }
  });
}

/// Record an uncaught zone/platform error to Crashlytics, downgrading
/// transient network failures to non-fatal.
///
/// just_audio runs a local proxy HTTP server for the stream; when the device
/// has no connectivity it throws `SocketException: Connection failed (Network
/// is unreachable)` from inside that proxy, on an async path the player's
/// `errorStream` never sees. It surfaces here as an uncaught error. The
/// reconnect policy already handles the user-facing recovery, so flagging it
/// `fatal: true` only pollutes crash-free-users with what is really a
/// "no internet" condition. Report it as a non-fatal instead.
void _recordError(Object error, StackTrace stack) {
  FirebaseCrashlytics.instance
      .recordError(error, stack, fatal: !_isTransientNetworkError(error));
}

bool _isTransientNetworkError(Object error) {
  if (error is SocketException || error is HttpException) return true;
  final message = error.toString();
  return message.contains('SocketException') ||
      message.contains('Network is unreachable') ||
      message.contains('Connection failed') ||
      message.contains('Connection refused') ||
      message.contains('Connection reset') ||
      message.contains('Connection closed');
}

Future<void> _bootstrap() async {
  final AnalyticsLogger analytics =
      kIsWeb ? const NoOpAnalyticsLogger() : FirebaseAnalyticsLogger();
  final controller = PlaybackController(
    player: JustAudioRadioPlayer(),
    preference: SharedPreferencesBitratePreference(),
    stationFallback: kStrings['station_name']!,
    analytics: analytics,
  );
  await controller.load();

  // audio_service / audio_session have no web implementation; awaiting their
  // platform channels on web hangs main() and leaves the engine unpainted.
  if (!kIsWeb) {
    // audio_service's Android side fetches artUri via flutter_cache_manager,
    // which only understands http(s)/file/content URIs — not asset://. Stage
    // the bundled logo into the cache dir and hand back a file:// URI.
    final artUri = await _stageNotificationArt();
    // audio_service's Android configure() reads the launching Activity's
    // intent; on some devices/launch paths that intent is null and it throws
    // `PlatformException(... Intent.mAction on a null object reference ...)`,
    // which otherwise aborts the whole launch. Degrade gracefully: without the
    // media session we lose the system notification / lock-screen controls,
    // but in-app PLAY/STOP still works. Report the failure as non-fatal.
    try {
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
    } catch (error, stack) {
      FirebaseCrashlytics.instance.recordError(error, stack, fatal: false);
    }

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
