# AirPods Detection and Battery Popup

## Feature Name

AirPods Detection and Battery Popup

## Summary

This feature detects a user's Apple AirPods from the Android device, reads available battery information, and shows a polished iOS-style popup with an original AirPods-inspired visual. It exists so Android users with AirPods can get the quick connection and battery experience they expect without needing an Apple device.

## Goal

Ship the v0.1 MVP flow: after setup, the app detects supported AirPods when they connect or become visible, then shows battery status for the earbuds and case in a clear, fast, reliable popup.

## Non-Goals

- Do not build a remote backend, account system, cloud sync, or server analytics.
- Do not copy Apple-owned artwork, icons, names, animations, or UI assets.
- Do not support non-AirPods earbuds in v0.1.
- Do not build widgets, quick settings tiles, history charts, or advanced customization in v0.1.
- Do not promise exact battery readings when Android or Bluetooth payloads do not expose them.

## Users

- Primary: Android users who use Apple AirPods and want an iOS-style connection and battery popup.
- Secondary: Android users evaluating whether their AirPods are connected and charged before listening.

## User Stories

- As an Android AirPods user, I want the app to detect my AirPods automatically so that I do not need to open Bluetooth settings to check them.
- As an Android AirPods user, I want to see left earbud, right earbud, and case battery levels so that I know whether I can keep listening.
- As an Android AirPods user, I want a familiar iOS-style popup so that connection feedback feels fast and premium.
- As a privacy-conscious user, I want the feature to run on-device so that my device and battery data are not sent to a server.

## User Flow

1. User starts from: the installed Apple Icon Android app, with Bluetooth enabled.
2. User takes action: completes onboarding permissions, enables AirPods monitoring, then opens the AirPods case or connects AirPods to the phone.
3. System responds: the local monitor scans for supported AirPods signals, parses available battery data, and decides whether the popup should be shown.
4. User completes: the popup appears with AirPods name, original animated visual, battery percentages, and a dismiss action. If overlay permission is unavailable, the user sees the same status in the app plus a notification entry when notification permission is granted.

## Requirements

### Must Have

- Request and explain required Android permissions before starting monitoring.
- Monitor AirPods locally using Android Bluetooth/BLE APIs after permission is granted.
- Detect supported AirPods when they are connected or visible through available Bluetooth/BLE data.
- Parse battery percentages for left earbud, right earbud, and case when those values are available.
- Represent unknown battery values explicitly instead of showing fake percentages.
- Show an iOS-style popup using original app visuals, not copied Apple assets.
- Include AirPods name or generic model label, left/right/case battery, charging state when known, and last-updated time.
- Provide a fallback in-app status card when system overlay permission is denied or unavailable.
- Avoid repeated popup spam by applying a cooldown for the same device state.
- Store only minimal local state in DataStore: settings, last detected device label, last known battery snapshot, and popup cooldown metadata.
- Work without any backend or network dependency.

### Should Have

- Show a persistent or heads-up notification when notification permission is granted and overlay popup cannot be shown.
- Provide a simple main screen with monitoring status, permission health, last AirPods snapshot, and a test popup action.
- Support generic AirPods, AirPods Pro, and AirPods Max labels when the model can be inferred; otherwise show "AirPods".
- Mark stale battery data if the last successful read is older than the configured freshness window.
- Let the user disable automatic popups without disabling the whole app.
- Keep popup animation short, smooth, and respectful of Android reduced-motion settings.
- Provide accessible labels for all battery indicators and controls.

### Nice To Have

- Model-specific original illustrations for each supported AirPods family.
- Home screen widget for last known battery status.
- Quick settings tile to toggle monitoring.
- Battery history and low-battery reminders.
- Custom popup themes after the MVP is stable.

## UI Notes

- Main screen: compact status dashboard with monitoring on/off, permission checklist, last detected AirPods snapshot, and settings for popup behavior.
- Popup: floating Compose surface with original AirPods-inspired visual, device label, left/right/case battery rows, charging indicators, timestamp, and close button.
- Empty state: show setup guidance when no AirPods have been detected yet, with clear actions for Bluetooth and permissions.
- Loading state: show "Scanning" status only while the app is actively checking; do not block the rest of the UI.
- Error state: show permission or Bluetooth issue with a direct action to fix it.
- Success state: show the latest snapshot and confirm monitoring is active.
- Mobile behavior: popup must fit small Android screens, avoid display cutouts, and remain usable in portrait orientation first.
- Accessibility notes: all battery graphics need text equivalents; popup must be dismissible by keyboard/accessibility action; respect reduced motion and system font scaling.

## Data Model

