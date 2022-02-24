// Copyright (c) 2020, 2022, Oracle and/or its affiliates.
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

    parameters {
        string (name: 'BASE_TAG',
                defaultValue: '1.0.0-1',
                description: 'Base value used as part of generated image tag',
                trim: true)
    }

    environment {
        DOCKER_CREDS = credentials('github-packages-credentials-rw')
        OCR_CREDS = credentials('ocr-pull-and-push-account')
        NETRC_FILE = credentials('netrc')

        OCI_CLI_TENANCY = credentials('oci-tenancy')
        OCI_CLI_USER = credentials('oci-user-ocid')
        OCI_CLI_FINGERPRINT = credentials('oci-api-key-fingerprint')
        OCI_CLI_KEY_FILE = credentials('oci-api-key')
        OCI_CLI_REGION = 'us-phoenix-1'

        // image names and tags are created from these variables:
        REPO = 'ghcr.io/verrazzano'
        BOBBYS_HELIDON = 'example-bobbys-helidon-stock-application'
        ROBERTS_HELIDON = 'example-roberts-helidon-stock-application'
        BOBBYS_COHERENCE = 'example-bobbys-coherence'
        ROBERTS_COHERENCE = 'example-roberts-coherence'
        BOBBYS_WEBLOGIC = 'example-bobbys-front-end'
        BOBS_WEBLOGIC = 'example-bobs-bookstore-order-manager'
        TODO_WEBLOGIC = 'example-todo'
        HELLO_HELIDON_V1 = 'example-helidon-greet-app-v1'
        HELLO_HELIDON_V2 = 'example-helidon-greet-app-v2'
        SPRING_SAMPLE = 'example-springboot'
        VERSION = get_image_tag()

        // secrets used during build
        BOB_DB_PASSWORD = credentials('bobs-bookstore-db-password')
        BOB_ADMIN_PASSWORD = credentials('bobs-bookstore-admin-password')

        // access to GitHub Packages Maven Repository
        MAVEN_SETTINGS = credentials('oracle-maven-settings')

        BUCKET_NAME = "build-shared-files"
        JDK14_BUNDLE = "openjdk-14.0.2_linux-x64_bin.tar.gz"
        JDK11_BUNDLE = "openjdk-11+28_linux-x64_bin.tar.gz"
        IMAGETOOL_BUNDLE = "imagetool.zip"
    }

    stages {
        stage('Initialize') {
            steps {
                sh """
                    find $WORKSPACE -mindepth 1 -maxdepth 1 | xargs rm -rf
                """
                sh """
                    cp -f "${NETRC_FILE}" $HOME/.netrc
                    chmod 600 $HOME/.netrc
                """
            }
        }

        stage('Default checkout') {
            steps {
                script {
                    def scmURL = scm.getUserRemoteConfigs()[0].getUrl()
                    defaultCheckoutTargetDir = scmURL.replaceAll(/^.*\//,'').replaceAll(/\.git$/, '')
                }
                checkout([
                    $class: 'GitSCM',
                    branches: scm.branches,
                    extensions: scm.extensions + [[$class: 'RelativeTargetDirectory', relativeTargetDir: defaultCheckoutTargetDir]],
                    userRemoteConfigs: scm.userRemoteConfigs
                ])
            }
        }

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
                copyrightScan "${WORKSPACE}/examples"
            }
        }

        stage('Parallel builds') {
            parallel {
                stage ('Bobbys Books') {
                    stages {
                        stage('Build Bobbys Coherence Application') {
                            steps {
                                sh """
                                    echo "${DOCKER_CREDS_PSW}" | docker login ghcr.io -u ${DOCKER_CREDS_USR} --password-stdin
                                    cd examples/bobs-books/bobbys-books/bobbys-coherence
                                    mvn -B -s $MAVEN_SETTINGS clean install
                                    oci os object get -bn ${BUCKET_NAME} --file ${JDK14_BUNDLE} --name ${JDK14_BUNDLE}
                                    docker build --build-arg JDK_BINARY=${JDK14_BUNDLE} --force-rm=true -f Dockerfile -t ${env.REPO}/${env.BOBBYS_COHERENCE}:${env.VERSION} .
                                    docker push ${env.REPO}/${env.BOBBYS_COHERENCE}:${env.VERSION}
                                """
                            }
                        }

                        stage('Scan Bobbys Coherence Application') {
                            steps {
                                scanContainerImage("${env.REPO}/${env.BOBBYS_COHERENCE}:${env.VERSION}", "bobby_coherence.scanning-report.json")
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
                                    echo "${DOCKER_CREDS_PSW}" | docker login ghcr.io -u ${DOCKER_CREDS_USR} --password-stdin
                                    cd examples/bobs-books/bobbys-books/bobbys-helidon-stock-application
                                    mvn -B -s $MAVEN_SETTINGS -Dmaven.compiler.fork=true -Dmaven.compiler.executable=\${JAVA_11_HOME}/bin/javac clean install
                                    oci os object get -bn ${BUCKET_NAME} --file ${JDK14_BUNDLE} --name ${JDK14_BUNDLE}
                                    docker build --build-arg JDK_BINARY=${JDK14_BUNDLE} --force-rm=true -f Dockerfile -t ${env.REPO}/${env.BOBBYS_HELIDON}:${env.VERSION} .
                                    docker push ${env.REPO}/${env.BOBBYS_HELIDON}:${env.VERSION}
                                """
                            }
                        }

                        stage('Scan Bobbys Helidon Stock Application') {
                            steps {
                                scanContainerImage("${env.REPO}/${env.BOBBYS_HELIDON}:${env.VERSION}", "bobby_helidon.scanning-report.json")
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
                                    echo "${DOCKER_CREDS_PSW}" | docker login ghcr.io -u ${DOCKER_CREDS_USR} --password-stdin
                                    echo "${OCR_CREDS_PSW}" | docker login container-registry.oracle.com -u ${OCR_CREDS_USR} --password-stdin
                                    cd examples/bobs-books/bobbys-books/bobbys-front-end
                                    mvn -B -s $MAVEN_SETTINGS clean install
                                    cd deploy
                                    ./build.sh  ${env.REPO}/${env.BOBBYS_WEBLOGIC}:${env.VERSION}
                                    docker push ${env.REPO}/${env.BOBBYS_WEBLOGIC}:${env.VERSION}
                                """
                            }
                        }

                        stage('Scan Bobbys Front-end WebLogic Application') {
                            steps {
                                scanContainerImage("${env.REPO}/${env.BOBBYS_WEBLOGIC}:${env.VERSION}", "bobby_weblogic.scanning-report.json")
                            }
                            post {
                                always {
                                    archiveArtifacts artifacts: '**/*scanning-report.json', allowEmptyArchive: true
                                }
                            }
                        }
                    }
                }

                stage('Bobs Backend') {
                    stages {
                        stage('Build Bobs Backend WebLogic Application') {
                            steps {
                                sh """
                                    # sleep 2 minutes to workaround WIT doing a docker prune at the same time
                                    # once WIT has a fix this can be removed
                                    sleep 2m
                                    echo "${DOCKER_CREDS_PSW}" | docker login ghcr.io -u ${DOCKER_CREDS_USR} --password-stdin
                                    echo "${OCR_CREDS_PSW}" | docker login container-registry.oracle.com -u ${OCR_CREDS_USR} --password-stdin
                                    cd examples/bobs-books/bobs-bookstore-order-manager
                                    mvn -B -s $MAVEN_SETTINGS clean install
                                    cd deploy
                                    ./build.sh ${env.REPO}/${env.BOBS_WEBLOGIC}:${env.VERSION}
                                    docker push ${env.REPO}/${env.BOBS_WEBLOGIC}:${env.VERSION}
                                """
                            }
                        }

                        stage('Scan Bobs Backend WebLogic Application') {
                            steps {
                                scanContainerImage("${env.REPO}/${env.BOBS_WEBLOGIC}:${env.VERSION}", "bobs_weblogic.scanning-report.json")
                            }
                            post {
                                always {
                                    archiveArtifacts artifacts: '**/*scanning-report.json', allowEmptyArchive: true
                                }
                            }
                        }
                    }
                }

                stage('Roberts Books') {
                    stages {
                        stage('Build Roberts Coherence Application') {
                            steps {
                                sh """
                                    echo "${DOCKER_CREDS_PSW}" | docker login ghcr.io -u ${DOCKER_CREDS_USR} --password-stdin
                                    cd examples/bobs-books/roberts-books/roberts-coherence
                                    mvn -B -s $MAVEN_SETTINGS clean install
                                    oci os object get -bn ${BUCKET_NAME} --file ${JDK14_BUNDLE} --name ${JDK14_BUNDLE}
                                    docker build --build-arg JDK_BINARY=${JDK14_BUNDLE} --force-rm=true -f Dockerfile -t ${env.REPO}/${env.ROBERTS_COHERENCE}:${env.VERSION} .
                                    docker push ${env.REPO}/${env.ROBERTS_COHERENCE}:${env.VERSION}
                                """
                            }
                        }

                        stage('Scan Roberts Coherence Application') {
                            steps {
                                scanContainerImage("${env.REPO}/${env.ROBERTS_COHERENCE}:${env.VERSION}", "roberts_coherence.scanning-report.json")
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
                                    echo "${DOCKER_CREDS_PSW}" | docker login ghcr.io -u ${DOCKER_CREDS_USR} --password-stdin
                                    cd examples/bobs-books/roberts-books/roberts-helidon-stock-application/src/main/web
                                    npm install
                                    cd ../../..
                                    mvn -B -s $MAVEN_SETTINGS -Dmaven.compiler.fork=true -Dmaven.compiler.executable=\${JAVA_11_HOME}/bin/javac clean install
                                    oci os object get -bn ${BUCKET_NAME} --file ${JDK14_BUNDLE} --name ${JDK14_BUNDLE}
                                    docker build --build-arg JDK_BINARY=${JDK14_BUNDLE} --force-rm=true -f Dockerfile -t ${env.REPO}/${env.ROBERTS_HELIDON}:${env.VERSION} .
                                    docker push ${env.REPO}/${env.ROBERTS_HELIDON}:${env.VERSION}
                                """
                            }
                        }

                        stage('Scan Roberts Helidon Stock Application') {
                            steps {
                                scanContainerImage("${env.REPO}/${env.ROBERTS_HELIDON}:${env.VERSION}", "roberts_helidon.scanning-report.json")
                            }
                            post {
                                always {
                                    archiveArtifacts artifacts: '**/*scanning-report.json', allowEmptyArchive: true
                                }
                            }
                        }
                    }
                }

                stage('Hello Helidon') {
                    stages {
                        stage('Build Hello Helidon V1 Application') {
                            steps {
                                sh """
                                    echo "${DOCKER_CREDS_PSW}" | docker login ghcr.io -u ${DOCKER_CREDS_USR} --password-stdin
                                    cd examples/hello-helidon/helidon-app-greet-v1
                                    mvn -B -s $MAVEN_SETTINGS -Dmaven.compiler.fork=true -Dmaven.compiler.executable=\${JAVA_11_HOME}/bin/javac clean install
                                    oci os object get -bn ${BUCKET_NAME} --file ${JDK14_BUNDLE} --name ${JDK14_BUNDLE}
                                    docker image build --build-arg JDK_BINARY=${JDK14_BUNDLE} -t ${env.REPO}/${env.HELLO_HELIDON_V1}:${env.VERSION} .
                                    docker push ${env.REPO}/${env.HELLO_HELIDON_V1}:${env.VERSION}
                                """
                            }
                        }

                        stage('Scan Hello Helidon V1 Application') {
                            steps {
                                scanContainerImage("${env.REPO}/${env.HELLO_HELIDON_V1}:${env.VERSION}", "hello_helidon_v1.scanning-report.json")
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
                                    echo "${DOCKER_CREDS_PSW}" | docker login ghcr.io -u ${DOCKER_CREDS_USR} --password-stdin
                                    cd examples/hello-helidon/helidon-app-greet-v2
                                    mvn -B -s $MAVEN_SETTINGS -Dmaven.compiler.fork=true -Dmaven.compiler.executable=\${JAVA_11_HOME}/bin/javac clean install
                                    oci os object get -bn ${BUCKET_NAME} --file ${JDK14_BUNDLE} --name ${JDK14_BUNDLE}
                                    docker image build --build-arg JDK_BINARY=${JDK14_BUNDLE} -t ${env.REPO}/${env.HELLO_HELIDON_V2}:${env.VERSION} .
                                    docker push ${env.REPO}/${env.HELLO_HELIDON_V2}:${env.VERSION}
                                """
                            }
                        }

                        stage('Scan Hello Helidon V2 Application') {
                            steps {
                                scanContainerImage("${env.REPO}/${env.HELLO_HELIDON_V2}:${env.VERSION}", "hello_helidon_v2.scanning-report.json")
                            }
                            post {
                                always {
                                    archiveArtifacts artifacts: '**/*scanning-report.json', allowEmptyArchive: true
                                }
                            }
                        }
                    }
                }
                
                stage ('TODO List') {
                    stages {
                        stage('Build TODO List WebLogic Application') {
                            steps {
                                sh """
                                    echo "${DOCKER_CREDS_PSW}" | docker login ghcr.io -u ${DOCKER_CREDS_USR} --password-stdin
                                    echo "${OCR_CREDS_PSW}" | docker login container-registry.oracle.com -u ${OCR_CREDS_USR} --password-stdin
                                    cd examples/todo-list
                                    mvn -B -s $MAVEN_SETTINGS clean install
                                    cd setup
                                    ./build.sh ${env.REPO}/${env.TODO_WEBLOGIC}:${env.VERSION}
                                    docker push ${env.REPO}/${env.TODO_WEBLOGIC}:${env.VERSION}
                                """
                            }
                        }

                        stage('Scan TODO List WebLogic Application') {
                            steps {
                                scanContainerImage("${env.REPO}/${env.TODO_WEBLOGIC}:${env.VERSION}", "todo_weblogic.scanning-report.json")
                            }
                            post {
                                always {
                                    archiveArtifacts artifacts: '**/*scanning-report.json', allowEmptyArchive: true
                                }
                            }
                        }
                    }
                }

                stage('Spring sample') {
                    stages {
                        stage('Build Spring sample application') {
                            steps {
                                sh """
                                    echo "${DOCKER_CREDS_PSW}" | docker login ghcr.io -u ${DOCKER_CREDS_USR} --password-stdin
                                    echo "${OCR_CREDS_PSW}" | docker login container-registry.oracle.com -u ${OCR_CREDS_USR} --password-stdin
                                    cd examples/springboot-app
                                    mvn -B -s $MAVEN_SETTINGS clean install
                                    oci os object get -bn ${BUCKET_NAME} --file ${JDK11_BUNDLE} --name ${JDK11_BUNDLE}
                                    docker build -t ${env.REPO}/${env.SPRING_SAMPLE}:${env.VERSION} .
                                    docker push ${env.REPO}/${env.SPRING_SAMPLE}:${env.VERSION}
                                """
                            }
                        }

                        stage('Scan Spring sample Application') {
                            steps {
                                scanContainerImage("${env.REPO}/${env.SPRING_SAMPLE}:${env.VERSION}", "spring_sample.scanning-report.json")
                            }
                            post {
                                always {
                                    archiveArtifacts artifacts: '**/*scanning-report.json', allowEmptyArchive: true
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

def get_image_tag() {
    time_stamp = sh(returnStdout: true, script: "date +%Y%m%d%H%M%S").trim()
    short_commit_sha = sh(returnStdout: true, script: "git rev-parse --short HEAD").trim()
    if ( env.BRANCH_NAME == 'master' ) {
        docker_image_tag = params.BASE_TAG + "-" + time_stamp + "-" + short_commit_sha
    } else {
        docker_image_tag = time_stamp + "-" + short_commit_sha
    }
    println("image tag: " + docker_image_tag)
    return docker_image_tag
}
