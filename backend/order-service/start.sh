##!/usr/bin/env bash
set -e

cd "$(dirname "$0")"

PROJECT_ROOT="$(dirname "$(dirname "$(pwd)")")"
set -a
[ -f "$PROJECT_ROOT/.env" ] && source "$PROJECT_ROOT/.env"
set +a

export SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_SECRET

echo "[order-service] Building & starting..."
./mvnw clean spring-boot:run
