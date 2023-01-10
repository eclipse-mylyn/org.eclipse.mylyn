pipeline {
	options {
		timeout(time: 40, unit: 'MINUTES')
		buildDiscarder(logRotator(numToKeepStr:'10'))
		disableConcurrentBuilds(abortPrevious: true)
	}
	agent {
		label "centos-latest"
	}
	tools {
		maven 'apache-maven-latest'
		jdk 'openjdk-jdk17-latest'
	}
	stages {
		stage('Build') {
			steps {
				wrap([$class: 'Xvnc', useXauthority: true]) {
					sh 'mvn clean verify -B -Psign -Dmaven.repo.local=$WORKSPACE/.m2/repository -Dmaven.test.failure.ignore=true -Dmaven.test.error.ignore=true -Ddash.fail=false'
				}
			}
			post {
				always {
					archiveArtifacts artifacts: '**/target/repository/**/*,**/target/*.zip,**/target/work/data/.metadata/.log'
					junit '**/target/surefire-reports/TEST-*.xml'
				}
			}
		}
		stage('Deploy Snapshot') {
			when {
				branch 'main'
			}
			steps {
				sshagent ( ['projects-storage.eclipse.org-bot-ssh']) {
				sh '''
					DOWNLOAD_AREA=/home/data/httpd/download.eclipse.org/mylyn/snapshots/nightly/mylyn/
					echo DOWNLOAD_AREA=$DOWNLOAD_AREA
					ssh genie.mylyn@projects-storage.eclipse.org "\
						rm -rf  ${DOWNLOAD_AREA}/* && \
						mkdir -p ${DOWNLOAD_AREA}"
					scp -r org.eclipse.mylyn-site/target/repository/* genie.mylyn@projects-storage.eclipse.org:${DOWNLOAD_AREA}
				'''
				sh '''
					DOWNLOAD_AREA=/home/data/httpd/download.eclipse.org/mylyn/snapshots/nightly/builds/
					echo DOWNLOAD_AREA=$DOWNLOAD_AREA
					ssh genie.mylyn@projects-storage.eclipse.org "\
						rm -rf  ${DOWNLOAD_AREA}/* && \
						mkdir -p ${DOWNLOAD_AREA}"
					scp -r mylyn.builds/org.eclipse.mylyn.builds-site/target/repository/* genie.mylyn@projects-storage.eclipse.org:${DOWNLOAD_AREA}
				'''
				}
			}
		}
	}
}
