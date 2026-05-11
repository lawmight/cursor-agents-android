# Cursor Agents Android

> First native Android client for Cursor's Cloud Agents API.

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-4285F4)
![minSdk](https://img.shields.io/badge/minSdk-26-success)

Control Cursor's Cloud Agents on the go: launch agents on any repo, monitor status, send follow-ups, attach images, all from your phone. Free, open source, MIT.

## Status

🚙 v0.1 scaffolding. Tracking on [Linear](https://linear.app/cursor-android-app/project/cursor-agents-android-99ff64c53382).

## Stack

- Kotlin 2.0
- Jetpack Compose + Material 3
- Ktor Client (CIO) + Kotlinx Serialization
- Hilt for DI
- DataStore Preferences + EncryptedSharedPreferences for the Cursor API key
- Coil 3 for images

## Build

```bash
git clone https://github.com/lawmight/cursor-agents-android.git
cd cursor-agents-android
./gradlew :app:assembleDebug
```

Open in Android Studio (Hedgehog or newer) → Run.

You need a Cursor API key from [cursor.com/dashboard?tab=integrations](https://cursor.com/dashboard?tab=integrations). The app validates the key via `GET /v0/me` on first launch.

## Roadmap

See the [Linear project](https://linear.app/cursor-android-app/project/cursor-agents-android-99ff64c53382) for the full backlog.

**v1**
- API key onboarding + validation
- Agent list with status badges + pull to refresh
- Launch agent (repo picker, branch, prompt, image attach)
- Agent detail: conversation + follow-up + stop + delete
- Settings: multi-key, theme, default model
- Voice input on the prompt
- Saved prompt templates
- Android share intent (send text from any app)

**v1.1+**
- FCM push when agents finish
- Home-screen widget
- Tablet / foldable layouts
- Tasker integration

## License

[MIT](LICENSE)
