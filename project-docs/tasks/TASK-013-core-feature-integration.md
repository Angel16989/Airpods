# Agent Task Packet

## Task ID

`TASK-013`

## Assigned Agent

Agent name: Frontend Agent

## Feature

Feature name: Core Feature

## Objective

Harden the core feature end to end: edge cases from the spec (validation feedback, permission-denied handling, network failure/retry), plus any Analytics events the spec defines.

## Background

TASK-012 delivered the happy path and basic states; this task closes the gap to the spec's full Error Handling and Analytics tables before QA.

## Files And Areas

### Likely Files To Read

- `project-docs/features/<core-feature>.md` (Error Handling, Analytics, Validation)
- `project-docs/handoffs/` (any open notes)

### Likely Files To Edit

- Frontend source and tests; backend only if an error response is missing (coordinate via handoff)

### Files To Avoid

- `general-templates/`

## Requirements

- Every row of the spec's Error Handling table produces the specified user message and recovery path.
- Invalid input shows inline validation feedback per spec.
- Analytics events fire per the spec's Analytics table (or log locally if no provider yet — record that in the spec).

## Constraints

- No new features; hardening only.

## Acceptance Criteria

- [ ] Each spec error case demonstrated in the app.
- [ ] Tests added for edge cases.
- [ ] Test and lint pass.
- [ ] Handoff note written for QA Agent.
- [ ] Committed to git.

## Suggested Checks

Simulate API failure (stop the backend) and confirm the UI degrades per spec.

## Dependencies

- Depends on: TASK-012
- Blocks: TASK-014

## Risks

- Silent catch-all error handling that hides real failures.

## Agent Notes

- Assumptions:
  - The task packet's "network failure/retry" item maps to the core spec's local scan retry/throttle recovery because v0.1 has no backend or network dependency.
  - "Invalid input" means Bluetooth payload validation for this feature; there are no free-form user inputs in the core dashboard.
  - Local analytics are acceptable as on-device debug events per the feature spec; no external provider was added.
- Questions:
  - None.
- Progress:
  - Continued existing TASK-013 branch work on `feature/task-013-core-feature-hardening`.
  - Added/verified local debug events for `airpods_detected`, `battery_popup_shown`, `popup_fallback_used`, and `permission_blocked`.
  - Added/verified mapper and Compose UI coverage for all core spec error messages and recovery actions.
  - Recorded the local debug-event implementation in `project-docs/features/airpods-detection-popup.md`.
  - Wrote QA handoff `project-docs/handoffs/frontend-to-qa-TASK-013-core-feature-hardening.md`.
  - Verification: plain `./gradlew test ktlintCheck` failed because `/usr/lib/jvm/java-25-openjdk` lacks the required compiler capability; rerunning with `JAVA_HOME=$HOME/.local/share/jdks/jdk-17.0.19+10` passed.
- Final status:
  - Complete. TASK-013 acceptance criteria are met except `connectedAndroidTest`, which was not run here and is called out for QA because it requires a connected device/emulator.
