# Agent Task Packet

## Task ID

`TASK-005`

## Assigned Agent

Agent name: Docs Agent

## Feature

Feature name: Second Feature (name it properly in this task)

## Objective

Spec the second-priority feature for v0.1 the same way as TASK-004: copy the boilerplate, fill every section, register it in the feature directory.

## Background

With the core feature specced (TASK-004), this covers the next most valuable flow. Implemented by TASK-015 to TASK-017.

## Files And Areas

### Likely Files To Read

- `project-docs/shared-context.md`
- `project-docs/features/<core-feature-name>.md` (for consistency)
- `general-templates/feature-boilerplate.md`

### Likely Files To Edit

- `project-docs/features/<second-feature-name>.md` (new, copied from template)
- `project-docs/features/feature-directory.md`

### Files To Avoid

- `general-templates/` (copy from it, never edit it)

## Requirements

- Confirm the feature choice with the user.
- Keep data model and API style consistent with the core feature spec.
- Every template section filled.

## Constraints

- If the user only wants one feature in v0.1, mark this task Skipped on the board with a note instead of inventing a feature.

## Acceptance Criteria

- [ ] Feature spec exists with no empty sections (or task marked Skipped with user confirmation).
- [ ] Feature directory updated.
- [ ] No conflicts with the core feature's data model.

## Suggested Checks

```bash
grep -rn "TBD" project-docs/features/
```

## Dependencies

- Depends on: TASK-004
- Blocks: TASK-015, TASK-016

## Risks

- Scope creep: keep v0.1 to two features maximum.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
