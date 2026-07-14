#!/usr/bin/env python3
"""Local live dashboard server for Apple Icon automation.

Serves dashboard/ static files from this checkout and exposes /api/status.
The API reads the target repo on disk, so it can show local feature branches,
dirty files, running worker processes, and dispatch log tails before anything
is merged to develop.
"""

from __future__ import annotations

import argparse
import json
import os
import re
import subprocess
import time
from datetime import datetime, timezone
from http import HTTPStatus
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from typing import Any
from urllib.parse import urlparse


SERVER_ROOT = Path(__file__).resolve().parent.parent
TASK_RE = re.compile(r"TASK-\d+")
DONE = {"complete", "shipped"}
CLAIMABLE = {"backlog", "ready"}
PRIORITY_ORDER = {"high": 0, "medium": 1, "low": 2}


def run_command(args: list[str], cwd: Path, timeout: int = 5) -> dict[str, Any]:
    try:
        proc = subprocess.run(
            args,
            cwd=cwd,
            text=True,
            capture_output=True,
            timeout=timeout,
            check=False,
        )
    except Exception as exc:  # noqa: BLE001 - API should report errors, not crash.
        return {"ok": False, "code": None, "stdout": "", "stderr": str(exc)}
    return {
        "ok": proc.returncode == 0,
        "code": proc.returncode,
        "stdout": proc.stdout,
        "stderr": proc.stderr,
    }


def git(repo: Path, args: list[str], timeout: int = 5) -> str:
    result = run_command(["git", *args], cwd=repo, timeout=timeout)
    return result["stdout"].strip() if result["ok"] else ""


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8")
    except FileNotFoundError:
        return ""


def section_lines(markdown: str, heading: str) -> list[str]:
    match = re.search(rf"^##\s+{re.escape(heading)}\s*$", markdown, re.M)
    if not match:
        return []
    rest = markdown[match.end() :]
    next_heading = re.search(r"^##\s+", rest, re.M)
    body = rest[: next_heading.start()] if next_heading else rest
    return body.splitlines()


def parse_table(lines: list[str]) -> list[dict[str, str]]:
    rows: list[list[str]] = []
    for line in lines:
        value = line.strip()
        if not value.startswith("|"):
            continue
        cells = [cell.strip() for cell in value.split("|")[1:-1]]
        if not cells or all(re.fullmatch(r":?-{3,}:?", cell or "") for cell in cells):
            continue
        rows.append(cells)
    if len(rows) < 2:
        return []
    header = rows[0]
    return [dict(zip(header, row)) for row in rows[1:]]


def parse_board(repo: Path) -> dict[str, Any]:
    markdown = read_text(repo / "project-docs" / "agent-workspace.md")
    agents = parse_table(section_lines(markdown, "Active Agents"))
    tasks = parse_table(section_lines(markdown, "Task Queue"))
    blockers = parse_table(section_lines(markdown, "Current Blockers"))
    completed = {
        task.get("Task ID", "")
        for task in tasks
        if task.get("Status", "").strip().lower() in DONE
    }
    next_tasks: list[dict[str, str]] = []
    for task in tasks:
        status = task.get("Status", "").strip().lower()
        deps = task.get("Depends On", "")
        dep_ids = TASK_RE.findall(deps)
        if status in CLAIMABLE and "user input" not in deps.lower() and all(dep in completed for dep in dep_ids):
            next_tasks.append(task)
    next_tasks.sort(
        key=lambda task: (
            PRIORITY_ORDER.get(task.get("Priority", "").lower(), 9),
            task.get("Task ID", ""),
        ),
    )
    return {
        "agents": agents,
        "tasks": tasks,
        "blockers": blockers,
        "next": next_tasks[:6],
    }


def ps_entries() -> list[dict[str, str]]:
    result = run_command(["ps", "-eo", "pid,ppid,stat,args"], cwd=SERVER_ROOT)
    if not result["ok"]:
        return []
    entries: list[dict[str, str]] = []
    needles = (
        "agent_dispatcher.py",
        "codex exec",
        "claude -p",
        "auto_git_push.py",
        "dashboard_server.py",
        "http.server 8721",
        "gradle-wrapper.jar",
    )
    for line in result["stdout"].splitlines()[1:]:
        if not any(needle in line for needle in needles):
            continue
        parts = line.strip().split(None, 3)
        if len(parts) < 4:
            continue
        pid, ppid, stat, command = parts
        task = next(iter(TASK_RE.findall(command)), "")
        if "agent_dispatcher.py" in command:
            role = "dispatcher"
        elif "codex exec" in command:
            role = "codex worker"
        elif "claude -p" in command:
            role = "claude worker"
        elif "auto_git_push.py" in command:
            role = "auto push watcher"
        elif "dashboard_server.py" in command:
            role = "dashboard api"
        elif "gradle-wrapper.jar" in command:
            role = "gradle"
        else:
            role = "legacy dashboard"
        entries.append(
            {
                "pid": pid,
                "ppid": ppid,
                "stat": stat,
                "role": role,
                "task": task,
                "command": command,
            },
        )
    return entries


