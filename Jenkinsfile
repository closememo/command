def NOW = new Date().format('yyyyMMddHHmm')

pipeline {
    environment {
        registry = 'https://reg.bitgadak.com'
        registryCredential = 'docker-repository'
        dockerImage = 'reg.bitgadak.com/closememo/command'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }

    agent any

    stages {
        stage('Test') {
            steps {
                sh './gradlew clean test'
            }
        }
        stage('Build jar') {
            when {
                anyOf {
                    branch 'master'
                    branch 'batch'
                }
            }
            steps {
                sh './gradlew clean bootJar'
            }
        }
        stage('Build image and Push') {
            when {
                anyOf {
                    branch 'master'
                    branch 'batch'
                }
            }
            steps {
                echo "Starting to build docker image with tag: $NOW"
                script {
                    def imageName = dockerImage
                    if (env.BRANCH_NAME != 'master') {
                        imageName = dockerImage + '-' + env.BRANCH_NAME
                    }

                    app = docker.build(imageName)

                    docker.withRegistry(registry, registryCredential) {
                        app.push(NOW)
                        app.push("latest")
                    }
                }
            }
        }
        stage('Trigger changing manifest job (dev)') {
            when {
                anyOf {
                    branch 'master'
                }
            }
            steps {
                build job: 'closememo-deploy-trigger', parameters: [
                        string(name: 'component', value: 'command'),
                        string(name: 'phase', value: 'dev'),
                        string(name: 'tag', value: NOW)
                    ]
            }
        }
    }
}
