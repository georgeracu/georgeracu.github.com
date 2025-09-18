---
layout: post
title: A Set of Topics to Follow when Developing Distributed Software Systems - Step 1 (The Walking Skeleton)
permalink: /blog/articles/step-1-walking-skeleton-distributed-software-systems/
tags: [post, distributed-systems, software-development, walking-skeleton]
mathjax: false
description: This is the first step in a collection of topics and how-to's on software development. This blog post describes the concept of a Walking Skeleton in software development.
---

## Why the Walking Skeleton Approach?

Building distributed software systems is inherently complex. Unlike monolithic applications where components communicate through in-memory calls, distributed systems introduce network communication, partial failures, eventual consistency, and coordination challenges that can catch even experienced developers off guard.

The Walking Skeleton approach serves as a practical foundation - a set of battle-tested practices that help teams avoid common pitfalls and build systems that are reliable, maintainable, and scalable from day one. Rather than learning these lessons through painful production incidents, teams can proactively adopt these patterns and processes.

The goal isn't perfection from the start, but establishing a foundation that supports iterative improvement and reduces the likelihood of architectural debt that becomes expensive to fix later.

## What is a Walking Skeleton

A Walking Skeleton, as described by Alistair Cockburn, is a tiny implementation of the system that performs a small end-to-end function. It need not use the final architecture, but it should link together the main architectural components. The Walking Skeleton grows to become the whole system - hence the metaphor of a skeleton that "walks" through the entire application lifecycle.

The concept, also discussed in "The Pragmatic Programmer," emphasizes starting with the smallest possible implementation that touches all major components of your system. This approach allows you to:

- Validate your architectural assumptions early
- Establish your development workflow and toolchain
- Create a foundation that the entire team can build upon
- Identify integration points and potential issues before they become expensive problems
- Demonstrate progress to stakeholders with working software

In the context of distributed systems, a Walking Skeleton becomes even more critical because it forces you to address cross-cutting concerns like service communication, deployment pipelines, monitoring, and configuration management from day one. Rather than building these capabilities as an afterthought, you integrate them into your development process from the very beginning.

For this phase, I'll use a Java-based stack with Spring Boot and Gradle as our example, though the principles apply to any technology stack.

## Target System Types

This approach focuses on distributed software systems - applications composed of multiple services that communicate over a network to deliver business value. This includes:

- Microservices architectures: where business capabilities are decomposed into independent services
- Event-driven systems: that react to and produce events across service boundaries
- Cloud-native applications: designed to leverage cloud platform capabilities
- API-first systems: where services expose well-defined interfaces

These systems share common characteristics: they're deployed across multiple processes/machines, they communicate over unreliable networks, they need to handle partial failures gracefully, and they require coordination between autonomous components.

The practices in this guide apply whether you're building a greenfield system or evolving an existing monolith towards a more distributed architecture.

## Key Architectural Decisions

### Monolith First Pattern

When building distributed systems, resist the temptation to immediately decompose into microservices. As {% assign link = site.data.links | where: "id", 10 | first %}[{{ link.author }} describes in his {{ link.title }}]({{ link.link }}) approach, start with a well-structured monolith that can be decomposed later when you better understand the domain boundaries.

#### Why Monolith First?

- Domain understanding: You don't know the right service boundaries until you understand the problem domain
- Reduced complexity: Avoid distributed system complexity whilst learning the business domain
- Faster iteration: Changes across service boundaries are expensive in distributed systems
- Easier refactoring: Moving code within a monolith is simpler than redefining service contracts

#### Implementation approach:

- Design your monolith with clear module boundaries
- Use packages or modules that could become services later
- Avoid shared databases between modules
- Define clear interfaces between modules
- Test module boundaries with architecture tests (ArchUnit)

### Critical Anti-Pattern: Distributed Monolith

Never build a distributed monolith - this combines the worst aspects of both architectures:

- High coupling: Services that must be deployed together
- Shared databases: Multiple services accessing the same database tables
- Synchronous communication: Services blocking on each other for every operation
- Distributed transactions: Coordinating transactions across service boundaries

