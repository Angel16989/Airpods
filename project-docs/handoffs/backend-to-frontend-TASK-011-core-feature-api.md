# Agent Handoff

## From

Agent name: Backend Agent

## To

Agent name: Frontend Agent

## Date

Date: 2026-07-14

## Task

Task ID: TASK-011

## Summary

Implemented the local core feature API contract for AirPods detection and popup eligibility. There is still no remote backend; the frontend should consume `AirPodsMonitor.observeSnapshots(request, permissions)` and handle `AirPodsMonitorResult.Snapshot` / `AirPodsMonitorResult.Failure`.

## Current State

- Completed: Local monitor request/permission/result models, error envelopes, payload parser, DataStore-backed monitor stream, stale detection, popup cooldown, manifest permissions, and JVM tests.
- In progress: None.
- Not started: Real Bluetooth scanner binding and runtime permission UI are left for downstream feature/UI work.

## Files Changed

| File | What Changed | Notes |
| --- | --- | --- |
| `app/src/main/java/com/angel16989/appleicon/data/model/AirPodsMonitorModels.kt` | Added local monitor API request, permission, result, payload, and error models. | `Snapshot` results carry `popupShouldShow` plus degraded-state `fallbackErrors`; fatal states emit `Failure`. |
| `app/src/main/java/com/angel16989/appleicon/domain/airpods/AirPodsMonitor.kt` | Added DataStore-backed `AirPodsMonitor.observeSnapshots()`. | Emits seeded/cached snapshots, processes signal-source payloads, saves snapshots, marks stale data, and applies popup cooldown. |
| `app/src/main/java/com/angel16989/appleicon/domain/airpods/AirPodsPayloadParser.kt` | Added normalized local Bluetooth payload parser. | Derives salted `airpods_<12 hex>` IDs and never returns raw Bluetooth identity. |
| `app/src/main/java/com/angel16989/appleicon/domain/airpods/AirPodsMonitorErrors.kt` | Added conventional local error envelope builders. | Codes match the feature spec error table. |
| `app/src/main/AndroidManifest.xml` | Declared Bluetooth, notification, overlay, and pre-Android-12 BLE permissions. | Runtime request flow still belongs in UI/platform integration. |
| `app/src/test/java/com/angel16989/appleicon/domain/airpods/` | Added parser and monitor JVM tests. | Covers seeded data, payload persistence, validation, permissions, stale data, overlay fallback, battery unavailable, scan throttling, and cooldown. |
| `project-docs/features/airpods-detection-popup.md` | Clarified implemented local API stream shape. | This resolves the spec ambiguity between success snapshots and error envelopes. |

## Commands Run

```bash
JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew ktlintFormat
JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew test
JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew ktlintCheck
JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew build
JAVA_HOME="$HOME/.local/share/jdks/jdk-17.0.19+10" ./gradlew :app:compileDebugAndroidTestKotlin :app:compileDebugAndroidTestJavaWithJavac
```

## Results

- Passing: `ktlintFormat`, `test`, `ktlintCheck`, `build`, Android test-source compilation.
- Failing: None.
- Not run: `connectedAndroidTest`, because no connected device/emulator is available in this environment.

## Known Issues

- Real BLE scanning is represented by the injectable `AirPodsSignalSource`; a platform scanner still needs to feed normalized `AirPodsBluetoothPayload` values.
- `AirPodsMonitor` does not mark a popup as shown automatically. The popup controller should call `AirPodsPreferencesRepository.markPopupShown(deviceId, shownAt)` after display.

## Next Steps

1. Build TASK-012 UI against `AirPodsMonitorResult.Snapshot` and `AirPodsMonitorResult.Failure`.
2. Map Android runtime permission and Bluetooth availability state into `AirPodsMonitorPermissions`.
3. Trigger `markPopupShown` after the popup is actually displayed so cooldown behavior persists.

## Warnings For Next Agent

- Do not display, log, or persist raw Bluetooth identity values.
- Treat `null` battery values as "Unknown"; do not invent percentages.
- `fallbackErrors` are degraded usable states, not fatal failures.
