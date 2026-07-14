#!/usr/bin/env python3
"""Auto git commit + push watcher.

Watches the repo for changes and automatically commits and pushes them.

Usage:
    python3 scripts/auto_git_push.py                # watch, check every 5s
    python3 scripts/auto_git_push.py --interval 30  # check every 30s
    python3 scripts/auto_git_push.py --once         # single check, then exit
    python3 scripts/auto_git_push.py --no-push      # commit locally only

Setup (once):
    git init
    git remote add origin <your-github-repo-url>
    git push -u origin main

Behavior:
- A change is only committed after the working tree is stable for two
  consecutive checks (so half-written files are not committed).
- If no remote named "origin" exists, commits are made locally and pushing
  is skipped with a warning.
- Stop with Ctrl+C.
"""

import argparse
import datetime
import functools
import subprocess
import sys
import time
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
print = functools.partial(print, flush=True)


def git(*args, check=True):
    """Run a git command in the repo, return stdout as text."""
    result = subprocess.run(
        ["git", "-C", str(REPO_ROOT), *args],
        capture_output=True,
        text=True,
    )
    if check and result.returncode != 0:
        raise RuntimeError(f"git {' '.join(args)} failed:\n{result.stderr.strip()}")
    return result.stdout.strip()


def is_git_repo():
    try:
        git("rev-parse", "--is-inside-work-tree")
        return True
    except RuntimeError:
        return False


def has_origin():
    return "origin" in git("remote").splitlines()


def current_branch():
    return git("rev-parse", "--abbrev-ref", "HEAD")


def working_tree_status():
    return git("status", "--porcelain")


def commit_and_push(push=True):
    status = working_tree_status()
    if not status:
        return False

    changed = len(status.splitlines())
    stamp = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    message = f"auto: {stamp} ({changed} file{'s' if changed != 1 else ''} changed)"

    git("add", "-A")
    git("commit", "-m", message)
    print(f"[commit] {message}")

    if not push:
        return True
    if not has_origin():
        print("[warn] no 'origin' remote configured - commit made locally, push skipped.")
        print("       run: git remote add origin <your-github-repo-url>")
        return True

    branch = current_branch()
    try:
        git("push", "-u", "origin", branch)
        print(f"[push] pushed to origin/{branch}")
    except RuntimeError as err:
        print(f"[error] push failed (commit is safe locally):\n{err}")
    return True


def git_operation_in_progress():
    """True while a merge/rebase/another git process is mid-flight."""
    git_dir = REPO_ROOT / ".git"
    return any((git_dir / f).exists()
               for f in ("MERGE_HEAD", "REBASE_HEAD", "CHERRY_PICK_HEAD", "index.lock"))


def watch(interval, push=True):
    print(f"Watching {REPO_ROOT} every {interval}s. Ctrl+C to stop.")
    previous = None
    while True:
        try:
            if current_branch() == "main":
                print("[skip] checkout is on protected 'main'; waiting...")
                previous = None
            elif git_operation_in_progress():
                print("[skip] another git operation is in progress; waiting...")
                previous = None
            else:
                status = working_tree_status()
                if status and status == previous:
                    # Tree changed and has been stable for one full interval.
                    commit_and_push(push=push)
                    previous = None
                elif status:
                    print(f"[change] {len(status.splitlines())} file(s) changed, "
                          "waiting one interval for writes to settle...")
                    previous = status
                else:
                    previous = None
        except KeyboardInterrupt:
            print("\nStopped.")
            return
        except Exception as err:  # never let one bad cycle kill the watcher
            print(f"[warn] cycle failed, will retry: {err}")
            previous = None
        try:
            time.sleep(interval)
        except KeyboardInterrupt:
            print("\nStopped.")
            return


def main():
    parser = argparse.ArgumentParser(description="Auto git commit + push watcher.")
    parser.add_argument("--interval", type=int, default=5,
                        help="seconds between checks (default 5)")
    parser.add_argument("--once", action="store_true",
                        help="check once, commit+push if dirty, then exit")
    parser.add_argument("--no-push", action="store_true",
                        help="commit locally but never push")
    args = parser.parse_args()

    if not is_git_repo():
        print(f"Not a git repository: {REPO_ROOT}")
        print("Run these first:")
        print("  git init")
        print("  git remote add origin <your-github-repo-url>")
        sys.exit(1)

    # Project rule: main is protected. Never auto-commit or auto-push to it.
    # See project-docs/branching-strategy.md.
    if current_branch() == "main":
        print("Refusing to run on 'main' (protected branch).")
        print("Switch to the integration branch first:  git switch develop")
        sys.exit(1)

    if args.once:
        if not commit_and_push(push=not args.no_push):
            print("Nothing to commit.")
        return

    watch(args.interval, push=not args.no_push)


if __name__ == "__main__":
    main()
