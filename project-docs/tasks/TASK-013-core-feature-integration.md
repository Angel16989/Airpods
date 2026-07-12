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
- Questions:
- Progress:
- Final status:
