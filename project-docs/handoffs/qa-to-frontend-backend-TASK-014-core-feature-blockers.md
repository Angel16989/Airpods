# Agent Handoff

## From

Agent name: QA Agent

## To

Agent name: Frontend Agent and Backend Agent

## Date

Date: 2026-07-15

## Task

Task ID: TASK-014

## Summary

QA completed the core feature verification pass. Automated tests are green, but the installed runtime flow is not shippable: the app does not produce a visible snapshot or popup from the built-in test signal, there is no real BLE scanner binding, and there is no Android system-overlay presenter beyond the in-activity Compose popup surface.

## Current State

- Completed: Unit/lint/build verification, connected Android tests on `SM-S731B - 16`, install/launch, permission-state checks, overlay fallback row check, small-screen/font-scale spot check, and source audit.
- In progress: None by QA.
- Not started: Fixing implementation blockers; QA did not patch application source.

## Files Changed

| File | What Changed | Notes |
| --- | --- | --- |
| `project-docs/tasks/TASK-014-qa-core-feature.md` | Recorded QA results and final status. | QA task is complete because failures are filed here. |
| `project-docs/features/airpods-detection-popup.md` | Checked only exercised acceptance criteria. | Runtime detection/popup/fallback criteria remain unchecked. |
| `project-docs/features/feature-directory.md` | Returned core feature to `In Progress`. | Not shipped after QA. |
| `project-docs/agent-workspace.md` | Marked TASK-014 complete and recorded project blockers. | Follow-up implementation work is required before release. |

## Commands Run

```bash
git fetch origin
git switch -c feature/task-014-qa-core-feature
JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew test ktlintCheck build
JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew connectedAndroidTest
JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew installDebug
/tmp/apple-icon-android-sdk/platform-tools/adb shell pm clear com.angel16989.appleicon
/tmp/apple-icon-android-sdk/platform-tools/adb shell pm grant com.angel16989.appleicon android.permission.BLUETOOTH_SCAN
/tmp/apple-icon-android-sdk/platform-tools/adb shell pm grant com.angel16989.appleicon android.permission.BLUETOOTH_CONNECT
/tmp/apple-icon-android-sdk/platform-tools/adb shell pm grant com.angel16989.appleicon android.permission.POST_NOTIFICATIONS
/tmp/apple-icon-android-sdk/platform-tools/adb shell appops set com.angel16989.appleicon SYSTEM_ALERT_WINDOW ignore
/tmp/apple-icon-android-sdk/platform-tools/adb shell appops set com.angel16989.appleicon SYSTEM_ALERT_WINDOW allow
/tmp/apple-icon-android-sdk/platform-tools/adb shell wm size 720x1280
/tmp/apple-icon-android-sdk/platform-tools/adb shell wm density 320
/tmp/apple-icon-android-sdk/platform-tools/adb shell settings put system font_scale 1.3
/tmp/apple-icon-android-sdk/platform-tools/adb shell wm size reset
/tmp/apple-icon-android-sdk/platform-tools/adb shell wm density reset
/tmp/apple-icon-android-sdk/platform-tools/adb shell settings put system font_scale 1.0
```

## Results

- Passing: `test ktlintCheck build`; `connectedAndroidTest` ran 9 tests on `SM-S731B - 16`; `installDebug`; clean launch; missing-permission UI; granted-permission scanning UI; overlay fallback permission row; small-screen/1.3 font spot check for visible scanning/settings UI.
- Failing: Runtime `Test Popup` does not show a snapshot or popup in the installed app. Tapping `Test Popup` logs a local `airpods_detected` event, but the dashboard remains in `Scanning`/`Loading` and no `AirPods Pro` snapshot or battery values appear.
- Failing: Real AirPods detection is not implemented. The test phone had Bluetooth ON and an AirPods entry visible in system Bluetooth state, but Apple Icon still remained in scanning state. Raw Bluetooth identifiers were intentionally not recorded.
- Failing: No Android system-overlay presenter is implemented. The manifest declares `SYSTEM_ALERT_WINDOW`, but source search found no `WindowManager`/overlay service path; the popup component is only rendered inside `AirPodsDashboardScreen`.
- Not run: Live TalkBack audio announcements and popup close-action keyboard traversal, because the runtime popup never appeared. UIAutomator and Compose tests cover some accessibility semantics, but this is not a substitute for final TalkBack verification.

## Known Issues

- `AppShell.kt` wires `AirPodsMonitor` to `AirPodsSignalSource { manualSignals.receiveAsFlow() }`; there is no platform scanner feeding real Bluetooth/BLE payloads.
- `onTestPopup` sends `manualTestPayload()` and then increments `scanRefresh`. In manual QA this produced an `airpods_detected` Logcat event but no UI state update, which suggests the scan refresh can cancel the collector before the snapshot is saved/emitted to the dashboard.
- `AirPodsMonitor` defaults to `emptyFlow()` for signals, so without an injected source it can only emit cached snapshots or fatal gate errors.
- Overlay permission can be marked ready/fallback in UI, but there is no system overlay window outside the activity.

## Next Steps

1. Frontend: fix the runtime `Test Popup` action so a manual test payload reliably produces a visible snapshot and popup/fallback state in the installed app.
2. Backend/platform: add a real Bluetooth/BLE scanner binding that feeds normalized `AirPodsBluetoothPayload` values into `AirPodsMonitor` without displaying, logging, or persisting raw identifiers.
3. Frontend/platform: implement or explicitly de-scope the system-overlay presenter. If retained, verify the popup appears outside the app when overlay permission is granted and falls back in-app when denied.
4. QA: rerun TASK-014 acceptance checks after the fixes, including hardware keyboard traversal, TalkBack announcements, and real AirPods physical validation.

## Warnings For Next Agent

- Do not include raw Bluetooth MAC addresses or hardware identifiers in docs, logs, UI, or persisted state.
- Keep the existing local-only monitor contract unless a new decision is recorded.
- The automated UI tests passing does not prove the installed runtime user flow works; reproduce with ADB on a device before handing back to QA.