| Field | Type | Required | Notes |
| --- | --- | --- | --- |
| device_id | string | Yes | Local-only stable identifier derived from available Bluetooth identity; do not sync. |
| display_name | string | Yes | User-visible name, defaulting to "AirPods" when unknown. |
| model_hint | enum | No | `airpods`, `airpods_pro`, `airpods_max`, or `unknown`. |
| connection_state | enum | Yes | `scanning`, `detected`, `connected`, `disconnected`, or `permission_blocked`. |
| left_battery_percent | integer | No | Nullable 0-100 value. Null means unknown. |
| right_battery_percent | integer | No | Nullable 0-100 value. Null means unknown. |
| case_battery_percent | integer | No | Nullable 0-100 value. Null means unknown. |
| left_charging | boolean | No | Nullable when charging state is unavailable. |
| right_charging | boolean | No | Nullable when charging state is unavailable. |
| case_charging | boolean | No | Nullable when charging state is unavailable. |
| source | enum | Yes | `ble_advertisement`, `bluetooth_connection`, `cached`, or `manual_test`. |
| last_seen_at | string | Yes | ISO-8601 timestamp for the latest detection or cache update. |
| is_stale | boolean | Yes | True when battery data is older than the freshness window. |
| popup_last_shown_at | string | No | ISO-8601 timestamp used for cooldown. |
| monitoring_enabled | boolean | Yes | User setting stored in DataStore. |
| overlay_enabled | boolean | Yes | User setting plus system permission result. |

## API Contract

### Endpoint

No HTTP endpoint. v0.1 is fully on-device. Downstream implementation should expose an internal app contract equivalent to:

`LOCAL AirPodsMonitor.observeSnapshots()`

The monitor returns a stream of `AirPodsMonitorResult` values to the UI and popup controller:

- `AirPodsMonitorResult.Snapshot(snapshot, popupShouldShow, fallbackErrors)` for successful or degraded local snapshots.
- `AirPodsMonitorResult.Failure(error)` for fatal local monitor states where scanning must not start.

Successful snapshot payloads are still the `AirPodsBatterySnapshot` domain model, with `popupShouldShow` added as monitor metadata. `fallbackErrors` contains conventional local error envelopes for degraded but usable states such as overlay fallback, unknown battery values, or stale cached data.

### Request

```json
{
  "monitoring_enabled": true,
  "overlay_enabled": true,
  "scan_reason": "app_start_or_bluetooth_event",
  "cooldown_seconds": 30
}
```

The implemented Kotlin request also includes `freshness_window_seconds` (default `120`) so cached snapshots can be marked stale deterministically. Platform permission and availability state is supplied separately through `AirPodsMonitorPermissions`:

```json
{
  "bluetooth_permission_granted": true,
  "bluetooth_available": true,
  "overlay_permission_granted": true,
  "notification_permission_granted": false,
  "scan_throttled": false
}
```

### Response

```json
{
  "device_id": "airpods_7f4a9c2d1b66",
  "display_name": "AirPods Pro",
  "model_hint": "airpods_pro",
  "connection_state": "connected",
  "left_battery_percent": 82,
  "right_battery_percent": 79,
  "case_battery_percent": 64,
  "left_charging": false,
  "right_charging": false,
  "case_charging": true,
  "source": "ble_advertisement",
  "last_seen_at": "2026-07-12T10:30:00+10:00",
  "is_stale": false,
  "popup_should_show": true
}
```

Fatal error response shape:

```json
{
  "ok": false,
  "error": {
    "code": "BLUETOOTH_PERMISSION_DENIED",
    "message": "Apple Icon needs Bluetooth permission to detect AirPods.",
    "recoverable": true,
    "user_action": "open_bluetooth_permission_settings",
    "details": {
      "permission": "BLUETOOTH_SCAN"
    }
  },
  "occurred_at": "2026-07-14T09:00:00+10:00"
}
```

## Permissions

- Who can view: any local device user who has installed the app.
- Who can create: the app can create local AirPods snapshots only after the user grants required Bluetooth permissions.
- Who can update: the local device user can update monitoring and popup settings; the app can update cached snapshots during monitoring.
- Who can delete: the local device user can clear local app data or use a future reset action; no remote data exists.
- Android permissions: request only permissions required for the current Android version, including nearby Bluetooth/BLE scanning or connection permissions, notifications when used for fallback, and overlay permission only when the popup is displayed outside the app.

## Validation Rules

- Battery percentages must be integers from 0 through 100.
- Unknown battery values must be stored as null and displayed as "Unknown", not guessed.
- A snapshot must include `device_id`, `display_name`, `connection_state`, `source`, `last_seen_at`, and `is_stale`.
- Popup cooldown must suppress duplicate popups for unchanged battery state within the cooldown window.
- Monitoring must not start until required permissions are granted and Bluetooth is available.
- Cached snapshots older than the freshness window must be marked stale.
- No Bluetooth identifier should be logged or displayed in raw form.

## Error Handling