def tail_lines(path: Path, limit: int = 80) -> list[str]:
    try:
        with path.open("rb") as handle:
            handle.seek(0, os.SEEK_END)
            size = handle.tell()
            block = min(size, 96_000)
            handle.seek(-block, os.SEEK_END)
            text = handle.read().decode("utf-8", errors="replace")
    except OSError:
        return []
    return text.splitlines()[-limit:]


def meaningful_line(lines: list[str]) -> str:
    noisy = (
        "WARN codex_core_skills",
        "WARN codex_core_plugins",
        "ERROR codex_models_manager",
        "failed to renew cache TTL",
    )
    ansi = re.compile(r"\x1b\[[0-9;]*m")
    for line in reversed(lines):
        clean = ansi.sub("", line).strip()
        if clean and not any(item in clean for item in noisy):
            return clean[:320]
    return ""


def dispatch_logs(repo: Path) -> list[dict[str, Any]]:
    log_dir = repo / "dispatch-logs"
    logs = sorted(log_dir.glob("TASK-*.log"), key=lambda path: path.stat().st_mtime, reverse=True)
    out: list[dict[str, Any]] = []
    for path in logs[:8]:
        lines = tail_lines(path)
        task = next(iter(TASK_RE.findall(path.name)), "")
        out.append(
            {
                "task": task,
                "name": path.name,
                "path": str(path),
                "modifiedAt": datetime.fromtimestamp(path.stat().st_mtime, timezone.utc).isoformat(),
                "size": path.stat().st_size,
                "summary": meaningful_line(lines),
                "tail": lines[-60:],
            },
        )
    watcher = repo / "dispatch-logs" / "watcher.log"
    return out + (
        [
            {
                "task": "watcher",
                "name": watcher.name,
                "path": str(watcher),
                "modifiedAt": datetime.fromtimestamp(watcher.stat().st_mtime, timezone.utc).isoformat(),
                "size": watcher.stat().st_size,
                "summary": meaningful_line(tail_lines(watcher, 40)),
                "tail": tail_lines(watcher, 40),
            },
        ]
        if watcher.exists()
        else []
    )


def status_payload(repo: Path) -> dict[str, Any]:
    status_lines = git(repo, ["status", "--short", "--branch"]).splitlines()
    dirty = [line for line in status_lines if not line.startswith("##")]
    processes = ps_entries()
    worker_tasks = sorted({entry["task"] for entry in processes if entry["task"]})
    return {
        "generatedAt": datetime.now(timezone.utc).isoformat(),
        "repo": str(repo),
        "branch": git(repo, ["rev-parse", "--abbrev-ref", "HEAD"]),
        "status": status_lines,
        "dirtyCount": len(dirty),
        "dirty": dirty[:80],
        "head": git(repo, ["rev-parse", "--short", "HEAD"]),
        "remote": git(repo, ["remote", "-v"]).splitlines(),
        "recentCommits": git(repo, ["log", "--oneline", "--decorate", "--max-count=12"]).splitlines(),
        "board": parse_board(repo),
        "processes": processes,
        "workerTasks": worker_tasks,
        "logs": dispatch_logs(repo),
    }


class DashboardHandler(SimpleHTTPRequestHandler):
    repo: Path

    def __init__(self, *args: Any, directory: str | None = None, **kwargs: Any) -> None:
        super().__init__(*args, directory=str(SERVER_ROOT), **kwargs)

    def do_GET(self) -> None:  # noqa: N802 - stdlib hook name.
        parsed = urlparse(self.path)
        if parsed.path == "/api/status":
            self.send_json(status_payload(self.repo))
            return
        if parsed.path in {"", "/"}:
            self.send_response(HTTPStatus.FOUND)
            self.send_header("Location", "/dashboard/")
            self.end_headers()
            return
        super().do_GET()

    def send_json(self, payload: dict[str, Any]) -> None:
        body = json.dumps(payload, indent=2).encode("utf-8")
        self.send_response(HTTPStatus.OK)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.send_header("Cache-Control", "no-store")
        self.end_headers()
        self.wfile.write(body)

    def log_message(self, fmt: str, *args: Any) -> None:
        stamp = time.strftime("%Y-%m-%d %H:%M:%S")
        print(f"[{stamp}] {self.address_string()} {fmt % args}")


def main() -> None:
    parser = argparse.ArgumentParser(description="Serve the local Apple Icon automation dashboard.")
    parser.add_argument("--repo", type=Path, default=SERVER_ROOT, help="repo whose live state should be shown")
    parser.add_argument("--bind", default="127.0.0.1")
    parser.add_argument("--port", type=int, default=8721)
    args = parser.parse_args()

    DashboardHandler.repo = args.repo.resolve()
    server = ThreadingHTTPServer((args.bind, args.port), DashboardHandler)
    print(f"[dashboard] http://{args.bind}:{args.port}/dashboard/")
    print(f"[dashboard] reading repo: {DashboardHandler.repo}")
    server.serve_forever()


if __name__ == "__main__":
    main()
