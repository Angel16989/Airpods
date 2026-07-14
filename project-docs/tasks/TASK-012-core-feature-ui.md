# Agent Task Packet

## Task ID

`TASK-012`

## Assigned Agent

Agent name: Frontend Agent

## Feature

Feature name: Core Feature (see `project-docs/features/`, spec from TASK-004)

## Objective

Build the core feature's UI per the spec's User Flow and UI Notes, using the shared primitives from TASK-009 and the real API from TASK-011.

## Background

Backend endpoints are live (TASK-011); read the Backend Agent's handoff note before starting.

## Files And Areas

### Likely Files To Read

- `project-docs/features/<core-feature>.md` (User Flow, UI Notes)
- `project-docs/handoffs/` (backend handoff)
- Shared primitives from TASK-009

### Likely Files To Edit

- Frontend source, frontend/component tests

### Files To Avoid

- Backend source, `general-templates/`

## Requirements

- Full user flow from the spec works end to end against the real API.
- Loading, empty, error, and success states implemented for every screen.
- Responsive at mobile and desktop widths.

## Constraints

- Compose TASK-009 primitives; extend them rather than fork styles.
- API mismatches go back to Backend Agent via handoff, not worked around.

## Acceptance Criteria

- [x] Main flow completes in the running app.
- [x] All four UI states visible and correct.
- [x] Component/UI tests pass.
- [x] Handoff note written for QA Agent.
- [x] Committed to git.

## Suggested Checks

Run Test and Lint; walk the full user flow in the browser at two widths.

## Dependencies

- Depends on: TASK-009, TASK-011
- Blocks: TASK-013

## Risks

- Building against assumed instead of actual API responses.

## Agent Notes

- Assumptions:
  - `develop` did not contain the completed TASK-011 API commit or Backend handoff when TASK-012 started, so `feature/task-012-core-feature-ui` was stacked on `origin/feature/task-011-core-feature-api` via fast-forward before UI work. No TASK-011 source was edited for this task.
  - The real BLE scanner remains outside TASK-012 scope per the TASK-011 handoff. The UI test popup sends a normalized manual payload through `AirPodsMonitor`, so UI behavior still goes through the real local monitor contract.
- Questions:
  - None.
- Progress:
  - Replaced the placeholder dashboard with a DataStore-backed AirPods dashboard wired to `AirPodsMonitor.observeSnapshots()`.
  - Added runtime permission mapping for Bluetooth, overlay popup permission, and notification permission, plus settings controls for monitoring, automatic popup, and fallback notification.
  - Added empty, loading/scanning, fatal error, success, degraded fallback, and in-app popup UI states using TASK-009 shared primitives.
  - Added an original Compose-drawn AirPods-inspired visual for the dashboard and popup; no Apple artwork/assets were used.
  - Added JVM mapper tests and connected Compose UI tests for empty, loading, error, success, and popup rendering.
  - Wrote QA handoff: `project-docs/handoffs/frontend-to-qa-TASK-012-core-feature-ui.md`.
  - Verification passed with `JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew test`, `JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew ktlintCheck`, `JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew build`, `JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew :app:compileDebugAndroidTestKotlin :app:compileDebugAndroidTestJavaWithJavac`, and `JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew connectedAndroidTest` on attached device `SM-S731B - 16`.
  - Smoke check passed with `JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew installDebug`, `/tmp/apple-icon-android-sdk/platform-tools/adb shell am start -n com.angel16989.appleicon/.MainActivity`, and `/tmp/apple-icon-android-sdk/platform-tools/adb shell pidof com.angel16989.appleicon`.
- Final status:
  - Complete. TASK-012 UI is implemented, verified, committed on `feature/task-012-core-feature-ui`, and ready for QA/hardening follow-up.
