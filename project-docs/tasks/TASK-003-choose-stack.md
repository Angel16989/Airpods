# Agent Task Packet

## Task ID

`TASK-003`

## Assigned Agent

Agent name: Backend Agent

## Feature

Feature name: Project bootstrap

## Objective

Choose the tech stack (frontend, backend, database, styling, testing, deployment), record it as a decision-log entry, get user approval, then fill in the Tech Stack and Commands sections of `project-docs/shared-context.md`.

## Background

The product definition comes from TASK-002. The stack must fit that definition. Nothing may be scaffolded until this decision is Active in the decision log.

## Files And Areas

### Likely Files To Read

- `project-docs/shared-context.md`
- `project-docs/decision-log.md`

### Likely Files To Edit

- `project-docs/decision-log.md`
- `project-docs/shared-context.md` (Tech Stack + Commands sections)

### Files To Avoid

- `general-templates/` (read-only)
- No application code in this task.

## Requirements

- [x] Propose one stack with 2–3 options considered and a reason for the final choice.
- [x] Present the proposal to the user and wait for approval before marking Active.
- [x] Update shared-context Tech Stack and Commands to match the approved stack.

## Constraints

- Prefer boring, well-documented technology over novel choices.
- No code scaffolding in this task (that is TASK-007).

## Acceptance Criteria

- [x] Decision-log entry exists with options, reason, and Active status.
- [x] User approved the stack.
- [x] shared-context.md Tech Stack has no TBDs.
- [x] shared-context.md Commands match the chosen stack.

## Suggested Checks

```bash
grep -n "TBD" project-docs/shared-context.md
```

## Dependencies

- Depends on: TASK-002
- Blocks: TASK-006, TASK-007

## Risks

- Choosing a stack before the product is defined would force rework.

## Agent Notes

- Assumptions: Product definition (Android app that detects connected Apple AirPods via BLE and shows an iOS-style popup with animated AirPod visual + battery percentage) was confirmed directly by the user in this session, ahead of TASK-002 formally updating shared-context's Project Summary. Tech stack choice assumes Android-only scope for v0.1 (no iOS/cross-platform requirement stated).
- Questions: None outstanding for this task.
- Progress: Proposed stack (Kotlin + Jetpack Compose, on-device only, DataStore, JUnit/Compose UI Testing/Espresso, Gradle → Play Store) with alternatives considered (Flutter, React Native, Room); presented to user and approved 2026-07-12.
- Final status: Complete. Decision logged as Active in `decision-log.md`; `shared-context.md` Tech Stack and Commands updated.
