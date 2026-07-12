# Agent Workspace

## Project

- Project name: Apple Icon
- Current objective: Complete the v0.1 pipeline: define product → plan → build → verify → ship (TASK-002 to TASK-021).
- Current release: pre-0.1
- Repo path: `/home/rasik/Desktop/APPLE ICON`
- Repo URL: https://github.com/Angel16989/Airpods
- Main branch: `main` — production branch; only release and hotfix merges land here
- Integration branch: `develop` — daily development branch; feature and bugfix work merges here before release
- Legacy branch: `dev` — existing branch kept for compatibility; prefer `develop` for new work
- Supporting branches: `feature/*`, `bugfix/*`, `hotfix/*`, `release/*`

## Active Agents

| Agent | Role | Current Task | Status | Last Update | Blockers |
| --- | --- | --- | --- | --- | --- |
| Docs Agent | Specs, task packets, handoffs | TASK-005 (Complete) | Ready | 2026-07-12 | None |
| Frontend Agent | UI implementation | None | Ready | 2026-07-12 | None |
| Backend Agent | API and data | TASK-003 (Complete) | Ready | 2026-07-12 | None |
| QA Agent | Testing and verification | None | Ready | 2026-07-12 | None |

## Task Queue

Every task has a packet in `project-docs/tasks/`. Work them roughly in order; a task is ready when everything in Depends On is Complete.

| Task ID | Feature | Owner | Priority | Status | Depends On |
| --- | --- | --- | --- | --- | --- |
| TASK-001 | Starter workspace setup | Docs Agent | Medium | Complete | None |
| TASK-002 | Define product + fill shared context | Docs Agent | High | Complete | User input |
| TASK-003 | Choose tech stack | Backend Agent | High | Complete | TASK-002 |
| TASK-004 | Spec core feature | Docs Agent | High | Complete | TASK-002 |
| TASK-005 | Spec second feature | Docs Agent | Medium | Complete | TASK-004 |
| TASK-006 | Data + API conventions | Backend Agent | High | Ready | TASK-003, TASK-004 |
| TASK-007 | Git init + app scaffold | Backend Agent | High | Ready | TASK-003 |
| TASK-008 | Test + lint toolchain | Backend Agent | High | Backlog | TASK-007 |
| TASK-009 | Base UI foundation | Frontend Agent | High | Backlog | TASK-007 |
| TASK-010 | Database setup + migrations | Backend Agent | High | Backlog | TASK-006, TASK-007 |
| TASK-011 | Core feature API | Backend Agent | High | Backlog | TASK-008, TASK-010 |
| TASK-012 | Core feature UI | Frontend Agent | High | Backlog | TASK-009, TASK-011 |
| TASK-013 | Core feature hardening | Frontend Agent | High | Backlog | TASK-012 |
| TASK-014 | QA core feature | QA Agent | High | Backlog | TASK-013 |
| TASK-015 | Second feature API | Backend Agent | Medium | Backlog | TASK-005, TASK-011 |
| TASK-016 | Second feature UI | Frontend Agent | Medium | Backlog | TASK-009, TASK-015 |
| TASK-017 | QA second feature | QA Agent | Medium | Backlog | TASK-016 |
| TASK-018 | Full regression + fix round | QA Agent | High | Backlog | TASK-014, TASK-017 |
| TASK-019 | Accessibility + responsive audit | Frontend Agent | High | Backlog | TASK-018 |
| TASK-020 | README + release notes | Docs Agent | High | Backlog | TASK-018 |
| TASK-021 | Release v0.1 (checklist + ship) | QA Agent | High | Backlog | TASK-019, TASK-020 |
| TASK-022 | Local task dashboard (offline fallback) | Backend Agent | Low | Paused | None |
| TASK-023 | Agent management dashboard (`dashboard/index.html`) | Lead Agent | High | Complete | None |

## Current Blockers

| Blocker | Owner | Needed From | Date Raised | Status |
| --- | --- | --- | --- | --- |
| None | None | None | 2026-07-12 | None |

## Integration Notes

- Shared context lives in `project-docs/shared-context.md`.
- Decisions live in `project-docs/decision-log.md`.
- Tasks live in `project-docs/tasks/`.
- Feature specs live in `project-docs/features/`.
- Agent role briefs live in `project-docs/agents/`.
- Handoffs live in `project-docs/handoffs/`.
- Branching strategy lives in `project-docs/branching-strategy.md`.
- Tooling scripts live in `scripts/` (`auto_git_push.py` is ready; run it after git init).

## Daily Update

### 2026-07-12

- Completed: TASK-001 starter workspace setup; full 21-task v0.1 pipeline planned with packets (TASK-002 to TASK-021) plus TASK-022 tooling; `scripts/auto_git_push.py` created. TASK-002 product definition completed and written into `shared-context.md`. TASK-003 tech stack chosen and approved by user (Kotlin + Jetpack Compose Android app, on-device only, DataStore, JUnit/Compose UI Testing/Espresso, Gradle -> Play Store); logged in `decision-log.md` and reflected in `shared-context.md` Tech Stack/Commands. TASK-004 core feature spec completed as `project-docs/features/airpods-detection-popup.md`. TASK-005 second feature spec completed as `project-docs/features/persistent-status-notification.md` (user chose a persistent battery-status notification over low-battery alerts or a home-screen widget); reuses the core feature's snapshot model and monitor stream.
- In progress: None.
- Blocked: None.
- Next: TASK-006 and TASK-007 are ready to start. TASK-015 remains blocked on TASK-011 (core feature API) even though its TASK-005 dependency is now clear.
