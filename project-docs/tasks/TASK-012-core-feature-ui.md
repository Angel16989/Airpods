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

- [ ] Main flow completes in the running app.
- [ ] All four UI states visible and correct.
- [ ] Component/UI tests pass.
- [ ] Handoff note written for QA Agent.
- [ ] Committed to git.

## Suggested Checks

Run Test and Lint; walk the full user flow in the browser at two widths.

## Dependencies

- Depends on: TASK-009, TASK-011
- Blocks: TASK-013

## Risks

- Building against assumed instead of actual API responses.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