#### Warning signs of a distributed monolith:

- Services that always deploy together
- Cascading failures when one service is down
- Database tables accessed by multiple services
- Long chains of synchronous service calls
- Shared libraries containing business logic

### Git Repository with Cloud Hosting

#### Setup

Create a GitHub repository for our project. Initialize it with a basic Spring Boot project structure using Spring Initializr (or some template project that you might already have).

```bash
# Example project structure
my-distributed-app/
├── .github/
│   └── workflows/
├── src/
│   └── main/java/com/example/app/
├── build.gradle
├── gradle.properties
└── README.md
```

#### Key practices
- Setup a branching model (trunk based development works wonders; others can be considered as well)
- Use conventional commit messages (e.g., "feat:", "fix:", "docs:")
- Set up branch protection rules requiring PR reviews
  - No direct commit or push to `main`
  - Only merge commits to `main`
- Configure repository settings for security (dependency alerts, secret scanning)

### CI/CD Pipeline with GitHub Actions

#### Setup

Create `.github/workflows/ci.yml` that runs on every push and pull request.

```yaml
# Example GitHub Actions workflow
name: CI
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - run: ./gradlew test
      - run: ./gradlew build
```

#### Key features

- Automated testing on every commit
- Build artifact creation
- Docker image building and pushing
- Deployment to staging environments
- Using latest version of Java that has long term support (at the time of this writing)

### Local Development Environment

**Ensure consistent development environments across the team.**

#### Setup

**Tools and practices:**

- Java SDK: Use SDKMAN! for managing Java versions: `sdk install java 21.0.1-tem`
- IDE: IntelliJ IDEA Ultimate or VS Code with Java extensions
- Docker: For running dependencies locally (databases, message queues)
- Docker Compose: Define development stack in `docker-compose.yml`

```yaml
# Example docker-compose.yml for local development
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: myapp
      POSTGRES_USER: dev
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
```

### Code Quality and Coverage Monitoring

**SonarCloud for cloud-based analysis, or SonarQube for self-hosted.**

#### Setup with SonarCloud

- Connect your GitHub repository to SonarCloud
- Add Gradle plugin to `build.gradle`:

```gradle
plugins {
    id 'org.sonarqube' version '4.4.1.3373'
    id 'jacoco'
}

jacocoTestReport {
    reports {
        xml.enabled true
    }
}
```

##### Integration

Add SonarCloud analysis to your GitHub Actions workflow.

### Linting and Code Style

#### Tools

- Checkstyle
- SpotBugs
- Google Java Format

**Setup in Gradle:**

```gradle
plugins {
    id 'checkstyle'
    id 'com.github.spotbugs' version '5.2.5'
    id 'com.diffplug.spotless' version '6.23.3'
}

spotless {
    java {
        googleJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}
```

#### Integration

- Run checks in CI pipeline
- Configure IDE to use the same formatting rules
- Set up pre-commit hooks with tools like pre-commit or Husky

### AI Coding Assistant Adoption

#### Tools

- GitHub Copilot
- Claude Code etc.

#### Best practices

- Establish team guidelines for AI assistant usage
- Review AI-generated code carefully, especially for security implications
- Use AI assistants to generate boilerplate code, tests, and documentation
- Train team members on effective prompt engineering

#### Example usage scenarios

- Generating Spring Boot controller boilerplate
- Creating unit test templates
- Writing API documentation
- Generating Gradle build configurations
- Reviewing pull requests

#### Integration considerations

- Ensure AI-generated code follows your established patterns and style
- Review AI suggestions for potential security vulnerabilities
- Document any specific AI tools and configurations used by the team

### Testing Strategy

A comprehensive testing strategy is crucial for the Walking Skeleton phase. It establishes the foundation for confidence in your code and enables rapid development without sacrificing quality.

#### Unit Tests

**JUnit 5** is the modern standard for Java unit testing, providing powerful features for test organisation and execution.

**Setup in Gradle:**

```gradle
dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.1'
    testImplementation 'org.mockito:mockito-core:5.8.0'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.8.0'
    testImplementation 'org.assertj:assertj-core:3.24.2'
}

test {
    useJUnitPlatform()
    testLogging {
        events "passed", "skipped", "failed"
    }
}
```

