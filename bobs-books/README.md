
## Bob's Books Application

The example application consists of three main parts:

* A back-end "order processing" application, which is a Java EE
  application with REST services and a very simple JSP UI, which
  stores data in a MySQL database.  This application runs on WebLogic
  Server.
* A front-end web store "Robert's Books", which is a general book
  seller.  This is implemented as a Helidon microservice, which
  gets book data from Coherence using CDI and has a React
  web UI.
* A front-end web store "Bobby's Books", which is a
  children's book store.  This is implemented as a Helidon
  microservice which gets book data from a (different) Coherence
  using CDI and has a JSF web UI running on WebLogic Server.

### Install the Example Application

Detailed instructions for installing Bob's Books can be found [here](https://verrazzano.io/latest/docs/examples/wls-coh/bobs-books/).


Copyright (c) 2020, 2023, Oracle and/or its affiliates.
