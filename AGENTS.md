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
