#!/usr/bin/env python3
"""Unattended task dispatcher: assigns board tasks to headless Claude Code agents.

Reads the Task Queue in project-docs/agent-workspace.md, picks the next
unblocked task (all dependencies Complete, not waiting on user input),
and spawns one `claude -p` agent at a time to do it. Sequential on purpose:
all agents share this working tree.

Usage:
    python3 scripts/agent_dispatcher.py --dry-run      # show the plan only
    python3 scripts/agent_dispatcher.py                # run up to 3 tasks (sonnet)
    python3 scripts/agent_dispatcher.py --max-tasks 5 --model haiku
    python3 scripts/agent_dispatcher.py --full-auto    # agents may also run
                                                       # commands (git/gradle) without prompts
    python3 scripts/agent_dispatcher.py --runner codex             # Codex, sandboxed
    python3 scripts/agent_dispatcher.py --runner codex --full-auto # Codex, no sandbox
    python3 scripts/agent_dispatcher.py --runner codex-local \
        --model qwen2.5-coder:3b                                  # Codex + local Ollama
    python3 scripts/agent_dispatcher.py --runner codex-tower \
        --model qwen3:8b                                           # Codex + tower Ollama

Notes:
- Default permission mode is acceptEdits: agents can read/edit files but
  shell commands are denied, so they can spec/document but not build or push.
  The auto_git_push.py watcher (run it separately) sweeps their edits to GitHub.
- --full-auto passes --dangerously-skip-permissions to spawned agents. More
  capable (they can run tests, git, gradle) — use it when you trust the queue.
- A task counts as done only if the agent sets its board row to Complete.
  Anything else is reported as a failure; the dispatcher stops unless
  --keep-going is set.
- Logs: dispatch-logs/<task>-<timestamp>.log
"""

import argparse
import datetime
import os
import re
import subprocess
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
BOARD = REPO_ROOT / "project-docs" / "agent-workspace.md"
TASKS_DIR = REPO_ROOT / "project-docs" / "tasks"
LOG_DIR = REPO_ROOT / "dispatch-logs"

PRIORITY_ORDER = {"high": 0, "medium": 1, "low": 2}
DONE = {"complete", "shipped"}
CLAIMABLE = {"backlog", "ready"}


def parse_queue():
    """Return the Task Queue rows as dicts."""
    md = BOARD.read_text()
    m = re.search(r"^##\s+Task Queue\s*$(.*?)(?=^##\s|\Z)", md, re.M | re.S)
    if not m:
        sys.exit(f"Could not find '## Task Queue' in {BOARD}")
    tasks = []
    for line in m.group(1).splitlines():
        line = line.strip()
        if not line.startswith("|"):
            continue
        cells = [c.strip() for c in line.split("|")[1:-1]]
        if len(cells) < 6 or cells[0] in ("Task ID",) or set(cells[0]) <= {"-", ":"}:
            continue
        tasks.append({
            "id": cells[0], "feature": cells[1], "owner": cells[2],
            "priority": cells[3], "status": cells[4], "deps": cells[5],
        })
    return tasks


def eligible(tasks):
    done = {t["id"] for t in tasks if t["status"].lower() in DONE}
    out = []
    for t in tasks:
        if t["status"].lower() not in CLAIMABLE:
            continue
        if "user input" in t["deps"].lower():
            continue
        dep_ids = re.findall(r"TASK-\d+", t["deps"])
        if all(d in done for d in dep_ids):
            out.append(t)
    out.sort(key=lambda t: (PRIORITY_ORDER.get(t["priority"].lower(), 9), t["id"]))
    return out


def packet_path(task_id):
    hits = sorted(TASKS_DIR.glob(f"{task_id}*.md"))
    return hits[0] if hits else None


def agent_brief_path(owner):
    slug = owner.lower().replace(" ", "-")
    p = REPO_ROOT / "project-docs" / "agents" / f"{slug}.md"
    return p if p.exists() else None


