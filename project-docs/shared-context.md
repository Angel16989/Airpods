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

```bash
./gradlew build
```

```bash
./gradlew installDebug
```

```bash
./gradlew test connectedAndroidTest
```

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

- Which AirPods models should be supported first in the MVP?
- What fallback should the popup use if Android overlay permissions are unavailable or denied?
- Which non-core features should be considered after the MVP is stable?
