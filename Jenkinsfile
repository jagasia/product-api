pipeline {

    agent any

    options {
        // If a new build starts, abort the previous running build
        disableConcurrentBuilds(abortPrevious: true)

        // Keep only the latest 10 Jenkins builds
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {

        stage('Build') {
            steps {
                echo 'Building Spring Boot application...'
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
                echo 'Stopping existing application on port 8081...'

                bat '''
                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081 ^| findstr LISTENING') do (
                        echo Stopping process %%a
                        taskkill /PID %%a /F
                    )
                '''
            }
        }

        stage('Start Application') {
            steps {
                echo 'Starting Spring Boot application on port 8081...'

                bat '''
                    start "SpringBootApp" cmd /c "java -jar target\\*.jar --server.port=8081 > application.log 2>&1"
                '''
            }
        }
    }

    post {

        success {
            echo 'Application deployed successfully on port 8081.'
        }

        failure {
            echo 'Pipeline failed.'
        }

        always {
            echo 'Pipeline execution completed.'
        }
    }
}