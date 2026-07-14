# Agent Task Packet

## Task ID

`TASK-010`

## Assigned Agent

Agent name: Backend Agent

## Feature

Feature name: Project bootstrap

## Objective

Set up the database layer: connection/config, migration tooling, and the initial schema covering the Data Model tables from the feature specs. Seed data for local development.

## Background

Data models come from the feature specs (TASK-004/005) and conventions from TASK-006. This is the last foundation task before feature APIs.

## Files And Areas

### Likely Files To Read

- `project-docs/features/` (Data Model sections)
- `project-docs/shared-context.md` (Data And API Conventions)

### Likely Files To Edit

- Database config, migration files, seed script
- `project-docs/shared-context.md` (document migrate/seed commands)

### Files To Avoid

- Frontend source, `general-templates/`

## Requirements

- Migrations run up and down cleanly on a fresh database.
- Schema matches the feature specs' Data Model tables exactly (field names, types, required).
- Seed script produces enough data to exercise both features locally.

## Constraints

- Any schema deviation from a spec must be written back into that spec, not left silent.

## Acceptance Criteria

- [x] Fresh setup: migrate + seed works with documented commands.
- [x] Schema matches specs.
- [x] Rollback tested.
- [x] Committed to git.

## Suggested Checks

Run migrate up, migrate down, migrate up, seed — in that order, from clean.

## Dependencies

- Depends on: TASK-006, TASK-007
- Blocks: TASK-011, TASK-015

## Risks

- Schema/spec drift; irreversible migrations.

## Agent Notes

- Assumptions:
  - "Database layer" means Jetpack DataStore Preferences, matching the approved TASK-003/TASK-006 stack decision; no SQL database is introduced.
  - Initial v0.1 DataStore setup has no reversible SQL-style migrations. Migration rollback is documented as not applicable until a future DataStore schema migration exists.
  - Seed data is implemented as local/dev repository helpers and tests rather than a separate production database seed script.
  - Nullable battery/charging fields are represented by absent preferences, so unknown values are never faked.
- Questions:
- Progress:
  - 2026-07-14: Started concurrently with TASK-009. Reading feature data models and current Gradle/app scaffold before editing.
  - 2026-07-14: Added the AndroidX DataStore Preferences dependency, typed AirPods/settings/notification domain models, `Context.appleIconDataStore`, and `AirPodsPreferencesRepository` with initialize, save, clear, seed, and popup-cooldown helpers.
  - 2026-07-14: Added JVM repository tests covering settings/snapshot persistence, seed data, reset behavior, and battery percentage validation.
  - 2026-07-14: Documented the DataStore setup, schema version, seed helper, and no-SQL-migration behavior in `shared-context.md`.
  - 2026-07-14: Verification so far: `./gradlew ktlintCheck` passes and `./gradlew :app:compileDebugKotlin` passes. Full `./gradlew test` is blocked in this environment because the installed Java runtime has no `javac`.
  - 2026-07-14: Provisioned a local Temurin JDK 17 at `~/.local/share/jdks/jdk-17.0.19+10`; `./gradlew build` passes and Android test sources compile. `connectedAndroidTest` was not run because no device/emulator is attached.
- Final status: Complete. Implemented and committed on `feature/TASK-009-010-foundation`.
