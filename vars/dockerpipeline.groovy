// vars/dockerpipeline.groovy

def call() {
    pipeline {
        agent {
            label 'agent1'
        }

        tools {
            jdk "java-8"
        }

        environment {
            DOCKER_USER = credentials('docker-user')
            DOCKER_PASS = credentials('docker-pass')
        }

        stages {
            stage('Clean Workspace') {
                steps {
                    sh 'rm -rf java python'
                }
            }

            stage('Login to DockerHub') {
                steps {
                    sh """
                        echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin
                    """
                }
            }

            stage('Clone Repositories') {
                steps {
                    sh '''
                        git clone -b master https://github.com/oelghareeb/java.git java
                        git clone -b main https://github.com/oelghareeb/python-CI-CD.git python
                    '''
                }
            }

            stage('Build and Push Docker Images') {
                parallel {
                    stage('Java Image') {
                        steps {
                            dir('java') {
                                sh '''
                                    mvn clean package -DskipTests
                                    docker build -t oelghareeb/java-app .
                                    docker push oelghareeb/java-app:latest
                                '''
                            }
                        }
                    }

                    stage('Python Image') {
                        steps {
                            dir('python') {
                                sh '''
                                    docker build -t oelghareeb/python-app .
                                    docker push oelghareeb/python-app:latest
                                '''
                            }
                        }
                    }
                }
            }
        }
    }
}
