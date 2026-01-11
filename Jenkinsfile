pipeline {
	// Agent 'any' is fine for this example, but in real projects you might
	// use specific labels for Docker-enabled agents.
	agent any

	tools {
		maven 'Maven-3.9'
		jdk 'Java-17'
	}

	stages {

		// Stage 1: Checkout the specific branch that triggered the pipeline
		stage('Checkout Code') {
			steps {
				// 'checkout scm' is the standard for Multibranch Pipelines.
				// It automatically checks out the correct feature branch.
				checkout scm
			}
		}

		// Stage 2: Build the project. This command automatically runs the tests.
		stage('Build & Test') {
			steps {
				// 'package' lifecycle runs 'test' first. If any test fails,
				// this stage will fail and stop the pipeline.
				sh 'mvn clean package'
			}
		}

		// Stage 3: Build Docker images using the JARs we just created
		stage('Build Docker Images') {
			steps {
				echo 'Building Docker images...'
				// This will build all services defined in your docker-compose file
				sh 'docker-compose build'
			}
		}

		// Stage 4: Deploy (for feature branches, this is optional but good for testing)
		stage('Deploy Services') {
			steps {
				echo 'Deploying application using Docker Compose...'
				// Stop any old running containers first, then start the new ones
				sh 'docker-compose down || true' // '|| true' prevents failure if nothing is running
				sh 'docker-compose up -d'

				echo 'Waiting 30 seconds for services to start...'
				sleep 30
			}
		}
	}

	// The 'post' block runs after all stages are complete
	post {
		// 'always' runs regardless of whether the build succeeded or failed
		always {
			// Step 1: Publish the test results so you can see them in the Jenkins UI
			echo 'Publishing test results...'
			junit '**/target/surefire-reports/*.xml'

			// Step 2: Archive the build artifacts (the JAR files)
			echo 'Archiving artifacts...'
			archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true

			// Step 3: Clean up the workspace
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