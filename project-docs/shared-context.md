# Shared Context

## Project Summary

- Project name: Apple Icon
- Product type: TBD
- Primary users: TBD
- Main goal: Create a reusable workspace for planning features with multiple agents.

## Current Objective

Set up a clear documentation structure so agents can receive tasks, understand context, hand off work, and track decisions.

## Tech Stack

- Frontend: TBD
- Backend: TBD
- Database: TBD
- Styling: TBD
- Testing: TBD
- Deployment: TBD

## Commands

Replace these once the actual project stack exists.

```bash
npm install
```

```bash
npm run dev
```

```bash
npm test
```

## Agent Rules

- Read this file before starting work.
- Read `project-docs/branching-strategy.md` before creating or merging branches.
- Read the assigned task packet before editing.
- Keep changes scoped to the assigned task.
- Update the task packet with assumptions, progress, and final status.
- Add a handoff note if another agent needs to continue.
- Record major decisions in `decision-log.md`.

## Branching Strategy

- `main`: Production branch. Only release merges and urgent hotfixes should land here.
- `develop`: Integration branch for daily team development before release.
- `feature/*`: New features or tasks. Branch from `develop`, then merge back into `develop`.
- `bugfix/*`: Routine fixes found during development or testing. Branch from `develop`, then merge back into `develop`.
- `hotfix/*`: Urgent production fixes. Branch from `main`, then merge back into both `main` and `develop`.
- `release/*`: Release preparation. Branch from `develop`; only bug fixes and documentation changes happen here. Merge into `main` and back into `develop`.

## Known Constraints

- The actual product requirements are not defined yet.
- TASK-001 is a starter setup task, not a final product feature.

## Open Questions

- What is the first real feature to build?
- Which agents will be used most often?
- What commands should every agent run before handoff?
