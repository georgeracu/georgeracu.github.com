---
layout: post
title: A Set of Steps to Follow when Developing Distributed Software Systems - Step 3 (Getting Ready for Production)
permalink: /blog/articles/step-3-getting-ready-for-production-distributed-software-systems/
tags: [post, distributed-systems, software-development, production-readiness]
mathjax: false
description: This is the third step in a collection of topics and how-to's on software development. This blog post describes how to prepare your distributed software system for production deployment with comprehensive monitoring, testing, and security practices.
---

## Getting Ready for Production

Moving from a solid foundation to production readiness requires careful attention to monitoring, testing, security, and operational procedures. This phase ensures that our system can handle real-world traffic, failures, and security threats whilst providing the observability needed to maintain and improve it.

For this phase, we'll continue with our Java-based stack using Spring Boot and Gradle, deployed to Kubernetes, whilst adding the production-grade tooling and practices necessary for enterprise applications.

## Monitoring and Alerting Infrastructure

Comprehensive monitoring and alerting form the nervous system of our production environment. Without proper monitoring, we're flying blind when issues occur.

### DataDog Integration

For this section I'll use DataDog, as I used it extensively in the last years. DataDog provides comprehensive application performance monitoring, infrastructure monitoring, and log management in one platform.

In this example, the DataDog agent is embedded in the application. Another way to do it is to have it as a side-car with your Docker container.

#### Setup Dependencies

```gradle
dependencies {
    implementation 'io.micrometer:micrometer-registry-datadog'
    implementation 'com.datadoghq:dd-trace-java'
}
```

#### Application Configuration

```yaml
management:
  metrics:
    export:
      datadog:
        enabled: true
        api-key: ${DATADOG_API_KEY}
        application-key: ${DATADOG_APP_KEY}
        step: 10s
        uri: https://api.datadoghq.eu/api/  # EU instance
    tags:
      service: ${spring.application.name}
      environment: ${ENVIRONMENT:local}
      version: ${BUILD_VERSION:unknown}

logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [dd.service=%X{dd.service:-},dd.trace_id=%X{dd.trace_id:-},dd.span_id=%X{dd.span_id:-}] %logger{36} - %msg%n"
```

#### Custom Metrics

Custom metrics are application-specific measurements that provide insights into our business logic and domain-specific behaviour. Unlike infrastructure metrics (CPU, memory, network), custom metrics track meaningful business events like user registrations, order processing times, or conversion rates.

Why use custom metrics:
- Business Intelligence: Track key performance indicators directly from our application
- Operational Insight: Understand how our business logic performs in production
- Proactive Monitoring: Detect business-level issues before they impact users
- Data-Driven Decisions: Make informed choices about system optimisation and feature development

```java
@Component
public class BusinessMetrics {

    private final MeterRegistry meterRegistry;
    private final Counter userRegistrations;
    private final Timer orderProcessingTime;

    public BusinessMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.userRegistrations = Counter.builder("user.registrations")
                .description("Total user registrations")
                .register(meterRegistry);
        this.orderProcessingTime = Timer.builder("order.processing.time")
                .description("Time taken to process orders")
                .register(meterRegistry);
    }

    public void recordUserRegistration() {
        userRegistrations.increment();
    }

    public void recordOrderProcessing(Duration processingTime) {
        orderProcessingTime.record(processingTime);
    }
}
```

### NewRelic Alternative

NewRelic provides similar capabilities with different strengths in APM and error tracking.

#### Setup

```gradle
dependencies {
    implementation 'com.newrelic.agent.java:newrelic-api:8.7.0'
}
```

#### JVM Arguments

```bash
-javaagent:/path/to/newrelic.jar
-Dnewrelic.config.file=/path/to/newrelic.yml
```

## Comprehensive Testing Strategy

Production readiness requires testing at multiple levels to ensure our system behaves correctly under various conditions.

### Unit Testing

Unit testing involves testing individual components or methods in isolation from the rest of the system. These tests focus on verifying that a single "unit" of code (typically a method or class) behaves correctly when given specific inputs.

Key characteristics of unit tests:
- Fast: Execute in milliseconds, enabling rapid feedback
- Isolated: Don't depend on external systems like databases or networks
- Deterministic: Always produce the same result for the same input
- Focused: Test one specific behaviour or scenario at a time

Unit tests form the foundation of our testing strategy, providing quick feedback during development and catching regressions early in the development cycle.

#### Enhanced JUnit 5 Setup

```gradle
dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.1'
    testImplementation 'org.mockito:mockito-core:5.8.0'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.8.0'
    testImplementation 'org.assertj:assertj-core:3.24.2'
    testImplementation 'org.awaitility:awaitility:4.2.0'
    testImplementation 'net.jqwik:jqwik:1.8.2'  // Property-based testing
}
```

