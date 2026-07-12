# Agent Task Packet

## Task ID

`TASK-005`

## Assigned Agent

Agent name: Docs Agent

## Feature

Feature name: Persistent Status Notification

## Objective

Spec the second-priority feature for v0.1 the same way as TASK-004: copy the boilerplate, fill every section, register it in the feature directory.

## Background

With the core feature specced (TASK-004), this covers the next most valuable flow. Implemented by TASK-015 to TASK-017.

## Files And Areas

### Likely Files To Read

- `project-docs/shared-context.md`
- `project-docs/features/<core-feature-name>.md` (for consistency)
- `general-templates/feature-boilerplate.md`

### Likely Files To Edit

- `project-docs/features/<second-feature-name>.md` (new, copied from template)
- `project-docs/features/feature-directory.md`

### Files To Avoid

- `general-templates/` (copy from it, never edit it)

## Requirements

- Confirm the feature choice with the user.
- Keep data model and API style consistent with the core feature spec.
- Every template section filled.

## Constraints

- If the user only wants one feature in v0.1, mark this task Skipped on the board with a note instead of inventing a feature.

## Acceptance Criteria

- [x] Feature spec exists with no empty sections (or task marked Skipped with user confirmation).
- [x] Feature directory updated.
- [x] No conflicts with the core feature's data model.

## Suggested Checks

```bash
grep -rn "TBD" project-docs/features/
```

## Dependencies

- Depends on: TASK-004
- Blocks: TASK-015, TASK-016

## Risks

- Scope creep: keep v0.1 to two features maximum.

## Agent Notes

- Assumptions: TASK-004's core feature spec (`project-docs/features/airpods-detection-popup.md`) was found completed but uncommitted in the working tree at task start; it was committed and merged to `develop` first so this task's "for consistency" dependency was real, not assumed. User was offered low-battery alerts, a persistent status notification, or a home-screen widget, plus the option to skip a second feature; user chose the persistent status notification.
- Questions:
  - Non-blocking: should the notification be swipe-dismissible while the setting stays on, or non-dismissible while connected? (left as an Open Question in the spec)
  - Non-blocking: should this later merge with a future low-battery alert feature into one notification?
- Progress: Read `airpods-detection-popup.md` for consistency; reused its snapshot fields (`device_id`, battery/charging fields, `connection_state`, `is_stale`) rather than redefining them, and reused its `AirPodsMonitor.observeSnapshots()` stream instead of a second monitoring path. Created `project-docs/features/persistent-status-notification.md` and registered it in the feature directory.
- Final status: Complete.
