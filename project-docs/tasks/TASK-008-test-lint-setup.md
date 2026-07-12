# Agent Task Packet

## Task ID

`TASK-008`

## Assigned Agent

Agent name: Backend Agent

## Feature

Feature name: Project bootstrap

## Objective

Set up the testing and linting toolchain: test runner configured with one passing example test per layer (unit + one integration or component test), linter/formatter configured, and the exact commands documented in shared-context.

## Background

Every downstream task's acceptance criteria include "tests or checks pass" — this task makes that possible.

## Files And Areas

### Likely Files To Read

- `project-docs/shared-context.md`
- Scaffold from TASK-007

### Likely Files To Edit

- Test/lint config files
- Example test files
- `project-docs/shared-context.md` (Commands + Testing Conventions sections)

### Files To Avoid

- `general-templates/`

## Requirements

- Test command runs and passes with at least 2 example tests.
- Lint command runs clean on the scaffold.
- Testing Conventions section in shared-context.md filled in (no TBDs).

## Constraints

- Use stack-default tools unless the decision log says otherwise.

## Acceptance Criteria

- [ ] Test and lint commands pass from a clean install.
- [ ] Testing Conventions documented.
- [ ] Committed to git.

## Suggested Checks

Run the Test and Lint commands from `project-docs/shared-context.md`.

## Dependencies

- Depends on: TASK-007
- Blocks: TASK-011, TASK-012

## Risks

- Flaky example tests would erode trust in every later QA pass — keep them trivial and deterministic.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
