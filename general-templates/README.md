# General Templates

This directory contains reusable boilerplates for planning, building, and documenting product features with multiple agents.

## Files

- `feature-boilerplate.md`: Full feature template covering goals, user flows, UI, API, data, testing, rollout, and open questions.
- `feature-directory.md`: Index template for tracking all features in one place.
- `release-checklist.md`: Practical checklist before shipping a feature.
- `agent-workspace.md`: Shared coordination board for many agents working in the same project.
- `agent-brief.md`: Role and responsibility template for a single agent.
- `agent-task-packet.md`: Clear work order template for assigning a task to an agent.
- `agent-handoff.md`: Handoff note template when one agent passes work to another.
- `decision-log.md`: Shared record of important decisions.
- `shared-context.md`: Project-wide facts, constraints, commands, and conventions.

## Suggested Use

1. Start with `shared-context.md` so every agent has the same facts.
2. Use `agent-workspace.md` as the main coordination board.
3. Give each agent an `agent-brief.md` so its role is clear.
4. Assign work with `agent-task-packet.md`.
5. Track features in `feature-directory.md`.
6. Record important calls in `decision-log.md`.
7. Use `agent-handoff.md` whenever work moves between agents.
8. Finish with `release-checklist.md`.

## Suggested Directory Layout

```text
project-docs/
  shared-context.md
  decision-log.md
  agent-workspace.md
  agents/
    frontend-agent.md
    backend-agent.md
    qa-agent.md
  features/
    feature-directory.md
    user-profile.md
  handoffs/
    frontend-to-backend-user-profile.md
  releases/
    release-checklist.md
```
