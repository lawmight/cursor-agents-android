# Cursor Agents Android

The first native Android client for Cursor's Cloud Agents.

[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
![Kotlin 2.0.20](https://img.shields.io/badge/Kotlin-2.0.20-7F52FF.svg)
![Min SDK 26](https://img.shields.io/badge/Min%20SDK-26-3DDC84.svg)
![Compose Material 3](https://img.shields.io/badge/Compose-Material%203-4285F4.svg)
![Hilt](https://img.shields.io/badge/Hilt-2.52-34A853.svg)
![Ktor](https://img.shields.io/badge/Ktor-2.3.12-087CFA.svg)

![Cursor Agents Android hero](docs/screenshots/hero.png)

## What is this?

Cursor Agents Android is a phone-first control surface for Cursor cloud agents.
Paste your Cursor API key, validate it on device, and launch agents against your repositories from anywhere.
Use it to monitor agent status, read conversations, send follow-ups, stop runs, and clean up agents without opening a laptop.
It is built for developers who want their cloud agents in reach while they are away from their desk.

## Screenshots

| Onboarding | Agents list | Launch | Detail |
| --- | --- | --- | --- |
| ![Onboarding screenshot placeholder](docs/screenshots/onboarding.png) | ![Agents list screenshot placeholder](docs/screenshots/agents-list.png) | ![Launch screenshot placeholder](docs/screenshots/launch.png) | ![Detail screenshot placeholder](docs/screenshots/detail.png) |

## Features

The current app is still moving toward v1. Items below link to the tracked Linear issues and are marked with their current status.

- [API key onboarding + key validation](https://linear.app/cursor-android-app/issue/CUR-14) - shipped.
- [Agent list with status badges + pull-to-refresh](https://linear.app/cursor-android-app/issue/CUR-15) - planned.
- [Launch screen with repo picker + image attach](https://linear.app/cursor-android-app/issue/CUR-16) - planned.
- [Detail screen with conversation viewer + follow-up + stop + delete](https://linear.app/cursor-android-app/issue/CUR-17) - planned.
- [Multi-key settings](https://linear.app/cursor-android-app/issue/CUR-18) - planned.
- [Voice input on prompt](https://linear.app/cursor-android-app/issue/CUR-19) - planned.
- [Saved prompt templates](https://linear.app/cursor-android-app/issue/CUR-20) - planned.
- [Android share intent](https://linear.app/cursor-android-app/issue/CUR-22) - planned.

## Install

### From source

```bash
git clone https://github.com/lawmight/cursor-agents-android.git
cd cursor-agents-android
./gradlew :app:assembleDebug
./gradlew testDebugUnitTest
./gradlew ktlintCheck detekt
```

Open in Android Studio and run the debug app on an emulator or device.

### From GitHub Releases

Signed APK on the Releases page once v1.0 ships. F-Droid listing tracked in [CUR-25](https://linear.app/cursor-android-app/issue/CUR-25).

### From Play Store

Coming after [CUR-21](https://linear.app/cursor-android-app/issue/CUR-21).

## First run

1. Grab a Cursor API key from [cursor.com/dashboard](https://cursor.com/dashboard).
2. Open Cursor Agents Android.
3. Paste the key into onboarding.
4. Validate it against Cursor's `/v0/me` endpoint.
5. You are in.

## Architecture

- Native Android app written in Kotlin 2.0.
- Jetpack Compose + Material 3 for the UI.
- `CursorAgentsApp` is the `@HiltAndroidApp` entry point.
- `MainActivity` hosts the Compose navigation graph.
- Ktor Client (CIO) + kotlinx.serialization talks to Cursor's Cloud Agents API.
- Hilt handles dependency injection.
- Repositories expose Flow-backed state as the single source of truth.
- DataStore stores plain preferences and EncryptedSharedPreferences stores the Cursor API key.

See [AGENTS.md](AGENTS.md) for the full guide for AI coding agents working on this repo.

## Roadmap

See the [Linear project](https://linear.app/cursor-android-app/project/cursor-agents-android-99ff64c53382) for the full backlog.

### v1

- [API key onboarding + validation](https://linear.app/cursor-android-app/issue/CUR-14)
- [Agent list with status badges + pull-to-refresh](https://linear.app/cursor-android-app/issue/CUR-15)
- [Launch agent with repo picker, branch, prompt, and image attach](https://linear.app/cursor-android-app/issue/CUR-16)
- [Agent detail with conversation, follow-up, stop, and delete](https://linear.app/cursor-android-app/issue/CUR-17)
- [Settings for multi-key, theme, and default model](https://linear.app/cursor-android-app/issue/CUR-18)
- [Voice input on the prompt](https://linear.app/cursor-android-app/issue/CUR-19)
- [Saved prompt templates](https://linear.app/cursor-android-app/issue/CUR-20)
- [Android share intent for sending text from any app](https://linear.app/cursor-android-app/issue/CUR-22)

### v1.1+

- [FCM push when agents finish](https://linear.app/cursor-android-app/issue/CUR-23)
- [Home-screen widget](https://linear.app/cursor-android-app/issue/CUR-24)
- [F-Droid listing](https://linear.app/cursor-android-app/issue/CUR-25)
- [Tablet / foldable layouts](https://linear.app/cursor-android-app/issue/CUR-27)
- [Tasker integration](https://linear.app/cursor-android-app/issue/CUR-28)

## Contributing

PRs welcome. See [CONTRIBUTING.md](CONTRIBUTING.md). Reference the Linear issue (CUR-N) in PR titles.

## License

MIT. See [LICENSE](LICENSE).
