# Agent Task Packet

## Task ID

`TASK-007`

## Assigned Agent

Agent name: Backend Agent

## Feature

Feature name: Project bootstrap

## Objective

Initialize git and scaffold the application skeleton in `src/` (or the layout standard for the chosen stack): project config, dependency manifest, entry points, and folder structure. No feature logic yet.

## Background

Stack was approved in TASK-003. This creates the empty-but-runnable app every later task builds on.

## Files And Areas

### Likely Files To Read

- `project-docs/shared-context.md` (Tech Stack + Commands)
- `project-docs/decision-log.md`

### Likely Files To Edit

- New app files at repo root / `src/` per stack conventions
- `.gitignore`
- `project-docs/shared-context.md` (Repo link, confirm Commands work)

### Files To Avoid

- `general-templates/`, `project-docs/` content unrelated to commands/links

## Requirements

- `git init` with a sensible `.gitignore`; make an initial commit of docs + scaffold.
- App starts with the documented Run command and shows a placeholder screen/response.
- Install/Run/Test/Lint commands in shared-context.md all execute successfully.

## Constraints

- Use the scaffolding tool standard for the stack; do not hand-roll config the tool generates.
- No feature code, no database schema yet.

## Acceptance Criteria

- [ ] Fresh clone/install runs with documented commands.
- [ ] Placeholder app starts and stops cleanly.
- [ ] Initial git commit exists.
- [ ] Board updated.

## Suggested Checks

```bash
git log --oneline
```

Run the Install, Run, Test, and Lint commands from `project-docs/shared-context.md`.

## Dependencies

- Depends on: TASK-003
- Blocks: TASK-008, TASK-009, TASK-010

## Risks

- Scaffold drift from stack decision — follow the decision log exactly.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
