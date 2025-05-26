def call() {
    node('agent1') {
        tools {
            jdk "java-8"
        }

        withCredentials([usernamePassword(credentialsId: 'docker-user', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {

            stage('Login to DockerHub') {
                sh "docker login -u ${env.DOCKER_USER} -p ${env.DOCKER_PASS}"
            }

            stage('Clone Repositories') {
                sh "rm -rf java"
                sh "git clone --branch master https://github.com/oelghareeb/java.git java"

                sh "rm -rf python"
                sh "git clone --branch main https://github.com/oelghareeb/python-CI-CD.git python"
            }

            stage('Build and Push Docker Images') {
                parallel(
                    "Java Image": {
                        dir('java') {
                            sh "mvn clean package -DskipTests"
                            sh "docker build -t oelghareeb/java-app-groovyscript:latest ."
                            sh "docker push oelghareeb/java-app-groovyscript:latest"
                        }
                    },
                    "Python Image": {
                        dir('python') {
                            sh "docker build -t oelghareeb/python-app-groovyscript:latest ."
                            sh "docker push oelghareeb/python-app-groovyscript:latest"
                        }
                    }
                )
            }
        }
    }
}
