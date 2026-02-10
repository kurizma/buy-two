#!/bin/bash

# Frontend Coverage Testing Script
# Runs Angular unit tests with code coverage

set -e

FRONTEND_DIR="$(dirname "$0")/frontend"

echo "========================================"
echo "  Frontend Coverage Testing"
echo "========================================"

cd "$FRONTEND_DIR"

echo ""
echo "Running Angular tests with coverage..."
echo ""

npx ng test --no-watch --browsers=ChromeHeadless --code-coverage

echo ""
echo "========================================"
echo "  Coverage report generated at:"
echo "  $FRONTEND_DIR/coverage/index.html"
echo "========================================"
