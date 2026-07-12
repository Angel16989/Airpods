# Agent Task Packet

## Task ID

`TASK-015`

## Assigned Agent

Agent name: Backend Agent

## Feature

Feature name: Second Feature (spec from TASK-005)

## Objective

Implement the second feature's backend per its spec: endpoints, validation, permissions, error handling, with unit + integration tests. Same standard as TASK-011.

## Background

Core feature backend is done (TASK-011) — reuse its patterns, helpers, and error handling instead of duplicating.

## Files And Areas

### Likely Files To Read

- `project-docs/features/<second-feature>.md`
- Core feature backend code (for patterns)
- `project-docs/shared-context.md` (conventions)

### Likely Files To Edit

- Backend source and tests; new migrations if the spec needs them

### Files To Avoid

- Frontend source, `general-templates/`

## Requirements

- Every endpoint in the spec implemented and tested.
- Conventions identical to the core feature (IDs, dates, errors, pagination).
- New migrations run up and down cleanly.

## Constraints

- Skip this task (mark Skipped on the board) if TASK-005 was skipped.
- Spec ambiguities: fix the spec, note it — do not silently deviate.

## Acceptance Criteria

- [ ] All spec endpoints work against seeded data.
- [ ] Tests cover happy path + validation/permission/error cases.
- [ ] Test and lint pass.
- [ ] Handoff note written for Frontend Agent.
- [ ] Committed to git.

## Suggested Checks

Run Test and Lint; exercise each endpoint against seed data.

## Dependencies

- Depends on: TASK-005, TASK-011
- Blocks: TASK-016

## Risks

- Pattern drift between the two features' backends.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
