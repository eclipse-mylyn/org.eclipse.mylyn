/*******************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/.
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *   See git history
 *******************************************************************************/
def secrets = [
  [path: 'cbi/tools.mylyn/develocity.eclipse.org', secretValues: [
    [envVar: 'DEVELOCITY_ACCESS_KEY', vaultKey: 'api-token']
    ]
  ]
]


pipeline {
	options {
		timeout(time: 80, unit: 'MINUTES')
		buildDiscarder(logRotator(numToKeepStr:'10'))
		disableConcurrentBuilds(abortPrevious: true)
	}
	agent {
		label "ubuntu-latest"
	}
	tools {
		maven 'apache-maven-latest'
		jdk 'openjdk-jdk21-latest'
	}
	parameters {
		choice(
			name: 'BUILD_TYPE',
			choices: ['nightly', 'milestone', 'release'],
			description: '''
			Choose the type of build.
			Note that a release build will not promote the build, but rather will promote the most recent milestone build.
			'''
		)
		booleanParam(
			name: 'PROMOTE',
			defaultValue: true,
			description: 'Whether to promote the build to the download server.'
		)
	}
	stages {
		stage('Display Parameters') {
				steps {
						script {
								env.BUILD_TYPE = params.BUILD_TYPE
								if (env.BRANCH_NAME == 'main') {
									if (params.PROMOTE) {
										env.MAVEN_PROFILES = "-Psign -Ppromote"
									} else {
										env.MAVEN_PROFILES = "-Psign"
									}
								} else {
									env.MAVEN_PROFILES = ""
								}
								def description = """
BUILD_TYPE=${env.BUILD_TYPE}
MAVEN_PROFILES=${env.MAVEN_PROFILES}
""".trim()
								echo description
								currentBuild.description = description.replace("\n", "<br/>")
						}
				}
		}
		stage('Initialize PGP') {
			steps {
				withCredentials([file(credentialsId: 'secret-subkeys.asc', variable: 'KEYRING')]) {
					sh 'gpg --batch --import "${KEYRING}"'
					sh 'for fpr in $(gpg --list-keys --with-colons  | awk -F: \'/fpr:/ {print $10}\' | sort -u); do echo -e "5\ny\n" |  gpg --batch --command-fd 0 --expert --edit-key ${fpr} trust; done'
				}
			}
		}
		stage('Build Mylyn') {
			steps {
				sshagent (['projects-storage.eclipse.org-bot-ssh']) {
					wrap([$class: 'Xvnc', useXauthority: true]) {
                        withVault([vaultSecrets: secrets]) {
                            sh '''
                                mvn \
                                clean \
                                verify \
                                -B \
                                $MAVEN_PROFILES \
                                -Dmaven.repo.local=$WORKSPACE/.m2/repository \
                                -Ddash.fail=false \
                                -Dorg.eclipse.justj.p2.manager.build.url=$JOB_URL \
                                -Dbuild.type=$BUILD_TYPE \
                                -Dgit.commit=$GIT_COMMIT
                            '''
                        }
					}
				}
			}
			post {
				always {
					archiveArtifacts artifacts: '**/target/repository/**/*,**/target/*.zip,**/target/work/data/.metadata/.log'
					junit '**/target/surefire-reports/TEST-*.xml'
				}
			}
		}
	}
}
