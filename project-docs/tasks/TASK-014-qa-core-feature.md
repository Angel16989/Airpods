# Agent Task Packet

## Task ID

`TASK-014`

## Assigned Agent

Agent name: QA Agent

## Feature

Feature name: Core Feature

## Objective

Verify the core feature against its spec: run the full Testing Plan (unit, integration, UI, manual QA sections) and check off the spec's Acceptance Criteria — or file failures back to the implementing agents.

## Background

Frontend and Backend report the feature complete (TASK-011 to TASK-013 handoffs). Nothing is "Shipped" until this pass is green.

## Files And Areas

### Likely Files To Read

- `project-docs/features/<core-feature>.md` (Testing Plan, Acceptance Criteria)
- `project-docs/handoffs/` (implementation handoffs)

### Likely Files To Edit

- Test files (may add missing coverage)
- The feature spec (tick Acceptance Criteria)
- This packet (record results)

### Files To Avoid

- Application source (report bugs, do not patch)

## Requirements

- Run the full automated suite; record pass/fail with output.
- Execute Manual QA: desktop, mobile, keyboard navigation, screen reader basics.
- Every failure filed as a handoff to the owning agent with reproduction steps.

## Constraints

- Never tick a criterion that was not actually exercised.

## Acceptance Criteria

- [ ] Automated suite results recorded.
- [ ] Manual QA checklist executed and recorded.
- [ ] Spec Acceptance Criteria all ticked, or failures handed off.
- [ ] Feature status updated in feature-directory.md (Review → Shipped or back to In Progress).

## Suggested Checks

Run the Test command; walk the user flow with keyboard only.

## Dependencies

- Depends on: TASK-013
- Blocks: TASK-018

## Risks

- Rubber-stamping — the value of this task is honest failure reports.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
