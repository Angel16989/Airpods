# Example Feature

## Summary

Example Feature is a placeholder feature used to prove the multi-agent workflow. Replace it with the first real product feature when ready.

## Goal

Show how agents should coordinate around a feature spec, task packet, shared context, decision log, and handoff notes.

## Non-Goals

- This does not define the final product.
- This does not require code changes.
- This does not choose the app stack.

## Users

- Project owner
- Coding agents
- QA agents
- Documentation agents

## User Stories

- As the project owner, I want agents to share context so work stays organized.
- As an agent, I want a clear task packet so I know what to do.
- As a follow-up agent, I want handoff notes so I can continue without guessing.

## Requirements

### Must Have

- Shared context
- Agent workspace board
- Task packet
- Decision log
- Handoff note

### Should Have

- Agent role briefs
- Feature directory
- Release checklist

### Nice To Have

- Project-specific commands
- Real feature examples
- Automated docs checks

## Agent Flow

1. Agent reads `project-docs/shared-context.md`.
2. Agent reads assigned task in `project-docs/tasks/`.
3. Agent checks `project-docs/decision-log.md`.
4. Agent completes scoped work.
5. Agent updates the task packet.
6. Agent writes a handoff note if needed.

## Acceptance Criteria

- [x] The workflow is documented.
- [x] TASK-001 is tracked.
- [x] The next agent can understand what happened.
