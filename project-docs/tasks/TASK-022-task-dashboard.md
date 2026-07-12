# Agent Task Packet

## Task ID

`TASK-022`

## Assigned Agent

Agent name: Backend Agent (any capable agent may take it — standalone tooling task)

## Feature

Feature name: Project tooling — task status dashboard

## Objective

Build `scripts/task_dashboard.py`: a zero-dependency Python 3 script that serves a local web page showing every task's status — which tasks are done, which are in progress, who owns each, and which are ready to start next.

## Background

The user wants an at-a-glance view of the task queue without opening markdown files. All data already exists in `project-docs/`: the Task Queue table in `agent-workspace.md` and the `## Agent Notes` → `Final status:` line plus `## Dependencies` section in each `project-docs/tasks/TASK-*.md`.

## Files And Areas

### Likely Files To Read

- `project-docs/agent-workspace.md` (Task Queue table format)
- `project-docs/tasks/TASK-001.md` and `TASK-003-choose-stack.md` (packet format to parse)
- `scripts/auto_git_push.py` (style reference: stdlib-only, argparse, docstring usage header)

### Likely Files To Edit

- `scripts/task_dashboard.py` (new)

### Files To Avoid

- Everything else. This task changes no docs and no app code.

## Requirements

- Python 3 standard library only (`http.server`, `pathlib`, `re`, `argparse`, `json`). No pip installs.
- Parse the Task Queue markdown table in `project-docs/agent-workspace.md` into: Task ID, Feature, Owner, Priority, Status, Depends On.
- Enrich each row from its packet file in `project-docs/tasks/` when one exists (objective first sentence, final status line).
- Serve one HTML page at `http://localhost:8721` (port configurable with `--port`) with:
  - A summary line: X complete, Y in progress, Z blocked, N total.
  - The full task table, color-coded by status (green complete, yellow in progress, red blocked, grey backlog).
  - A "Next up" section listing tasks whose dependencies are all Complete and whose status is Backlog/Ready — this answers "what should be done next".
  - Owner column so "who is doing it" is visible.
- Also serve `GET /status.json` returning the same data as JSON (this is the webhook-style endpoint the user asked for — anything can poll it).
- Page auto-refreshes every 30 seconds (meta refresh is fine).
- Graceful message (not a crash) if the board or a packet file is missing or malformed.

## Constraints

- Read-only: the dashboard must never modify any file.
- Single file, under ~250 lines, clear enough for any agent to maintain.
- No external network access; binds to localhost only.

## Acceptance Criteria

- [ ] `python3 scripts/task_dashboard.py` serves the page on localhost:8721.
- [ ] All tasks from the board render with correct status colors and owners.
- [ ] "Next up" correctly lists only unblocked, not-started tasks.
- [ ] `/status.json` returns valid JSON with the same rows.
- [ ] Malformed/missing files produce a readable error page, not a traceback.
- [ ] Board updated and this packet's Agent Notes filled in.

## Suggested Checks

```bash
python3 scripts/task_dashboard.py --port 8721 &
curl -s http://localhost:8721/status.json | python3 -m json.tool
```

Then open the page in a browser and compare against `project-docs/agent-workspace.md`.

## Dependencies

- Depends on: nothing (docs already exist) — can be done any time.
- Blocks: nothing.

## Risks

- Parser tied too tightly to exact table formatting — tolerate extra whitespace and missing packet files.

## Agent Notes

- Assumptions:
- Questions:
- Progress:
- Final status:
