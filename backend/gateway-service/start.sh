#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"

# Load .env from parent directory (backend/.env)
PROJECT_ROOT="$(dirname "$(dirname "$(pwd)")")"
set -a
[ -f "$PROJECT_ROOT/.env" ] && source "$PROJECT_ROOT/.env"
set +a

echo "[gateway-service] Building & starting..."
./mvnw clean spring-boot:run
