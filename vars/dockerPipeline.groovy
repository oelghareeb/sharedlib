def call() {
    def dockerx = new org.lab3.docker()
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
            stage('Login to DockerHub') {
                steps {
                    script {
                        dockerx.login(env.DOCKER_USER, env.DOCKER_PASS)
                    }
                }
            }

            stage('Clone Repositories') {
                steps {
                    script {
                        dockerx.gitClone('https://github.com/oelghareeb/java.git', 'master', 'java')
                        dockerx.gitClone('https://github.com/oelghareeb/python-CI-CD.git', 'main', 'python')
                    }
                }
            }

            stage('Build and Push Docker Images') {
                parallel {
                    stage('Java Image') {
                        steps {
                            script {
                                dockerx.buildJava()
                                dockerx.push('oelghareeb/java-app', 'latest')
                            }
                        }
                    }

                    stage('Python Image') {
                        steps {
                            dir('python') {
                                script {
                                    dockerx.build('oelghareeb/python-app', 'latest')
                                    dockerx.push('oelghareeb/python-app', 'latest')
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
