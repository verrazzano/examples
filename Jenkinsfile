// Copyright (c) 2020, 2021, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

pipeline {
   agent {
        docker {
            image "${EXPERIMENTAL_RUNNER_DOCKER_IMAGE}"
            args "${RUNNER_DOCKER_ARGS}"
            registryUrl "${RUNNER_DOCKER_REGISTRY_URL}"
            registryCredentialsId 'ocir-pull-and-push-account'
            label 'small'
        }
    }

    parameters {
        string (name: 'BASE_TAG',
                defaultValue: '0.1.12-1',
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
        HELLO_HELIDON_V1 = 'example-helidon-greet-app-v1'
        HELLO_HELIDON_V2 = 'example-helidon-greet-app-v2'
        HELIDON_CONFIG = 'example-helidon-config-app'
        VERSION = get_image_tag()

        // secrets used during build
        BOB_DB_PASSWORD = credentials('bobs-bookstore-db-password')
        BOB_ADMIN_PASSWORD = credentials('bobs-bookstore-admin-password')

        // access to GitHub Packages Maven Repository
        MAVEN_SETTINGS = credentials('oracle-maven-settings')

        BUCKET_NAME = "build-shared-files"
        JDK8_BUNDLE = "jdk-8u261-linux-x64.tar.gz"
        JDK14_BUNDLE = "openjdk-14.0.2_linux-x64_bin.tar.gz"
        WEBLOGIC_BUNDLE = "fmw_12.2.1.4.0_wls.jar"
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

        stage('Parallel builds') {
            parallel {
                stage('Helidon Config') {
                     stages {
                         stage('Build Helidon Config Application') {
                             steps {
                                 sh """
                                     echo "${DOCKER_CREDS_PSW}" | docker login ghcr.io -u ${DOCKER_CREDS_USR} --password-stdin
                                     env
                                     cd examples/helidon-config/
                                     java -version
                                     echo "\${JAVA_HOME}"
                                     echo "\${env.JAVA_HOME}"
                                     mvn -B -s $MAVEN_SETTINGS clean install
                                     oci os object get -bn ${BUCKET_NAME} --file ${JDK14_BUNDLE} --name ${JDK14_BUNDLE}
                                     docker image build --build-arg JDK_BINARY=${JDK14_BUNDLE} -t ${env.REPO}/${env.HELIDON_CONFIG}:${env.VERSION} .
                                     docker push ${env.REPO}/${env.HELIDON_CONFIG}:${env.VERSION}
                                 """
                             }
                         }

                         stage('Scan Helidon Config Application') {
                             steps {
                                 clairScan("${env.REPO}/${env.HELIDON_CONFIG}:${env.VERSION}", "helidon_config.scanning-report.json")
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
