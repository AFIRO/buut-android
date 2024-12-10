pipeline {
    agent { label 'App' }

    environment {
        ANDROID_HOME = '/opt/android-sdk'
        APP_ARCHIVE_NAME = 'app' 
        CHANGELOG_CMD = 'git log --date=format:"%Y-%m-%d" --pretty="format: * %s% b (%an, %cd)" | head -n 10 > commit-changelog.txt'
        DISCORD_WEBHOOK_URL = "https://discord.com/api/webhooks/1301160382307766292/kROxjtgZ-XVOibckTMri2fy5-nNOEjzjPLbT9jEpr_R0UH9JG0ZXb2XzUsYGE0d3yk6I" // NEEDS TO BE CHANGED
        JENKINS_CREDENTIALS_ID = "jenkins-master-key"
        SSH_KEY_FILE = '/var/lib/jenkins/.ssh/id_rsa'
        TEST_RESULT_PATH = 'app/build/test-results/'
        TRX_FILE_PATH = 'app/build/test-results/'
        TRX_TO_XML_PATH = 'app/build/test-results/'
        JENKINS_SERVER = 'http://139.162.132.174:8080/'
        GRADLE_PATH = '/opt/gradle/bin/gradle'
    }

    options {
        disableConcurrentBuilds()
    }

    stages {
        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Checkout Code') {
            steps {
                script {
                    git credentialsId: JENKINS_CREDENTIALS_ID, url: 'git@github.com:HOGENT-RISE/android-2425-tiao1.git', branch: 'main'
                    echo 'Gather GitHub info!'
                    def gitInfo = sh(script: 'git show -s HEAD --pretty=format:"%an%n%ae%n%s%n%H%n%h" 2>/dev/null', returnStdout: true).trim().split("\n")
                    env.GIT_AUTHOR_NAME = gitInfo[0]
                    env.GIT_AUTHOR_EMAIL = gitInfo[1]
                    env.GIT_COMMIT_MESSAGE = gitInfo[2]
                    env.GIT_COMMIT = gitInfo[3]
                    env.GIT_BRANCH = gitInfo[4]
                }
            }
        }

        stage("Build Application") {
            stages {
                stage("Generate License Report") {
                    steps {
                        sh "${GRADLE_PATH} generateLicenseReport"
                    }
                    post {
                        always {
                            archiveArtifacts artifacts: "**/build/reports/dependency-license/*.html"
                        }
                    }
                }

                stage("Build and Bundle") {
                    steps {
                        sh "${GRADLE_PATH} clean assembleRelease bundleRelease --no-daemon"
                    }
                    post {
                        always {
                            archiveArtifacts artifacts: "app/build/outputs/bundle/release/${APP_ARCHIVE_NAME}-release.aab"
                        }
                    }
                }

                stage("Run Tests") {
                    steps {
                        sh "${GRADLE_PATH} testReleaseUnitTest"
                    }
                    post {
                        always {
                            junit 'app/build/test-results/testReleaseUnitTest/TEST-*.xml'
                            sh "${GRADLE_PATH} jacocoTestReport"
                        }
                    }
                }

                stage("Lint Check") {
                    steps {
                        sh "${GRADLE_PATH} lintRelease"
                    }
                    post {
                        always {
                            archiveArtifacts 'app/build/reports/*.html'
                        }
                    }
                }

                stage("Static Analysis with Detekt") {
                    steps {
                        sh "${GRADLE_PATH} downloadDetektConfig detektRelease"
                    }
                    post {
                        always {
                            archiveArtifacts 'app/build/reports/detekt/*.html'
                        }
                    }
                }

                stage('Coverage Report') {
                    steps {
                        script {
                            sh "${GRADLE_PATH} clean jacocoTestReport"
                
                            def coverageHtmlDir = 'app//build/reports/coverage/debug'
                            def coverageExecFile = 'app/build/jacoco/test.exec'
                
                            echo "Checking for coverage files..."
                            if (fileExists(coverageExecFile)) {
                                echo "Coverage report generated."
                
                                publishHTML([
                                    allowMissing: false,
                                    alwaysLinkToLastBuild: true,
                                    keepAll: true,
                                    reportDir: coverageHtmlDir,
                                    reportFiles: 'index.html',
                                    reportName: 'Coverage Report'
                                ])
                            } else {
                                error 'Coverage report was not generated. Verify JaCoCo configuration.'
                            }
                        }
                    }
                }
            }
        }

        stage("Publish to Play Store") {
            steps {
                sh "${GRADLE_PATH} assembleRelease"
            }
        }
    }

    post {
        success {
            echo 'Build and Play Store Deployment Successfully Completed!'
            archiveArtifacts artifacts: '**/*.dll', fingerprint: true
            script {
                sendDiscordNotification("Build Success")
            }
        }
        failure {
            echo 'Build or Play Store Deployment has failed!'
            script {
                sendDiscordNotification("Build Failed")
            }
        }
        always {
            echo 'Build process has completed.'
        }
    }
}

def sendDiscordNotification(status) {
    script {
        discordSend(
            title: "${env.JOB_NAME} - ${status}",
            description: """
                Build #${env.BUILD_NUMBER} ${status == "Build Success" ? 'completed successfully!' : 'has failed!'}
                **Commit**: ${env.GIT_COMMIT}
                **Author**: ${env.GIT_AUTHOR_NAME} <${env.GIT_AUTHOR_EMAIL}>
                **Branch**: ${env.GIT_BRANCH}
                **Message**: ${env.GIT_COMMIT_MESSAGE}
                
                [**Build output**](${JENKINS_SERVER}/job/${env.JOB_NAME}/${env.BUILD_NUMBER}/console)
                [**Test result**](${JENKINS_SERVER}/job/${env.JOB_NAME}/lastBuild/testReport/)
                [**Coverage report**](${JENKINS_SERVER}/job/${env.JOB_NAME}/${env.BUILD_NUMBER}/Coverage_20Report/)
                [**History**](${JENKINS_SERVER}/job/${env.JOB_NAME}/${env.BUILD_NUMBER}/testReport/history/)
            """,
            footer: "Build Duration: ${currentBuild.durationString.replace(' and counting', '')}",
            webhookURL: DISCORD_WEBHOOK_URL,
            result: status == "Build Success" ? 'SUCCESS' : 'FAILURE'
        )
    }
}