**Example Unit Test:**

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should create user with valid data")
    void shouldCreateUserWithValidData() {
        // Arrange
        User user = new User("john.doe", "john@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.createUser("john.doe", "john@example.com");

        // Asser
        assertThat(result.getUsername()).isEqualTo("john.doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        verify(userRepository).save(any(User.class));
    }
}
```

#### Integration Tests

**TestContainers** enables testing with real databases and external services without requiring complex setup.

**Setup Dependencies:**

```gradle
dependencies {
    testImplementation 'org.testcontainers:junit-jupiter:1.19.3'
    testImplementation 'org.testcontainers:postgresql:1.19.3'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

**Example Integration Test:**

```java
@SpringBootTest
@Testcontainers
class UserRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldPersistAndRetrieveUser() {
        // Given
        User user = new User("jane.doe", "jane@example.com");

        // When
        User saved = userRepository.save(user);
        Optional<User> retrieved = userRepository.findById(saved.getId());

        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getUsername()).isEqualTo("jane.doe");
    }
}
```

#### LocalStack for AWS Services

LocalStack provides local emulation of AWS services for testing cloud integrations.

#### Docker Compose Setup

```yaml
# Add to docker-compose.yml
services:
  localstack:
    image: localstack/localstack:3.0
    ports:
      - "4566:4566"
    environment:
      - SERVICES=s3,sqs,sns,dynamodb
      - DEBUG=1
    volumes:
      - "/var/run/docker.sock:/var/run/docker.sock"
```

#### Integration with TestContainers

```java
@SpringBootTest
@Testcontainers
class S3ServiceIntegrationTest {

    @Container
    static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.0"))
            .withServices(LocalStackContainer.Service.S3);

    @Test
    void shouldUploadAndDownloadFile() {
        // Configure S3 client to use LocalStack
        S3Client s3Client = S3Client.builder()
                .endpointOverride(localstack.getEndpoint())
                .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("test", "test")))
                .region(Region.US_EAST_1)
                .build();

        // Test S3 operations
        s3Client.createBucket(CreateBucketRequest.builder()
            .bucket("test-bucket")
            .build());

        // Verify bucket creation and file operations
        assertThat(s3Client.listBuckets().buckets()).hasSize(1);
    }
}
```

#### Testing Best Practices

##### Test Organisation

- Keep unit tests fast (< 1 second each)
- Use descriptive test names with `@DisplayName`
- Follow the Arrange-Act-Assert pattern
- Use TestContainers for integration tests requiring external dependencies

##### CI/CD Integration

- Run unit tests on every commit
- Run integration tests in pull request pipelines
- Generate test coverage reports with JaCoCo
- Fail builds on test failures or coverage drops

#### Architecture Testing with ArchUnit

ArchUnit enables testing of architectural rules and constraints as part of our automated test suite. This ensures that our codebase adheres to architectural principles and prevents architectural drift over time.

##### Setup Dependencies

```gradle
dependencies {
    testImplementation 'com.tngtech.archunit:archunit-junit5:1.2.1'
}
```

##### Example Architecture Tests

```java
@AnalyzeClasses(packages = "com.example.app")
class ArchitectureTest {

    @ArchTest
    static final ArchRule services_should_only_be_accessed_by_controllers =
        classes().that().resideInAPackage("..service..")
            .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..");

    @ArchTest
    static final ArchRule repositories_should_only_be_accessed_by_services =
        classes().that().resideInAPackage("..repository..")
            .should().onlyBeAccessed().byAnyPackage("..service..");

    @ArchTest
    static final ArchRule controllers_should_not_access_repositories_directly =
        noClasses().that().resideInAPackage("..controller..")
            .should().accessClassesThat().resideInAPackage("..repository..");

    @ArchTest
    static final ArchRule entities_should_not_depend_on_services =
        noClasses().that().resideInAPackage("..entity..")
            .should().dependOnClassesThat().resideInAPackage("..service..");

    @ArchTest
    static final ArchRule services_should_be_named_correctly =
        classes().that().resideInAPackage("..service..")
            .and().areNotInterfaces()
            .should().haveSimpleNameEndingWith("Service");

    @ArchTest
    static final ArchRule controllers_should_be_annotated_with_rest_controller =
        classes().that().resideInAPackage("..controller..")
            .should().beAnnotatedWith(RestController.class);
}
```

##### Custom Architecture Rules

```java
@AnalyzeClasses(packages = "com.example.app")
class CustomArchitectureTest {

    @ArchTest
    static final ArchRule no_spring_framework_in_domain =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework..");

    @ArchTest
    static final ArchRule use_slf4j_for_logging =
        noClasses().should().accessClassesThat()
            .resideInAnyPackage("java.util.logging..", "org.apache.log4j..")
            .because("Use SLF4J for logging instead");

    @ArchTest
    static final ArchRule configuration_classes_should_be_in_config_package =
        classes().that().areAnnotatedWith(Configuration.class)
            .should().resideInAPackage("..config..");
}
```

##### Integration with CI/CD

Architecture tests run as part of our regular test suite and will fail the build if architectural rules are violated, ensuring consistent adherence to our design principles.

This testing strategy ensures that our Walking Skeleton is robust from the beginning and provides confidence for rapid iteration.

## Development Practices

Establishing solid development practices from the beginning ensures team alignment and code quality throughout the project lifecycle.

### Trunk-Based Development

Adopt trunk-based development as your branching strategy:

- Main branch: All developers commit to a single main branch frequently
- Short-lived branches: Feature branches should live for less than a day
- Feature flags: Use feature toggles for incomplete features rather than long branches
- Continuous integration: Every commit triggers automated testing

##### Benefits

- Reduces merge conflicts and integration problems
- Enables rapid deployment and rollback
- Forces good testing practices
- Improves team collaboration

### Environment Strategy

Establish three core environments with clear purposes:

1. Development:
   - Individual developer environments (local + shared dev)
   - Latest code changes
   - Mock external dependencies

2. Staging:
   - Production-like environment
   - Integration testing with real dependencies
   - Pre-production validation

3. Production:
   - Live system serving users
   - Monitoring and alerting fully operational
   - Automated deployment and rollback capabilities

### Performance Testing Environment

Create a dedicated environment that mimics production:

- Infrastructure: Same instance types and network configuration
- Data volume: Production-like data sets for realistic testing
- Load patterns: Representative user traffic simulation
- Monitoring: Same observability stack as production

### Code Review Practices

Implement comprehensive code review processes:

Human Reviews:
- All code must be reviewed before merging
- Focus on logic, maintainability, and architectural alignment
- Review both production and test code with equal rigour

AI-Assisted Reviews:
- Use tools like GitHub Copilot for initial code suggestions
- Leverage AI for identifying potential bugs and security issues
- Maintain human oversight for architectural decisions

### Test Code Quality

Treat test code as production code:

- Apply the same quality standards to test code
- Refactor test code to eliminate duplication
- Use descriptive test names and clear assertions
- Maintain test code with the same rigour as application code

Testing principles:

- Tests should be independent and repeatable
- Use clear arrange-act-assert structure
- Mock external dependencies appropriately
- Maintain good test coverage without obsessing over percentages

### Commit and Collaboration Practices

Establish clear guidelines for code commits and team collaboration:

#### Commit Templates

- Ensure work item numbers are referenced in every commit (Jira task, GitHub issue etc.)
- Use consistent commit message formats for better traceability
- Include context about why changes were made, not just what changed

#### Pair Programming and AI Assistance

- Use `Co-authored-by` when doing pair programming or using an AI coding assistant
- Give proper attribution to all contributors in commit messages
- Document AI tool usage for transparency and learning

#### Semantic Versioning

- Use semantic versioning as described in [SemVer](https://semver.org/)
- Follow MAJOR.MINOR.PATCH versioning convention
- Increment versions based on the nature of changes (breaking, feature, fix)

This foundation ensures that our Walking Skeleton not only works but can evolve sustainably as the system grows.
