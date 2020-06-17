 // Copyright (c) 2020, Oracle Corporation and/or its affiliates. 

pipeline {
    options {
      disableConcurrentBuilds()
    }

    agent {
        label 'VM.Standard2.8'
    }

    environment {
        DOCKER_CREDS = credentials('github-markxnelns-private-access-token')
        // image names and tags are created from these variables:
        REPO = 'docker.pkg.github.com/verrazzano/demo-apps'
        BOBBYS_HELIDON = 'bobbys-helidon-stock-application'
        VERSION = '0.1.0'
    }

    stages {
        stage('Build Bobbys Helidon Stock Application') {
            steps {
                sh """
                    echo "${DOCKER_CREDS_PSW}" | docker login docker.pkg.github.com -u ${DOCKER_CREDS_USR} --password-stdin
                    cd bobs-books/bobbys-books/bobbys-helidon-stock-application
                    mvn clean install
                    docker build --force-rm=true -f Dockerfile -t ${env.REPO}/${env.BOBBYS_HELIDON}:${env.VERSION} .
                    docker push ${env.REPO}/${env.BOBBYS_HELIDON}:${env.VERSION}
                """
            }
        }

    }
}

