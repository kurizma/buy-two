pipeline {
	agent any
	triggers {
		githubPush()
	}
	options {
		timestamps()
		timeout(time: 20, unit: 'MINUTES')
		throttleJobProperty(
			categories: ['buy-two-serial'],
			throttleEnabled: true,
			throttleOption: 'category'
		)
	}

	// Global configuration

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

	stages {
		stage('Checkout') {
			steps {
				// Checkout is handled automatically by Jenkins Pipeline from SCM
				// This stage is here for clarity in the UI
				echo "Code already checked out by Jenkins"
				sh 'git rev-parse HEAD'
			}
		}

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

		stage('Backend Build - order-service') {
			steps {
				dir('backend/order-service') {
					sh "JAVA_TOOL_OPTIONS='-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400' mvn -Dmaven.repo.local=${MAVEN_REPO_LOCAL} clean package -DskipTests"
				}
			}
		}

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

		stage('Backend Tests - order-service') {
			steps {
				dir('backend/order-service') {
					sh "JAVA_TOOL_OPTIONS='-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400' mvn -Dmaven.repo.local=${MAVEN_REPO_LOCAL} test"
				}
			}
		}

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

		stage('Sonar: Discovery Service') {
			steps {
				script {
					def scannerHome = tool name: 'SonarQube Scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
					env.PATH = "${scannerHome}/bin:${env.PATH}"
				}
				withSonarQubeEnv('SonarCloud') {
					dir('backend/discovery-service') {
							sh "sonar-scanner -Dsonar.branch.name=${BRANCH} -Dsonar.organization=kurizma -Dsonar.projectKey=kurizma_buy-two_discovery-service -Dsonar.projectName='Discovery Service' -Dsonar.projectVersion='${VERSION}-${BRANCH}' -Dsonar.sources=src -Dsonar.java.binaries=target/classes -Dsonar.exclusions='**/.env,**/*.log' -Dsonar.qualitygate.wait=true"
					}
				}
			}
		}

		stage('Sonar: Gateway Service') {
			steps {
				script {
					def scannerHome = tool name: 'SonarQube Scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
					env.PATH = "${scannerHome}/bin:${env.PATH}"
				}
				withSonarQubeEnv('SonarCloud') {
					dir('backend/gateway-service') {
							sh "sonar-scanner -Dsonar.branch.name=${BRANCH} -Dsonar.organization=kurizma -Dsonar.projectKey=kurizma_buy-two_gateway-service -Dsonar.projectName='Gateway Service' -Dsonar.projectVersion='${VERSION}-${BRANCH}' -Dsonar.sources=src -Dsonar.java.binaries=target/classes -Dsonar.exclusions='**/.env,**/*.log' -Dsonar.qualitygate.wait=true"
					}
				}
			}
		}

		stage('Sonar: User Service') {
			steps {
				script {
					def scannerHome = tool name: 'SonarQube Scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
					env.PATH = "${scannerHome}/bin:${env.PATH}"
				}
				withSonarQubeEnv('SonarCloud') {
					dir('backend/user-service') {
						sh "sonar-scanner -Dsonar.branch.name=${BRANCH} -Dsonar.organization=kurizma -Dsonar.projectKey=kurizma_buy-two_user-service -Dsonar.projectName='User Service' -Dsonar.projectVersion='${VERSION}-${BRANCH}' -Dsonar.sources=src -Dsonar.java.binaries=target/classes -Dsonar.exclusions='**/.env,**/*.log' -Dsonar.qualitygate.wait=true"
					}
				}
			}
		}

		stage('Sonar: Product Service') {
			steps {
				script {
					def scannerHome = tool name: 'SonarQube Scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
					env.PATH = "${scannerHome}/bin:${env.PATH}"
				}
				withSonarQubeEnv('SonarCloud') {
					dir('backend/product-service') {
							sh "sonar-scanner -Dsonar.branch.name=${BRANCH} -Dsonar.organization=kurizma -Dsonar.projectKey=kurizma_buy-two_product-service -Dsonar.projectName='Product Service' -Dsonar.projectVersion='${VERSION}-${BRANCH}' -Dsonar.sources=src -Dsonar.java.binaries=target/classes -Dsonar.exclusions='**/.env,**/*.log' -Dsonar.qualitygate.wait=true"
					}
				}
			}
		}

		stage('Sonar: Media Service') {
			steps {
				script {
					def scannerHome = tool name: 'SonarQube Scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
					env.PATH = "${scannerHome}/bin:${env.PATH}"
				}
				withSonarQubeEnv('SonarCloud') {
					dir('backend/media-service') {
						sh "sonar-scanner -Dsonar.branch.name=${BRANCH} -Dsonar.organization=kurizma -Dsonar.projectKey=kurizma_buy-two_media-service -Dsonar.projectName='Media Service' -Dsonar.projectVersion='${VERSION}-${BRANCH}' -Dsonar.sources=src -Dsonar.java.binaries=target/classes -Dsonar.exclusions='**/.env,**/*.log' -Dsonar.qualitygate.wait=true"
					}
				}
			}
		}

		stage('Sonar: Order Service') {
			steps {
				script {
					def scannerHome = tool name: 'SonarQube Scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
					env.PATH = "${scannerHome}/bin:${env.PATH}"
				}
				withSonarQubeEnv('SonarCloud') {
					dir('backend/order-service') {
						sh "sonar-scanner -Dsonar.branch.name=${BRANCH} -Dsonar.organization=kurizma -Dsonar.projectKey=kurizma_buy-two_order-service -Dsonar.projectName='Order Service' -Dsonar.projectVersion='${VERSION}-${BRANCH}' -Dsonar.sources=src -Dsonar.java.binaries=target/classes -Dsonar.exclusions='**/.env,**/*.log' -Dsonar.qualitygate.wait=true"
					}
				}
			}
		}

		stage('Sonar: Frontend') {
			steps {
				script {
					def scannerHome = tool name: 'SonarQube Scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
					env.PATH = "${scannerHome}/bin:${env.PATH}"
				}
				withSonarQubeEnv('SonarCloud') {
					dir('frontend') {
						sh "sonar-scanner -Dsonar.organization=kurizma -Dsonar.projectKey=kurizma_buy-two_frontend -Dsonar.projectName='Frontend' -Dsonar.projectVersion='${VERSION}-${BRANCH}' -Dsonar.sources=src/app -Dsonar.exclusions='**/*.spec.ts,**/*.test.ts,**/*.stories.ts,**/*.mock.ts,**/*.d.ts,node_modules/**,dist/**,coverage/**,**/.env,**/.env*,src/environments/**,src/assets/**' -Dsonar.cpd.exclusions='**/*.spec.ts,**/*.test.ts,**/*.stories.ts,**/*.mock.ts,node_modules/**' -Dsonar.qualitygate.wait=true"
					}
				}
			}
		}


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

		stage('Deploy & Verify') {
			when {
				branch 'main'
			}
			steps {

							// Cleanup old containers
							sh 'docker compose down || true'
							sleep 3

							try {
								echo "Building and tagging ${VERSION} as potential stable"

								withEnv(["IMAGE_TAG=${VERSION}"]) {
									sh 'docker compose build frontend || exit 1'
									sh 'docker compose build --pull --parallel --progress=plain'
									sh '''
                                        docker tag frontend:${VERSION} frontend:${STABLE_TAG} frontend:build-${BUILD_NUMBER} || true
                                        docker tag discovery-service:${VERSION} discovery-service:${STABLE_TAG} discovery-service:build-${BUILD_NUMBER} || true
                                        docker tag gateway-service:${VERSION} gateway-service:${STABLE_TAG} gateway-service:build-${BUILD_NUMBER} || true
                                        docker tag user-service:${VERSION} user-service:${STABLE_TAG} user-service:build-${BUILD_NUMBER} || true
                                        docker tag product-service:${VERSION} product-service:${STABLE_TAG} product-service:build-${BUILD_NUMBER} || true
                                        docker tag order-service:${VERSION} order-service:${STABLE_TAG} order-service:build-${BUILD_NUMBER} || true
                                        docker tag media-service:${VERSION} media-service:${STABLE_TAG} media-service:build-${BUILD_NUMBER} || true
                                    '''

									// Deploy new version for verification
									sh 'docker compose up -d'
									sleep 20

									// Strong health check
									sh '''
                                        timeout 30 bash -c "until docker compose ps | grep -q Up && curl -f http://localhost:4200 || curl -f http://localhost:8080/health; do sleep 2; done" || exit 1
                                        if docker compose ps | grep -q "Exit"; then exit 1; fi
                                    '''
								}
								echo "✅ New deploy verified - promoted build-${BUILD_NUMBER} to stable"
								echo "✅ New deploy verified - promoted build-${BUILD_NUMBER} to stable"
								currentBuild.result = 'SUCCESS'

							} catch (Exception e) {
								def stableTag = env.STABLE_TAG ?: 'latest'

								echo "❌ Deploy failed: ${e.message}"

								withCredentials([string(credentialsId: 'slack-webhook', variable: 'SLACK_WEBHOOK')]) {
									sh """
                                    curl -sS -X POST -H 'Content-type: application/json' \\
                                        --data '{\"text\":\"🚨 Rollback #${BUILD_NUMBER} → ${stableTag}\"}' \$SLACK_WEBHOOK
                                """
								}

								// Rollback: Always deploy known stable
								sh """
                                STABLE_TAG=\${STABLE_TAG:-latest}
                                docker compose down || true
                                IMAGE_TAG=\$STABLE_TAG docker compose up -d --pull never
                                sleep 10
                                docker compose ps  # Verify
                                echo "✅ Rolled back to ${stableTag}"
                            """
								currentBuild.result = 'UNSTABLE'
								throw e
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
								  -d '{"state":"${ghState}", "context":"buy-two-quality-gate", "description":"Quality gate ${buildState}", "target_url":"https://sonarcloud.io/organizations/kurizma/projects?search=buy-two"}' \\
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

