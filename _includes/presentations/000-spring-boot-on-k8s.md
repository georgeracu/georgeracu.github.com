
<!-- background-image: url(./../../assets/img/presentation/spring-boot-k8s/Running-a-Java-app-with-Kubernetes.svg) -->

---

## Agenda

<hr/>

* Recipe
* About Docker
* Containerize the app
* About Kubernetes
* Run the app on k8s
* Scale the app horizontally
* Trigger a change in the cluster

---

## Recipe

<hr/>

* A web service 
* A fat JAR
* A container image
* A container runtime
* An environment where to run the container

---

<!-- background-image: url(./../../assets/img/presentation/spring-boot-k8s/a-web-service-page.svg) -->

---

## Tech stack

<hr/>

* Spring Boot
    * Spring Boot DevTools
    * Spring Configuration Processor
    * Spring Web
* Maven
* Java 17
* Package as a JAR

---

## Coding session

<hr/>

* Create a template app from https://start.spring.io
* Create a web service with Spring Boot
* Package the service as a fat JAR with name "app" 
    * `mvn clean package`
* Run it as a JAR 
    * `java -jar target/app.jar`

---

## Recipe completion

<hr/>

* A web service  ✅
* A fat JAR ✅
* A container image
* A container runtime
* An environment where to run the container

---

<!-- background-image: url(./../../assets/img/presentation/spring-boot-k8s/application-hosting-evolution.svg) -->

## Application hosting evolution

---

<!-- background-image: url(./../../assets/img/presentation/spring-boot-k8s/linux-containers.svg) -->

---

## A container

<hr/>

