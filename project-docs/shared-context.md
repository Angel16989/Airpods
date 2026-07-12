# Shared Context

## Project Summary

- Project name: Apple Icon
- Product type: Native Android utility app for Apple AirPods users.
- Primary users: Android users who use Apple AirPods and want an iOS-style connection and battery experience.
- Main goal: Ship a focused v0.1 MVP that detects connected AirPods via BLE and shows a polished iOS-style popup with an animated AirPod visual and battery percentage, deferring additional features until after the MVP is stable.

## Current Objective

Spec and build the core AirPods detection and iOS-style battery popup MVP for Android.

## Tech Stack

- Frontend: Kotlin + Jetpack Compose (Android), Material 3 theming
- Backend: None — fully on-device for v0.1, no server component
- Database: Jetpack DataStore (Preferences) for small key-value state
- Styling: Material Design 3 (Compose Material3)
- Testing: JUnit + Compose UI Testing + Espresso
- Deployment: Gradle build → Google Play Store (internal testing track, then production)

See `decision-log.md` (2026-07-12, TASK-003) for options considered and reasoning.

## Commands

Prerequisites: a JDK 17+ available via `JAVA_HOME` (or on `PATH`), and an Android SDK
referenced by a local, gitignored `local.properties` (`sdk.dir=...`) or `ANDROID_HOME`.
`installDebug` and the `connectedAndroidTest` half of the test command need a connected
device or running emulator.

```bash
./gradlew build
```

```bash
./gradlew installDebug
```

```bash
./gradlew test connectedAndroidTest
```

Verified 2026-07-12 (TASK-007): `./gradlew build` (includes `test` + `lint`) passes from a
fresh clone. `installDebug`/`connectedAndroidTest` could not be exercised in the scaffold
sandbox — no device/emulator was attached.

## Data And API Conventions

- Runtime model: v0.1 has no remote backend and no public HTTP API. "API" means local Kotlin contracts between the monitor, DataStore repository, notification controller, popup controller, and Compose UI.
- Naming: Kotlin code uses `camelCase`; serialized examples and DataStore key names use `snake_case`; enum values serialize as lowercase `snake_case`.
- IDs: AirPods snapshots use `device_id` values in the format `airpods_<12 lowercase hex chars>`, derived from a salted hash of available Bluetooth identity data. Raw Bluetooth MAC addresses, serial numbers, and hardware identifiers must not be displayed, logged, or persisted in raw form.
- Local numeric IDs: Android notification IDs are stable non-negative integers. The persistent status notification uses one stable ID per active `device_id`, so updates replace the existing notification.
- Dates and times: persisted and serialized timestamps use ISO-8601/RFC-3339 date-time strings with an explicit timezone offset, for example `2026-07-12T10:30:00+10:00`. UI may display localized relative time, but stored values must keep the explicit offset.
- Battery values: battery percentages are nullable integers from 0 through 100. `null` means unknown and must display as "Unknown"; code must not infer or fake missing battery values.
- Success payloads: local snapshot streams return the domain model directly, matching the feature specs. Successful responses are not wrapped in an HTTP-style envelope.
- Error response format: recoverable local failures use a typed error envelope with `ok`, `error.code`, `error.message`, `error.recoverable`, `error.user_action`, optional `error.details`, and `occurred_at`.

```json
{
  "ok": false,
  "error": {
    "code": "BLUETOOTH_PERMISSION_DENIED",
    "message": "Bluetooth permission is required before AirPods monitoring can start.",
    "recoverable": true,
    "user_action": "open_bluetooth_permission_settings",
    "details": {
      "permission": "BLUETOOTH_SCAN"
    }
  },
  "occurred_at": "2026-07-12T10:32:00+10:00"
}
```

- Error codes: use uppercase `SCREAMING_SNAKE_CASE`; user-facing copy can be friendlier, but logs and tests should assert on the stable code.
- Pagination: current v0.1 contracts are single-item state streams or settings reads and do not paginate. If a future local collection is added, use cursor pagination with `page_size` default `50`, maximum `100`, newest-first ordering by ISO timestamp, and `next_cursor: null` when complete.
- Authentication and authorization: v0.1 has no accounts, passwords, sessions, API keys, bearer tokens, or cloud auth. Access is authorized only by the local Android user through runtime permissions, overlay permission, notification permission, Bluetooth availability, and in-app settings such as `monitoring_enabled`, `overlay_enabled`, and `notification_enabled`.
- Persistence: DataStore stores only minimal local state required by the specs: settings, last detected display label, last known battery snapshot, popup cooldown metadata, and notification state. No remote sync is allowed for v0.1.
- Diagnostics: no external analytics or network diagnostics are enabled in v0.1. Any debug events stay on-device unless a later decision explicitly adds opt-in diagnostics.

## Agent Rules

- Read this file before starting work.
- Read `project-docs/branching-strategy.md` before creating or merging branches.
- Read the assigned task packet before editing.
- Keep changes scoped to the assigned task.
- Update the task packet with assumptions, progress, and final status.
- Add a handoff note if another agent needs to continue.
- Record major decisions in `decision-log.md`.

## Branching Strategy

- `main`: Production branch. Only release merges and urgent hotfixes should land here.
- `develop`: Integration branch for daily team development before release.
- `feature/*`: New features or tasks. Branch from `develop`, then merge back into `develop`.
- `bugfix/*`: Routine fixes found during development or testing. Branch from `develop`, then merge back into `develop`.
- `hotfix/*`: Urgent production fixes. Branch from `main`, then merge back into both `main` and `develop`.
- `release/*`: Release preparation. Branch from `develop`; only bug fixes and documentation changes happen here. Merge into `main` and back into `develop`.

## Known Constraints

- TASK-001 is a starter setup task, not a final product feature.
- v0.1 should stay focused on the AirPods detection and battery popup MVP; additional features can be added later.
- Apple and AirPods references should be descriptive only; the app must not imply official Apple affiliation unless rights are confirmed.
- The app is fully on-device for v0.1 and should not require a backend service.

## Open Questions

- Which exact AirPods models will be physically tested before v0.1 release?
- What final app-store wording should describe Apple/AirPods compatibility without implying official affiliation?
- Which non-core features should be considered after the MVP is stable?
