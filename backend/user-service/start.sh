#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"

PROJECT_ROOT="$(dirname "$(dirname "$(pwd)")")"
set -a
[ -f "$PROJECT_ROOT/.env" ] && source "$PROJECT_ROOT/.env"
set +a

echo "[user-service] Building & starting..."
./mvnw clean spring-boot:run
