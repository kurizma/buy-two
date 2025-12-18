#!/usr/bin/env bash
set -e

PORTS="8761 8080 8456 8567 8678 4200"

for p in $PORTS; do
  pids=$(lsof -ti :$p || true)
  if [ -n "$pids" ]; then
    for pid in $pids; do
      echo "Sending SIGTERM to PID $pid on port $p"
      kill "$pid" || true
    done
  fi
done

# Wait a bit for graceful shutdown
sleep 5

# Force kill anything still left on those ports
for p in $PORTS; do
  pids=$(lsof -ti :$p || true)
  if [ -n "$pids" ]; then
    for pid in $pids; do
      echo "PID $pid on port $p still alive, sending SIGKILL"
      kill -9 "$pid" || true
    done
  fi
done
