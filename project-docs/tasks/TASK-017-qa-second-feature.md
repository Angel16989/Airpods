# Agent Task Packet

## Task ID

`TASK-017`

## Assigned Agent

Agent name: QA Agent

## Feature

Feature name: Second Feature

## Objective

Verify the second feature against its spec: full Testing Plan, Manual QA, and Acceptance Criteria — same standard as TASK-014.

## Background

TASK-016 handoff says the feature is complete. Verify honestly; file failures back to the owning agent.

## Files And Areas

### Likely Files To Read

- `project-docs/features/<second-feature>.md`
- `project-docs/handoffs/`

### Likely Files To Edit

- Test files (missing coverage only)
- The feature spec (tick criteria)
- This packet (results)

### Files To Avoid

- Application source (report, do not patch)

## Requirements

- Automated suite run with results recorded.
- Manual QA: desktop, mobile, keyboard, screen reader basics.
- Cross-feature check: using feature B does not break the core feature.

## Constraints

- Skip if the second feature was skipped (mark on board).

## Acceptance Criteria

- [ ] Suite results recorded.
- [ ] Manual QA recorded.
- [ ] Spec criteria ticked or failures handed off.
- [ ] Feature status updated in feature-directory.md.

## Suggested Checks

Run the Test command; exercise both features in one session.

## Dependencies

- Depends on: TASK-016
- Blocks: TASK-018

## Risks

- Regressions in the core feature introduced by feature B going unnoticed.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
