#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"

# Load .env from safe-zone root (2 levels up)
PROJECT_ROOT="$(dirname "$(dirname "$(pwd)")")"
set -a
[ -f "$PROJECT_ROOT/.env" ] && source "$PROJECT_ROOT/.env"
set +a 

echo "[media-service] Building & starting..."
./mvnw clean spring-boot:run
