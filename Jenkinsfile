pipeline {
    agent any

    tools {
        maven 'maven-3.9'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/kurizma/java-jenk.git',
                        credentialsId: 'github-java-jenk'
                    ]]
                ])
            }
        }

        stage('Backend - discovery-service') {
            steps {
                dir('backend/discovery-service') {
                    sh 'mvn clean verify'
                }
            }
        }

        stage('Backend - gateway-service') {
            steps {
                dir('backend/gateway-service') {
                    sh 'mvn clean verify'
                }
            }
        }

        stage('Backend - user-service') {
            steps {
                dir('backend/user-service') {
                    sh 'mvn clean verify'
                }
            }
        }
       stage('Backend - product-service') {
            steps {
                dir('backend/product-service') {
                    sh 'mvn clean verify'
                }
            }
        }

        stage('Backend - media-service') {
            steps {
                dir('backend/media-service') {
                    sh 'mvn clean verify'
                }
            }
        }
        stage('Frontend') {
            steps {
                dir('frontend') {
                    nodejs(nodeJSInstallationName: 'node-20.19.6') { // exact NodeJS tool name
                        sh 'npm ci'
                        // sh 'npx ng test --no-watch --no-progress'
                        sh 'npx ng build --configuration production'
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    // Work from project root where docker-compose.yml is
                    dir("${env.WORKSPACE}") {
                        try {
                            // 1) Stop any existing stack for this project
                            sh 'docker compose -f docker-compose.yml down'

                            // 2) Rebuild images and start containers in background
                            sh 'docker compose -f docker-compose.yml up -d --build'
                        } catch (err) {
                            echo "Deploy failed: ${err}"
                            echo "Attempting basic rollback (restart previous containers without rebuild)..."

                            // Basic rollback: restart stack with existing local images
                            sh 'docker compose -f docker-compose.yml down || true'
                            sh 'docker compose -f docker-compose.yml up -d || true'

                            // Mark build as failed so the broken deployment is visible
                            error "Deployment failed and rollback was attempted."
                        }
                    }
                }
            }
        }
    }
}
