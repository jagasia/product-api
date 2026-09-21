pipeline {

    agent any

    options {
        disableConcurrentBuilds(abortPrevious: true)

        buildDiscarder(
            logRotator(numToKeepStr: '10')
        )
    }

    environment {
        APP_NAME = 'product-api'
        APP_PORT = '8083'
        JAR_NAME = 'product-api-0.0.1-SNAPSHOT.jar'

        // EC2 server details
        EC2_USER = 'ec2-user'
        EC2_HOST = 'YOUR_EC2_PUBLIC_IP'

        // Location where the application will be deployed on EC2
        DEPLOY_DIR = '/home/ec2-user/product-api'
    }

    stages {

        stage('Build') {
            steps {
                echo 'Building application...'

                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'

                sh 'mvn test'
            }
        }

        stage('Prepare Deployment') {
            steps {
                echo 'Preparing deployment directory on EC2...'

                sh '''
                    ssh -o StrictHostKeyChecking=no \
                        ${EC2_USER}@${EC2_HOST} \
                        "mkdir -p ${DEPLOY_DIR}"
                '''
            }
        }

        stage('Copy JAR to EC2') {
            steps {
                echo 'Copying JAR to EC2 server...'

                sh '''
                    scp -o StrictHostKeyChecking=no \
                        target/${JAR_NAME} \
                        ${EC2_USER}@${EC2_HOST}:${DEPLOY_DIR}/
                '''
            }
        }

        stage('Stop Existing Application') {
            steps {
                echo 'Stopping existing Spring Boot application on EC2...'

                sh '''
                    ssh -o StrictHostKeyChecking=no \
                        ${EC2_USER}@${EC2_HOST} \
                        "PID=\\$(lsof -t -i:${APP_PORT} || true); \
                        if [ -n \\"\\$PID\\" ]; then \
                            echo 'Stopping existing application PID:' \\$PID; \
                            kill \\$PID || true; \
                        else \
                            echo 'No application currently running on port ${APP_PORT}.'; \
                        fi"
                '''
            }
        }

        stage('Start New Application') {
            steps {
                echo 'Starting Spring Boot application on EC2...'

                sh '''
                    ssh -o StrictHostKeyChecking=no \
                        ${EC2_USER}@${EC2_HOST} \
                        "cd ${DEPLOY_DIR} && \
                        nohup java -jar ${JAR_NAME} \
                        --server.port=${APP_PORT} \
                        > application.log 2>&1 & \
                        echo \\$! > application.pid"
                '''

                echo 'Waiting for application to start...'

                sleep 10
            }
        }

        stage('Verify Deployment') {
            steps {
                echo 'Verifying application on EC2...'

                sh '''
                    echo "Testing /hello..."

                    curl -f http://${EC2_HOST}:${APP_PORT}/hello

                    echo
                    echo "Testing /products..."

                    curl -f http://${EC2_HOST}:${APP_PORT}/products

                    echo
                    echo "Application deployment verified successfully."
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

Spring Boot application was successfully
built, tested and deployed to EC2.

Application Port:
8083

Endpoints:

http://${EC2_HOST}:8083/hello

http://${EC2_HOST}:8083/products

========================================

Deployment Directory
--------------------

${DEPLOY_DIR}

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
