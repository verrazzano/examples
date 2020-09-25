# ToDo List Lift-and-Shift Application
This example walk-through demonstrates the "Lift-and-Shift" of an on-premises WebLogic Server domain to a cloud environment running
Kubernetes using Verrazzano.  The initial steps create a very simple on-premises domain that you will lift
and shift to Kubernetes.  The sample domain is the starting point for the lift and shift process; it contains
one application (ToDo List) and one data source.  The steps to configure the database and the WebLogic Server domain are
described in the [Setup](#setup) section.  After the initial setup, you will begin the process of moving this domain
to Kubernetes with Verrazzano in the [Lift and Shift](#lift-and-shift) section.  This simple example does not
include the setup of the virtual cloud network that would be needed to access an on-premises database, nor does it document
how to migrate a database to the cloud.  

## Requires
- MySQL Database 8.x (Database Server)
- [WebLogic Server 12.2.1.4.0](https://www.oracle.com/middleware/technologies/weblogic-server-downloads.html) (Application Server)

  **NOTE**: All WebLogic Server installers are supported except the Quick Installer.

- [Maven](https://maven.apache.org/download.cgi) (To build the ToDo List application)
- [WebLogic Deploy Tooling](https://github.com/oracle/weblogic-deploy-tooling/releases) (WDT) (To convert the WebLogic Server domain to and from metadata)
- [WebLogic Image Tool](https://github.com/oracle/weblogic-image-tool/releases) (WIT) (To build the Docker image)

## Setup
Create a sample domain that represents your on-premises WebLogic Server domain:
1. [Create a database](#create-a-database-using-mysql-called-tododb) in MySQL called `tododb`.
2. [Create a WebLogic Server domain](#create-a-new-weblogic-server-domain).
    - Create the sample domain that represents the on-premises domain.
    - Configure a data source to access the MySQL database.
    - Be aware of these domain limitations:
        - There are two supported domain types, single server and single cluster.
        - Domains must use the default value, `AdminServer`, for `AdminServerName`.
        - Domains must be using:
            - WebLogic Server Listen Port for the Administration Server: `7001`.
            - WebLogic Server Listen Port for the Managed Server: `8001`.
            - Note that these are all standard WebLogic Server default values.
3. [Build and deploy the ToDo List application](#build-and-deploy-the-application).
    - Build the sample application, `todo.war`.
    - Deploy the sample application, `todo.war`, to the sample WebLogic Server domain.
4. [Initialize the database](#initialize-the-database).
    - Create the database table for the ToDo List application and initialize the table with a few entries.
    - Verify the configuration by using the application to see the ToDo list.

## Lift and Shift
Begin the process to move the sample domain to Kubernetes with Verrazzano:
1. [Create a WDT Model](#create-a-wdt-model).
    - Run WDT's `discoverDomain` tool to create a model and application archive.
2. [Create a Docker image](#create-a-docker-image).
    - Run WIT to create a new Docker image with the domain metadata and WebLogic Server binaries.
    - Push the Docker image to your repository.
3. [Deploy to Verrazzano](#deploying-to-verrazzano).
    - The database is assumed to be reachable from Kubernetes where Verrazzano will run the domain.
    - Create the Kubernetes secrets for the domain.
    - Create the Kubernetes secret for the Verrazzano Docker pull.
    - Apply the model and binding resources for Verrazzano.

## Setup steps
### Create a database using MySQL called `tododb`
- Download the MySQL image from Docker Hub.
    ```
    docker pull mysql:latest
    ```
- Start the container database (and optionally mount a volume for data).
    ```
    docker run --name tododb -p3306:3306 -e MYSQL_USER=derek -e MYSQL_PASSWORD=welcome1 -e MYSQL_DATABASE=tododb -e MYSQL_ROOT_PASSWORD=welcome1 -d mysql:latest
    ```

- Start a MySQL client to change the password algorithm to `mysql_native_password`.
    - Assuming that the database server is running, start a database CLI client with:
        ```shell script
        docker exec -it tododb mysql -uroot -p
        ```
    - When prompted for the password, enter the password for the root user, `welcome1` if you used the command above.  
    After being connected, run the `ALTER` command at the MySQL prompt.
        ```mysql
        ALTER USER 'derek'@'%' IDENTIFIED WITH mysql_native_password BY 'welcome1';
        ```

### Create a new WebLogic Server domain
If you do not have WebLogic Server 12.2.1.4.0 installed, you will need to install it now.  Choose the **GENERIC** installer
from [WebLogic Server Downloads](https://www.oracle.com/middleware/technologies/weblogic-server-downloads.html),
and follow the documented installation instructions. Save the installer after you have finished.  You will
need this installer again to build the Docker image.  To make copying commands easier, define an environment variable for `ORACLE_HOME` that points to the folder where you installed WebLogic Server 12.2.1.4.0.

```shell script
export ORACLE_HOME=/install/directory
```

Using the Oracle WebLogic Server Configuration Wizard, create a new domain called `tododomain`.  Add the password for the administrative
user, and accept the defaults for everything else to create a simple domain with a single Administration Server.

```shell script
$ORACLE_HOME/oracle_common/common/bin/config.sh
```

Start the newly created domain using the domain `startWebLogic` script.
Access the Console of the newly started domain with your browser (for example, [http://localhost:7001/console](http://localhost:7001/console)).
Using the WebLogic Server Administration Console, log in and add a data source configuration to access the MySQL database.
During the data source configuration, you can accept the default values for most fields, but the following fields are
required to match the application and database settings from when you started and created the MySQL database:

    - In the WebLogic Server Administration Console, navigate to Services -> Data Sources.
    - New -> Generic Data Source
    - First page:
        - JNDI Name: jdbc/ToDoDB
        - Database Type: MySQL
    - Fourth page:
        - Database Name: tododb
        - Host Name: localhost
        - Database Port: 3306
        - Database User Name: derek
        - Password: welcome1
        - Confirm Password: welcome1   
     - Click Next and then Finish to complete the configuration.  

### Build and deploy the application
Using Maven, build this project to produce the artifact, `todo.war`.
```shell script
git clone https://github.com/verrazzano/examples.git
cd examples/todo-list/
mvn clean package
```

Using the WebLogic Server Administration Console, deploy the ToDo List application.  Accepting all the default options is fine.  **NOTE:** The
remaining steps assume that the application context is `todo`.

### Initialize the database
After the application is deployed and running in WebLogic Server, access the http://localhost:7001/todo/rest/items/init
REST service to create the database table used by the application. In addition to creating the application table, the
`init` service also will load four sample items into the table.

### Access the application
Access the application at http://localhost:7001/todo/index.html.  Add a few entries or delete some.
After verifying the application and database, you may shut down the local WebLogic Server domain.

## Lift and Shift steps

The following steps will move the sample domain to Kubernetes with Verrazzano.

### Create a WDT Model
If you have not already done so, download WebLogic Deploy Tooling (WDT) from [GitHub](https://github.com/oracle/weblogic-deploy-tooling/releases).
Unzip the installer `weblogic-deploy.zip` file so that you can access `bin/discoverDomain.sh`. To make copying
commands easier, define an environment variable for `WDT_HOME` that points to the folder where you installed
WebLogic Deploy Tooling.
```shell script
export WDT_HOME=/install/directory
```

To create a reusable model of the application and domain, use WebLogic Deploy Tooling (WDT) to create a metadata model
of the domain.  First, create an output directory to hold the generated scripts and models.  Then, run WDT `discoverDomain`.
```shell script
mkdir v8o

$WDT_HOME/bin/discoverDomain.sh -oracle_home $ORACLE_HOME -domain_home /path/to/domain/dir -model_file ./v8o/wdt-model.yaml -archive_file ./v8o/wdt-archive.zip -target vz -output_dir v8o
```

You will find the following files in `./v8o`:
- `binding.yaml` - Verrazzano Binding file
- `model.yaml` - Verrazzano Model template
- `wdt-archive.zip` - The WDT archive containing the ToDo List application WAR file
- `wdt-model.yaml` - The WDT model of the WebLogic Server domain
- `vz_variable.properties` - A set of properties extracted from the WDT domain model
- `create_k8s_secrets.sh` - A helper script with `kubectl` commands to apply the Kubernetes secrets needed for this domain

If you chose to skip the "Access the application" step and did not verify that the ToDo List application was deployed, you should
verify that you see the `todo.war` file inside the `wdt-archive.zip` file.  If you do not see the WAR file, there was something wrong
in your deployment of the application on WebLogic Server that will require additional troubleshooting in your domain.

### Create a Docker image
At this point, the Verrazzano model is just a template for the real model.  The WebLogic Image Tool (WIT) will
fill in the placeholders for you, or you can edit the model manually to set the image name and domain home directory.

If you have not already done so, download WebLogic Image Tool (WIT) from [GitHub](https://github.com/oracle/weblogic-image-tool/releases).
Unzip the installer `imagetool.zip` file so that you can access `bin/imagetool.sh`. To make copying commands easier, define an environment variable for `WIT_HOME` that points to the folder where you installed WebLogic Image Tool.

```shell script
export WIT_HOME=/install/directory
```

You will need a Docker image to run your WebLogic Server domain in Kubernetes.  To use the WebLogic Image Tool (WIT) to
create the Docker image, run `imagetool create`.  Although WIT will download patches and PSUs for you, it does not yet
download installers.  Until then, you must download the WebLogic Server and Java Development Kit installer manually,
and provide their location to WIT using the `imagetool cache addInstaller` command.

```shell script
# The directory created in the previous step to hold the generated scripts and models.
cd v8o

$WIT_HOME/bin/imagetool.sh cache addInstaller --path /path/to/intaller/jdk-8u231-linux-x64.tar.gz --type jdk --version 8u231

# The installer file name may be slightly different depending on which version of the 12.2.1.4.0 installer that you downloaded, slim or generic.
$WIT_HOME/bin/imagetool.sh cache addInstaller --path /path/to/intaller/fmw_12.2.1.4.0_wls_Disk1_1of1.zip --type wls --version 12.2.1.4.0

$WIT_HOME/bin/imagetool.sh cache addInstaller --path /path/to/intaller/weblogic-deploy.zip --type wdt --version latest

# Paths for the files in this command assume that you are running it from the v8o directory created during the `discoverDomain` step.
$WIT_HOME/bin/imagetool.sh create --tag your/repo/todo:1 --version 12.2.1.4.0 --jdkVersion 8u231 --wdtModel ./wdt-model.yaml --wdtArchive ./wdt-archive.zip --wdtVariables ./vz_variable.properties  --vzModel ./model.yaml --wdtModelOnly
```

The `imagetool create` command creates a local Docker image and updates the Verrazzano model with the domain home
and image name.  Check your Docker images for the tag that you used in the create command using `docker images` from the Docker
CLI.  If everything worked correctly, now you push that image to the repository that Verrazzano will use to access
the image from Kubernetes. You can use the Oracle Cloud Infrastructure Registry (OCIR) as your repository for this
example, but most Docker compliant registries should work.

**NOTE:** The image name must be the same as what is in the Verrazzano `model.yaml` file under
`spec -> weblogicDomains -> domainCRValues -> image`.

```shell script
docker push your/repo/todo:1
```

### Deploying to Verrazzano
The following steps assume that you have a Kubernetes cluster and that Verrazzano is already installed in that cluster.

If you haven't already done so, edit and run the `create_k8s_secrets.sh` script to generate the Kubernetes secrets.
WDT does not discover passwords from your existing domain.  Before running the create secrets script, you will need to
edit `create_k8s_secrets.sh` to set the passwords for the WebLogic Server domain and the data source.  In this domain,
there are only two passwords that you need to enter: administrator credentials (like weblogic/welcome1) and the
ToDo database credential (like derek/welcome1).

For example:
```shell script
# Update <admin-user> and <admin-password> for weblogic-credentials
create_paired_k8s_secret weblogic-credentials weblogic welcome1

# Update <user> and <password> for jdbc-todo-datasource
create_paired_k8s_secret jdbc-todo-datasource derek welcome1
```

Verrazzano will need a credential to pull the image that you just created, so you need to create one more secret.
The name for this credential can be changed in the `model.yaml` file to anything you like, but it defaults to `ocir`.  
Assuming that you left the name as `ocir`, you will need to run a `kubectl create secret` command similar to the following:
```shell script
kubectl create secret docker-registry ocir --docker-server=phx.ocir.io --docker-email=your.name@company.com --docker-username=tenancy/username --docker-password='passwordForUsername'
```

Finally, run `kubectl apply` to apply the Verrazzano model and binding files to Verrazzano to start your domain.

```shell script
kubectl apply -f model.yaml
kubectl apply -f binding.yaml
```

## Copyright
Copyright (c) 2020, Oracle and/or its affiliates.
