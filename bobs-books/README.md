
## Bob's Books Application

The application consists of three main parts:

* A backend "order processing" application which is a Java EE
  application with REST services and a very simple JSP UI which
  stores data in a MySQL database.  This application runs on WebLogic
  Server.
* A front end web store "Robert's Books" which is a general book
  seller.  This is implemented as a Helidon microservice which
  gets book data from Coherence (using CDI)  and has a React
  web UI.
* A front end web store "Bobby's Books" which is a specialist
  children's book store.  This is implemented as a Helidon
  microservice which gets book data from a (different) Coherence
  using CDI and has a JSF Faces web UI running on WebLogic Server.

### Install Demo

Detailed instructions for installing Bob's Books can be found [here](https://github.com/verrazzano/verrazzano/blob/master/examples/bobs-books/README.md)
