#!/bin/bash

# Script to generate self-signed SSL certificates for development
# This allows developers to run the full stack locally with HTTPS enabled

set -e

echo "Generating SSL certificates..."

# Default password for local development
PASSWORD="changeit" 
VALIDITY_DAYS=365

# 1. API Gateway (Spring Boot - PKCS12)
echo "----------------------------------------------------------------"
echo "Generating certificate for API Gateway (backend/gateway-service)..."
mkdir -p backend/gateway-service/src/main/resources

KEYSTORE_PATH="backend/gateway-service/src/main/resources/gateway-keystore.p12"

# Remove existing if present to avoid "alias exists" errors
rm -f "$KEYSTORE_PATH"

keytool -genkeypair -alias gateway \
  -keyalg RSA -keysize 2048 \
  -storetype PKCS12 \
  -keystore "$KEYSTORE_PATH" \
  -validity $VALIDITY_DAYS \
  -storepass "$PASSWORD" \
  -dname "CN=localhost, OU=Engineering, O=BuyTwo, L=Local, S=Dev, C=US"

echo "✅ Created: $KEYSTORE_PATH"

# 2. Frontend (Angular - PEM)
echo "----------------------------------------------------------------"
echo "Generating certificate for Frontend..."
mkdir -p frontend/ssl

openssl req -x509 -newkey rsa:2048 \
  -keyout frontend/ssl/localhost-key.pem \
  -out frontend/ssl/localhost-cert.pem \
  -days $VALIDITY_DAYS \
  -nodes \
  -subj "/CN=localhost/O=BuyTwo/OU=Frontend" \
  2>/dev/null

echo "✅ Created: frontend/ssl/localhost-key.pem & localhost-cert.pem"

echo "----------------------------------------------------------------"
echo "🎉 Setup Complete!"
echo "Gateway Keystore Password: '$PASSWORD'"
echo "NOTE: These are self-signed. Your browser will warn you."
echo "      For PRODUCTION, Jenkins injects the real trusted keys."
