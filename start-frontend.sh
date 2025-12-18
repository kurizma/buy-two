#!/usr/bin/env bash
set -e

cd frontend

echo "Frontend version: $(node -p "require('./package.json').version")"
echo "Node version: $(node -v)"
echo "npm version:  $(npm -v)"

npm ci
echo "Starting frontend..."
NG_CLI_ANALYTICS=false npm start

# chmod +x start-frontend.sh && ./start-frontend.sh
