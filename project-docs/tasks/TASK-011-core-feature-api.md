# Agent Task Packet

## Task ID

`TASK-011`

## Assigned Agent

Agent name: Backend Agent

## Feature

Feature name: Core Feature (see `project-docs/features/`, spec from TASK-004)

## Objective

Implement the core feature's backend exactly per its spec: endpoints from the API Contract, validation rules, permissions, and error handling. Unit + integration tests for each endpoint.

## Background

The spec is the contract — the Frontend Agent builds against it in TASK-012 without reading your code.

## Files And Areas

### Likely Files To Read

- `project-docs/features/<core-feature>.md` (API Contract, Data Model, Validation, Permissions, Error Handling)
- `project-docs/shared-context.md` (conventions)

### Likely Files To Edit

- Backend source, backend tests

### Files To Avoid

- Frontend source, migrations from TASK-010 (add new ones if schema must change), `general-templates/`

## Requirements

- Every endpoint in the spec implemented with documented request/response shapes.
- Every validation rule and permission enforced, with tests proving it.
- Every error case from the spec's Error Handling table returns the conventional error format.

## Constraints

- If the spec is ambiguous or wrong, update the spec and note it in Agent Notes — do not silently deviate.

## Acceptance Criteria

- [ ] All spec endpoints work against seeded data.
- [ ] Tests cover happy path + each validation/permission/error case.
- [ ] Test and lint commands pass.
- [ ] Handoff note written for Frontend Agent.
- [ ] Committed to git.

## Suggested Checks

Run the Test and Lint commands; exercise each endpoint manually against seed data.

## Dependencies

- Depends on: TASK-008, TASK-010
- Blocks: TASK-012, TASK-015

## Risks

- Contract drift from the spec breaks TASK-012 invisibly.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
