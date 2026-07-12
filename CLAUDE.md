# APPLE ICON — Project Rules For All Agents

Read this file first. It tells you how to work in this project, regardless of which model or agent you are.

## Start Here (in this order)

1. Read `project-docs/shared-context.md` — the single source of truth for facts, stack, commands, and conventions.
2. Read `project-docs/decision-log.md` — never contradict an Active decision without recording a new one.
3. Read `project-docs/agent-workspace.md` — the coordination board. Find your task in the Task Queue.
4. Read your agent brief in `project-docs/agents/` (frontend, backend, or qa).
5. Read the task packet for your assigned task in `project-docs/tasks/` if one exists.

## Workflow Rules

- Every new feature gets a spec: copy `general-templates/feature-boilerplate.md` into `project-docs/features/<feature-name>.md` and fill it in BEFORE writing code.
- Register every feature in `project-docs/features/feature-directory.md`.
- Every assigned task gets a packet: copy `general-templates/agent-task-packet.md` into `project-docs/tasks/TASK-XXX-<short-name>.md`. Use the next free TASK number.
- Record important decisions (architecture, stack, behavior changes) in `project-docs/decision-log.md`.
- When you stop mid-task or pass work to another agent, write a handoff: copy `general-templates/agent-handoff.md` into `project-docs/handoffs/<from>-to-<to>-<task>.md`.
- Update `project-docs/agent-workspace.md` (Active Agents table + Task Queue) after meaningful progress.
- Before shipping, complete `project-docs/releases/release-checklist.md` (copy it per release: `release-<version>.md`).

## Hard Rules

- Keep changes scoped to your assigned task. Do not touch unrelated files.
- Do not overwrite another agent's in-progress work — check the workspace board first.
- Match existing code patterns; do not introduce new libraries or abstractions without a decision-log entry.
- Record every assumption you make in your task packet under Agent Notes.
- If blocked, log it in the Current Blockers table in `agent-workspace.md` and stop — do not guess.

## Directory Map

```text
CLAUDE.md                     <- you are here
general-templates/            <- blank templates. NEVER edit these; copy them.
project-docs/
  shared-context.md           <- facts, stack, commands, conventions
  decision-log.md             <- record of important decisions
  agent-workspace.md          <- coordination board: agents, task queue, blockers
  agents/                     <- one brief per agent role
  features/                   <- feature-directory.md + one spec per feature
  tasks/                      <- one task packet per task (TASK-001-...)
  handoffs/                   <- handoff notes between agents
  releases/                   <- release checklists
src/                          <- application code (not created yet; see shared-context.md)
```
