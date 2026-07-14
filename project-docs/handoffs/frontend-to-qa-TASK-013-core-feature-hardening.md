# Agent Handoff

## From

Agent name: Frontend Agent

## To

Agent name: QA Agent

## Date

Date: 2026-07-15

## Task

Task ID: TASK-013

## Summary

Hardened the core AirPods feature against the spec's error and analytics tables. The dashboard now has test coverage for every core spec error message/recovery action, degraded fallback issues render in the app, and the monitor emits local-only debug events for detected AirPods, popup display, overlay fallback, and permission-blocked states.

## Current State

- Completed: TASK-013 source, unit tests, Compose UI test coverage, feature spec analytics note, and this QA handoff.
- In progress: None.
- Not started: Real AirPods BLE scanner binding and notification posting are still outside this task's scope.

## Files Changed

| File | What Changed | Notes |
| --- | --- | --- |
| `app/src/main/java/com/angel16989/appleicon/domain/airpods/AirPodsDebugEvents.kt` | Added local debug event model/logger for the core analytics-table events. | Events stay on-device and avoid raw Bluetooth identifiers. |
| `app/src/main/java/com/angel16989/appleicon/domain/airpods/AirPodsMonitor.kt` | Emits debug events for parsed AirPods, popup eligibility, overlay fallback, and Bluetooth permission blocking. | No external analytics provider was added. |
| `app/src/main/java/com/angel16989/appleicon/ui/AppShell.kt` | Wires the runtime monitor to a Logcat debug event logger. | Log tag: `AppleIconAirPods`. |
| `app/src/test/java/com/angel16989/appleicon/domain/airpods/AirPodsMonitorTest.kt` | Adds local debug-event assertions for detection, popup display, fallback, and permission blocking. | JVM coverage. |
| `app/src/test/java/com/angel16989/appleicon/ui/airpods/AirPodsDashboardMapperTest.kt` | Covers every core spec error case with expected user message and recovery action. | JVM coverage. |
| `app/src/androidTest/java/com/angel16989/appleicon/ui/airpods/AirPodsDashboardScreenTest.kt` | Covers rendered fallback issue messages and recovery buttons. | Android test source compiles; running requires a device/emulator. |
| `project-docs/features/airpods-detection-popup.md` | Records TASK-013 local debug-event implementation for the analytics table. | No provider/network added. |

## Commands Run

```bash
./gradlew test ktlintCheck
JAVA_HOME=$HOME/.local/share/jdks/jdk-17.0.19+10 ./gradlew test ktlintCheck
```

## Results

- Passing: `JAVA_HOME=$HOME/.local/share/jdks/jdk-17.0.19+10 ./gradlew test ktlintCheck`.
- Failing: plain `./gradlew test ktlintCheck` selected `/usr/lib/jvm/java-25-openjdk`, which does not provide the Java compiler capability Gradle requires.
- Not run: `connectedAndroidTest`; QA should run it on a connected device/emulator.

## Known Issues

- The app still uses the manual/test signal source from TASK-012; physical AirPods BLE detection remains unverified.
- Notification fallback UI state is covered, but posting the actual persistent/status notification belongs to the second feature tasks.

## Next Steps

1. Run `connectedAndroidTest` on a device or emulator.
2. Manually verify Bluetooth permission denial, Bluetooth-off recovery, overlay-denied fallback, unknown battery display, stale cached snapshot, retry scan, and popup dismissal.
3. Verify Logcat `AppleIconAirPods` lines are present for local debug events and contain no raw Bluetooth identifiers.

## Warnings For Next Agent

- Do not treat Logcat debug events as external analytics; v0.1 remains on-device only.
- Keep raw Bluetooth identities out of UI, persistence display, and debug logs.
- Use `JAVA_HOME=$HOME/.local/share/jdks/jdk-17.0.19+10` if Gradle picks the incomplete Java 25 install.
