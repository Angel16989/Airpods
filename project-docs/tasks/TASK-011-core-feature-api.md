# Agent Task Packet

## Task ID

`TASK-011`

## Assigned Agent

Agent name: Backend Agent

## Feature

Feature name: Core Feature (see `project-docs/features/`, spec from TASK-004)

## Objective

Implement the core feature's backend exactly per its spec: endpoints from the API Contract, validation rules, permissions, and error handling. Unit + integration tests for each endpoint.

## Background

The spec is the contract — the Frontend Agent builds against it in TASK-012 without reading your code.

## Files And Areas

### Likely Files To Read

- `project-docs/features/<core-feature>.md` (API Contract, Data Model, Validation, Permissions, Error Handling)
- `project-docs/shared-context.md` (conventions)

### Likely Files To Edit

- Backend source, backend tests

### Files To Avoid

- Frontend source, migrations from TASK-010 (add new ones if schema must change), `general-templates/`

## Requirements

- Every endpoint in the spec implemented with documented request/response shapes.
- Every validation rule and permission enforced, with tests proving it.
- Every error case from the spec's Error Handling table returns the conventional error format.

## Constraints

- If the spec is ambiguous or wrong, update the spec and note it in Agent Notes — do not silently deviate.

## Acceptance Criteria

- [ ] All spec endpoints work against seeded data.
- [ ] Tests cover happy path + each validation/permission/error case.
- [ ] Test and lint commands pass.
- [ ] Handoff note written for Frontend Agent.
- [ ] Committed to git.

## Suggested Checks

Run the Test and Lint commands; exercise each endpoint manually against seed data.

## Dependencies

- Depends on: TASK-008, TASK-010
- Blocks: TASK-012, TASK-015

## Risks

- Contract drift from the spec breaks TASK-012 invisibly.

## Agent Notes

- Assumptions:
  - "Core feature API" means the local Kotlin contract described in `shared-context.md`, not an HTTP/server endpoint.
  - Real BLE scanner integration can be injected later through `AirPodsSignalSource`; TASK-011 implements the stable local contract, parser, permission gates, persistence, and tests without requiring physical AirPods hardware.
  - The spec was ambiguous about how `observeSnapshots()` should expose both success snapshots and conventional error envelopes, so `airpods-detection-popup.md` now documents `AirPodsMonitorResult.Snapshot` and `AirPodsMonitorResult.Failure`.
- Questions:
  - None blocking.
- Progress:
  - 2026-07-14: Created branch `feature/task-011-core-feature-api` from current `develop` after confirming there was no existing local or remote TASK-011 branch and the worktree was clean.
  - 2026-07-14: Added local monitor API models (`AirPodsMonitorRequest`, `AirPodsMonitorPermissions`, `AirPodsMonitorResult`, `AirPodsBluetoothPayload`, typed error envelopes) and manifest permission declarations for Bluetooth/BLE, notifications, and overlay popup support.
  - 2026-07-14: Added `AirPodsPayloadParser` with salted `airpods_<12 hex>` IDs, battery validation, unknown/null battery handling, model hint inference, and conventional parse errors without exposing raw Bluetooth identifiers.
  - 2026-07-14: Added `AirPodsMonitor.observeSnapshots()` backed by `AirPodsPreferencesRepository`, including seeded/cached snapshot emission, signal payload persistence, permission/Bluetooth/scan-throttle failures, overlay fallback warnings, stale data marking, battery-unavailable warnings, and popup cooldown suppression.
  - 2026-07-14: Added JVM parser and monitor tests covering happy path, seeded DataStore behavior, validation, permission gates, every spec error case, stale detection, and cooldown.
  - 2026-07-14: Wrote Frontend handoff at `project-docs/handoffs/backend-to-frontend-TASK-011-core-feature-api.md`.
  - 2026-07-14: Verified `ktlintFormat`, `./gradlew test`, `./gradlew ktlintCheck`, `./gradlew build`, and Android test-source compilation pass with JDK 17 at `~/.local/share/jdks/jdk-17.0.19+10`. `connectedAndroidTest` was not run because no device/emulator is available.
- Final status: Complete. Acceptance criteria met; ready for TASK-012 Frontend Agent.
