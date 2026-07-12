# Agent Task Packet

## Task ID

`TASK-002`

## Assigned Agent

Agent name: Unassigned (any agent, with user input)

## Feature

Feature name: Project bootstrap

## Objective

Fill in every `TBD` in `project-docs/shared-context.md`: what the APPLE ICON product is, who it is for, and the main goal. Get answers from the user — do not invent them.

## Background

The docs structure was scaffolded on 2026-07-12. No application code exists. The product definition and tech stack are the two blockers for all other work (see the workspace board).

## Files And Areas

### Likely Files To Read

- `project-docs/shared-context.md`
- `project-docs/agent-workspace.md`

### Likely Files To Edit

- `project-docs/shared-context.md`
- `project-docs/agent-workspace.md` (mark this task done, unblock later implementation tasks)

### Files To Avoid

- `general-templates/` (read-only reference)

## Requirements

- Project Summary section has no remaining TBDs.
- Current Objective updated to the first real feature goal.
- Open Questions section updated (answered ones removed, new ones added).

## Constraints

- Ask the user for facts; do not guess the product definition.
- Record any stack proposal in `decision-log.md`, not directly in shared-context.

## Acceptance Criteria

- [ ] Shared context Project Summary is fully filled in.
- [ ] Workspace board updated (task status, blockers cleared).
- [ ] Decision log entry added if any decision was made.
- [ ] No unrelated files were changed.

## Suggested Checks

```bash
grep -n "TBD" project-docs/shared-context.md
```

## Dependencies

- Depends on: user input
- Blocks: future feature planning and implementation tasks

## Risks

- Guessing the product instead of asking would poison every downstream spec.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
