# Agent Task Packet

## Task ID

`TASK-006`

## Assigned Agent

Agent name: Backend Agent

## Feature

Feature name: Project bootstrap

## Objective

Define the project-wide data and API conventions (ID format, date format, error response format, pagination, authentication) in `project-docs/shared-context.md`, consistent with the feature specs and chosen stack.

## Background

Feature specs (TASK-004/005) define per-feature contracts; this task locks the cross-cutting conventions so frontend and backend agents never improvise formats.

## Files And Areas

### Likely Files To Read

- `project-docs/features/` (all specs)
- `project-docs/shared-context.md`
- `project-docs/decision-log.md`

### Likely Files To Edit

- `project-docs/shared-context.md` (Data And API Conventions section)
- `project-docs/decision-log.md` (one entry for auth approach)

### Files To Avoid

- `general-templates/` (read-only)

## Requirements

- Every convention line has a concrete value (no TBD left).
- Error response format includes a worked JSON example.
- Auth decision recorded in the decision log.

## Constraints

- Conventions must not contradict any feature spec's API Contract; update specs if needed and note it.

## Acceptance Criteria

- [x] No TBDs remain in the Data And API Conventions section.
- [x] Auth decision-log entry is Active.
- [x] Feature specs cross-checked for consistency.

## Suggested Checks

```bash
grep -n "TBD" project-docs/shared-context.md
```

## Dependencies

- Depends on: TASK-003, TASK-004
- Blocks: TASK-010, TASK-011

## Risks

- Skipping this leads to mismatched formats between frontend and backend later.

## Agent Notes

- Assumptions: v0.1 remains fully on-device with no backend, no account system, and no remote sync; Android runtime permissions and local settings are the authorization boundary.
- Questions: None.
- Progress: Added concrete project-wide data/API conventions to `project-docs/shared-context.md`, including local ID format, timestamp format, error envelope example, pagination rule, and auth model.
- Progress: Added an Active auth decision to `project-docs/decision-log.md`.
- Progress: Cross-checked `airpods-detection-popup.md` and `persistent-status-notification.md`; aligned example `device_id` values with the shared convention.
- Final status: Complete on 2026-07-12.