#### Property-Based Testing Example

```java
class UserValidationTest {

    @Property
    void validEmailsShouldPassValidation(@ForAll @Email String email) {
        assertTrue(EmailValidator.isValid(email));
    }

    @Property
    void invalidEmailsShouldFailValidation(
        @ForAll @StringLength(min = 1, max = 50)
        @Chars({'a', 'b', 'c', '@'}) String invalidEmail) {

        Assume.that(!invalidEmail.contains("@") ||
                   invalidEmail.indexOf("@") != invalidEmail.lastIndexOf("@"));

        assertFalse(EmailValidator.isValid(invalidEmail));
    }
}
```

### Component Integration Testing

Component integration testing verifies that multiple parts of our system work correctly together whilst still maintaining control over external dependencies. These tests validate the interactions between our application components (like services, repositories, and message handlers) using real implementations but with lightweight, controlled versions of external systems.

Key benefits:
- Realistic Testing: Uses actual database connections, message brokers, and other infrastructure
- Controlled Environment: Lightweight containers provide consistent, isolated test environments
- Fast Feedback: Faster than full end-to-end tests but more comprehensive than unit tests
- Confidence: Validates that your application components integrate properly

TestContainers is particularly powerful for these tests because it spins up real database instances, message brokers, or other services in Docker containers specifically for your tests.

#### TestContainers with Multiple Services

```java
@SpringBootTest
@Testcontainers
class UserServiceIntegrationTest {

    @Container
    static Network network = Network.newNetwork();

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withNetwork(network)
            .withNetworkAliases("postgres")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withNetwork(network)
            .withNetworkAliases("redis")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Test
    void shouldCreateUserWithCaching() {
        // Test implementation that verifies both database persistence
        // and Redis caching behaviour
    }
}
```

### End-to-End Integration Testing

End-to-end integration testing validates complete user workflows across our entire system, including all external dependencies and integrations. These tests simulate real user scenarios from start to finish, ensuring that the entire system works as expected from the user's perspective.

Characteristics:
- Complete Workflows: Tests entire user journeys (e.g., registration → login → purchase → confirmation)
- All Dependencies: Includes databases, external APIs, message queues, and other services
- Realistic Environment: Runs against production-like environments
- Slower Execution: Takes longer to run but provides highest confidence in system functionality

Whilst valuable for critical workflows, end-to-end tests should be used sparingly due to their complexity and maintenance overhead. I recommend to have a dedicated suite of end-to-end tests that are independent from any service repository.

#### Contract Testing with Pact

Pact is a contract testing framework that enables independent testing of service interactions. Instead of testing against real provider services, consumers define "contracts" (expectations) about how they'll interact with providers. These contracts are then used to test both the consumer and provider independently.

Benefits of Pact testing:
- Independent Development: Teams can develop and test services independently
- Fast Feedback: No need to coordinate deployments across services for testing
- Evolutionary Design: Contracts evolve as service interfaces change
- Confidence: Ensures service compatibility without end-to-end test complexity

```gradle
dependencies {
    testImplementation 'au.com.dius.pact.consumer:junit5:4.6.4'
    testImplementation 'au.com.dius.pact.provider:junit5:4.6.4'
}
```

#### Consumer Test

```java
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "user-service", port = "8080")
class UserServiceConsumerTest {

    @Pact(consumer = "notification-service")
    public RequestResponsePact createUserPact(PactDslWithProvider builder) {
        return builder
                .given("user exists")
                .uponReceiving("a request for user details")
                .path("/api/v1/users/123")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(LambdaDsl.newJsonBody(object -> object
                        .stringType("id", "123")
                        .stringType("name", "John Doe")
                        .stringType("email", "john@example.com")
                ).build())
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createUserPact")
    void testUserRetrieval(MockServer mockServer) {
        // Test implementation using the mock server
    }
}
```

## Configuration Management

Proper configuration management ensures our application can be deployed across different environments without code changes.

### Helm Charts

Helm is the "package manager for Kubernetes" that simplifies deploying and managing applications on Kubernetes clusters. A Helm chart is a collection of files that describe a related set of Kubernetes resources.

Key concepts:
- Templates: Kubernetes YAML files with placeholders for dynamic values
- Values: Configuration parameters that customize deployments for different environments
- Charts: Packaged applications that can be versioned, shared, and reused
- Releases: Deployed instances of charts with specific configurations

Why use Helm charts:
- Environment Management: Deploy the same application with different configurations across dev, staging, and production
- Complexity Management: Simplify complex Kubernetes deployments into manageable packages
- Reusability: Share and reuse common deployment patterns
- Rollbacks: Easy rollback to previous versions when deployments fail

