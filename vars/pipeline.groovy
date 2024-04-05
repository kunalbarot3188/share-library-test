pipeline {
    agent any
    
    stages {
        stage('Clean workspace') {
            environment {
                PROJECT_NAME = "${env.PROJECT_NAME}"
                GIT_REPO_URL = "${env.GIT_REPO_URL}"
            }
            steps {
                cleanWs()
            }
        }
        
        stage('Checkout from Git') {
            environment {
                GIT_REPO_URL = "${env.GIT_REPO_URL}"
            }
            steps {
                git branch: 'main', url: "${GIT_REPO_URL}"
            }
        }
        
        stage('Sonarqube Analysis') {
            environment {
                PROJECT_NAME = "${env.PROJECT_NAME}"
                SONAR_TOKEN = "${env.SONAR_TOKEN}"
            }
            steps {
                withSonarQubeEnv('sonar-server') {
                    sh '''$SCANNER_HOME/bin/sonar-scanner \
                        -Dsonar.projectName="${PROJECT_NAME}" \
                        -Dsonar.projectKey="${PROJECT_NAME}"'''
                }
            }
        }
        
        stage('Quality Gate') {
            environment {
                SONAR_TOKEN = "${env.SONAR_TOKEN}"
            }
            steps {
                script {
                    waitForQualityGate abortPipeline: false, credentialsId: SONAR_TOKEN
                }
            }
        }
        
        stage('Install Dependencies') {
            steps {
                sh "npm install"
            }
        }
        
        stage('Docker Build & Push') {
            environment {
                PROJECT_NAME = "${env.PROJECT_NAME}"
                DOCKER_REGISTRY = "${env.DOCKER_REGISTRY}"
                DOCKER_CREDENTIALS_ID = "${env.DOCKER_CREDENTIALS_ID}"
            }
            steps {
                script {
                    withDockerRegistry(credentialsId: DOCKER_CREDENTIALS_ID, toolName: 'docker') {
                        sh "docker build -t ${PROJECT_NAME} ."
                        sh "docker tag ${PROJECT_NAME} ${DOCKER_REGISTRY}/${PROJECT_NAME}:latest"
                        sh "docker push ${DOCKER_REGISTRY}/${PROJECT_NAME}:latest"
                    }
                }
            }
        }
        
        stage('Deploy to container') {
            environment {
                PROJECT_NAME = "${env.PROJECT_NAME}"
                DOCKER_REGISTRY = "${env.DOCKER_REGISTRY}"
            }
            steps {
                sh "docker run -d --name ${PROJECT_NAME} -p 8000:8000 ${DOCKER_REGISTRY}/${PROJECT_NAME}:latest"
            }
        }
        
        stage('Deploy to Kubernetes') {
            environment {
                K8S_NAMESPACE = "${env.K8S_NAMESPACE}"
                AWS_CREDENTIALS_ID = "${env.AWS_CREDENTIALS_ID}"
                K8S_CREDENTIALS_ID = "${env.K8S_CREDENTIALS_ID}"
            }
            steps {
                withAWS(credentials: AWS_CREDENTIALS_ID, region: 'us-east-1') {
                    script {
                        withKubeConfig(
                            credentialsId: K8S_CREDENTIALS_ID,
                            serverUrl: '',
                            namespace: K8S_NAMESPACE,
                            caCertificate: '',
                            clusterName: '',
                            contextName: '',
                            restrictKubeConfigAccess: false
                        ) {
                            sh 'kubectl apply -f deployment.yml --namespace=${K8S_NAMESPACE} --validate=false'
                        }
                    }
                }
            }
        }
    }
}

