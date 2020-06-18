 // Copyright (c) 2020, Oracle Corporation and/or its affiliates.

pipeline {
    options {
      disableConcurrentBuilds()
    }

    agent {
        docker {
            image "${RUNNER_DOCKER_IMAGE}"
            args "${RUNNER_DOCKER_ARGS} -v /build-shared-files:/build-shared-files"
            registryUrl "${RUNNER_DOCKER_REGISTRY_URL}"
            registryCredentialsId 'ocir-pull-and-push-account'
            label 'small'
        }
    }

    environment {
        DOCKER_CREDS = credentials('github-markxnelns-private-access-token')
        // image names and tags are created from these variables:
        REPO = 'docker.pkg.github.com/verrazzano/demo-apps'
        BOBBYS_HELIDON = 'bobbys-helidon-stock-application'
        ROBERTS_HELIDON = 'roberts-helidon-stock-application'
        BOBBYS_COHERENCE = 'bobbys-coherence'
        ROBERTS_COHERENCE = 'roberts-coherence'
        BOBBYS_WEBLOGIC = 'bobbys-front-end'
        BOBS_WEBLOGIC = 'bobs-bookstore-order-manager'
        VERSION = '0.1.0'

        // access to Oracle Maven Repository
        MAVEN_SETTINGS_SECURITY = credentials('oracle-maven-settings-security')
        MAVEN_SETTINGS = credentials('oracle-maven-settings')
    }

    stages {
        stage('Prepare Environment') {
            steps {
                sh """
                    mkdir -p $HOME/.m2/repository/com
                    cp $MAVEN_SETTINGS_SECURITY $HOME/.m2/settings-security.xml
                    tar xz -C $HOME/.m2/repository/com -f /build-shared-files/oracle-maven.tar.gz
                    sudo yum -y install wget
                """
            }
        }

        stage('Build Bobbys Coherence Application') {
            steps {
                sh """
                    echo "${DOCKER_CREDS_PSW}" | docker login docker.pkg.github.com -u ${DOCKER_CREDS_USR} --password-stdin
                    cd bobs-books/bobbys-books/bobbys-coherence
                    mvn -B -s $MAVEN_SETTINGS clean deploy
                    docker build --force-rm=true -f Dockerfile -t ${env.REPO}/${env.BOBBYS_COHERENCE}:${env.VERSION} .
                    docker push ${env.REPO}/${env.BOBBYS_COHERENCE}:${env.VERSION}
                """
            }
        }

        stage('Build Bobbys Helidon Stock Application') {
            steps {
                sh """
                    echo "${DOCKER_CREDS_PSW}" | docker login docker.pkg.github.com -u ${DOCKER_CREDS_USR} --password-stdin
                    cd bobs-books/bobbys-books/bobbys-helidon-stock-application
                    mvn -X -B -s $MAVEN_SETTINGS clean deploy \
                        -DaltDeploymentRepository=snapshotRepository::default::https://maven.pkg.github.com/verrazzano/demo-apps
                    docker build --force-rm=true -f Dockerfile -t ${env.REPO}/${env.BOBBYS_HELIDON}:${env.VERSION} .
                    docker push ${env.REPO}/${env.BOBBYS_HELIDON}:${env.VERSION}
                """
            }
        }

        stage('Build Bobbys Front-end WebLogic Application') {
            steps {
                sh """
                    echo "${DOCKER_CREDS_PSW}" | docker login docker.pkg.github.com -u ${DOCKER_CREDS_USR} --password-stdin
                    cd bobs-books/bobbys-books/bobbys-front-end
                    mvn -X -B -s $MAVEN_SETTINGS clean deploy
                    cd deploy
                    ./build.sh ${env.REPO}/${env.BOBBYS_WEBLOGIC}:${env.VERSION}
                    docker push ${env.REPO}/${env.BOBBYS_WEBLOGIC}:${env.VERSION}
                """
            }
        }

        stage('Build Bobs Backend WebLogic Application') {
            steps {
                sh """
                    echo "${DOCKER_CREDS_PSW}" | docker login docker.pkg.github.com -u ${DOCKER_CREDS_USR} --password-stdin
                    cd bobs-books/bobs-bookstore-order-manager
                    mvn -B -s $MAVEN_SETTINGS clean deploy
                    cd deploy
                    ./build.sh ${env.REPO}/${env.BOBS_WEBLOGIC}:${env.VERSION}
                    docker push ${env.REPO}/${env.BOBS_WEBLOGIC}:${env.VERSION}
                """
            }
        }

        stage('Build Roberts Coherence Application') {
            steps {
                sh """
                    echo "${DOCKER_CREDS_PSW}" | docker login docker.pkg.github.com -u ${DOCKER_CREDS_USR} --password-stdin
                    cd bobs-books/roberts-books/roberts-coherence
                    mvn -B -s $MAVEN_SETTINGS clean deploy
                    docker build --force-rm=true -f Dockerfile -t ${env.REPO}/${env.ROBERTS_COHERENCE}:${env.VERSION} .
                    docker push ${env.REPO}/${env.ROBERTS_COHERENCE}:${env.VERSION}
                """
            }
        }

        stage('Build Roberts Helidon Stock Application') {
            steps {
                sh """
                    echo "${DOCKER_CREDS_PSW}" | docker login docker.pkg.github.com -u ${DOCKER_CREDS_USR} --password-stdin
                    cd bobs-books/roberts-books/roberts-helidon-stock-application
                    mvn -B -s $MAVEN_SETTINGS clean deploy
                    docker build --force-rm=true -f Dockerfile -t ${env.REPO}/${env.ROBERTS_HELIDON}:${env.VERSION} .
                    docker push ${env.REPO}/${env.ROBERTS_HELIDON}:${env.VERSION}
                """
            }
        }
    }
}

