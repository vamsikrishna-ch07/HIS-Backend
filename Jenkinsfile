pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'Java-17'
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Build Docker Images') {
            steps {
                echo 'Building Docker images...'
                // Updated to use 'docker compose' (v2) instead of 'docker-compose'
                sh 'docker compose build'
            }
        }

        stage('Deploy Services') {
            steps {
                echo 'Force stopping and removing old containers...'
                // Updated to use 'docker compose'
                sh 'docker compose down --remove-orphans -v || true'

                echo 'Deploying new application stack...'
                sh 'docker compose up -d'

                echo 'Waiting 30 seconds for services to start...'
                sleep 30
            }
        }
    }

    post {
        always {
            echo 'Publishing test results...'
            junit '**/target/surefire-reports/*.xml'

            echo 'Archiving artifacts...'
            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true

            // Updated to use 'docker compose'
            echo 'Cleaning up Docker environment...'
            sh 'docker compose down -v || true'

            cleanWs()
        }
        failure {
            echo '❌ Build Failed! Check Jenkins logs and test results.'
        }
        success {
            echo '✅ CI/CD Pipeline executed successfully!'
        }
    }
}
