#!/bin/bash
OUTPUT="buy-two-complete-project-dump.txt"
echo "Generating PERFECT dump for Buy-Two project..."

> $OUTPUT

# HEADER
cat >> $OUTPUT << 'EOF'
# 🚀 Buy-Two Complete Project Dump
Generated: $(date)
Project Structure: Microservices (backend) + Angular (frontend) + API Tests
EOF

# 1. FULL PROJECT STRUCTURE
echo "## 📁 COMPLETE PROJECT STRUCTURE" >> $OUTPUT
echo '```' >> $OUTPUT
tree -I 'node_modules|target|.git|build|.idea|*.log|notebook_files' -a >> $OUTPUT 2>/dev/null || echo "Use 'tree' or 'dir /s'" >> $OUTPUT
echo '```' >> $OUTPUT
echo "" >> $OUTPUT

# 2. DOCKER & START SCRIPTS
echo "## 🐳 DOCKER & START SCRIPTS" >> $OUTPUT
echo "" >> $OUTPUT
for file in docker-compose.yml *.sh .env; do
    if [ -f "$file" ]; then
        echo "### $file" >> $OUTPUT
        echo '```' >> $OUTPUT
        cat "$file" >> $OUTPUT
        echo '```' >> $OUTPUT
        echo "" >> $OUTPUT
    fi
done

# 3. ALL MICROSERVICES (backend/*)
echo "## 🏗️ MICROSERVICES (6 Services)" >> $OUTPUT
echo "" >> $OUTPUT

SERVICES=("discovery-service" "gateway-service" "media-service" "order-service" "product-service" "user-service")

for SERVICE in "${SERVICES[@]}"; do
    if [ -d "backend/$SERVICE" ]; then
        echo "### 🟢 $SERVICE" >> $OUTPUT
        echo "" >> $OUTPUT
        
        # pom.xml
        if [ -f "backend/$SERVICE/pom.xml" ]; then
            echo "#### pom.xml" >> $OUTPUT
            echo '```xml' >> $OUTPUT
            cat "backend/$SERVICE/pom.xml" >> $OUTPUT
            echo '```' >> $OUTPUT
            echo "" >> $OUTPUT
        fi
        
        # application.yml
        for conf in "backend/$SERVICE/src/main/resources/application*.yml" "backend/$SERVICE/src/main/resources/application*.yaml" "backend/$SERVICE/src/main/resources/application*.properties"; do
            if [ -f "$conf" ]; then
                echo "#### $conf" >> $OUTPUT
                echo '```yaml' >> $OUTPUT
                cat "$conf" >> $OUTPUT
                echo '```' >> $OUTPUT
                echo "" >> $OUTPUT
            fi
        done
        
        # Controllers
        echo "#### Controllers" >> $OUTPUT
        find "backend/$SERVICE/src/main/java" -name "*Controller.java" -path "*/com/buyone/*" | sort | while read file; do
            echo "##### $(basename "$file")" >> $OUTPUT
            echo '```java' >> $OUTPUT
            cat "$file" >> $OUTPUT
            echo '```' >> $OUTPUT
            echo "" >> $OUTPUT
        done
        
        # Services
        echo "#### Services" >> $OUTPUT
        find "backend/$SERVICE/src/main/java" -name "*Service*.java" -path "*/com/buyone/*" | sort | while read file; do
            echo "##### $(basename "$file")" >> $OUTPUT
            echo '```java' >> $OUTPUT
            cat "$file" >> $OUTPUT
            echo '```' >> $OUTPUT
            echo "" >> $OUTPUT
        done
        
        # Repositories
        echo "#### Repositories" >> $OUTPUT
        find "backend/$SERVICE/src/main/java" -name "*Repository.java" -path "*/com/buyone/*" | sort | while read file; do
            echo "##### $(basename "$file")" >> $OUTPUT
            echo '```java' >> $OUTPUT
            cat "$file" >> $OUTPUT
            echo '```' >> $OUTPUT
            echo "" >> $OUTPUT
        done
        
        # Models/DTOs (top 10)
        echo "#### Models/DTOs (Top 10)" >> $OUTPUT
        find "backend/$SERVICE/src/main/java" \( -path "*/model/*" -o -path "*/dto/*" -o -path "*/entity/*" \) -name "*.java" | sort | head -10 | while read file; do
            echo "##### $(basename "$file")" >> $OUTPUT
            echo '```java' >> $OUTPUT
            cat "$file" >> $OUTPUT
            echo '```' >> $OUTPUT
            echo "" >> $OUTPUT
        done
        
        echo "---" >> $OUTPUT
        echo "" >> $OUTPUT
    fi
done

# 4. FRONTEND (Angular)
echo "## ⚛️ FRONTEND (Angular)" >> $OUTPUT
echo "" >> $OUTPUT
echo "### package.json" >> $OUTPUT
echo '```json' >> $OUTPUT
cat frontend/package.json >> $OUTPUT 2>/dev/null || echo "No frontend/package.json" >> $OUTPUT
echo '```' >> $OUTPUT

echo "" >> $OUTPUT
echo "### angular.json (key parts)" >> $OUTPUT
grep -A 20 -B 5 '"projects"' frontend/angular.json >> $OUTPUT 2>/dev/null || echo "No angular.json details" >> $OUTPUT

# 5. API TESTS (Bruno)
echo "## 🧪 API TESTS (Bruno)" >> $OUTPUT
echo "" >> $OUTPUT
if [ -f "bruno/buy-two-api/bruno.json" ]; then
    echo "### bruno.json" >> $OUTPUT
    echo '```json' >> $OUTPUT
    head -100 "bruno/buy-two-api/bruno.json" >> $OUTPUT  # First 100 lines
    echo '```' >> $OUTPUT
fi

# 6. INFRA & DOCS
echo "## 🏗️ INFRA & DOCUMENTATION" >> $OUTPUT
echo "" >> $OUTPUT
for file in README.md B02-Task.md POSTMAN_TESTING_GUIDE.md docker-compose.yml Jenkinsfile; do
    if [ -f "$file" ]; then
        echo "### $file" >> $OUTPUT
        echo '```' >> $OUTPUT
        head -200 "$file" >> $OUTPUT  # First 200 lines
        echo '```' >> $OUTPUT
        echo "" >> $OUTPUT
    fi
done

echo "## ✅ COMPLETE DUMP READY" >> $OUTPUT
echo "Upload to NotebookLM/Perplexity for analysis" >> $OUTPUT

echo "✅ Generated: $OUTPUT ($(du -h $OUTPUT | cut -f1))"
