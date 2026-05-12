# AGENTS.md

> Context for AI coding agents working on this repo.

## What this is

A native Android client for Cursor's Cloud Agents API. Kotlin + Jetpack Compose + Material 3.

## Layout

```
app/
  src/main/kotlin/fr/lawmight/cursoragents/
    CursorAgentsApp.kt        # @HiltAndroidApp entry
    MainActivity.kt           # ComponentActivity hosting Compose
    data/
      api/                    # CursorApiClient + models (Ktor + kotlinx.serialization)
      auth/                   # EncryptedKeyStore (EncryptedSharedPreferences)
      repository/             # AgentsRepository (single source of truth, Flow-based)
    di/                       # Hilt modules
    ui/
      theme/                  # design tokens (colors, type, shapes, spacing, status)
      components/             # reusable composables (StatusBadge, etc.)
      nav/                    # AppNavHost + Routes
      onboarding/             # API key paste + validation
      agents/                 # list screen
      launch/                 # launch agent
      detail/                 # detail + conversation + follow-up
      settings/               # multi-key, theme, default model
```

## Conventions

- Kotlin 2.0, Compose Compiler plugin
- Hilt for DI (no manual ServiceLocator)
- Coroutines + Flow; no RxJava
- Ktor Client (CIO) for HTTP, never OkHttp directly
- DataStore for plain preferences; **EncryptedSharedPreferences only for the Cursor API key**
- One ViewModel per screen, exposes `state: StateFlow<UiState>` + `event(UiEvent)`
- 4dp spacing scale (xxs=4, xs=8, s=12, m=16, l=24, xl=32) — use `LocalSpacing`, no magic dp values

## Cursor API

- Base: `https://api.cursor.com`
- Auth: `Authorization: Bearer <CURSOR_API_KEY>`
- Endpoints: `/v0/me`, `/v0/agents` (list/launch), `/v0/agents/{id}` (get/delete), `/v0/agents/{id}/conversation`, `/v0/agents/{id}/followup`, `/v0/agents/{id}/stop`, `/v0/models`, `/v0/repositories`
- Statuses: CREATING, RUNNING, FINISHED, STOPPED, FAILED
- Max 256 active agents per key
- Errors: 401 invalid key, 403 forbidden, 404 not found, 429 rate-limited

## Run

```bash
./gradlew :app:assembleDebug
./gradlew testDebugUnitTest
./gradlew ktlintCheck detekt
```

## Pull request expectations

- Reference the Linear issue (CUR-N) in the PR title/body
- Keep PRs scoped to a single issue when possible
- Update screen state + ViewModel + repository in the same PR if they touch the same feature

## Cursor Cloud specific instructions

### Environment

- JDK 17 is at `/usr/lib/jvm/java-17-openjdk-amd64` — set `JAVA_HOME` to this path.
- Android SDK is at `/opt/android-sdk` — set both `ANDROID_HOME` and `ANDROID_SDK_ROOT`.
- These env vars are already in `~/.bashrc` and sourced automatically in new shells.
- The Gradle wrapper (`gradlew`) was generated during setup (Gradle 8.7). If it's missing after a fresh clone, regenerate it with `gradle wrapper --gradle-version 8.7` (requires `/opt/gradle-8.7/bin` in PATH).

### Build & Test (see "Run" section above for commands)

- `./gradlew ktlintCheck` — passes cleanly (ktlint has `ignoreFailures = true`).
- `./gradlew detekt` — reports MagicNumber warnings on Color.kt hex values; this is a pre-existing issue, not a blocker.
- `./gradlew testDebugUnitTest` — runs the smoke test (and any future unit tests).
- `./gradlew :app:assembleDebug` — produces APK at `app/build/outputs/apk/debug/app-debug.apk`.

### Gotchas

- No Android emulator is available in Cloud Agent VMs. Testing is limited to compilation, unit tests, and lint. GUI/instrumented testing requires a physical device or external emulator.
- The Cursor API key (`CURSOR_API_KEY` secret) is needed only at app runtime on-device, not for building or unit testing.
- Gradle configuration cache is enabled; if you change build scripts in non-trivial ways, add `--no-configuration-cache` to the first run after the change.
