
# Bob's Books Demo Application

See the [diagram here](#FIXME)
for a graphical overview of the application.

It consists of three main parts:

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

