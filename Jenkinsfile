 // Copyright (c) 2020, Oracle Corporation and/or its affiliates.

pipeline {
    options {
      disableConcurrentBuilds()
    }

    agent {
            docker {
            image "${RUNNER_DOCKER_IMAGE}"
            args "${RUNNER_DOCKER_ARGS}"
            registryUrl "${RUNNER_DOCKER_REGISTRY_URL}"
            registryCredentialsId 'ocir-pull-and-push-account'
        }
    }

    environment {
        DOCKER_CREDS = credentials('github-markxnelns-private-access-token')
        // image names and tags are created from these variables:
        REPO = 'docker.pkg.github.com/verrazzano/demo-apps'
        BOBBYS_HELIDON = 'bobbys-helidon-stock-application'
        VERSION = '0.1.0'

        // access to Oracle Maven Repository
        MAVEN_SETTINGS_SECURITY = credentials('oracle-maven-settings-security')
        MAVEN_SETTINGS = credentials('oracle-maven-settings')
    }

    stages {
        stage('Prepare Maven Settings') {
            steps {
                sh """"
                    mkdir -p $HOME/.m2
                    cp $MAVEN_SETTINGS_SECURITY $HOME/.m2/settings-security.xml
                """
            }
        }

        stage('Build Bobbys Helidon Stock Application') {
            steps {
                sh """
                    echo "${DOCKER_CREDS_PSW}" | docker login docker.pkg.github.com -u ${DOCKER_CREDS_USR} --password-stdin
                    cd bobs-books/bobbys-books/bobbys-helidon-stock-application
                    mvn -s $MAVEN_SETTINGS clean install
                    docker build --force-rm=true -f Dockerfile -t ${env.REPO}/${env.BOBBYS_HELIDON}:${env.VERSION} .
                    docker push ${env.REPO}/${env.BOBBYS_HELIDON}:${env.VERSION}
                """
            }
        }

    }
}