def build_prompt(task):
    packet = packet_path(task["id"])
    brief = agent_brief_path(task["owner"])
    lines = [
        f"You are the {task['owner']} for the Apple Icon project, working unattended.",
        f"Complete exactly one task: {task['id']} — {task['feature']}.",
        "",
        "Steps:",
        "1. Read CLAUDE.md, project-docs/shared-context.md, and project-docs/branching-strategy.md.",
    ]
    if brief:
        lines.append(f"2. Read your role brief: {brief.relative_to(REPO_ROOT)}.")
    if packet:
        lines.append(f"3. Read your task packet: {packet.relative_to(REPO_ROOT)} and follow it exactly.")
    lines += [
        "4. Check for existing work on this task before starting anything "
        "(uncommitted files, existing feature branches for this task ID) and continue it rather than redoing it.",
        "5. Do only this task. Keep every change scoped to it.",
        f"6. When finished, update the packet's Agent Notes and set the {task['id']} row's "
        "Status to Complete in project-docs/agent-workspace.md. If you cannot finish, set it to "
        "Blocked and add a row to Current Blockers explaining exactly what is missing.",
        "",
        "Rules: follow the branching strategy; NEVER commit or push to main. "
        "If a shell command is denied by permissions, do not fight it — do the file work, "
        "and note in the packet which commands still need to be run. Be honest in all status "
        "reporting: never mark Complete unless the packet's acceptance criteria are actually met.",
    ]
    return "\n".join(lines)


def run_task(task, args):
    LOG_DIR.mkdir(exist_ok=True)
    stamp = datetime.datetime.now().strftime("%Y%m%d-%H%M%S")
    log_path = LOG_DIR / f"{task['id']}-{stamp}.log"
    if args.runner in {"codex", "codex-local", "codex-tower"}:
        cmd = ["codex", "exec", "-C", str(REPO_ROOT)]
        if args.runner == "codex-local":
            cmd += ["--oss", "--local-provider", args.local_provider]
        elif args.runner == "codex-tower":
            cmd += [
                "-c", f'model_provider="{args.tower_provider_name}"',
                "-c", f'model_providers.{args.tower_provider_name}.name="Tower Ollama"',
                "-c", f'model_providers.{args.tower_provider_name}.base_url="{args.tower_ollama_base_url}"',
                "-c", f'model_providers.{args.tower_provider_name}.wire_api="responses"',
                "-c", f'model_providers.{args.tower_provider_name}.env_key="{args.tower_api_key_env}"',
            ]
        if args.model:
            cmd += ["-m", args.model]
        if args.full_auto:
            cmd.append("--dangerously-bypass-approvals-and-sandbox")
        else:
            # Codex's own sandbox is the gate: writes confined to the workspace.
            # Network enabled inside the sandbox so git push works.
            cmd += ["-s", "workspace-write",
                    "-c", "sandbox_workspace_write.network_access=true"]
        cmd.append(build_prompt(task))
    else:
        cmd = ["claude", "-p", build_prompt(task), "--model", args.model or "sonnet"]
        if args.full_auto:
            cmd.append("--dangerously-skip-permissions")
        elif args.allow_build:
            # File edits auto-approved + only build/vcs commands; everything else still denied.
            cmd += ["--permission-mode", "acceptEdits", "--allowedTools",
                    "Bash(git:*),Bash(./gradlew:*),Bash(gradle:*),Bash(java:*),Bash(mkdir:*),Bash(chmod:*)"]
        else:
            cmd += ["--permission-mode", "acceptEdits"]

    print(f"[dispatch] {task['id']} -> {task['owner']} "
          f"(runner {args.runner}, model {args.model or 'default'}), log: {log_path}")
    with open(log_path, "w") as log:
        log.write(f"# {task['id']} {task['feature']}\n# started {stamp}\n\n")
        log.flush()
        env = os.environ.copy()
        if args.ollama_host:
            env["OLLAMA_HOST"] = args.ollama_host
        if args.runner == "codex-tower":
            env.setdefault(args.tower_api_key_env, "ollama")
        try:
            proc = subprocess.run(cmd, cwd=REPO_ROOT, stdout=log, stderr=subprocess.STDOUT,
                                  timeout=args.task_timeout, env=env)
            code = proc.returncode
        except subprocess.TimeoutExpired:
            log.write(f"\n# DISPATCHER: killed after {args.task_timeout}s timeout\n")
            code = -1
    status = next((t["status"] for t in parse_queue() if t["id"] == task["id"]), "?")
    ok = status.lower() in DONE
    print(f"[result] {task['id']}: agent exit {code}, board status now '{status}' -> "
          f"{'SUCCESS' if ok else 'NOT COMPLETE'}")
    return ok


