
## Hello World Helidon Application

This example application provides a simple Hello World REST service written with [Helidon](https://helidon.io).

### Install the example application

This example Hello World Helidon application has two endpoints:

- `/greet`, which uses microprofile properties file as a configuration source. Installation instructions are specified as part of [hello-helidon](https://github.com/verrazzano/verrazzano/blob/master/examples/hello-helidon/README.md) example.
- `/config`, which uses a Kubernetes ConfigMap as a configuration source. Installation instructions are specified as part of [helidon-config](https://github.com/verrazzano/verrazzano/blob/master/examples/helidon-config/README.md) example.