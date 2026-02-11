#!/bin/bash
# ============================================================
# check-all-coverage.sh
# Run unit tests + JaCoCo coverage report for all backend services
# Usage: ./check-all-coverage.sh
# Reports: backend/<service>/target/site/jacoco/index.html
# ============================================================

set -e

SERVICES=("discovery-service" "gateway-service" "user-service" "product-service" "media-service" "order-service")
PASS=0
FAIL=0
REPORTS=()

for SERVICE in "${SERVICES[@]}"; do
  echo ""
  echo "============================================"
  echo "📊 Running Coverage for: $SERVICE"
  echo "============================================"

  cd "backend/$SERVICE"

  if ./mvnw clean test jacoco:report -q; then
    if [ -f "target/site/jacoco/index.html" ]; then
      echo "✅ $SERVICE — Report: backend/$SERVICE/target/site/jacoco/index.html"
      REPORTS+=("backend/$SERVICE/target/site/jacoco/index.html")
      PASS=$((PASS + 1))
    else
      echo "⚠️  $SERVICE — Tests passed but no JaCoCo report generated"
      FAIL=$((FAIL + 1))
    fi
  else
    echo "❌ $SERVICE — Tests or coverage FAILED"
    FAIL=$((FAIL + 1))
  fi

  cd ../..
done

echo ""
echo "============================================"
echo "📋 COVERAGE SUMMARY"
echo "============================================"
echo "✅ Passed: $PASS / ${#SERVICES[@]}"
echo "❌ Failed: $FAIL / ${#SERVICES[@]}"
echo ""

if [ ${#REPORTS[@]} -gt 0 ]; then
  echo "📂 Generated Reports:"
  for REPORT in "${REPORTS[@]}"; do
    echo "   → $REPORT"
  done
  echo ""
  echo "💡 To open a report in your browser:"
  echo "   open backend/<service>/target/site/jacoco/index.html"
fi

if [ $FAIL -gt 0 ]; then
  exit 1
fi
