pipeline {

    agent any

    options {
        disableConcurrentBuilds(abortPrevious: true)

        buildDiscarder(
            logRotator(numToKeepStr: '10')
        )
    }

    stages {

        stage('Build') {
            steps {
                echo 'Building application...'

                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'

                bat 'mvn test'
            }
        }

        stage('Stop Existing Application') {
            steps {
                echo 'Stopping existing Spring Boot application on port 8083...'

                bat '''
                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8083" ^| findstr "LISTENING"') do (
                        echo Stopping PID %%a
                        taskkill /PID %%a /F >nul 2>&1
                    )

                    exit /b 0
                '''
            }
        }

        stage('Start New Application') {
            steps {
                echo 'Starting new Spring Boot application on port 8083...'

                bat '''
                    start "SpringBootApp" cmd /c "java -jar target\\product-api-0.0.1-SNAPSHOT.jar --server.port=8083 > application.log 2>&1"

                    timeout /t 10 /nobreak >nul

                    netstat -ano | findstr ":8083" | findstr "LISTENING"

                    if %ERRORLEVEL% NEQ 0 (
                        echo ERROR: Spring Boot application did not start on port 8083.
                        echo.
                        echo ===== application.log =====
                        type application.log
                        exit /b 1
                    )

                    echo Spring Boot application is running on port 8083.
                '''
            }
        }

        stage('Archive Artifact') {
            steps {
                echo 'Archiving JAR artifact...'

                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true
            }
        }
    }

    post {

        always {
            echo 'Pipeline execution completed.'
        }

        success {
            echo 'Deployment successful. Sending success email...'

            emailext(
                to: 'upskillit.jag@gmail.com',
                subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
Jenkins CI/CD Pipeline Successful

========================================

Job Name     : ${env.JOB_NAME}
Build Number : #${env.BUILD_NUMBER}
Build Status : SUCCESS

========================================

Application
-----------
Spring Boot application was successfully deployed.

Application Port:
8083

========================================

Artifact
--------
The JAR artifact has been archived successfully.

========================================

Build Information
-----------------
Build URL:
${env.BUILD_URL}

Jenkins Job:
${env.JOB_NAME}

Build Number:
${env.BUILD_NUMBER}

========================================

The CI/CD pipeline completed successfully.

"""
            )
        }

        failure {
            echo 'Pipeline failed. Sending failure email...'

            emailext(
                to: 'upskillit.jag@gmail.com',
                subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
Jenkins CI/CD Pipeline Failed

========================================

Job Name     : ${env.JOB_NAME}
Build Number : #${env.BUILD_NUMBER}
Build Status : FAILURE

========================================

The Jenkins CI/CD pipeline failed.

One of the pipeline stages did not complete successfully.

Please check the Jenkins console output for details.

========================================

Build Information
-----------------
Build URL:
${env.BUILD_URL}

Jenkins Job:
${env.JOB_NAME}

Build Number:
${env.BUILD_NUMBER}

========================================

Please investigate the failed stage and Jenkins console log.

"""
            )
        }
    }
}