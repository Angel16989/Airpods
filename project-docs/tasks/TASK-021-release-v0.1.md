# Agent Task Packet

## Task ID

`TASK-021`

## Assigned Agent

Agent name: QA Agent (final sign-off requires the user)

## Feature

Feature name: Release v0.1

## Objective

Ship v0.1: copy `general-templates/release-checklist.md` to `project-docs/releases/release-v0.1.md`, work through every item, get user sign-off, tag the release in git, and deploy per the stack's deployment decision.

## Background

This is the final gate. Every earlier task feeds a checklist item — nothing here should be new work, only verification.

## Files And Areas

### Likely Files To Read

- All of `project-docs/`
- `project-docs/releases/release-notes-v0.1.md`

### Likely Files To Edit

- `project-docs/releases/release-v0.1.md` (new, copied from template)
- `project-docs/agent-workspace.md` (close out the queue)
- `project-docs/features/feature-directory.md` (mark features Shipped)

### Files To Avoid

- Application source — if a checklist item fails, hand off a fix, do not patch.

## Requirements

- Every checklist item genuinely verified, not assumed.
- Rollback plan written down before deploying.
- User explicitly approves Final Sign-Off — do not self-approve.
- Git tag `v0.1.0` created after sign-off.

## Constraints

- Any failed checklist item reopens the owning task; this task pauses until fixed.

## Acceptance Criteria

- [ ] Release checklist complete with all boxes ticked honestly.
- [ ] User sign-off recorded.
- [ ] `v0.1.0` tagged and deployed (or delivered per the deployment decision).
- [ ] Board and feature directory show Shipped.

## Suggested Checks

```bash
git tag --list && git log --oneline -5
```

## Dependencies

- Depends on: TASK-019, TASK-020
- Blocks: nothing — this finishes v0.1.

## Risks

- Shipping with a silently skipped checklist item.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
