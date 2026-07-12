# Agent Task Packet

## Task ID

`TASK-020`

## Assigned Agent

Agent name: Docs Agent

## Feature

Feature name: Release v0.1

## Objective

Write the shipping documentation: project README (what it is, setup, commands), brief user-facing usage notes, and release notes for v0.1. Sync all project-docs to reality.

## Background

Code and features are final after TASK-018/019 — documentation written earlier would have gone stale.

## Files And Areas

### Likely Files To Read

- Feature specs, decision log, shared context
- The actual codebase (verify commands and flows really work as documented)

### Likely Files To Edit

- `README.md` (repo root — note: CLAUDE.md stays agent-focused, README is human-focused)
- `project-docs/releases/release-notes-v0.1.md` (new)
- `project-docs/shared-context.md` (final sync)

### Files To Avoid

- Application source, `general-templates/`

## Requirements

- README setup instructions verified by actually running them from clean.
- Release notes list shipped features, known limitations, and deferred bugs from TASK-018.
- No remaining TBDs anywhere in project-docs except explicitly deferred items.

## Constraints

- Document what exists, not what was planned.

## Acceptance Criteria

- [ ] README complete and verified.
- [ ] Release notes written.
- [ ] `grep -rn "TBD" project-docs/` returns only intentionally deferred items.
- [ ] Committed to git.

## Suggested Checks

```bash
grep -rn "TBD" project-docs/ README.md
```

## Dependencies

- Depends on: TASK-018
- Blocks: TASK-021

## Risks

- Docs describing the spec instead of the shipped behavior.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
