---
layout: post
title: A Set of Steps to Follow when Developing Distributed Software Systems - Step 2 (Establish a Solid Foundation)
permalink: /blog/articles/step-2-solid-foundation-distributed-software-systems/
tags: [post, distributed-systems, software-development]
mathjax: false
description: This is the second step in a collection of topics and how-to's on software development. This blog post describes how to build a software foundation that's solid and also flexible for building software at a fast pace.
---

## Why do we need a Solid Foundation

Building a solid foundation for software development is like laying the groundwork for a skyscraper - you can't build high without building strong from the ground up. In the fast-paced world of software development, it's tempting to skip foundational work and jump straight into feature development. However, this approach often leads to technical debt that becomes exponentially more expensive to address later.

Another reason to have a solid foundation is to enable your development team(s) to iterate fast and to hit the ground running in providing value to the customer and not be bogged down into manual work or faulty processes.

More benefits of a solid foundation are listed below.

### Operational Visibility

Without proper health checks, logging, and monitoring, our service becomes a black box. When issues arise in production (and they will), we'll be flying blind, unable to quickly diagnose problems or understand system behaviour.

### Reliable Deployments

Infrastructure as Code and proper CI/CD pipelines ensure that deployments are consistent, repeatable, and auditable. Manual deployment processes are error-prone and don't scale with team growth.

### Integration Confidence

Well-defined API contracts prevent integration nightmares. When services communicate through clearly defined interfaces, teams can work independently while maintaining system coherence.

### Scalability Readiness

A proper foundation makes it easier to scale our system both technically (handling more load) and organisationally (adding more teams and services). Organisational scaling is ignored many times and leads to slower and slower delivery times as the teams scale.

The key insight is that these foundational elements are much harder to retrofit into an existing system than to build from the beginning. Starting with a solid foundation allows our team to move fast while maintaining reliability.

## Basic Health Checks

Health checks are the foundation of operational visibility in distributed systems. They allow both Kubernetes and our monitoring systems to understand the state of our service and make automated decisions about traffic routing, scaling, and recovery.

### Kubernetes Probes

Kubernetes provides three types of probes:

#### Liveness Probe

Determines if the container should be restarted. If this fails, Kubernetes kills and restarts the pod.

#### Readiness Probe

Determines if the container is ready to receive traffic. If this fails, the pod is removed from service endpoints.

#### Startup Probe

Gives the container additional time to start up before liveness probes begin. Useful for services with slow initialisation.

### Spring Boot Actuator Setup

Spring Boot Actuator is a production-ready feature module that provides monitoring and management capabilities for Spring Boot applications. It exposes operational information about the running application through HTTP endpoints, including health status, metrics, environment properties, and application configuration. Actuator integrates seamlessly with Kubernetes probes and monitoring systems, making it essential for observable distributed systems.

Add the dependency to our `build.gradle`:

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'io.micrometer:micrometer-registry-prometheus'
}
```

Configure health endpoints in `application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
      probes:
        enabled: true
  health:
    livenessstate:
      enabled: true
    readinessstate:
      enabled: true
```

### Kubernetes Deployment Configuration

Kubernetes deployment configurations define how applications should be deployed and managed in a Kubernetes cluster. They specify container images, resource requirements, environment variables, health check probes, and scaling parameters. These configurations ensure consistent, declarative deployments that can be version-controlled and automatically managed by Kubernetes controllers.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-service
  labels:
    app: my-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-service
  template:
    metadata:
      labels:
        app: my-service
    spec:
      containers:
      - name: my-service
        image: my-service:latest
        ports:
        - containerPort: 8080
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 3
        startupProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 30
```

### Custom Health Indicators

Custom health indicators extend Spring Boot Actuator's health checking capabilities to monitor external dependencies like databases, message queues, or third-party APIs. They implement the `HealthIndicator` interface to perform specific health checks and return detailed status information. This allows our application to report not just its own health, but also the health of critical dependencies, enabling more intelligent load balancing and alerting decisions.

## Structured Logging

