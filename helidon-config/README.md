# Helidon Config

Sample Helidon MP project that showcases how to load helidon config from a kubernetes configmap. Also, how updating 
properties in configmap can be directly reflected in application without requiring redeployment or restart.

## Build the Docker Image

```
docker build -f Dockerfile.local -t helidon-config .
```

## Deploy the application to Kubernetes

```
kubectl cluster-info                         # Verify which cluster
kubectl get pods                             # Verify connectivity to cluster
kubectl create -f app.yaml                   # Deploy application
kubectl get pods                             # Wait for quickstart pod to be RUNNING
kubectl get service helidon-quickstart-mp    # Verify deployed service
```

Note the PORTs. You can now exercise the application as following but use the second
port number (the NodePort) instead of 7001.

## Exercise the application

```
curl -X GET http://localhost:<NodePort>/greet
{"message":"Hello World!"}

curl -X GET http://localhost:<NodePort>/greet/Joe
{"message":"Hello Joe!"}
```

## Update the configmap

```
kubectl edit configmaps helidon-config-cm
...
Update the value for app.greeting from Hello to say Howdy and save it
...
configmap/helidon-config-cm edited
```

## Exercise the application

It takes about 30 seconds on kubernetes cluster setup with docker-for-desktop to update values from configmap in a pod. 
This is the time taken by kubernetes control-plane and its configurable on kubernetes api-server.

```
curl -X GET http://localhost:<NodePort>/greet
{"message":"Howdy World!"}

curl -X GET http://localhost:<NodePort>/greet/Joe
{"message":"Howdy Joe!"}
```

## Try health and metrics

```
curl -s -X GET http://localhost:<NodePort>/health
{"outcome":"UP",...
. . .

# Prometheus Format
curl -s -X GET http://localhost:<NodePort>/metrics
# TYPE base:gc_g1_young_generation_count gauge
. . .

# JSON Format
curl -H 'Accept: application/json' -X GET http://localhost:<NodePort>/metrics
{"base":...
. . .

```

# After you’re done, cleanup.

```
kubectl delete -f app.yaml
```