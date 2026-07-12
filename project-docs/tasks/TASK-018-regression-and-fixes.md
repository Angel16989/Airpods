# Agent Task Packet

## Task ID

`TASK-018`

## Assigned Agent

Agent name: QA Agent (fixes handed off to Frontend/Backend Agents)

## Feature

Feature name: Release v0.1

## Objective

Full regression pass over the whole app, consolidate every open bug from TASK-014/017 handoffs, drive the fix round to zero known release-blocking bugs.

## Background

Both features are individually verified; this pass catches cross-feature and whole-app issues before release polish.

## Files And Areas

### Likely Files To Read

- All open handoffs in `project-docs/handoffs/`
- Feature specs (Acceptance Criteria)

### Likely Files To Edit

- This packet (bug list + statuses)
- Handoffs to implementing agents for each fix
- Application source: implementing agents only, one handoff per fix

### Files To Avoid

- QA does not patch application source directly.

## Requirements

- One consolidated bug list in this packet: severity, steps, owner, status.
- Each release-blocking bug fixed by its owning agent and re-verified by QA.
- Full automated suite green at the end.

## Constraints

- Non-blocking bugs get logged in the packet and deferred with user agreement, not silently dropped.

## Acceptance Criteria

- [ ] Bug list complete with final statuses.
- [ ] Zero open release-blocking bugs.
- [ ] Full suite green.
- [ ] Board updated.

## Suggested Checks

Run the full Test command; re-walk both feature flows end to end.

## Dependencies

- Depends on: TASK-014, TASK-017
- Blocks: TASK-019, TASK-020

## Risks

- Fix round introducing new regressions — re-run the suite after every fix batch.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