#### Chart Structure

```bash
helm-chart/
├── Chart.yaml
├── values.yaml
├── values-dev.yaml
├── values-staging.yaml
├── values-production.yaml
└── templates/
    ├── deployment.yaml
    ├── service.yaml
    ├── configmap.yaml
    ├── secret.yaml
    └── ingress.yaml
```

## Secrets Management

Secure handling of sensitive configuration data is crucial for production systems. No plaintext secrets will be stored in any application code or configuration file. All secrets are encrypted and decrypted only at runtime and made available to the running application.

### HashiCorp Vault Integration

HashiCorp Vault provides centralised secrets management with encryption, access control, and audit logging capabilities.

### Mozilla SOPS Alternative

Mozilla SOPS (Secrets OPerationS) is a command-line tool for encrypting and decrypting structured data files (YAML, JSON, ENV, INI) using various key management systems like AWS KMS, GCP KMS, Azure Key Vault, or PGP.

Key features:
- Partial Encryption: Only encrypts values, leaving keys readable for easy management
- Version Control Safe: Encrypted files can be safely committed to Git repositories
- Multiple Key Sources: Supports various key management systems and PGP
- Team Collaboration: Multiple team members can decrypt secrets using their own keys

Why use SOPS:
- GitOps Workflows: Store encrypted secrets alongside code in version control
- Audit Trail: Track changes to secrets through Git history
- Access Control: Fine-grained control over who can decrypt specific secrets
- Simplicity: Easier to manage than complex secret management systems for smaller teams

## Security Scanning

Automated security scanning helps identify vulnerabilities before they reach production.

### SAST (Static Application Security Testing)

Static Application Security Testing (SAST) analyses source code, bytecode, or binary code for security vulnerabilities without executing the application. SAST tools scan your codebase to identify potential security flaws like SQL injection vulnerabilities, cross-site scripting (XSS), buffer overflows, and insecure coding practices.

Key characteristics:
- Early Detection: Finds vulnerabilities during development, before deployment
- Comprehensive Coverage: Analyses all code paths, including rarely executed ones
- No Runtime Required: Works on source code without needing a running application
- Developer Integration: Can run in IDEs and CI/CD pipelines for immediate feedback

When to use SAST:
- Development Phase: Integrate into IDE and commit hooks for immediate feedback
- CI/CD Pipeline: Gate deployments based on security scan results
- Regular Audits: Scheduled scans to catch newly discovered vulnerability patterns
- Compliance Requirements: Meet regulatory requirements for security testing

#### SonarQube Security Rules

```gradle
sonar {
    properties {
        property "sonar.security.hotspots.ignored", "false"
        property "sonar.coverage.exclusions", "**/config/**,**/dto/**"
        property "sonar.cpd.exclusions", "**/generated/**"
    }
}
```

#### SpotBugs Security Extensions

```gradle
spotbugs {
    effort = 'max'
    reportLevel = 'low'
    visitors = ['FindSecBugs']
}

dependencies {
    spotbugsPlugins 'com.h3xstream.findsecbugs:findsecbugs-plugin:1.12.0'
}
```

### DAST (Dynamic Application Security Testing)

Dynamic Application Security Testing (DAST) tests running applications for security vulnerabilities by simulating attacks against the application from the outside. Unlike SAST, DAST doesn't require access to source code—it tests the application as a "black box" the way an attacker would.

Key characteristics:
- Runtime Testing: Tests the actual running application in a realistic environment
- External Perspective: Simulates how an external attacker would interact with your system
- Configuration Testing: Identifies security issues in deployment configuration and infrastructure
- Realistic Scenarios: Tests how security measures work under real conditions

When to use DAST:
- Pre-Production Testing: Test staging environments before production deployment
- Penetration Testing: Automated security testing as part of regular security assessments
- Compliance Validation: Verify that security controls work as intended in deployed environments
- Regression Testing: Ensure that new deployments don't introduce security vulnerabilities

DAST complements SAST by finding vulnerabilities that only appear when the application is running, such as authentication bypasses, session management issues, and configuration problems.

#### OWASP ZAP Integration

```yaml
# GitHub Actions workflow
- name: OWASP ZAP Scan
  uses: zaproxy/action-full-scan@v0.8.0
  with:
    target: 'https://staging.example.com'
    rules_file_name: '.zap/rules.tsv'
    cmd_options: '-a'
```

## API Contracts and Schema Registry

Production systems require robust API governance and schema management.

### Schema Registry with Contract Validation

#### Confluent Schema Registry Setup

