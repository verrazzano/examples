// Copyright (c) 2020, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

pipeline {

    agent {
        docker {
            image "${RUNNER_DOCKER_IMAGE}"
            args "${RUNNER_DOCKER_ARGS}"
            registryUrl "${RUNNER_DOCKER_REGISTRY_URL}"
            registryCredentialsId 'ocir-pull-and-push-account'
            label 'small'
        }
    }

    environment {
        DOCKER_CREDS = credentials('github-markxnelns-private-access-token')
        OCR_CREDS = credentials('ocr-pull-and-push-account')

        // image names and tags are created from these variables:
        REPO = 'docker.pkg.github.com/verrazzano/examples'
        BOBBYS_HELIDON = 'bobbys-helidon-stock-application'
        ROBERTS_HELIDON = 'roberts-helidon-stock-application'
        BOBBYS_COHERENCE = 'bobbys-coherence'
        ROBERTS_COHERENCE = 'roberts-coherence'
        BOBBYS_WEBLOGIC = 'bobbys-front-end'
        BOBS_WEBLOGIC = 'bobs-bookstore-order-manager'
        HELLO_HELIDON_V1 = 'helidon-greet-app-v1'
        HELLO_HELIDON_V2 = 'helidon-greet-app-v2'
        VERSION = '0.1.4'

        // secrets used during build
        BOB_DB_PASSWORD = credentials('bobs-bookstore-db-password')
        BOB_ADMIN_PASSWORD = credentials('bobs-bookstore-admin-password')

        // access to GitHub Packages Maven Repository
        MAVEN_SETTINGS = credentials('oracle-maven-settings')
    }

    stages {
        stage('Prepare Environment') {
            steps {
                getMavenSeedData '/build-shared-files'
                sh """
                    mkdir -p $HOME/.m2/repository/com
                    tar xz -C $HOME/.m2/repository/com -f /build-shared-files/oracle-maven.tar.gz
                    sudo yum -y install wget
                """
            }
        }

        stage('Copyright Compliance Check') {
            when { not { buildingTag() } }
            steps {
                copyrightScan "${WORKSPACE}"
            }
        }

        stage('Build Hello Helidon V1 Application') {
            steps {
                sh """
                    echo "${DOCKER_CREDS_PSW}" | docker login docker.pkg.github.com -u ${DOCKER_CREDS_USR} --password-stdin
                    cd hello-helidon/helidon-app-greet-v1
                    mvn -B -s $MAVEN_SETTINGS clean deploy
                    docker build --force-rm=true -f Dockerfile -t ${env.REPO}/${env.HELLO_HELIDON_V1}:${env.VERSION} .
                    docker push ${env.REPO}/${env.HELLO_HELIDON_V1}:${env.VERSION}
                """
            }
        }

        stage('Scan Hello Helidon V1 Application') {
            steps {
                script {
                    clairScanTemp "${env.REPO}/${env.HELLO_HELIDON_V1}:${env.VERSION}"
                }
                sh "mv scanning-report.json hello_helidon_v1.scanning-report.json"
            }
            post {
                always {
                    archiveArtifacts artifacts: '**/*scanning-report.json', allowEmptyArchive: true
                }
            }
       }

        stage('Build Hello Helidon V2 Application') {
            steps {
                sh """
                    echo "${DOCKER_CREDS_PSW}" | docker login docker.pkg.github.com -u ${DOCKER_CREDS_USR} --password-stdin
                    cd hello-helidon/helidon-app-greet-v2
                    mvn -B -s $MAVEN_SETTINGS clean deploy
                    docker build --force-rm=true -f Dockerfile -t ${env.REPO}/${env.HELLO_HELIDON_V2}:${env.VERSION} .
                    docker push ${env.REPO}/${env.HELLO_HELIDON_V2}:${env.VERSION}
                """
            }
        }

        stage('Scan Hello Helidon V2 Application') {
            steps {
                script {
                    clairScanTemp "${env.REPO}/${env.HELLO_HELIDON_V2}:${env.VERSION}"
                }
                sh "mv scanning-report.json hello_helidon_v2.scanning-report.json"
            }
            post {
                always {
                    archiveArtifacts artifacts: '**/*scanning-report.json', allowEmptyArchive: true
                }
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

        stage('Scan Bobbys Coherence Application') {
            steps {
                script {
                    clairScanTemp "${env.REPO}/${env.BOBBYS_COHERENCE}:${env.VERSION}"
                }
                sh "mv scanning-report.json bobby_coherence.scanning-report.json"
            }
            post {
                always {
                    archiveArtifacts artifacts: '**/*scanning-report.json', allowEmptyArchive: true
                }
            }
        }

        stage('Build Bobbys Helidon Stock Application') {
            steps {
                sh """
                    echo "${DOCKER_CREDS_PSW}" | docker login docker.pkg.github.com -u ${DOCKER_CREDS_USR} --password-stdin
                    cd bobs-books/bobbys-books/bobbys-helidon-stock-application
                    mvn -B -s $MAVEN_SETTINGS clean deploy 
                    docker build --force-rm=true -f Dockerfile -t ${env.REPO}/${env.BOBBYS_HELIDON}:${env.VERSION} .
                    docker push ${env.REPO}/${env.BOBBYS_HELIDON}:${env.VERSION}
                """
            }
        }

        stage('Scan Bobbys Helidon Stock Application') {
            steps {
                script {
                    clairScanTemp "${env.REPO}/${env.BOBBYS_HELIDON}:${env.VERSION}"
                }
                sh "mv scanning-report.json bobby_helidon.scanning-report.json"
            }
            post {
                always {
                    archiveArtifacts artifacts: '**/*scanning-report.json', allowEmptyArchive: true
                }
            }
        }

        stage('Build Bobbys Front-end WebLogic Application') {
            steps {
                sh """
                    echo "${DOCKER_CREDS_PSW}" | docker login docker.pkg.github.com -u ${DOCKER_CREDS_USR} --password-stdin
                    echo "${OCR_CREDS_PSW}" | docker login container-registry.oracle.com -u ${OCR_CREDS_USR} --password-stdin
                    cd bobs-books/bobbys-books/bobbys-front-end
                    mvn -B -s $MAVEN_SETTINGS clean deploy
                    cd deploy
                    ./build.sh ${env.REPO}/${env.BOBBYS_WEBLOGIC}:${env.VERSION}
                    docker push ${env.REPO}/${env.BOBBYS_WEBLOGIC}:${env.VERSION}
                """
            }
        }

        stage('Scan Bobbys Front-end WebLogic Application') {
            steps {
                script {
                    clairScanTemp "${env.REPO}/${env.BOBBYS_WEBLOGIC}:${env.VERSION}"
                }
                sh "mv scanning-report.json bobby_weblogic.scanning-report.json"
            }
            post {
                always {
                    archiveArtifacts artifacts: '**/*scanning-report.json', allowEmptyArchive: true
                }
            }
        }

        stage('Build Bobs Backend WebLogic Application') {
            steps {
                sh """
                    echo "${DOCKER_CREDS_PSW}" | docker login docker.pkg.github.com -u ${DOCKER_CREDS_USR} --password-stdin
                    echo "${OCR_CREDS_PSW}" | docker login container-registry.oracle.com -u ${OCR_CREDS_USR} --password-stdin
                    cd bobs-books/bobs-bookstore-order-manager
                    mvn -B -s $MAVEN_SETTINGS clean deploy
                    cd deploy
                    echo 'Update passwords from Jenkins secrets'
                    sed -i -e "s|XX_DB_PASSWORD_XX|${env.BOB_DB_PASSWORD}|g" properties/docker-build/bobs-bookstore-topology.properties.encoded
                    sed -i -e "s|XX_ADMIN_PASSWORD_XX|${env.BOB_ADMIN_PASSWORD}|g" properties/docker-build/bobs-bookstore-topology.properties.encoded
                    ./build.sh ${env.REPO}/${env.BOBS_WEBLOGIC}:${env.VERSION}
                    docker push ${env.REPO}/${env.BOBS_WEBLOGIC}:${env.VERSION}
                """
            }
        }

        stage('Scan Bobs Backend WebLogic Application') {
            steps {
                script {
                    clairScanTemp "${env.REPO}/${env.BOBS_WEBLOGIC}:${env.VERSION}"
                }
                sh "mv scanning-report.json bobs_weblogic.scanning-report.json"
            }
            post {
                always {
                    archiveArtifacts artifacts: '**/*scanning-report.json', allowEmptyArchive: true
                }
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

        stage('Scan Roberts Coherence Application') {
            steps {
                script {
                    clairScanTemp "${env.REPO}/${env.ROBERTS_COHERENCE}:${env.VERSION}"
                }
                sh "mv scanning-report.json roberts_coherence.scanning-report.json"
            }
            post {
                always {
                    archiveArtifacts artifacts: '**/*scanning-report.json', allowEmptyArchive: true
                }
            }
        }

        stage('Build Roberts Helidon Stock Application') {
            steps {
                sh """
                    echo "${DOCKER_CREDS_PSW}" | docker login docker.pkg.github.com -u ${DOCKER_CREDS_USR} --password-stdin
                    cd bobs-books/roberts-books/roberts-helidon-stock-application/src/main/web
                    npm install
                    cd ../../..
                    mvn -B -s $MAVEN_SETTINGS clean deploy
                    docker build --force-rm=true -f Dockerfile -t ${env.REPO}/${env.ROBERTS_HELIDON}:${env.VERSION} .
                    docker push ${env.REPO}/${env.ROBERTS_HELIDON}:${env.VERSION}
                """
            }
        }

        stage('Scan Roberts Helidon Stock Application') {
            steps {
                script {
                    clairScanTemp "${env.REPO}/${env.ROBERTS_HELIDON}:${env.VERSION}"
                }
                sh "mv scanning-report.json roberts_helidon.scanning-report.json"
            }
            post {
                always {
                    archiveArtifacts artifacts: '**/*scanning-report.json', allowEmptyArchive: true
                }
            }
        }
    }
}

