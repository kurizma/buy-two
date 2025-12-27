pipeline {
    agent any

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

        stage('Smoke') {
            steps {
                echo 'Jenkins pipeline skeleton works'
            }
        }

        stage('Backend - discovery-service') {
            steps {
                dir('backend/discovery-service') {
                    sh './mvnw clean verify'
                }
            }
        }

        stage('Backend - gateway-service') {
            steps {
                dir('backend/gateway-service') {
                    sh './mvnw clean verify'
                }
            }
        }

        stage('Backend - user-service') {
            steps {
                dir('backend/user-service') {
                    sh './mvnw clean verify'
                }
            }
        }
    }
}
