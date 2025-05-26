package org.lab3;

def login(USERNAME, PASSWORD){
    sh "docker login -u ${USERNAME} -p ${PASSWORD}"
}

def build(IMAGE_NAME, IMAGE_TAG){
    sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
}

def push(IMAGE_NAME, IMAGE_TAG){
    sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
}

def gitClone(String repoUrl, String branch = 'main', String targetDir = '.') {
    sh "rm -rf ${targetDir}"
    sh "git clone --branch ${branch} ${repoUrl} ${targetDir}"
}

// build the Java app Docker image due to issue in the building
def buildJava(){
    dir('java') {
        // Build the jar using Maven
        sh "mvn clean package -DskipTests"
        // Now build the docker image, with current dir as context so Docker can find target/demo1-0.0.1-SNAPSHOT.jar
        sh "docker build -t oelghareeb/java-app:latest ."
    }
}