| Error | Cause | User Message | Recovery |
| --- | --- | --- | --- |
| Bluetooth off | Device Bluetooth is disabled. | Turn on Bluetooth to detect your AirPods. | Link to Bluetooth settings or show retry after Bluetooth turns on. |
| Permission denied | Required Bluetooth permission is missing. | Apple Icon needs Bluetooth permission to detect AirPods. | Show permission action and keep monitoring disabled. |
| Overlay unavailable | System overlay permission is denied or not supported. | Popup permission is off, so battery status will appear inside the app. | Show in-app status card and optional notification fallback. |
| Battery unavailable | AirPods payload is detected but battery values cannot be parsed. | AirPods detected, but battery level is unavailable right now. | Show detected state with unknown battery values and retry on next signal. |
| Stale data | No fresh AirPods signal has been seen recently. | Showing last known battery from earlier. | Mark timestamp clearly and continue scanning when allowed. |
| Scan throttled | Android limits background Bluetooth scanning. | Waiting for Android to allow the next scan. | Resume on foreground open, Bluetooth event, or next allowed scan window. |

## Analytics

No external analytics service is required for v0.1. If local debug events are added, they must stay on-device unless the user explicitly opts into diagnostics later.

Implementation note (TASK-013): the core feature emits the analytics-table events as on-device `AirPodsDebugEvent` values and writes them to local Logcat under `AppleIconAirPods`. No external analytics provider is used for v0.1, and raw Bluetooth identifiers are not included in event properties.

| Event | Trigger | Properties |
| --- | --- | --- |
| airpods_detected | Supported AirPods signal is parsed. | model_hint, source, has_left, has_right, has_case |
| battery_popup_shown | Popup is displayed. | model_hint, source, is_stale, overlay_enabled |
| popup_fallback_used | Overlay popup cannot be shown. | reason, notification_available |
| permission_blocked | Monitoring cannot start due to missing permission. | permission_type |

## Testing Plan

### Unit Tests

- Test battery payload parsing for valid, partial, unknown, and malformed data.
- Test battery percentage validation.
- Test stale snapshot detection.
- Test popup cooldown logic.
- Test permission state mapping.

### Integration Tests

- Test monitor emits snapshots to UI state without a backend.
- Test DataStore saves and restores monitoring settings and last known snapshot.
- Test popup controller receives only eligible snapshots after cooldown filtering.
- Test notification or in-app fallback is selected when overlay permission is unavailable.

### UI Tests

- Test onboarding permission checklist.
- Test main screen empty, scanning, detected, stale, and permission-blocked states.
- Test popup layout with all batteries known, some unknown, and large system font.
- Test popup dismissal and test-popup action.

### Manual QA

- Desktop: not applicable; this is an Android app.
- Mobile: verify on at least one small phone and one larger phone in portrait orientation.
- Keyboard navigation: verify hardware keyboard or accessibility focus can reach settings and dismiss controls.
- Screen reader basics: verify TalkBack announces device label, battery values, stale state, and close action.
- Real device Bluetooth: verify with at least one supported AirPods model before release.

## Rollout Plan

- Feature flag: local `airpods_popup_enabled` setting; default on after required onboarding is complete.
- Migration needed: none for first release.
- Backward compatibility: if a device or Android version cannot provide battery payloads, show detected/unknown state instead of failing.
- Release notes: "Apple Icon detects your AirPods on Android and shows a quick battery popup for the earbuds and case when data is available."

## Risks

- AirPods battery payload behavior may vary by model, firmware, and Android device.
- Android background scan limits may delay detection.
- Overlay permission may reduce popup reliability if users deny it.
- Using Apple-like language or visuals too closely could create brand or store-review risk.
- Battery values may be partial or stale, so the UI must communicate uncertainty honestly.

## Open Questions

- Which exact AirPods models will be physically tested before v0.1 release?
- What final app-store wording should be used to describe Apple/AirPods compatibility without implying official affiliation?

## Acceptance Criteria

- [x] User can enable monitoring after granting required permissions.
- [ ] AirPods detection produces a local snapshot when supported Bluetooth/BLE data is available.
- [ ] Popup shows device label, original visual, battery values, unknown states, and timestamp.
- [ ] Overlay-denied fallback shows battery status inside the app and optionally through notification.
- [x] Duplicate popups are suppressed during the cooldown window.
- [x] Error and permission states are handled with clear recovery actions.
- [x] Tests are added or updated for parser, state mapping, cooldown, DataStore, and UI states.
- [x] Documentation is updated and downstream API/UI tasks can implement from this spec.
- [x] Feature is ready for TASK-006, TASK-011, and TASK-012 planning.

## QA Status

TASK-014 QA on 2026-07-15 did not ship the feature. Automated tests pass, but installed runtime verification found blockers: the `Test Popup` action logs an `airpods_detected` event without rendering a snapshot/popup, real AirPods BLE detection is not wired into the app, and system-overlay presentation is not implemented beyond the in-activity popup component. See `project-docs/handoffs/qa-to-frontend-backend-TASK-014-core-feature-blockers.md`.