Structured logging transforms log data from human-readable text into machine-parseable formats (typically `JSON`), enabling powerful log analysis, alerting, and debugging capabilities. Unlike traditional text logs, structured logs contain key-value pairs that can be easily indexed, searched, and aggregated by log management systems. JSON format provides consistent structure, better performance in log processing systems, and seamless integration with modern observability platforms like ELK stack, Splunk, or cloud-native logging services (DataDog).

### OpenTelemetry Integration

OpenTelemetry is an open-source observability framework that provides unified instrumentation for metrics, logs, and traces. It offers several key advantages: vendor-neutral data collection, automatic instrumentation for popular frameworks, standardized telemetry data formats, and seamless integration across different observability tools. This eliminates vendor lock-in and provides consistent observability data regardless of your monitoring backend.

```gradle
dependencies {
    implementation 'io.opentelemetry:opentelemetry-api'
    implementation 'io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter'
    implementation 'net.logstash.logback:logstash-logback-encoder:7.4'
}
```

### Logback Configuration

Configure JSON logging in `src/main/resources/logback-spring.xml`:

```xml
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>

    <springProfile name="!local">
        <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
                <providers>
                    <timestamp/>
                    <version/>
                    <logLevel/>
                    <message/>
                    <mdc/>
                    <arguments/>
                    <stackTrace/>
                    <pattern>
                        <pattern>
                            {
                                "service": "my-service",
                                "traceId": "%X{traceId:-}",
                                "spanId": "%X{spanId:-}"
                            }
                        </pattern>
                    </pattern>
                </providers>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="STDOUT"/>
        </root>
    </springProfile>

    <springProfile name="local">
        <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level [%X{traceId:-},%X{spanId:-}] %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="STDOUT"/>
        </root>
    </springProfile>
</configuration>
```

### Application Configuration

Configure OpenTelemetry in `application.yml`:

```yaml
spring:
  application:
    name: my-service

otel:
  service:
    name: ${spring.application.name}
  resource:
    attributes:
      service.name: ${spring.application.name}
      service.version: @project.version@
  instrumentation:
    spring-webmvc:
      enabled: true
    jdbc:
      enabled: true
    logback-appender:
      enabled: true
```

### Structured Logging in Code

Use structured logging with MDC (Mapped Diagnostic Context):

```java
@RestController
@Slf4j
public class UserController {

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUser(@PathVariable String userId) {
        try (MDCCloseable mdcCloseable = MDC.putCloseable("userId", userId)) {
            log.info("Retrieving user");

            User user = userService.findById(userId);
            if (user == null) {
                log.warn("User not found");
                return ResponseEntity.notFound().build();
            }

            log.info("User retrieved successfully");
            return ResponseEntity.ok(user);
        }
    }
}
```

## Infrastructure as Code Basics

Infrastructure as Code (IaC) ensures that our deployment pipeline is repeatable, auditable, and version-controlled. Using GitHub Actions for CI/CD provides tight integration with our source code repository.

### GitHub Actions Workflow

Create `.github/workflows/deploy.yml`:

```yaml
name: Build and Deploy

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

env:
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4

    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'

    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}

    - name: Run tests
      run: ./gradlew test

    - name: Generate test report
      uses: dorny/test-reporter@v1
      if: success() || failure()
      with:
        name: Test Results
        path: build/test-results/test/*.xml
        reporter: java-junit

  build-and-push:
    needs: test
    runs-on: ubuntu-latest
    if: github.event_name == 'push' && github.ref == 'refs/heads/main'

    permissions:
      contents: read
      packages: write

    steps:
    - uses: actions/checkout@v4

    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'

    - name: Build application
      run: ./gradlew bootJar

    - name: Log in to Container Registry
      uses: docker/login-action@v3
      with:
        registry: ${{ env.REGISTRY }}
        username: ${{ github.actor }}
        password: ${{ secrets.GITHUB_TOKEN }}

    - name: Extract metadata
      id: meta
      uses: docker/metadata-action@v5
      with:
        images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
        tags: |
          type=ref,event=branch
          type=sha,prefix={{branch}}-

    - name: Build and push Docker image
      uses: docker/build-push-action@v5
      with:
        context: .
        push: true
        tags: ${{ steps.meta.outputs.tags }}
        labels: ${{ steps.meta.outputs.labels }}

  deploy:
    needs: build-and-push
    runs-on: ubuntu-latest
    if: github.event_name == 'push' && github.ref == 'refs/heads/main'

    steps:
    - uses: actions/checkout@v4

    - name: Deploy to Kubernetes
      run: |
        echo "Deployment would happen here"
        # kubectl apply -f k8s/
```

