# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Flutter application (`dubstepfm`), Dart SDK `^3.12.1`. Currently at the **001-android-baseline-migration** baseline — a static visual prototype that reproduces the legacy `dubstepRadioPlayer` Android app's first-launch screen, launcher identity (`com.wishnewjam.dubstepfm`, label "DUBSTEP.FM"), and assets. No real audio playback. See `specs/001-android-baseline-migration/quickstart.md` for build / run / verify. All platform shells (android/, ios/, macos/, linux/, windows/, web/) are present as generated; only Android is parity-bound.

## Commands

```bash
flutter pub get                    # install dependencies
flutter run                        # run on default device (interactive: r=hot reload, R=hot restart, q=quit)
flutter run -d chrome              # pick a target device explicitly
flutter analyze                    # static analysis / lint (uses analysis_options.yaml)
flutter test                       # run all tests
flutter test test/widget_test.dart # run a single test file
flutter test --name "smoke"        # run tests matching a name pattern
flutter build apk|ios|web|macos    # platform builds
flutter clean                      # wipe build/ and .dart_tool/ when builds get weird
```

## Lint config

`analysis_options.yaml` includes `package:flutter_lints/flutter.yaml`. Custom rules go in the `linter.rules:` block of that file.

## Source layout (after 001-android-baseline-migration)

- `lib/main.dart` — `DubstepFmApp` entry; wires `PlaybackController`, `AudioService.init`, and `AudioSession`.
- `lib/src/home_screen.dart` — home screen; PLAY/STOP bound to `PlaybackController`, status text reflects current state and ICY title.
- `lib/src/bitrate_dialog.dart` — `showBitrateDialog(context, controller:)` switches the live stream.
- `lib/src/theme.dart` — legacy palette + `buildDubstepTheme()`.
- `lib/src/l10n/strings.dart` — `kStrings` map keyed by legacy `strings.xml` names.
- `lib/src/playback/` — playback module:
  - `radio_player.dart` — engine-wrapping interface (`RadioPlayer`, `RadioEngineEvent`, `IcyFrame`).
  - `just_audio_radio_player.dart` — production `RadioPlayer` over `package:just_audio`.
  - `playback_state.dart` — sealed `PlaybackState` (`Idle` / `Loading` / `Playing(title, artist)` / `Error`).
  - `playback_controller.dart` — state machine + reconnect policy (≤10 attempts × 10 s).
  - `endpoint_catalog.dart` — four direct-IP stream URLs (`50.118.246.51`); `shout.dubstep.fm` deliberately absent.
  - `bitrate_preference.dart` — `SharedPreferences`-backed persistence of selected URL.
  - `icy_metadata.dart` — pure-Dart `parseIcyTitle` (artist/title split, fallback).
  - `audio_handler.dart` — `BaseAudioHandler` bridge to `audio_service`.
- `assets/images/img_logo.png` — centre logo from legacy `drawable/img_logo.png`.
- `android/app/src/main/res/values/strings.xml` — native `app_name` for launcher.
- `android/app/src/main/res/mipmap-*/ic_launcher*.png` + `mipmap-anydpi-v26/ic_launcher.xml` + `values/ic_launcher_background.xml` — legacy launcher icon.
- `android/app/src/main/res/xml/network_security_config.xml` — cleartext HTTP allowance scoped to dubstep.fm stream hosts.
- `android/app/src/main/AndroidManifest.xml` — `INTERNET`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK`, `WAKE_LOCK` permissions and `audio_service` `<service>` / `<receiver>` entries; `MainActivity` extends `AudioServiceActivity`.

<!-- SPECKIT START -->
Active feature plan: [specs/002-stream-playback-migration/plan.md](specs/002-stream-playback-migration/plan.md).
For additional context about technologies, project structure, and shell commands, read the current plan and `specs/002-stream-playback-migration/quickstart.md`.
<!-- SPECKIT END -->