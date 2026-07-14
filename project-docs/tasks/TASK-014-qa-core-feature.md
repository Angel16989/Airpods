# Agent Task Packet

## Task ID

`TASK-014`

## Assigned Agent

Agent name: QA Agent

## Feature

Feature name: Core Feature

## Objective

Verify the core feature against its spec: run the full Testing Plan (unit, integration, UI, manual QA sections) and check off the spec's Acceptance Criteria — or file failures back to the implementing agents.

## Background

Frontend and Backend report the feature complete (TASK-011 to TASK-013 handoffs). Nothing is "Shipped" until this pass is green.

## Files And Areas

### Likely Files To Read

- `project-docs/features/<core-feature>.md` (Testing Plan, Acceptance Criteria)
- `project-docs/handoffs/` (implementation handoffs)

### Likely Files To Edit

- Test files (may add missing coverage)
- The feature spec (tick Acceptance Criteria)
- This packet (record results)

### Files To Avoid

- Application source (report bugs, do not patch)

## Requirements

- Run the full automated suite; record pass/fail with output.
- Execute Manual QA: desktop, mobile, keyboard navigation, screen reader basics.
- Every failure filed as a handoff to the owning agent with reproduction steps.

## Constraints

- Never tick a criterion that was not actually exercised.

## Acceptance Criteria

- [x] Automated suite results recorded.
- [x] Manual QA checklist executed and recorded.
- [x] Spec Acceptance Criteria all ticked, or failures handed off.
- [x] Feature status updated in feature-directory.md (Review → Shipped or back to In Progress).

## Suggested Checks

Run the Test command; walk the user flow with keyboard only.

## Dependencies

- Depends on: TASK-013
- Blocks: TASK-018

## Risks

- Rubber-stamping — the value of this task is honest failure reports.

## Agent Notes

- Assumptions:
  - `develop` did not contain TASK-011 through TASK-013; TASK-014 was branched from the clean `feature/task-013-core-feature-hardening` dependency branch so QA tested the completed core feature state.
  - Small-phone coverage used an ADB display override (`720x1280`, density `320`) on the same physical `SM-S731B - 16` device; no second physical phone was available.
  - Live TalkBack audio could not be honestly verified unattended. TalkBack is installed but disabled; UIAutomator and Compose tests were used to inspect exposed text/semantics where runtime UI was available.
- Questions:
  - Which exact AirPods model/device matrix should be used for the final physical validation pass after scanner integration?
- Progress:
  - Automated PASS: `JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew test ktlintCheck build` completed with `BUILD SUCCESSFUL` (`104 actionable tasks: 3 executed, 101 up-to-date`).
  - Automated PASS: `JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew connectedAndroidTest` completed with `BUILD SUCCESSFUL`; 9 tests ran on `SM-S731B - 16`.
  - Automated PASS: `JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew installDebug` installed `app-debug.apk` on `SM-S731B - 16`.
  - Manual PASS: clean launch without app permissions showed the expected missing Bluetooth permission, notification permission, and popup fallback rows with recovery actions.
  - Manual PASS: after granting Bluetooth and notification permissions, the dashboard reached the scanning state and showed Bluetooth/notification permission health on the physical phone.
  - Manual PASS: with `SYSTEM_ALERT_WINDOW` set to `ignore`, the app showed the popup fallback row and "Battery status will stay inside the app."
  - Manual PASS with caveat: a 720x1280 / 1.3 font-scale viewport showed the visible scanning/settings UI without obvious text overlap, but this was a simulated small viewport on one physical device.
  - Manual FAIL: tapping `Test Popup` logged a local `airpods_detected` event, but the installed app stayed on `Scanning`/`Loading`; no snapshot, popup, `AirPods Pro` label, or battery values appeared.
  - Manual FAIL: Bluetooth was ON and the device had an AirPods entry visible in system Bluetooth state, but the app remained scanning because no real BLE scanner is wired into the runtime app. Raw Bluetooth identifiers were not recorded.
  - Source-audit FAIL: source search found no `BluetoothLeScanner`, `ScanCallback`, `WindowManager`, or notification posting implementation in `app/src/main`; the runtime monitor is fed only by the manual test channel in `AppShell`.
  - Manual NOT VERIFIED: hardware keyboard traversal to popup close and live TalkBack announcements, because the runtime popup never appeared.
  - Failures filed in `project-docs/handoffs/qa-to-frontend-backend-TASK-014-core-feature-blockers.md`.
- Final status:
  - QA task complete with failures handed off. Core feature returned to `In Progress`; it is not shipped.
