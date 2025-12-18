#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"

echo "[product-service] Building & starting..."
./mvnw clean spring-boot:run
