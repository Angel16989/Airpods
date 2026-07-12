# Agent Task Packet

## Task ID

`TASK-023`

## Assigned Agent

Agent name: Lead Agent (Claude) — completed 2026-07-12

## Feature

Feature name: Agent management dashboard

## Objective

A single-file website (`dashboard/index.html`) showing live, authentic repo state: which tasks are complete / in progress / blocked, which agents are on them, what is unblocked next, and a live commit feed.

## Background

User request: "a simple website that shows which ones are live being done … a live demonstration of what's happening in my repo … and it shall be authentic too." All data is fetched at view time from GitHub — commits via the GitHub REST API, the task board parsed from `project-docs/agent-workspace.md` on `develop` via raw.githubusercontent.com. Nothing is hardcoded or mocked.

## Files And Areas

### Files Created

- `dashboard/index.html` — self-contained (no build step, no dependencies, works opened as a local file)
- `project-docs/tasks/TASK-023-agent-dashboard.md` (this packet)

### Files Updated

- `project-docs/agent-workspace.md` — TASK-023 row added; TASK-022 marked Paused (superseded: revive only if an offline/local-file view is ever needed — this dashboard requires internet access to GitHub)

## Requirements

- Stat tiles: total / complete / in progress / blocked / backlog, computed from the live board.
- Active Agents table and full Task Queue table with icon+label status badges (never color alone).
- "Next up" list computed from dependencies (all deps Complete, not gated on user input).
- Live commit feed for `develop` and `main` with author, relative time, branch tag, and link to the commit on GitHub.
- Auto-refresh every 3 minutes + manual refresh; stays within GitHub's 60 unauthenticated API requests/hour.
- Graceful error banner on API failure or rate limiting; light and dark theme.

## Acceptance Criteria

- [x] JS syntax checked (`node --check`).
- [x] Both data endpoints verified live (HTTP 200).
- [x] Board parsing + next-up logic tested in node against the real board: 22 tasks, 4 agents, correct statuses.
- [x] Board updated (TASK-023 row, TASK-022 paused).

## Suggested Checks

Open `dashboard/index.html` in a browser (double-click the file). It should show the current board within a few seconds.

## Dependencies

- Depends on: nothing.
- Blocks: nothing. Supersedes TASK-022 unless an offline view is needed.

## Agent Notes

- Assumptions: board on `develop` is the source of truth; board reads may lag pushes by up to ~5 min (GitHub raw CDN cache).
- Known limits: unauthenticated API = 60 req/hour per IP (dashboard uses ~40/hour when left open). Visual layout was not screenshot-verified in a browser — logic and endpoints were tested; user should eyeball it once.
- Final status: Complete.