```gradle
dependencies {
    implementation 'io.confluent:kafka-avro-serializer:7.5.1'
    implementation 'io.confluent:kafka-schema-registry-client:7.5.1'
}
```

#### Schema Evolution Policy

Schema evolution allows you to modify data structures over time whilst maintaining backward compatibility. This is crucial for distributed systems where different services may be updated at different times.

### Client Generation

#### OpenAPI Client Generation

```gradle
plugins {
    id 'org.openapi.generator' version '7.2.0'
}

openApiGenerate {
    generatorName = 'java'
    inputSpec = "$rootDir/api-specs/user-service-api.yaml"
    outputDir = "$buildDir/generated-client"
    apiPackage = 'com.example.userservice.client.api'
    modelPackage = 'com.example.userservice.client.model'
    configOptions = [
        dateLibrary: "java8-localdatetime",
        java8: "true",
        interfaceOnly: "false",
        useTags: "true"
    ]
}
```

## Run Books and Play Books

Operational procedures must be documented and accessible to your team.

### Incident Response Runbook

Runbooks are documented procedures that provide step-by-step instructions for handling specific operational scenarios, incidents, or maintenance tasks. They serve as the "playbook" for your operations team, ensuring consistent and effective responses to common situations.

Key components of runbooks:
- Symptoms: How to identify when the runbook applies
- Investigation Steps: Systematic approach to diagnosing the issue
- Resolution Actions: Step-by-step instructions to resolve the problem
- Escalation Procedures: When and how to escalate if initial steps don't work
- Prevention Measures: Long-term actions to prevent recurrence

Why runbooks are essential:
- Consistency: Ensure all team members respond to incidents the same way
- Speed: Reduce time to resolution with pre-planned response procedures
- Knowledge Sharing: Capture institutional knowledge and make it accessible
- Training: Help new team members learn operational procedures
- Stress Reduction: Provide clear guidance during high-pressure incident situations

#### Structure

```markdown
# User Service Incident Response

## High CPU Usage

### Symptoms
- CPU utilisation > 80% for 5+ minutes
- Response times > 2 seconds
- Error rate > 1%

### Investigation Steps
1. Check DataDog dashboard: https://app.datadoghq.com/dashboard/abc-123
2. Review recent deployments in GitHub
3. Check database connection pool metrics
4. Examine heap memory usage

### Immediate Actions
1. Scale horizontally: `kubectl scale deployment user-service --replicas=10`
2. Restart unhealthy pods: `kubectl delete pods -l app=user-service --field-selector=status.phase!=Running`

### Root Cause Analysis
- Review application logs for memory leaks
- Check for inefficient database queries
- Analyse traffic patterns for unusual spikes
```

### Automated Database Migrations

Database migrations are version-controlled scripts that systematically evolve our database schema over time. Each migration represents a specific change to the database structure (like adding tables, modifying columns, or creating indexes) and can be applied or rolled back as needed.

Key principles:
- Version Control: Each migration has a unique version number and is stored in source control
- Incremental Changes: Small, focused changes that can be applied sequentially
- Idempotent: Safe to run multiple times without causing issues
- Rollback Support: Ability to undo changes if problems occur

Why automated database migrations are crucial:
- Environment Consistency: Ensure all environments (dev, staging, production) have identical database schemas
- Deployment Safety: Reduce risk of manual schema changes causing production issues
- Team Collaboration: Multiple developers can work on schema changes without conflicts
- Audit Trail: Track exactly when and why schema changes were made
- Automated Deployment: Include database changes as part of our CI/CD pipeline

Flyway is a popular migration tool that tracks applied migrations and ensures they're applied in the correct order.

#### Flyway Configuration

```gradle
plugins {
    id 'org.flywaydb.flyway' version '9.22.3'
}

flyway {
    url = 'jdbc:postgresql://localhost:5432/myapp'
    user = 'dbuser'
    password = 'dbpass'
    schemas = ['public']
    locations = ['classpath:db/migration']
    baselineOnMigrate = true
    validateOnMigrate = true
}
```

#### Migration Scripts

```sql
-- V1__Create_users_table.sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at);
```

This production readiness phase ensures our distributed system can handle real-world challenges whilst providing the operational visibility and maintainability needed for long-term success. The next phase will focus on operational maturity and advanced reliability patterns.

---

## References

{% assign clean_code = site.data.links | where: "id", 3 | first %}
- [{{ clean_code.author }}, _{{ clean_code.title }}_]({{ clean_code.link }})

{% assign building_microservices = site.data.links | where: "id", 4 | first %}
- [{{ building_microservices.author }}, _{{ building_microservices.title }}_]({{ building_microservices.link }})
