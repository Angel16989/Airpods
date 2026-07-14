#!/usr/bin/env bash
# Start the full AFK automation stack for Apple Icon:
#   1. dashboard server  -> http://127.0.0.1:8721/dashboard/
#   2. auto commit+push watcher (default every 5s)
#   3. task dispatcher (foreground, so you see progress)
#
# Usage:
#   bash scripts/start_automation.sh                     # Claude agents, 4 tasks
#   bash scripts/start_automation.sh --max-tasks 6
#   bash scripts/start_automation.sh --runner codex      # Codex agents instead
#   bash scripts/start_automation.sh --runner codex-local --model qwen2.5-coder:3b
#   bash scripts/start_automation.sh --runner codex-local --model qwen2.5-coder:3b --ollama-host http://127.0.0.1:11434
#   bash scripts/start_automation.sh --runner codex-tower --model qwen3:8b
#
# Stop everything:
#   pkill -f agent_dispatcher.py; pkill -f auto_git_push.py; pkill -f "dashboard_server.py.*8721"

set -u
cd "$(dirname "$0")/.." || exit 1
mkdir -p dispatch-logs

runner=""
args=("$@")
tower_base_arg_present=false
extra_args=()
for ((i = 0; i < ${#args[@]}; i++)); do
  case "${args[$i]}" in
    --runner)
      if [[ $((i + 1)) -lt ${#args[@]} ]]; then
        runner="${args[$((i + 1))]}"
      fi
      ;;
    --runner=*)
      runner="${args[$i]#--runner=}"
      ;;
    --tower-ollama-base-url|--tower-ollama-base-url=*)
      tower_base_arg_present=true
      ;;
  esac
done

if [[ "$runner" == "codex-tower" ]]; then
  : "${TOWER_OLLAMA_SSH:=theimp@100.75.101.89}"
  : "${TOWER_OLLAMA_LOCAL_PORT:=11435}"
  : "${TOWER_OLLAMA_BASE_URL:=http://127.0.0.1:${TOWER_OLLAMA_LOCAL_PORT}/v1}"
  export TOWER_OLLAMA_API_KEY="${TOWER_OLLAMA_API_KEY:-ollama}"

  if ! curl -fsS "${TOWER_OLLAMA_BASE_URL%/}/models" >/dev/null 2>&1; then
    echo "[start] tower Ollama tunnel: ${TOWER_OLLAMA_SSH} -> 127.0.0.1:${TOWER_OLLAMA_LOCAL_PORT}"
    ssh -fN -o ExitOnForwardFailure=yes -o BatchMode=yes \
      -L "127.0.0.1:${TOWER_OLLAMA_LOCAL_PORT}:127.0.0.1:11434" \
      "$TOWER_OLLAMA_SSH"
  fi

  if ! curl -fsS "${TOWER_OLLAMA_BASE_URL%/}/models" >/dev/null 2>&1; then
    echo "[error] tower Ollama endpoint is not reachable at ${TOWER_OLLAMA_BASE_URL}" >&2
    exit 1
  fi

  if [[ "$tower_base_arg_present" == false ]]; then
    extra_args+=(--tower-ollama-base-url "${TOWER_OLLAMA_BASE_URL%/}/")
  fi
fi

if ! pgrep -f "dashboard_server.py.*8721" >/dev/null; then
  pkill -f "http.server 8721" >/dev/null 2>&1 || true
  nohup python3 scripts/dashboard_server.py --repo "$PWD" --bind 127.0.0.1 --port 8721 >> dispatch-logs/dashboard.log 2>&1 &
  echo "[start] dashboard: http://127.0.0.1:8721/dashboard/"
else
  echo "[ok] dashboard already running: http://127.0.0.1:8721/dashboard/"
fi

if ! pgrep -f "auto_git_push.py" >/dev/null; then
  : "${AUTO_PUSH_INTERVAL:=5}"
  setsid python3 -u scripts/auto_git_push.py --interval "$AUTO_PUSH_INTERVAL" >> dispatch-logs/watcher.log 2>&1 &
  echo "[start] auto-push watcher every ${AUTO_PUSH_INTERVAL}s (log: dispatch-logs/watcher.log)"
else
  echo "[ok] auto-push watcher already running"
fi

if pgrep -f "agent_dispatcher.py" >/dev/null; then
  echo "[ok] dispatcher already running — not starting a second one."
  exit 0
fi

echo "[start] dispatcher..."
exec python3 scripts/agent_dispatcher.py --full-auto --keep-going --max-tasks 4 "$@" "${extra_args[@]}"
