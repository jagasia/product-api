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
                echo 'Stopping existing Spring Boot application on port 8082...'

                bat '''
                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8082" ^| findstr "LISTENING"') do (
                        echo Stopping PID %%a
                        taskkill /PID %%a /F >nul 2>&1
                    )
                    exit /b 0
                '''
            }
        }

        stage('Start New Application') {
            steps {
                echo 'Starting new Spring Boot application on port 8082...'

                bat '''
                    start "SpringBootApp" cmd /c "java -jar target\\product-api-0.0.1-SNAPSHOT.jar --server.port=8082 > application.log 2>&1"

                    timeout /t 10 /nobreak >nul

                    netstat -ano | findstr ":8082" | findstr "LISTENING"

                    if %ERRORLEVEL% NEQ 0 (
                        echo ERROR: Spring Boot application did not start on port 8082.
                        echo.
                        echo ===== application.log =====
                        type application.log
                        exit /b 1
                    )

                    echo Spring Boot application is running on port 8082.
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

        success {
            echo 'Deployment successful.'
        }

        failure {
            echo 'Pipeline failed. Existing application was not replaced.'
        }
    }

}