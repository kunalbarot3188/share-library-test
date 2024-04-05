// vars/sonarPipeline.groovy

def call(Map config) {
    def projectName = config.projectName
    def gitRepoUrl = config.gitRepoUrl
    def sonarToken = config.sonarToken

    stage('Clean workspace') {
        steps {
            cleanWs()
        }
    }

    stage('Checkout from Git') {
        steps {
            git branch: 'main', url: gitRepoUrl
        }
    }

    stage('Sonarqube Analysis') {
        steps {
            withSonarQubeEnv('sonar-server') {
                sh "$SCANNER_HOME/bin/sonar-scanner -Dsonar.projectName='${projectName}' -Dsonar.projectKey='${projectName}'"
            }
        }
    }

    stage('Quality Gate') {
        steps {
            script {
                waitForQualityGate abortPipeline: false, credentialsId: sonarToken
            }
        }
    }
}
