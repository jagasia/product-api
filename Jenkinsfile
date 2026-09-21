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

                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'

                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image...'

                sh '''
                    docker build -t product-api:latest .
                '''
            }
        }

        stage('Stop Existing Container') {
            steps {
                echo 'Stopping existing Product API container...'

                sh '''
                    if docker ps -a --format '{{.Names}}' | grep -q '^product-api$'; then
                        echo "Stopping existing product-api container..."
                        docker stop product-api || true

                        echo "Removing existing product-api container..."
                        docker rm product-api || true
                    else
                        echo "No existing product-api container found."
                    fi
                '''
            }
        }

        stage('Deploy New Container') {
            steps {
                echo 'Starting new Product API container on port 8083...'

                sh '''
                    docker run -d \
                        --name product-api \
                        -p 8083:8083 \
                        product-api:latest
                '''

                echo 'Waiting for Spring Boot application to start...'

                sh '''
                    for i in 1 2 3 4 5 6 7 8 9 10; do

                        if curl -s http://localhost:8083/hello >/dev/null 2>&1; then
                            echo "Spring Boot application is running on port 8083."
                            exit 0
                        fi

                        echo "Application not ready yet... waiting 2 seconds."
                        sleep 2
                    done

                    echo "ERROR: Spring Boot application did not start."
                    echo
                    echo "===== Docker Container Status ====="
                    docker ps -a --filter name=product-api

                    echo
                    echo "===== Application Logs ====="
                    docker logs product-api

                    exit 1
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                echo 'Verifying Product API...'

                sh '''
                    echo "Testing /hello endpoint..."
                    curl -f http://localhost:8083/hello

                    echo
                    echo "Testing /products endpoint..."
                    curl -f http://localhost:8083/products

                    echo
                    echo "Product API deployment verified successfully."
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

Spring Boot Product API was successfully
built, tested, Dockerized and deployed.

Application Port:
8083

Endpoints:
http://<EC2-PUBLIC-IP>:8083/hello
http://<EC2-PUBLIC-IP>:8083/products

========================================

Docker
------

Container:
product-api

Image:
product-api:latest

Port:
8083:8083

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
#${env.BUILD_NUMBER}

========================================

Please investigate the failed stage and Jenkins console log.
"""
            )
        }
    }
}
