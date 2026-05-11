# Session log: 2026-05-11 — CUR-14 onboarding shipped

## What happened

Kicked off the Cursor Agents Android v1 build. Linear shows 5 P1 issues open in the CUR project (cursor-agents-android scaffold already in place, Kotlin + Compose + Material 3 + Hilt + Ktor wired up).

Picked CUR-14 (Onboarding screen with API key validation) as the first one to ship since it's the entry point and only depends on CursorApiClient (already stubbed).

## What shipped

Cursor cloud agent landed PR #1: https://github.com/lawmight/cursor-agents-android/pull/1

- Paste field with show/hide toggle
- Validates against Cursor API via `GET /v0/me`
- Inline errors for 401, 403, 429, and network failures
- Custom Tabs deep link to grab a key from cursor.com/dashboard
- Connected state with account email + key name + "Use a different key" reset
- Eager validation of saved keys on launch
- Start destination conditional on saved key presence
- New `CursorApiClientFactory` for per-key client construction
- `@IoDispatcher` qualifier added to the DI graph
- ViewModel tests with Ktor MockEngine + Turbine covering all error paths

Side effects: caught and fixed two scaffold bugs (escaped dollar signs in `EncryptedKeyStore` and `CursorApiException` string interpolation).

## Reviewer notes

- CodeRabbit skipped because the PR was opened as draft. Ping `@coderabbitai review` after marking ready.
- Sourcery generated a reviewer's guide (sequence + class diagrams) but no critique.
- Things to eyeball on review:
  - `MainActivity.kt` uses `remember { keyStore.read() != null }` — flag stays stale until process restart after "use a different key". Minor v1 paper cut.
  - Saved-key validation on init silently falls back to Idle if the saved key fails. UX could explain that better.
  - `detekt.yml` loosened MaxLineLength, UnusedParameter, ForbiddenComment globally. Worth tightening before merge.

## Linear

- CUR-14 → In Progress (will move to Done when PR merges).

## Cron poller

Spun up a 4-min poller (job_id `0a280458d421`) to notify on terminal state. Self-remove failed and it spammed status updates for a few hours overnight. Killed manually. Lesson: hardcode the job_id into the polling prompt, or cap repeat count instead of relying on self-remove.

## What's next

Design batch dispatched in parallel:
- CUR-29 (design tokens spec sheet) → cursor cloud agent
- CUR-26 (README polish + screenshots + roadmap) → cursor cloud agent

CUR-8 (brand), CUR-10 (Figma mockups), CUR-7 (Figma component library) are visual-design tasks that cloud agents can't handle without Figma access — they stay in backlog until handled manually or with a Figma-capable tool.

Next P1 work after design lands:
- CUR-16 agent list screen
- CUR-12 launch agent screen
- CUR-20 agent detail screen
- CUR-19 (API client) — already mostly done by CUR-14's refactor, may close as duplicate.