def main():
    ap = argparse.ArgumentParser(description="Assign board tasks to headless Claude Code agents.")
    ap.add_argument("--runner", choices=["claude", "codex", "codex-local", "codex-tower"], default="claude",
                    help="which CLI runs the agents: Claude Code, hosted Codex, local Codex, or tower-backed Codex")
    ap.add_argument("--model", default=None,
                    help="model for spawned agents (default: sonnet for claude, "
                         "your configured default for codex, qwen2.5-coder:3b recommended for codex-local, "
                         "qwen3:8b recommended for codex-tower)")
    ap.add_argument("--local-provider", choices=["ollama", "lmstudio"], default="ollama",
                    help="local OSS provider used only with --runner codex-local (default ollama)")
    ap.add_argument("--ollama-host", default=None,
                    help="Ollama endpoint for --runner codex-local, e.g. http://127.0.0.1:11434 or an SSH-forwarded tower port")
    ap.add_argument("--tower-ollama-base-url", default="http://127.0.0.1:11435/v1/",
                    help="OpenAI-compatible Ollama /v1 endpoint for --runner codex-tower")
    ap.add_argument("--tower-provider-name", default="tower-ollama",
                    help="custom Codex provider name for --runner codex-tower")
    ap.add_argument("--tower-api-key-env", default="TOWER_OLLAMA_API_KEY",
                    help="env var Codex should read as the dummy API key for tower Ollama")
    ap.add_argument("--max-tasks", type=int, default=3, help="stop after this many attempts (default 3)")
    ap.add_argument("--task-timeout", type=int, default=2400, help="seconds per task (default 2400)")
    ap.add_argument("--dry-run", action="store_true", help="print the plan, run nothing")
    ap.add_argument("--keep-going", action="store_true", help="continue past a failed task")
    ap.add_argument("--allow-build", action="store_true",
                    help="agents may run git/gradle/java commands (scoped allowlist); "
                         "everything else still denied")
    ap.add_argument("--full-auto", action="store_true",
                    help="spawned agents skip ALL permission prompts — requires explicit user sign-off")
    args = ap.parse_args()

    branch = subprocess.run(["git", "-C", str(REPO_ROOT), "rev-parse", "--abbrev-ref", "HEAD"],
                            capture_output=True, text=True).stdout.strip()
    if branch == "main":
        sys.exit("Checkout is on protected 'main'. Switch to develop first: git switch develop")

    attempted = 0
    results = []
    while attempted < args.max_tasks:
        queue = eligible(parse_queue())
        queue = [t for t in queue if t["id"] not in {r[0] for r in results}]
        if not queue:
            print("[done] no eligible tasks left (blocked, waiting on user, or all complete).")
            break
        task = queue[0]
        if args.dry_run:
            print(f"[plan] would dispatch {task['id']} ({task['feature']}) to {task['owner']} "
                  f"[{task['priority']}], deps: {task['deps']}")
            results.append((task["id"], "dry-run"))
            attempted += 1
            continue
        ok = run_task(task, args)
        results.append((task["id"], "ok" if ok else "failed"))
        attempted += 1
        if not ok and not args.keep_going:
            print("[stop] task did not reach Complete; stopping (use --keep-going to continue).")
            break

    print("\n=== dispatch summary ===")
    for tid, res in results:
        print(f"  {tid}: {res}")


if __name__ == "__main__":
    main()
