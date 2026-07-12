# Agent Task Packet

## Task ID

`TASK-016`

## Assigned Agent

Agent name: Frontend Agent

## Feature

Feature name: Second Feature (spec from TASK-005)

## Objective

Build the second feature's UI per its spec, composing the shared primitives from TASK-009 against the real API from TASK-015. Include the full loading/empty/error/success states and edge-case hardening (same standard as TASK-012 + TASK-013 combined).

## Background

This feature is smaller than the core one, so build and hardening are one task. Read the Backend Agent's handoff first.

## Files And Areas

### Likely Files To Read

- `project-docs/features/<second-feature>.md`
- `project-docs/handoffs/` (backend handoff)
- Core feature UI code (for patterns)

### Likely Files To Edit

- Frontend source and tests

### Files To Avoid

- Backend source, `general-templates/`

## Requirements

- Full user flow works end to end against the real API.
- All four UI states per screen; spec's Error Handling table covered.
- Responsive at mobile and desktop widths.

## Constraints

- Skip if TASK-005/015 were skipped (mark on board).
- Reuse core-feature UI patterns; do not fork styles.

## Acceptance Criteria

- [ ] Main flow completes in the running app.
- [ ] Error cases from the spec demonstrated.
- [ ] Component/UI tests pass; lint passes.
- [ ] Handoff note written for QA Agent.
- [ ] Committed to git.

## Suggested Checks

Run Test and Lint; walk the flow at two widths; stop the backend and confirm graceful failure.

## Dependencies

- Depends on: TASK-009, TASK-015
- Blocks: TASK-017

## Risks

- Inconsistent UX between the two features.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
