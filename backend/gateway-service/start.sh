#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"

echo "[gateway-service] Building & starting..."
./mvnw clean spring-boot:run
