# Persistent Status Notification

## Feature Name

Persistent Status Notification

## Summary

While AirPods are connected, this feature shows an ongoing (persistent, low-priority) Android notification with live battery percentages for the left earbud, right earbud, and case. It gives users a glanceable, always-available battery status without needing to reopen the app or wait for the connection popup from the core feature.

## Goal

Keep an accurate, low-priority notification visible for the duration of an AirPods connection, updating battery values as new snapshots arrive from the same on-device monitor used by the core detection feature (`project-docs/features/airpods-detection-popup.md`).

## Non-Goals

- Do not duplicate the connection popup; this notification is a persistent companion, not a replacement for the popup shown on connect.
- Do not add actionable buttons (e.g. "Find My AirPods", "Disconnect") in v0.1 — display only.
- Do not sync notification state to a server; it is derived entirely from the same on-device snapshot used by the core feature.
- Do not show the notification when AirPods are disconnected; it must not linger as stale/dead UI.
- Do not build a home-screen widget or quick settings tile — that is a separate, later feature.

## Users

- Primary: Android users who use Apple AirPods and want to glance at battery status from the notification shade without opening the app.
- Secondary: Users who have denied the system overlay permission and rely on the in-app/notification fallback described in the core feature spec.

## User Stories

- As an Android AirPods user, I want a notification showing current battery levels so that I can check status without opening the app or waiting for the popup.
- As a user who denied overlay permission, I want the notification to be the primary way I see battery status, so that I am not left without any feedback.
- As a user, I want the notification to disappear automatically when my AirPods disconnect so that it never shows stale information.

## User Flow

1. User starts from: AirPods connected and a battery snapshot already available from the core detection feature.
2. User takes action: enables notification status in app settings (on by default once notification permission is granted).
3. System responds: the monitor's snapshot stream updates a single ongoing notification with left/right/case battery and charging state each time a new snapshot arrives.
4. User completes: user pulls down the notification shade to check battery at any time; the notification is automatically removed when `connection_state` becomes `disconnected`.

## Requirements

### Must Have

- Show one ongoing, low-priority (silent, no sound/vibration) notification per active AirPods connection.
- Update the notification in place as new battery snapshots arrive; never spawn duplicate notifications for the same connection.
- Display left/right/case battery percentages and charging indicators, matching the same unknown/null handling as the core feature (show "Unknown" rather than a fake value).
- Remove the notification automatically when `connection_state` transitions to `disconnected` or monitoring is turned off.
- Only show the notification when the user has granted Android notification permission and enabled the setting.
- Reuse the existing `AirPodsMonitor.observeSnapshots()` stream from the core feature; do not create a second monitoring path.

### Should Have

- Mark the notification as stale (e.g. dimmed subtitle) using the same `is_stale` flag and freshness window as the core feature when no fresh signal has arrived recently.
- Let the user disable the notification independently of the popup and independently of overlay settings.
- Tapping the notification opens the app's main status screen.

### Nice To Have

- Notification icon reflects approximate battery level (e.g. tiered icon).
- Group left/right/case into a notification with an expanded view showing timestamps.
- Per-component low-battery emphasis (e.g. bold text) once low-battery alerts are specced separately.

## UI Notes

- Main screen: add a "Persistent notification" toggle in the same settings area as the overlay/popup toggle from the core feature.
- Empty state: no notification is shown/posted before the first connection or when monitoring is off.
- Loading state: not applicable — the notification only appears once a snapshot exists; no separate loading UI.
- Error state: if notification permission is missing, the settings toggle shows a disabled state with a permission prompt, consistent with the core feature's permission-blocked pattern.
- Success state: notification content reflects the latest non-stale snapshot.
- Mobile behavior: standard Android notification; must render correctly in collapsed and expanded form and respect system notification styling (light/dark).
- Accessibility notes: notification title/text must be readable by screen readers with explicit battery values and charging state, not icon-only.

## Data Model

Reuses the snapshot fields defined in `airpods-detection-popup.md` (`device_id`, `display_name`, `connection_state`, `left_battery_percent`, `right_battery_percent`, `case_battery_percent`, `left_charging`, `right_charging`, `case_charging`, `last_seen_at`, `is_stale`). Adds the following notification-specific state:

| Field | Type | Required | Notes |
| --- | --- | --- | --- |
| notification_enabled | boolean | Yes | User setting stored in DataStore, independent of `overlay_enabled`. |
| notification_permission_granted | boolean | Yes | Reflects the Android runtime notification permission result. |
| notification_active | boolean | Yes | True while an ongoing notification is currently posted for the active connection. |
| notification_id | integer | Yes | Stable local notification ID tied to `device_id`, so updates replace rather than duplicate. |
| last_notification_update_at | string | No | ISO-8601 timestamp of the last notification content update. |

## API Contract

### Endpoint

No HTTP endpoint. v0.1 is fully on-device, same as the core feature.

`LOCAL AirPodsMonitor.observeSnapshots()` (existing stream, reused) drives a new local consumer:

`LOCAL StatusNotificationController.onSnapshot(snapshot)`