* A container is a self-contained ready to run application
* A container needs a container runtime to run
* The container runtime facilitates connection between the container and the host kernel
* All containers running on the same host use the same kernel (as opposed to VMs)
* [Open Container Initiative](https://opencontainers.org/) provides standardisation for containers and runtimes

---

## [Docker](https://www.docker.com/)

<hr/>

* The most popular container runtime
* The runtime that will be used in this demo
* Docker provides
    * a container runtime
    * an image format
    * a registry to keep images, and many more

---

## A Docker container image

<hr/>

* A read-only environment
* Contains the application to be run and all its dependencies
* It can be built from a Dockerfile (a descriptor)
* It can be shared through the [registry](https://hub.docker.com/)

---

## Docker file

<hr/>

* A script to create Docker container images
* It contains all instructions to build an image
* Can be shared such that everyone will build the same image
* Use `docker build .` to build the image in the current directory
* YAML format

---

## Coding session

<hr/>

### Dockerfile definition

```yaml
FROM openjdk:17
VOLUME /tmp
COPY target/app.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=production","-jar","/app.jar"]
```

### Create and tag a Docker image

`docker build -t sussex-demo:v1 .`

### Run a Docker container

`docker run -it -p8080:8080 --name=sussex-demo sussex-demo:v1`

---

## Recipe (so far)

<hr/>

* A web service  ✅
* A fat JAR ✅
* A container image ✅
* A container runtime ✅
* An environment where to run the container

---

<!-- background-image: url(./../../assets/img/presentation/spring-boot-k8s/kubernetes-subpage.svg) -->

---

## [Kubernetes (k8s)](https://kubernetes.io/)

<hr/>

* _An open-source system for automating deployment, scaling, and management of containerized applications._

* Based on Google's Borg system

* _Desired State Management_

---

## Kubernetes management interface

<hr/>

* It has a RESTful API for management
* Configuration is stored in etcd
* How to get information from the API:
    * `kubectl`
    * k8s dashboard
    * `curl`

---

<!-- background-image: url(./../../assets/img/presentation/spring-boot-k8s/kubernetes-components.svg) -->

---

## Kubernetes deployment options

<hr/>

* Public clouds
* On-premise in a data center
* Minikube: for testing and development 

For these examples we will use Minikube, available from https://kubernetes.io/docs/tasks/tools/install-kubectl

---

## Minikube

<hr/>

* After installation it can be started with `minikube start`
* The dashboard can be accessed at `minikube dashboard`
* It can be stopped with `minikube stop`

---

## Configuration files

<hr/>

* YAML format
* API version needs to be specified
* They are a declarative way to specify the K8s objects
* They allow to have reproducible artifacts

---

## Kubernetes objects

<hr/>

* Containers
* Pods
* Deployment
    * Replication
    * Update strategy
* Services
* Persistent Volumes
* Ingress

---

## Kubernetes pods

<hr/>

* The smallest entity managed by K8s
* Similar to a server, in the "old" world
* Can run multiple containers (most of the times only 1)
* All containers are under the same namespace
* All containers are under the same IP address
* You address pods, not containers
* Typically, pods are started through a deployment

---

## Kubernetes services

<hr/>

* Logical grouping of pods that perform the same function
* Act as a load balancer (LB) between the pods
* Use a round robin scheduler for the LB
* Translate the DNS service name (eg. sussex-demo) into IPs and forward the request to those pods
* Have a static IP and port

---

<!-- background-image: url(./../../assets/img/presentation/spring-boot-k8s/kubernetes-cluster-diagram.svg) -->

---

## Demo session

<hr/>

* Start minikube locally
    * `minikube start`
* Use minikube's Docker daemon to build the image
    * `eval $(minikube docker-env)`
    * `docker build -t sussex-demo:v1 .`
* Create a deployment
    * `kubectl create deployment sussex-demo --image=sussex-demo:v1 --port=8080`
* Expose a service
    * `kubectl expose deployment sussex-demo --type=NodePort`
* Access the service from browser
    * `minikube service sussex-demo`

---

## Replica set

<hr/>

* The main job is to maintain a given number of running pods at any given time
* Removes pods if there are too many
* Adds pods if there are too few
* It needs a pod template to start new pods
* It matches pods by selectors (labels)
* Provides resiliency and scalability to the service
* Can have auto-scaling enabled for self scaling

---

## Horizontal pod autoscaller

<hr/>

YAML file with definition:

```yaml
kind: HorizontalPodAutoscaler
spec:
  maxReplicas: 5
  minReplicas: 1
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: sussex-demo-app
  targetCPUUtilizationPercentage: 50
```

---

<!-- background-image: url(./../../assets/img/presentation/spring-boot-k8s/horizontal-pod-autoscaller.svg) -->

---

## Demo session

<hr/>

* Delete the service and the deployment
    * `kubectl delete service sussex-demo`
    * `kubectl delete deployment sussex-demo`
* Enable metrics server
    * `minikube addons enable metrics-server`
* Apply deployment and service from the yaml file
    * `kubectl apply -f deployment.yaml`
    * `kubectl apply -f service.yaml`
* Apply horizontal autoscaler from the yaml file
    * `kubectl apply -f horizontal-autoscaler.yaml`

---

## Service availability

<hr/>

* Zero downtime rolling updates
* Rollbacks
* Users do not see the impact
* Continuous Delivery pipeline releases new versions immediately

---

<!-- background-image: url(./../../assets/img/presentation/spring-boot-k8s/rolling-updates.svg) -->

---

## Demo session

<hr/>

* Change the app controller to return _Hello World v2_
* Package the app again
    * `mvn clean package`
* Tag v2 of the Docker image
    * `docker build -t sussex-demo:v2 .`
* Update the image in the deployment
    * `kubectl set image deployment sussex-demo sussex-demo=sussex-demo:v2`
* Watch the dashboard as K8s performs the rolling update

---

## There's more

<hr/>

* ConfigMaps
* Secrets
* Jobs
* StatefulSets
* Monitoring
* Logging
* Debugging

---

<!-- background-image: url(./../../assets/img/presentation/spring-boot-k8s/questions.svg) -->

---

## Copyright

<hr/>

### All rights belong to their respective owners for images and logos used in this presentation.

---

{% include contact-info.md %}
