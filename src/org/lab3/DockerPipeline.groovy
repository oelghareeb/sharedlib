package org.lab3;

class DockerPipeline implements Serializable {
    def script

    DockerPipeline(script) {
        this.script = script
    }

    def runPipeline() {
        script.node('agent1') {
            script.tools {
                jdk "java-8"
            }

            script.withCredentials([
                script.usernamePassword(credentialsId: 'docker-user', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')
            ]) {

                script.stage('Login to DockerHub') {
                    login(script.env.DOCKER_USER, script.env.DOCKER_PASS)
                }

                script.stage('Clone Repositories') {
                    gitClone('https://github.com/oelghareeb/java.git', 'master', 'java')
                    gitClone('https://github.com/oelghareeb/python-CI-CD.git', 'main', 'python')
                }

                script.stage('Build and Push Docker Images') {
                    script.parallel(
                        "Java Image": {
                            buildJava()
                            push('oelghareeb/java-app-groovyscript', 'latest')
                        },
                        "Python Image": {
                            script.dir('python') {
                                build('oelghareeb/python-app-groovyscript', 'latest')
                                push('oelghareeb/python-app-groovyscript', 'latest')
                            }
                        }
                    )
                }
            }
        }
    }

    def login(USERNAME, PASSWORD) {
        script.sh "docker login -u ${USERNAME} -p ${PASSWORD}"
    }

    def build(IMAGE_NAME, IMAGE_TAG) {
        script.sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
    }

    def push(IMAGE_NAME, IMAGE_TAG) {
        script.sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
    }

    def gitClone(String repoUrl, String branch = 'main', String targetDir = '.') {
        script.sh "rm -rf ${targetDir}"
        script.sh "git clone --branch ${branch} ${repoUrl} ${targetDir}"
    }

    def buildJava() {
        script.dir('java') {
            script.sh "mvn clean package -DskipTests"
            script.sh "docker build -t oelghareeb/java-app-groovyscript:latest ."
        }
    }
}
