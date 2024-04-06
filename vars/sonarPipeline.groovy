def call(Map config) {
    pipeline {
        agent any
        
        environment {
            // Define environment variables directly in the environment block
            PROJECT_NAME = config.projectName
            GIT_REPO_URL = config.gitRepoUrl
            SONAR_TOKEN = config.sonarToken
        }
        
        stages {
            stage('Clean workspace') {
                steps {
                    cleanWs()
                }
            }

            stage('Checkout from Git') {
                steps {
                    // Access environment variables using the env object
                    git branch: 'main', url: env.GIT_REPO_URL
                }
            }

            stage('Sonarqube Analysis') {
                steps {
                    withSonarQubeEnv('sonar-server') {
                        // Access environment variables using the env object
                        sh "${tool 'sonar-scanner'}/bin/sonar-scanner -Dsonar.projectName='${env.PROJECT_NAME}' -Dsonar.projectKey='${env.PROJECT_NAME}'"
                    }
                }
            }

            stage('Quality Gate') {
                steps {
                    script {
                        // Access environment variables using the env object
                        waitForQualityGate abortPipeline: false, credentialsId: env.SONAR_TOKEN
                    }
                }
            }
        }
    }
}




// ********************************************

// // sucessful one
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
//                         // Use the configured SonarQube Scanner installation
//                         sh "${tool 'sonar-scanner'}/bin/sonar-scanner -Dsonar.projectName='${projectName}' -Dsonar.projectKey='${projectName}'"
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

// ********************************************

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
//                         sh "${SONAR_RUNNER_HOME}/bin/sonar-scanner -Dsonar.projectName='${projectName}' -Dsonar.projectKey='${projectName}'"
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
