
# Bob's Books Demo Application

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

* Pre-requisites: Create secrets for WebLogic admin, OCR, OCIR
```
kubectl apply -f working-yamls/bobby.bobbys-front-end-weblogic-credentials.secret.yaml
kubectl apply -f working-yamls/bob.bobs-bookstore-weblogic-credentials.secret.yaml
kubectl create secret docker-registry ocr --docker-username='<username>' --docker-password='<password>' --docker-server=container-registry.oracle.com
kubectl create secret docker-registry ocir --docker-server=phx.ocir.io --docker-username='<objectnamespace/username>' --docker-password='<password>' --docker-email='<email>'
```

* Install demo
```
./install_demo.sh
```
* Verify if all objects have started:
```
kubectl get all -n bob
kubectl get all -n bobby
kubectl get all -n robert
kubectl get all
```
* Get the External IP for istio-ingressgateway service
```
kubectl get service istio-ingressgateway -n istio-system
```
* Use the external IP to connect to Bobby's Books and Robert's Books Apps
    - Bobby's Books: http://<external_ip>/bobbys-front-end
    - Robert's Books: http://<external_ip>
    - Bob's Order Manager App: http://<external_ip>/bobs-bookstore-order-manager/orders

### Uninstall Demo

* Uninstall demo
```
./uninstall_demo.sh
```