### Dockerfile

A Dockerfile is a text file containing a series of instructions that Docker uses to automatically build container images. It defines the application's runtime environment, dependencies, configuration, and startup commands in a reproducible, version-controlled format. Creating a Dockerfile ensures consistent deployments across different environments, eliminates `it works on my machine` issues, and enables containerized deployment strategies essential for cloud-native and Kubernetes environments.

```dockerfile
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

COPY build/libs/*.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

### Kubernetes Manifests

Kubernetes manifests are `YAML` or `JSON` files that declaratively define the desired state of Kubernetes resources like deployments, services, configmaps, and secrets. They enable Infrastructure as Code by allowing you to version-control your entire application infrastructure, ensuring consistent deployments across environments and enabling automated GitOps workflows.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-service
  labels:
    app: my-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-service
  template:
    metadata:
      labels:
        app: my-service
    spec:
      containers:
      - name: my-service
        image: ghcr.io/your-org/my-service:main-abc123
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  selector:
    app: my-service
  ports:
  - port: 80
    targetPort: 8080
  type: ClusterIP
```

## API Contracts

API contracts define the interface between services in a formal, machine-readable specification. **OpenAPI** (for REST APIs) and **AsyncAPI** (for event-driven APIs) are industry-standard formats that provide clear documentation, enable code generation, support contract testing, and facilitate independent team development. These specifications serve as a single source of truth for API behaviour, preventing integration issues and enabling automated tooling for validation, testing, and client generation.

### AsyncAPI for Event-Driven Communication

For message-driven architectures, use AsyncAPI. Create `src/main/resources/asyncapi.yaml`:

```yaml
asyncapi: 3.0.0
info:
  title: My Service Events
  version: 1.0.0
  description: Event specifications for My Service

channels:
  user.events:
    address: user.events
    messages:
      UserCreated:
        $ref: '#/components/messages/UserCreated'
      UserUpdated:
        $ref: '#/components/messages/UserUpdated'

operations:
  publishUserCreated:
    action: send
    channel:
      $ref: '#/channels/user.events'
    messages:
      - $ref: '#/channels/user.events/messages/UserCreated'

components:
  messages:
    UserCreated:
      name: UserCreated
      title: User Created Event
      summary: Published when a new user is created
      contentType: application/json
      payload:
        $ref: '#/components/schemas/UserCreatedPayload'

  schemas:
    UserCreatedPayload:
      type: object
      properties:
        userId:
          type: string
          format: uuid
        email:
          type: string
          format: email
        createdAt:
          type: string
          format: date-time
      required:
        - userId
        - email
        - createdAt
```

### Schema Registry Integration

Schema registries provide centralised schema management for event-driven architectures, ensuring data compatibility and evolution across services. They enable schema validation, versioning, and compatibility checking, preventing breaking changes from being deployed. AWS EventBridge includes built-in schema discovery and validation features that automatically detect event schemas from your applications and create a centralised schema registry, enabling type-safe event handling and cross-service integration without manual schema management.

### SDK Generation

Generating SDKs from API specifications eliminates manual client development, ensures type safety, and keeps client code synchronised with API changes. Teams should publish generated SDKs to internal artifact repositories (like Artifactory or GitHub Packages) or package registries, making them easily consumable by other teams. This approach reduces integration errors, accelerates development velocity, and provides consistent API interaction patterns across different programming languages and teams.

This foundation provides the essential building blocks for a robust, observable, and maintainable distributed system. Each component works together to create a system that's ready for production workloads and team scalability.
