#!/usr/bin/env bash
set -e
trap "echo 'Stopping all services...'; kill 0" EXIT

wait_for_http() {
  local url=$1
  local name=$2
  echo "Waiting for $name at $url ..."
  for i in {1..60}; do
    if curl -k -sSf "$url" >/dev/null 2>&1; then
      echo "$name is up."
      return 0
    fi
    sleep 1
  done
  echo "ERROR: $name did not start in time."
  return 1
}

start_service_script() {
  local script_path=$1
  echo "Starting via $script_path ..."
  "$script_path" &
}

cd backend

echo "============================"
# 1. discovery-service :8761
start_service_script "./discovery-service/start.sh"

wait_for_http "http://localhost:8761/eureka/apps" "discovery-service" || exit 1

# 2. gateway-service :8080
start_service_script "./gateway-service/start.sh"
wait_for_http "https://localhost:8080/actuator/health" "gateway-service" || exit 1

# 3. user-service :8456
start_service_script "./user-service/start.sh"
wait_for_http "http://localhost:8456/actuator/health" "user-service" || exit 1

# 4. product-service :8567
start_service_script "./product-service/start.sh"
wait_for_http "http://localhost:8567/actuator/health" "product-service" || exit 1

# 5. media-service :8678
start_service_script "./media-service/start.sh"
wait_for_http "http://localhost:8678/actuator/health" "media-service" || \
  echo "media-service HTTP not healthy yet (maybe waiting for Kafka)."

cd ..

wait

# chmod +x start-backend.sh && ./start-backend.sh