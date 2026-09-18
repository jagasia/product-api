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
                echo 'Stopping existing Spring Boot application on port 8081...'

                bat '''
                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081 ^| findstr LISTENING') do (
                        echo Stopping PID %%a
                        taskkill /PID %%a /F
                    )
                '''
            }
        }

        stage('Start New Application') {
            steps {
                echo 'Starting new Spring Boot application on port 8081...'

                bat '''
                    start "SpringBootApp" cmd /c "java -jar target\\product-api-0.0.1-SNAPSHOT.jar --server.port=8081 > application.log 2>&1"
                '''
            }
        }
    }

    post {

        success {
            echo 'Deployment successful.'
        }

        failure {
            echo 'Pipeline failed. Existing application was not replaced.'
        }
    }
}