pipeline {
    agent any
    
    environment {
        SONAR_RUNNER_HOME = tool name: 'SonarScanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
    }
    
    stages {
        stage('Clean workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Checkout from Git') {
            steps {
                git branch: 'main', url: 'https://github.com/my-user/my-repo.git'
            }
        }

        stage('Sonarqube Analysis') {
            steps {
                withSonarQubeEnv('sonar-server') {
                    sh "${SONAR_RUNNER_HOME}/bin/sonar-scanner -Dsonar.projectName='my-project' -Dsonar.projectKey='my-project'"
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    waitForQualityGate abortPipeline: false, credentialsId: 'my-sonar-token'
                }
            }
        }
    }
}




// def call(Map config) {
//     def projectName = config.projectName
//     def gitRepoUrl = config.gitRepoUrl
//     def sonarToken = config.sonarToken

//     pipeline {
//         agent any
        
//         stages {
//             stage('Clean workspace') {
//                 steps {
//                     cleanWs()
//                 }
//             }

//             stage('Checkout from Git') {
//                 steps {
//                     git branch: 'main', url: gitRepoUrl
//                 }
//             }

//             stage('Sonarqube Analysis') {
//                 steps {
//                     withSonarQubeEnv('sonar-server') {
//                         sh "$SCANNER_HOME/bin/sonar-scanner -Dsonar.projectName='${projectName}' -Dsonar.projectKey='${projectName}'"
//                     }
//                 }
//             }

//             stage('Quality Gate') {
//                 steps {
//                     script {
//                         waitForQualityGate abortPipeline: false, credentialsId: sonarToken
//                     }
//                 }
//             }
//         }
//     }
// }







// // vars/sonarPipeline.groovy

// def call(Map config) {
//     def projectName = config.projectName
//     def gitRepoUrl = config.gitRepoUrl
//     def sonarToken = config.sonarToken

//     stage('Clean workspace') {
//         steps {
//             cleanWs()
//         }
//     }

//     stage('Checkout from Git') {
//         steps {
//             git branch: 'main', url: gitRepoUrl
//         }
//     }

//     stage('Sonarqube Analysis') {
//         steps {
//             withSonarQubeEnv('sonar-server') {
//                 sh "$SCANNER_HOME/bin/sonar-scanner -Dsonar.projectName='${projectName}' -Dsonar.projectKey='${projectName}'"
//             }
//         }
//     }

//     stage('Quality Gate') {
//         steps {
//             script {
//                 waitForQualityGate abortPipeline: false, credentialsId: sonarToken
//             }
//         }
//     }
// }
