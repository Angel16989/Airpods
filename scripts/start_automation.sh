#!/usr/bin/env bash
# Start the full AFK automation stack for Apple Icon:
#   1. dashboard server  -> http://127.0.0.1:8721/dashboard/
#   2. auto commit+push watcher
#   3. task dispatcher (foreground, so you see progress)
#
# Usage:
#   bash scripts/start_automation.sh                     # Claude agents, 4 tasks
#   bash scripts/start_automation.sh --max-tasks 6
#   bash scripts/start_automation.sh --runner codex      # Codex agents instead
#
# Stop everything:
#   pkill -f agent_dispatcher.py; pkill -f auto_git_push.py; pkill -f "http.server 8721"

set -u
cd "$(dirname "$0")/.." || exit 1
mkdir -p dispatch-logs

if ! pgrep -f "dashboard_server.py.*8721" >/dev/null; then
  pkill -f "http.server 8721" >/dev/null 2>&1 || true
  nohup python3 scripts/dashboard_server.py --repo "$PWD" --bind 127.0.0.1 --port 8721 >> dispatch-logs/dashboard.log 2>&1 &
  echo "[start] dashboard: http://127.0.0.1:8721/dashboard/"
else
  echo "[ok] dashboard already running: http://127.0.0.1:8721/dashboard/"
fi

if ! pgrep -f "auto_git_push.py" >/dev/null; then
  nohup python3 scripts/auto_git_push.py --interval 60 >> dispatch-logs/watcher.log 2>&1 &
  echo "[start] auto-push watcher (log: dispatch-logs/watcher.log)"
else
  echo "[ok] auto-push watcher already running"
fi

if pgrep -f "agent_dispatcher.py" >/dev/null; then
  echo "[ok] dispatcher already running — not starting a second one."
  exit 0
fi

echo "[start] dispatcher..."
exec python3 scripts/agent_dispatcher.py --full-auto --keep-going --max-tasks 4 "$@"