### Request

```json
{
  "notification_enabled": true,
  "notification_permission_granted": true,
  "device_id": "local-airpods-id"
}
```

### Response

```json
{
  "notification_id": 1001,
  "notification_active": true,
  "connection_state": "connected",
  "left_battery_percent": 82,
  "right_battery_percent": 79,
  "case_battery_percent": 64,
  "is_stale": false,
  "last_notification_update_at": "2026-07-12T10:31:00+10:00"
}
```

## Permissions

- Who can view: the local device user, via the Android notification shade.
- Who can create: the app posts the notification only after Android notification permission (`POST_NOTIFICATIONS` on Android 13+) is granted and the user setting is enabled.
- Who can update: the app updates notification content automatically as new snapshots arrive; the user cannot manually edit content.
- Who can delete: the user can dismiss the app's setting to stop future notifications; the system removes the current notification automatically on disconnect. The user can also clear it manually from the shade, which does not disable the setting.
- Android permissions: requires the standard notification permission prompt already covered by the core feature's onboarding; no additional permission types are introduced.

## Validation Rules

- Never post more than one active notification per connected device (`notification_id` keyed on `device_id`).
- Do not post or update the notification when `notification_enabled` is false or permission is not granted.
- Notification must be cancelled within one snapshot cycle of `connection_state` becoming `disconnected`.
- Battery values shown must come directly from the same snapshot validated by the core feature (0-100 integers or null/"Unknown"); this feature must not independently reparse Bluetooth data.

## Error Handling

| Error | Cause | User Message | Recovery |
| --- | --- | --- | --- |
| Notification permission denied | User denied `POST_NOTIFICATIONS`. | Notifications are off. Battery status is still available in the app. | Show a permission prompt from the settings toggle; fall back to in-app status only. |
| Snapshot unavailable | Monitor has no current snapshot (e.g. between disconnect and next connect). | No notification is shown. | No action needed; notification simply is not posted. |
| Stale snapshot | No fresh signal within the freshness window while still "connected". | Notification subtitle indicates last-updated time. | Continue showing last known values with staleness noted, matching core feature behavior. |
| Duplicate post attempt | Snapshot arrives faster than the notification can be updated. | Not user-visible. | Debounce updates to the same `notification_id` instead of posting duplicates. |

## Analytics

No external analytics service for v0.1, consistent with the core feature.

| Event | Trigger | Properties |
| --- | --- | --- |
| status_notification_shown | Notification is first posted for a connection. | model_hint, is_stale |
| status_notification_updated | Notification content is refreshed. | is_stale, source |
| status_notification_dismissed_setting | User turns the notification setting off. | previous_state |

## Testing Plan

### Unit Tests

- Test notification content mapping from a snapshot, including unknown/null battery values.
- Test debouncing of rapid successive snapshot updates.
- Test notification cancellation on disconnect.
- Test gating logic when permission or setting is off.

### Integration Tests

- Test `StatusNotificationController` consumes the same `AirPodsMonitor.observeSnapshots()` stream as the popup without a second BLE scan path.
- Test DataStore persistence of `notification_enabled` across app restarts.

### UI Tests

- Test settings toggle enabling/disabling the notification.
- Test permission-denied state in settings.

### Manual QA

- Desktop: not applicable; this is an Android app.
- Mobile: verify notification appears on connect, updates on battery change, and clears on disconnect on at least one physical device.
- Keyboard navigation: verify settings toggle is reachable via accessibility focus.
- Screen reader basics: verify TalkBack reads full battery text from the notification, not just an icon.

## Rollout Plan

- Feature flag: local `status_notification_enabled` setting; default on when notification permission is already granted, otherwise prompted.
- Migration needed: none for first release.
- Backward compatibility: on Android versions without runtime notification permission (pre-13), treat permission as granted by default per platform behavior.
- Release notes: "Apple Icon can now show an ongoing notification with live AirPods battery levels while connected."

## Risks

- Overlapping settings (popup vs. notification vs. overlay-denied fallback) could confuse users if not clearly labeled as independent toggles.
- Android battery-optimization/background restrictions could delay notification updates on some OEM devices.
- Poor debouncing could cause notification flicker if snapshots arrive in rapid bursts.

## Open Questions

- Should the notification be dismissible by swipe while the setting stays enabled (re-posted on next update), or should it be non-dismissible while connected?
- Should this notification later merge with the low-battery alert feature once that is specced, to avoid two separate AirPods notifications?

## Acceptance Criteria

- [ ] Notification is posted only when AirPods are connected, permission is granted, and the setting is enabled.
- [ ] Notification content matches the latest snapshot, including unknown/stale handling consistent with the core feature.
- [ ] Notification updates in place without duplicates across the life of one connection.
- [ ] Notification is cancelled automatically on disconnect or when the setting is turned off.
- [ ] Settings toggle and permission-denied state are implemented and accessible.
- [ ] Tests are added or updated for content mapping, debouncing, cancellation, and settings persistence.
- [ ] Documentation is updated and downstream API/UI tasks (TASK-015, TASK-016) can implement from this spec.
