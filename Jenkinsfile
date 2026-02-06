properties([
	throttleJobProperty(
		categories: ['buy-two-serial'],
		throttleEnabled: true,
		throttleOption: 'category'
	)
])

pipeline {
	agent any
	options {
		timestamps()
		timeout(time: 20, unit: 'MINUTES')
	}

	/**********************
	 * Global configuration
	 **********************/
	parameters {
		string(name: 'BRANCH', defaultValue: 'main', description: 'Branch to build')
	}

	environment {
		BRANCH = "${env.BRANCH_NAME ?: env.GIT_BRANCH ?: params.BRANCH ?: 'main'}"

		// Image versioning
		VERSION    = "v${env.BUILD_NUMBER}"
		STABLE_TAG = "stable"

		// Shared Maven repo on the agent disk
		MAVEN_REPO_LOCAL = "${env.JENKINS_HOME}/.m2/repository"
		// Optional: shared npm cache
		NPM_CONFIG_CACHE = "${env.JENKINS_HOME}/.npm"
		
		// Maven memory limits for 4GB VM
		MAVEN_OPTS = "-Xmx768m -Xms384m -XX:+UseG1GC -XX:MaxGCPauseMillis=100"
	}

	// Tools section commented out - using system Maven/Node instead
	// Uncomment and configure in Jenkins if you want managed tool versions
	// tools {
	// 	maven 'maven-3.9'
	// 	nodejs 'node-20.19.6'
	// }

	stages {

		/************
		 * Checkout *
		 ************/
		stage('Checkout') {
			steps {
				// Checkout is handled automatically by Jenkins Pipeline from SCM
				// This stage is here for clarity in the UI
				echo "Code already checked out by Jenkins"
				sh 'git rev-parse HEAD'
			}
		}

		/******************************
		 * Clean Maven lock files
		 ******************************/
		stage('Clean Maven Cache') {
			steps {
				script {
					// Remove any stale Maven lock files that might cause hangs
					sh """
						find ${MAVEN_REPO_LOCAL} -name '*.lock' -type f -delete 2>/dev/null || true
						find ${MAVEN_REPO_LOCAL} -name '_remote.repositories' -mtime +7 -type f -delete 2>/dev/null || true
					"""
				}
			}
		}

		/*************************
		 * Backend build (no tests)
		 *************************/
		stage('Backend Build - discovery-service') {
			steps {
				dir('backend/discovery-service') {
					sh "JAVA_TOOL_OPTIONS='-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400' mvn -Dmaven.repo.local=${MAVEN_REPO_LOCAL} clean package -DskipTests"
				}
			}
		}

		stage('Backend Build - gateway-service') {
			steps {
				dir('backend/gateway-service') {
					sh "JAVA_TOOL_OPTIONS='-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400' mvn -Dmaven.repo.local=${MAVEN_REPO_LOCAL} clean package -DskipTests"
				}
			}
		}

		stage('Backend Build - user-service') {
			steps {
				dir('backend/user-service') {
					sh "JAVA_TOOL_OPTIONS='-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400' mvn -Dmaven.repo.local=${MAVEN_REPO_LOCAL} clean package -DskipTests"
				}
			}
		}

		stage('Backend Build - product-service') {
			steps {
				dir('backend/product-service') {
					sh "JAVA_TOOL_OPTIONS='-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400' mvn -Dmaven.repo.local=${MAVEN_REPO_LOCAL} clean package -DskipTests"
				}
			}
		}

		stage('Backend Build - media-service') {
			steps {
				dir('backend/media-service') {
					sh "JAVA_TOOL_OPTIONS='-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400' mvn -Dmaven.repo.local=${MAVEN_REPO_LOCAL} clean package -DskipTests"
				}
			}
		}

		/***********************
		 * Backend unit tests  *
		 ***********************/
		stage('Backend Tests - discovery-service') {
			steps {
				dir('backend/discovery-service') {
					sh "JAVA_TOOL_OPTIONS='-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400' mvn -Dmaven.repo.local=${MAVEN_REPO_LOCAL} test"
				}
			}
		}

		stage('Backend Tests - gateway-service') {
			steps {
				dir('backend/gateway-service') {
					sh "JAVA_TOOL_OPTIONS='-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400' mvn -Dmaven.repo.local=${MAVEN_REPO_LOCAL} test"
				}
			}
		}

		stage('Backend Tests - user-service') {
			steps {
				dir('backend/user-service') {
					sh "JAVA_TOOL_OPTIONS='-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400' mvn -Dmaven.repo.local=${MAVEN_REPO_LOCAL} test"
				}
			}
		}

		stage('Backend Tests - product-service') {
			steps {
				dir('backend/product-service') {
					sh "JAVA_TOOL_OPTIONS='-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400' mvn -Dmaven.repo.local=${MAVEN_REPO_LOCAL} test"
				}
			}
		}

		stage('Backend Tests - media-service') {
			steps {
				dir('backend/media-service') {
					sh "JAVA_TOOL_OPTIONS='-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400' mvn -Dmaven.repo.local=${MAVEN_REPO_LOCAL} test"
				}
			}
		}

		/************
		 * Frontend *
		 ************/
		stage('Frontend - Tests Included') {
			steps {
				dir('frontend') {
					// nodejs(nodeJSInstallationName: 'node-20.19.6')
					sh 'JAVA_TOOL_OPTIONS=\'-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400\' npm ci'
					sh 'JAVA_TOOL_OPTIONS=\'-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400\' npm test -- --watch=false --browsers=ChromeHeadlessNoSandbox --no-progress'
					sh 'ls -la test-results/junit/ || echo "No test-results dir"'
					sh 'JAVA_TOOL_OPTIONS=\'-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400\' npx ng build --configuration production'
				}
			}
		}

		/************
		 * Test Failure Handling → Early Slack → Skip Sonar/deploy → Post FAILURE *
		 ************/
		stage('Test Summary') {
			steps {
				script {
					def testFailed = false
					def cleanBranch = "${BRANCH ?: GIT_BRANCH ?: 'main'}".replaceAll(/^origin\//, '')
					try {
						sh 'find . -name "*.xml" -path "*/surefire-reports/*.xml" | head -1 && echo "Tests passed" || testFailed = true'
					} catch (e) {
						testFailed = true
					}
					if (testFailed) {
						withCredentials([string(credentialsId: 'slack-webhook', variable: 'SLACK_WEBHOOK')]) {
							sh '''
                                curl -sS -X POST -H "Content-type: application/json" --data "{
                                    \\"text\\": \\":x: TESTS FAILED!\\n*Job:* ${JOB_NAME}\\n*Build:* ${BUILD_NUMBER}\\n*Branch:* ${cleanBranch}
                                }" "${SLACK_WEBHOOK}"
                            '''
						}
						error "Tests failed - aborting deploy"
					}
				}
			}
		}

		/****************************
		* SonarQube Code Analysis *
		****************************/
		// stage('SonarQube Analysis - Backend') {
		// 	steps {
		// 		script {
		// 			def scannerHome = tool name: 'SonarQube Scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
		// 			env.PATH = "${scannerHome}/bin:${env.PATH}"
		//
		// 			withSonarQubeEnv('SonarQube Dev') {
		// 				withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN'),
		// 					string(credentialsId: 'sonarqube-host-url', variable: 'SONAR_HOST')]) {
		// 					dir('backend/discovery-service') {
		// 						sh '''
//                                     sonar-scanner \
//                                         -Dsonar.projectKey=buy-two-discovery-service \
//                                         -Dsonar.projectName="Buy-Two - Discovery Service" \
//                                         -Dsonar.sources=src \
//                                         -Dsonar.java.binaries=target/classes \
//                                         -Dsonar.exclusions="**/.env,**/.env*,**/*.log" \
//                                         -Dsonar.host.url=${SONAR_HOST} \
//                                         -Dsonar.token=${SONAR_TOKEN}
//                                 '''
		// 					}
		// 					dir('backend/gateway-service') {
		// 						sh '''
//                                     sonar-scanner \
//                                         -Dsonar.projectKey=buy-two-gateway-service \
//                                         -Dsonar.projectName="Buy-Two - Gateway Service" \
//                                         -Dsonar.sources=src \
//                                         -Dsonar.java.binaries=target/classes \
//                                         -Dsonar.exclusions="**/.env,**/.env*,**/*.log" \
//                                         -Dsonar.host.url=${SONAR_HOST} \
//                                         -Dsonar.token=${SONAR_TOKEN}
//                                 '''
		// 					}
		// 					dir('backend/user-service') {
		// 						sh '''
//                                     sonar-scanner \
//                                         -Dsonar.projectKey=buy-two-user-service \
//                                         -Dsonar.projectName="Buy-Two - User Service" \
//                                         -Dsonar.sources=src \
//                                         -Dsonar.java.binaries=target/classes \
//                                         -Dsonar.exclusions="**/.env,**/.env*,**/*.log" \
//                                         -Dsonar.host.url=${SONAR_HOST} \
//                                         -Dsonar.token=${SONAR_TOKEN}
//                                 '''
		// 					}
		// 					dir('backend/product-service') {
		// 						sh '''
//                                     sonar-scanner \
//                                         -Dsonar.projectKey=buy-two-product-service \
//                                         -Dsonar.projectName="Buy-Two - Product Service" \
//                                         -Dsonar.sources=src \
//                                         -Dsonar.java.binaries=target/classes \
//                                         -Dsonar.exclusions="**/.env,**/.env*,**/*.log" \
//                                         -Dsonar.host.url=${SONAR_HOST} \
//                                         -Dsonar.token=${SONAR_TOKEN}
//                                 '''
		// 					}
		// 					dir('backend/media-service') {
		// 						sh '''
//                                     sonar-scanner \
//                                         -Dsonar.projectKey=buy-two-media-service \
//                                         -Dsonar.projectName="Buy-Two - Media Service" \
//                                         -Dsonar.sources=src \
//                                         -Dsonar.java.binaries=target/classes \
//                                         -Dsonar.exclusions="**/.env,**/.env*,**/*.log" \
//                                         -Dsonar.host.url=${SONAR_HOST} \
//                                         -Dsonar.token=${SONAR_TOKEN}
//                                 '''
		// 					}
		// 				}
		// 			}
		// 		}
		// 	}
		// }

		// stage('SonarQube Analysis - Frontend') {
		// 	steps {
		// 		dir('frontend') {
		// 			script {
		// 				def scannerHome = tool name: 'SonarQube Scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
		// 				env.PATH = "${scannerHome}/bin:${env.PATH}"
		//
		// 				withSonarQubeEnv('SonarQube Dev') {
		// 					withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN'),
		// 						string(credentialsId: 'sonarqube-host-url', variable: 'SONAR_HOST')]) {
		// 						sh '''
//                                     sonar-scanner \
//                                         -Dsonar.projectKey=buy-two-frontend \
//                                         -Dsonar.projectName="Buy-Two - Frontend" \
//                                         -Dsonar.sources=src/app \
//                                         -Dsonar.exclusions=**/*.spec.ts,**/*.test.ts,**/*.stories.ts,**/*.mock.ts,**/*.d.ts,node_modules/**,dist/**,coverage/**,**/.env,**/.env*,src/environments/**,src/assets/** \
//                                         -Dsonar.cpd.exclusions=**/*.spec.ts,**/*.test.ts,**/*.stories.ts,**/*.mock.ts,node_modules/** \
//                                         -Dsonar.host.url=${SONAR_HOST} \
//                                         -Dsonar.token=${SONAR_TOKEN}
//                                 '''
		// 					}
		// 				}
		// 			}
		// 		}
		// 	}
		// }

		/****************************
		 * Quality Gate Check → Skip deploy → Post FAILURE Slack *
		 ****************************/
		// stage('Quality Gate Check') {
		// 	steps {
		// 		script {
		// 			echo 'Checking SonarQube Quality Gate...'
		// 			timeout(time: 5, unit: 'MINUTES') {
		// 				waitForQualityGate abortPipeline: true
		// 			}
		// 		}
		// 	}
		// }

		/************************
		 * Build Docker images  *
		 ************************/
		stage('Build Images') {
			steps {
				script {
					echo "Building Docker images with tag: ${VERSION}"
					dir("${env.WORKSPACE}") {
						withEnv([
							"IMAGE_TAG=${VERSION}",
							"JAVA_TOOL_OPTIONS=-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400"
						]) {
							sh 'docker compose -f docker-compose.yml build --parallel --progress=plain'
						}
					}
				}
			}
		}

		/******************************
		 * Deploy, verify, and rollback
		 ******************************/
		stage('Deploy & Verify - Debug Mode') {
			steps {
				timeout(time: 30, unit: 'MINUTES') {
					script {
						dir("${env.WORKSPACE}") {
							
							echo "=================================================="
							echo "   STARTING DEBUG DEPLOYMENT PROCEDURE"
							echo "=================================================="

                            // 0. Diagnostic: Credential Check
                            echo "--- STEP 0: Verifying Existence of Credentials ---"
                            // We check them one-by-one to identify which one causes the silent crash
                            try { withCredentials([string(credentialsId: 'atlas-uri', variable: 'X')]) { echo "✅ Found: atlas-uri" } } catch(e) { error "❌ MISSING: atlas-uri" }
                            try { withCredentials([string(credentialsId: 'jwt-secret', variable: 'X')]) { echo "✅ Found: jwt-secret" } } catch(e) { error "❌ MISSING: jwt-secret" }
                            try { withCredentials([string(credentialsId: 'keystore-password', variable: 'X')]) { echo "✅ Found: keystore-password" } } catch(e) { error "❌ MISSING: keystore-password" }
                            try { withCredentials([string(credentialsId: 'r2-endpoint', variable: 'X')]) { echo "✅ Found: r2-endpoint" } } catch(e) { error "❌ MISSING: r2-endpoint" }
                            try { withCredentials([string(credentialsId: 'r2-access-key', variable: 'X')]) { echo "✅ Found: r2-access-key" } } catch(e) { error "❌ MISSING: r2-access-key" }
                            try { withCredentials([string(credentialsId: 'r2-secret-key', variable: 'X')]) { echo "✅ Found: r2-secret-key" } } catch(e) { error "❌ MISSING: r2-secret-key" }

							// 1. Secrets Setup
                            echo "--- STEP 1: Setting up Secrets (Batch) ---"
							withCredentials([
								string(credentialsId: 'atlas-uri', variable: 'ATLAS_URI'),
								string(credentialsId: 'jwt-secret', variable: 'JWT_SECRET'),
								string(credentialsId: 'keystore-password', variable: 'KEYSTORE_PASSWORD'),
								string(credentialsId: 'r2-endpoint', variable: 'R2_ENDPOINT'),
								string(credentialsId: 'r2-access-key', variable: 'R2_ACCESS_KEY'),
								string(credentialsId: 'r2-secret-key', variable: 'R2_SECRET_KEY')
							]) {
                                // Diagnostic checks
                                sh 'echo "DEBUG: Entering Shell Block"'

                                sh '''
                                    set -e
                                    set -x
                                    
                                    TARGET="/opt/buy-two/secrets/gateway-keystore.p12"
                                    
                                    echo "Verifying existence of Keystore at $TARGET..."
                                    if [ -f "$TARGET" ]; then
                                        echo "✅ File exists on host."
                                        ls -l "$TARGET"
                                    else
                                        echo "❌ Keystore MISSING at $TARGET"
                                        echo "FATAL: Deployment requires this file. Please manually place it there."
                                        exit 1
                                    fi
                                '''

                                sh '''
                                    set -e
                                    set -x
                                    echo "Creating .env file..."
                                    cat > .env << EOF
ATLAS_URI=${ATLAS_URI}
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_SECRET=${JWT_SECRET}
KEY_STORE_PASSWORD=${KEYSTORE_PASSWORD}
R2_ENDPOINT=${R2_ENDPOINT}
R2_ACCESS_KEY=${R2_ACCESS_KEY}
R2_SECRET_KEY=${R2_SECRET_KEY}
USER_DB=buy-two
PRODUCT_DB=buy-two
MEDIA_DB=buy-two
ORDER_DB=buy-two
SPRING_SECURITY_USER_NAME=user
SPRING_SECURITY_USER_PASSWORD=password
EOF
                                    echo "✅ .env file created"
                                '''
                            }

                            // 2. Cleanup
                            echo "--- STEP 2: Cleaning up Old Containers & Ports ---"
							sh '''
                                set +e # Continue even if commands fail
                                echo "Running docker compose down..."
								docker compose down --remove-orphans || true
								
                                echo "1. Force killing by container name..."
								services="discovery-service gateway-service frontend user-service product-service media-service order-service kafka"
								for service in $services; do
									ids=$(docker ps -aq --filter "name=$service")
									if [ -n "$ids" ]; then
										echo "Targeting $service containers: $ids"
										docker rm -f $ids || true
									fi
								done
                                
                                echo "2. Force killing processes holding specific ports..."
                                # Ports: 8761(discovery), 8080(gateway/others), 4200(frontend), 9092(kafka), 8789(order), 8456(user), 8567(product), 8678(media)
                                ports="8761 8080 4200 9092 8789 8456 8567 8678"
                                for port in $ports; do
                                    echo "Checking port $port..."
                                    # Find PID using lsof or netstat if available, or docker ps
                                    # Fallback: finding docker container mapping this port if lsof/fuser not available
                                    cid=$(docker ps -q --filter "publish=$port")
                                    if [ -n "$cid" ]; then
                                         echo "Killing container $cid holding port $port"
                                         docker rm -f $cid
                                    fi
                                done
                                
                                echo "Verifying cleanup (should be empty of project containers):"
                                docker ps -a
							'''
							sleep 2

                            // 3. Build
                            try {
                                echo "--- STEP 3: Building Images ---"
								withEnv(["IMAGE_TAG=${VERSION}"]) {
                                    sh 'docker compose build frontend || exit 1'
									sh 'docker compose build --pull --parallel --progress=plain'
                                    
                                    echo "Tagging images..."
									sh '''
                                        docker tag frontend:${VERSION} frontend:${STABLE_TAG} frontend:build-${BUILD_NUMBER} || true
                                        docker tag discovery-service:${VERSION} discovery-service:${STABLE_TAG} discovery-service:build-${BUILD_NUMBER} || true
                                        docker tag gateway-service:${VERSION} gateway-service:${STABLE_TAG} gateway-service:build-${BUILD_NUMBER} || true
                                        docker tag user-service:${VERSION} user-service:${STABLE_TAG} user-service:build-${BUILD_NUMBER} || true
                                        docker tag product-service:${VERSION} product-service:${STABLE_TAG} product-service:build-${BUILD_NUMBER} || true
                                        docker tag media-service:${VERSION} media-service:${STABLE_TAG} media-service:build-${BUILD_NUMBER} || true
                                    '''
                                }
                            } catch (Exception e) {
                                echo "❌ Build Failed"
                                throw e
                            }

                            // 4. Debug Deployment
                            echo "--- STEP 4: Debug Deployment (Step-by-Step) ---"
                            try {
                                withEnv(["IMAGE_TAG=${VERSION}"]) {
                                    
                                    // Start Infra
                                    echo "▶️ Starting Kafka..."
                                    sh 'docker compose up -d kafka'
                                    sleep 5
                                    sh 'docker ps | grep kafka || echo "⚠️ Kafka not running"'
                                    
                                    // Start Discovery
                                    echo "▶️ Starting Discovery Service..."
                                    sh 'docker compose up -d discovery-service'
                                    sleep 10
                                    sh 'docker ps | grep discovery || echo "⚠️ Discovery not running"'
                                    sh 'docker logs discovery-service || true'

                                    // Start Gateway (Dependent on Discovery)
                                    echo "▶️ Starting Gateway Service..."
                                    sh 'docker compose up -d gateway-service'
                                    sleep 10
                                    sh 'docker ps | grep gateway || echo "⚠️ Gateway not running"' 
                                    echo "--- GATEWAY LOGS (Initial 10s) ---"
                                    sh 'docker logs gateway-service || true'
                                    echo "--------------------"

                                    // Start Rest
                                    echo "▶️ Starting Remaining Services..."
                                    sh 'docker compose up -d'
                                    sleep 20
                                    
                                    echo "--- STEP 5: Verification ---"
                                    sh 'docker ps -a'

                                    // Check if Gateway matches requirements
                                    echo "🔍 Checking Gateway Health:"
                                    // We use || true so script doesn't abort immediately, we handle checking manually
                                    sh 'curl -v http://localhost:8080/actuator/health || echo "Curl failed"'
                                    
                                    // Final check
                                    sh '''
                                        if docker compose ps | grep -q "Exited"; then 
                                            echo "❌ FOUND EXITED CONTAINERS:"
                                            docker compose ps --filter "status=exited"
                                            echo "Dumping logs for exited containers..."
                                            docker compose logs
                                            exit 1
                                        else
                                            echo "✅ All containers seem to be running."
                                        fi
                                    '''
                                }
                                
                                echo "✅ Deployment Success"
                                currentBuild.result = 'SUCCESS'

                            } catch (Exception e) {
                                echo "❌ DEPLOYMENT FAILED: ${e.message}"
                                echo "--- FINAL SYSTEM STATE ---"
                                sh 'docker ps -a'
                                throw e
                            }
						}
					}
				}
			}
		}
	}
	// end of stages

	post {
		always {
			script {
				def buildState = currentBuild.currentResult?.toLowerCase() ?: 'success'
				def ghState = (buildState == 'success') ? 'success' : 'failure'
				def cleanBranch = "${BRANCH ?: GIT_BRANCH ?: 'main'}".replaceAll(/^origin\//, '')

				withCredentials([string(credentialsId: 'slack-webhook', variable: 'SLACK_WEBHOOK')]) {
					def emoji = (buildState == 'success') ? ':white_check_mark:' : ':x:'
					sh """
                        curl -sS -X POST \\
                            -H 'Content-type: application/json' \\
                            -d '{"text":"${emoji} *${buildState.toUpperCase()}*\\nJob: ${JOB_NAME}\\nBuild: #${BUILD_NUMBER}\\nBranch: ${cleanBranch}\\nCommit: <https://github.com/kurizma/buy-two/commit/${GIT_COMMIT}|${GIT_COMMIT[0..7]}>"}' \\
                            \$SLACK_WEBHOOK || true
                    """
				}

				// Archive artifacts BEFORE cleaning workspace
				archiveArtifacts artifacts: 'backend/*/target/surefire-reports/*.xml', allowEmptyArchive: true
				archiveArtifacts artifacts: 'frontend/test-results/junit/*.xml', allowEmptyArchive: true
				junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
				junit allowEmptyResults: true, testResults: '**/test-results/junit/*.xml'

				// Clean workspace LAST
				cleanWs notFailBuild: true

				if (env.GIT_COMMIT) {
					try {
						withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
							sh """
								set +e

								curl -s -H "Authorization: token ${GITHUB_TOKEN}" \\
								  -X POST -H "Accept: application/vnd.github.v3+json" \\
								  -d '{"state":"${ghState}", "context":"buy-two", "description":"Jenkins ${buildState}", "target_url":"${BUILD_URL}"}' \\
								  https://api.github.com/repos/kurizma/buy-two/statuses/${GIT_COMMIT} || true

								curl -s -H "Authorization: token ${GITHUB_TOKEN}" \\
								  -X POST -H "Accept: application/vnd.github.v3+json" \\
								  -d '{"state":"${ghState}", "context":"buy-two-quality-gate", "description":"Quality gate ${buildState}"}' \\
								  https://api.github.com/repos/kurizma/buy-two/statuses/${GIT_COMMIT} || true

								exit 0
							"""
						}
					} catch (Exception e) {
						echo "⚠️ Could not update GitHub status: ${e.message}"
					}
				}

			}
		}
	}
}
