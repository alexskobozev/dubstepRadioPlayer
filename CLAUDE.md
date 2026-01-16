# Project: Dubstep FM Radio Player

Android app for streaming Dubstep FM radio with playback controls and bitrate selection.

## Tech Stack

- **Language**: Kotlin
- **Min SDK**: 21, Target SDK: 35
- **Build**: Gradle 8.7.1 with Kotlin DSL, version catalog (`gradle/libs.versions.toml`)
- **Java**: 17

## Key Dependencies

- **Media**: ExoPlayer 2.13.3, AndroidX Media 1.6.0
- **Firebase**: Crashlytics, Analytics (requires `google-services.json`)
- **UI**: Material Dialogs 3.3.0, ConstraintLayout
- **Debug**: LeakCanary, Timber

## Project Structure

```
app/src/main/java/com/wishnewjam/dubstepfm/
├── MainActivity.kt          # Main UI with play/stop controls
├── MainService.kt           # MediaBrowserService for background playback
├── MediaPlayerInstance.kt   # ExoPlayer wrapper
├── MediaViewModel.kt        # ViewModel for media state
├── ChooseBitrateDialogFragment.kt  # Bitrate selection dialog
├── CurrentUrl.kt            # Current stream URL holder
├── Links.kt                 # Stream URLs
├── UIStates.kt              # UI state definitions
└── Tools.kt                 # Utility functions
```

## Commands

```bash
# Run unit tests
./gradlew app:testDebugUnitTest

# Run instrumented tests (requires emulator)
./gradlew app:connectedDebugAndroidTest

# Build debug APK
./gradlew app:assembleDebug

# Download dependencies (cache warmup)
./gradlew app:dependencies
```

## Tests

- **Unit tests**: `app/src/test/` - Uses JUnit, MockK, Robolectric
- **Instrumented tests**: `app/src/androidTest/` - Uses Espresso, AndroidX Test

## CI/CD (GitHub Actions)

Located in `.github/workflows/`:

- `tests.yml` - Runs on push/PR to master/main/develop:
  - Unit tests job
  - Instrumented tests job (with Android emulator)
  - Build APK job
- `cache-warmup.yml` - Manual workflow to pre-warm Gradle cache

## Secrets

- `GOOGLE_SERVICES_JSON`: Base64-encoded `google-services.json` file (required for Firebase)
  - Decoded in CI with: `echo "${{ secrets.GOOGLE_SERVICES_JSON }}" | base64 --decode > app/google-services.json`

## Notes

- `google-services.json` is gitignored - must be provided via secret in CI or locally
- Uses MediaBrowserCompat/MediaSessionCompat pattern for media playback
- Supports multiple bitrate streams