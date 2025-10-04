
# Common Tools used for testing Java Web Services

<hr/>

A short introduction into common tools that I use in all of my projects for testing Java web applications and services.

This presentation covers the essential testing tools in the Java ecosystem that enable effective unit, integration, and acceptance testing. We'll explore libraries and frameworks that support different testing strategies including mocking, assertion handling, and container-based testing.

---

## Agenda

<hr/>
- Types of testing
  - Value-based
  - State-based
  - Interaction-based
- Test objects
  - Mocks
  - Spies
  - Stubs
- Tools and frameworks
  - JUnit5 Jupyter
  - Mockito
  - AssertJ
  - TestContainers
  - LocalStack
  - WireMock
- Gradle
- Maven
- Questions
- Resources

---

## Value-based testing

<hr/>

* Tests focus on verifying that methods return the correct values
* Also known as output-based testing
* Pure functions with no side effects are ideal for this approach
* Assertions verify the returned value matches expected results

```java
@Test
void shouldCalculateCorrectSum() {
    // arrange
    Calculator calculator = new Calculator();

    // act
    int result = calculator.add(2, 3);

    // assert
    assertEquals(5, result);
}
```

---

## State-based testing

<hr/>

* Tests verify the state of the system after an operation
* Focuses on checking object properties or system state changes
* Useful when methods modify object state or have side effects
* Often uses argument captors to verify state changes

```java
@Test
void shouldAddItemToShoppingCart() {
    // arrange
    ShoppingCart cart = new ShoppingCart();
    Item item = new Item("Book", 20.0);

    // act
    cart.addItem(item);

    // assert
    assertEquals(1, cart.getItemCount());
    assertTrue(cart.contains(item));
    assertEquals(20.0, cart.getTotalValue());
}
```

---

## Interaction-based testing

<hr/>

* Tests verify that methods are called correctly on dependencies
* Also known as behaviour verification or communication-based testing
* Uses mocks to verify method calls, call counts, and parameters
* Focuses on how objects collaborate rather than the final result

```java
@Test
void shouldCallEmailServiceWhenOrderCompleted() {
    // arrange
    EmailService emailService = mock(EmailService.class);
    OrderProcessor processor = new OrderProcessor(emailService);
    Order order = new Order("123", "customer@example.com");

    // act
    processor.completeOrder(order);

    // assert
    verify(emailService).sendConfirmation("customer@example.com", "123");
    verify(emailService, times(1)).sendConfirmation(anyString(), anyString());
}
```

---

## Test objects (doubles .red[*])

<hr/>

Test doubles are objects that are used in tests to replace real objects, to model behaviour and state, to arrange the setup for testing.

### Types of test doubles:

- Mocks
- Spies
- Stubs

{% assign test_double_link = site.data.links | where: "id", 23 | first %}
.footnote[.red[*] See Martin Fowler on [Doubles]({{ test_double_link.link }})]

---

### Mocks .red[*]

<hr/>

* Mocks are test objects that are used for *behaviour testing*
* Replace dependencies in tests and are used to assert for
    * Method calls (a certain method was called, or not)
    * Methods are called with expected values (using argument captors, or argument matchers)
* Should throw an exception when they receive a call they don't expect
* Are part of the assertion category

{% assign mocks_stubs_link = site.data.links | where: "id", 24 | first %}
.footnote[.red[*] See Martin Fowler on [Mocks]({{ mocks_stubs_link.link }})]

---

### Spies

<hr/>

* Spies wrap real objects and observe their behaviour
* Allow calling real methods while recording interactions
* Useful when you want to test partial behaviour of real objects
* Can verify method calls on real objects without replacing them entirely

```java
@Test
void shouldSpyOnRealObject() {
    // arrange
    List<String> realList = new ArrayList<>();
    List<String> spyList = spy(realList);

    // act
    spyList.add("item1");
    spyList.add("item2");

    // assert
    verify(spyList, times(2)).add(anyString());
    assertEquals(2, spyList.size()); // Real behaviour preserved
    assertTrue(spyList.contains("item1"));
}
```

---

### Stubs

<hr/>

* Stubs provide predetermined responses to method calls
* Used to isolate the system under test from dependencies
* Return specific values or throw exceptions as needed for test scenarios
* Do not verify interactions - only provide necessary data

```java
@Test
void shouldUseStubForExternalService() {
    // arrange
    PaymentService paymentStub = mock(PaymentService.class);
    when(paymentStub.processPayment(100.0)).thenReturn(true);
    when(paymentStub.processPayment(0.0)).thenThrow(new IllegalArgumentException());

    OrderService orderService = new OrderService(paymentStub);

    // act
    boolean result = orderService.processOrder(100.0);

    // assert
    assertTrue(result);
    // No verification of method calls - stubs just provide data
}
```

---

## JUnit 5 - Jupyter .red[*]

<hr/>

* JUnit5 Jupyter is a component of the JUnit5 framework
* It provides a test engine for running tests on the JVM
* Provides a programming model and an extension model for writing tests

{% assign junit5_link = site.data.links | where: "id", 19 | first %}
.footnote[.red[*] See JUnit5 [Documentation]({{ junit5_link.link }})]

---

### JUnit5 - General Notes

<hr/>

* A test class doesn't need to be marked as `public`
* Test classes don't need to be marked with any annotations
* Test methods don't need to be `public`
* Test classes can be extended with `@ExtendWith()` annotation
* Provides an assertion package

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SampleTest {

    @Test
    void shouldBeTrue() {
        assertEquals(2, 2);
    }
}
```

---

### JUnit5 - Popular APIs

<hr/>

* `@Test` - used to mark tests such that JUnit will run them
* `@ParameterizedTest` - run same tests multiple times with different arguments
* `@DisplayName` - custom test name, overrides the default name for a method. Useful in Java to name tests with spaces etc.
* `@BeforeEach` - used to decorate a method that will run before each test
* `BeforeAll` - used to decorate a method that will run before all tests start running. Similar to `@BeforeAll` from JUnit4
* `@AfterEach` - decorate a method that will run after each test
* `AfterAll` - decorate a method that will run after all tests
* `@Tag` - declares tags for filtering tests at class or method level
* `@Disabled` - disable a test. Will be ignored by the test engine

---

### JUnit5 - Parameterized Tests

<hr/>

Parameterized tests are a useful way to run same test multiple times with multiple parameters.
Instead of duplicating test cases for variation between parameters, it can re-use the same test and provide variations between parameters.

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SampleTest {

    @ParameterizedTest(name = "When input is {0} then the result should be {1}")
    @CsvSource({"2,2","4,4"})
    void shouldBeTrue(int testVal, int expected) {
        assertEquals(testVal, expected);
    }
}
```

---

## Mockito .red[*]

<hr/>

Mockito is an open source testing framework for Java, that allows creation of mocks, stubs and spyes.

It can create mocks in several ways:

* Using a JUnit extension and annotations
* Creating mocks using static factory methods

{% assign mockito_link = site.data.links | where: "id", 20 | first %}
.footnote[.red[*] See Mockito's [Documentation]({{ mockito_link.link }})]

---

### Using Mockito's JUnit extension

<hr/>

```java
@ExtendWith(MockitoExtension.class)
class SampleTest {

    @InjectMocks
    private HotelCreator sut;

    @Mock
    private HotelRepository repository;

    @Test
    void shouldCreateHotel() {
        // arrange
        var hotel = Hotel.builder().build();
        var hotelEntity = HotelEntity.builder().build();
        when(repository.create(hotelEntity)).thenReturn(anotherEntity);

        // act
        sut.create(hotel);

        // assert
        verify(repository).create(hotelEntity);
        // more assertions here
    }
}
```

---

### Using Mockito's static factory methods

<hr/>

```java
class SampleTest {

    private HotelCreator sut;

    private final HotelRepository repository = Mockito.mock(HotelRepository.class);

    @BeforeEach
    void setup() {
        Mockito.reset(repository);
        sut = new HotelCreator(repository);
    }

    @Test
    void shouldCreateHotel() {
        // arrange
        var hotel = Hotel.builder().build();
        var hotelEntity = HotelEntity.builder().build();
        when(repository.create(hotelEntity)).thenReturn(anotherEntity);

        // act
        sut.create(hotel);

        // assert
        verify(repository).create(hotelEntity);
        // more assertions here
    }
}
```

---

### Using Mockito's Argument Captor

<hr/>

* `ArgumentCaptor` is used to capture method arguments when are called in tests
* Useful to further assert on these objects
* Used for state based testing

```java
@Test
void shouldCaptureAndVerify() {
    // arrange
    var expectedEntity = EntityCreator.createDefault();
    var hotel = Hotel.builder().build();
    ArgumentCaptor<HotelEntity> hotelCaptor = ArgumentCaptor.forClass(HotelEntity.class);
    when(repository.save(hotelCaptor.capture())).thenReturn(anotherObject);

    // act
    repository.save(hotel);

    // assert
    var actualEntity = hotelCaptor.getValue();

    assertThat(actualEntity).isNotNull().isEqualTo(expectedEntity);
}
```
---

## TestContainers .red[*]

<hr/>

Testcontainers is a Java library that provides automatic lifecycle management to third party dependencies running in Docker containers and that are used in testing.
Most common usecases are databases, AWS stack or messaging tools.

### Advantages

* Allows using in tests same version for dependencies as used in production
* The container(s) lifecycle management is handled by the library
* Containers start and stop automatically, when needed
* Configurable via modules
* Can specify which version from your dependency to run
* Integrates seamlessly with testing frameworks
* Requires an environment capable of running Docker containers

{% assign tc_link = site.data.links | where: "id", 21 | first %}
.footnote[.red[*] See TestContainers' [Documentation]({{ tc_link.link }})]

---

## AssertJ .red[*]

<hr/>

AssertJ is an assertions library, providing a rich set of assertions, designed for readability and fluent code.

### Advantages

* Allows chaining assertion methods to have a fluent code
* Provides assertions for collections, exceptions, object fields, comparing field by field etc.
* Can be configured
* Provides an extension mechanism

{% assign assertj_link = site.data.links | where: "id", 22 | first %}
.footnote[.red[*] See AssertJ's [Documentation]({{ assertj_link.link }})]

---

### Fluent Assertions with AssertJ

<hr/>

#### Simple assertions

```java
assertThat("The Lord of the Rings")
                            .isNotNull()
                            .startsWith("The")
                            .contains("Lord")
                            .endsWith("Rings");
```

#### Field by field recursive assertion

```java
assertThat(sherlock).usingRecursiveComparison()
                    .ignoringFields("name", "home.address.street")
                    .isEqualTo(moriarty);
```

#### Exception assertion

```java
assertThatExceptionOfType(RuntimeException.class)
         .isThrownBy(() -> sut.executeAndThrow())
         .havingRootCause()
         .withMessage("root error");
```

---

## LocalStack .red[*]

<hr/>

LocalStack is a cloud service emulator that runs in a single container on your machine for testing and developing cloud and serverless applications offline.

### Use Cases

* Test AWS integrations locally without connecting to real AWS services
* Develop and test Lambda functions, S3 buckets, DynamoDB tables, SQS queues
* Integration testing for cloud-native applications
* Cost-effective testing without AWS charges

{% assign localstack_link = site.data.links | where: "id", 30 | first %}
.footnote[.red[*] See LocalStack's [Documentation]({{ localstack_link.link }})]

---

### Advantages of using LocalStack

<hr/>

* Supports most AWS services (S3, Lambda, DynamoDB, SQS, SNS, etc.)
* Runs locally in Docker containers
* No internet connection required for testing
* Integrates with TestContainers for automated test lifecycle
* Faster feedback loop compared to real AWS testing

---

### Example of using LocalStack with TestContainers

<hr/>

```java
@Testcontainers
class S3ServiceTest {

    @Container
    static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withServices(LocalStackContainer.Service.S3);

    @Test
    void shouldUploadFileToS3() {
        // arrange
        S3Client s3Client = S3Client.builder()
                .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                .region(Region.of(localstack.getRegion()))
                .build();

        s3Client.createBucket(CreateBucketRequest.builder().bucket("test-bucket").build());

        // act
        s3Client.putObject(PutObjectRequest.builder()
                .bucket("test-bucket")
                .key("test-file.txt")
                .build(), RequestBody.fromString("Hello LocalStack!"));

        // assert
        GetObjectResponse response = s3Client.getObject(GetObjectRequest.builder()
                .bucket("test-bucket")
                .key("test-file.txt")
                .build(), ResponseTransformer.toBytes());

        assertThat(response.contentLength()).isGreaterThan(0);
    }
}
```

---

## WireMock .red[*]

<hr/>

WireMock is a flexible API mocking tool for fast, robust and comprehensive testing. It can mock HTTP services by running as a standalone server or embedded within your tests.

### Use Cases

* Mock external REST APIs and web services during testing
* Create test doubles for microservices integration testing
* Simulate network failures and slow responses
* Test API contracts and service interactions

### Advantages

* Standalone server or embedded mode
* Record and playback real API interactions
* Advanced request matching (URL, headers, body)
* Response templating and transformation
* Fault injection and latency simulation

{% assign wiremock_link = site.data.links | where: "id", 31 | first %}
.footnote[.red[*] See WireMock's [Documentation]({{ wiremock_link.link }})]

---

### Example of using WireMock

<hr/>

```java
@Test
void shouldCallExternalApiWithWireMock() {
    // arrange
    WireMockServer wireMockServer = new WireMockServer(8089);
    wireMockServer.start();

    wireMockServer.stubFor(get(urlEqualTo("/api/users/123"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":123,\"name\":\"John Doe\"}")));

    UserService userService = new UserService("http://localhost:8089");

    // act
    User user = userService.getUser(123);

    // assert
    assertThat(user.getId()).isEqualTo(123);
    assertThat(user.getName()).isEqualTo("John Doe");

    wireMockServer.stop();
}
```

---

## Build Tools for Testing

<hr/>

Both Gradle and Maven provide comprehensive testing capabilities with different approaches to organizing and running unit and integration tests.

---

## Gradle Testing .red[*]

<hr/>

Gradle provides built-in testing capabilities through the Java plugin with flexible test task configuration.

### Unit Tests
* Uses `test` task by default
* Runs tests from `src/test/java`
* Automatic dependency management for test frameworks

### Integration Tests
* Custom test tasks (e.g., `integrationTest`)
* Separate source sets: `src/integrationTest/java`
* Can run against running applications or use TestContainers

```gradle
tasks.register('integrationTest', Test) {
    useJUnitPlatform()
    testClassesDirs = sourceSets.integrationTest.output.classesDirs
    classpath = sourceSets.integrationTest.runtimeClasspath
}
```

{% assign gradle_test_link = site.data.links | where: "id", 32 | first %}
.footnote[.red[*] See Gradle [Testing Documentation]({{ gradle_test_link.link }})]

---

## Maven Testing Plugins .red[*]

<hr/>

Maven uses dedicated plugins for different test phases with clear separation between unit and integration tests.

### Surefire Plugin (Unit Tests)
* Runs during `test` phase
* Tests from `src/test/java`
* Fails fast on test failures

### Failsafe Plugin (Integration Tests)
* Runs during `integration-test` phase
* Tests from `src/test/java` with `*IT.java` naming convention
* Defers failure until `post-integration-test` phase

{% assign surefire_link = site.data.links | where: "id", 33 | first %}
{% assign failsafe_link = site.data.links | where: "id", 34 | first %}
.footnote[.red[*] See Maven [Surefire]({{ surefire_link.link }}) and [Failsafe]({{ failsafe_link.link }}) Documentation]

---

### Maven FailSafe Plugin - Configuration Example

<hr/>

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---
## Resources

<hr/>

{% assign junit_link = site.data.links | where: "id", 19 | first %}
{% assign mockito_doc_link = site.data.links | where: "id", 25 | first %}
{% assign testcontainers_link = site.data.links | where: "id", 21 | first %}
{% assign assertj_link = site.data.links | where: "id", 22 | first %}
{% assign localstack_link = site.data.links | where: "id", 30 | first %}
{% assign wiremock_link = site.data.links | where: "id", 31 | first %}
{% assign gradle_test_link = site.data.links | where: "id", 32 | first %}
{% assign surefire_link = site.data.links | where: "id", 33 | first %}
{% assign failsafe_link = site.data.links | where: "id", 34 | first %}
### Official Documentation

#### Testing Frameworks & Libraries
* [{{ junit_link.title }}]({{ junit_link.link }})
* [{{ mockito_doc_link.title }}]({{ mockito_doc_link.link }})
* [{{ testcontainers_link.title }}]({{ testcontainers_link.link }})
* [{{ assertj_link.title }}]({{ assertj_link.link }})
* [{{ localstack_link.title }}]({{ localstack_link.link }})
* [{{ wiremock_link.title }}]({{ wiremock_link.link }})

#### Build Tools & Plugins
* [{{ gradle_test_link.title }}]({{ gradle_test_link.link }})
* [{{ surefire_link.title }}]({{ surefire_link.link }})
* [{{ failsafe_link.title }}]({{ failsafe_link.link }})

---

## Resources - Continued

<hr/>

{% assign mocks_stubs_link = site.data.links | where: "id", 24 | first %}
{% assign test_double_link = site.data.links | where: "id", 23 | first %}
{% assign uncle_bob_link = site.data.links | where: "id", 26 | first %}
### Articles and Guides

* [{{ mocks_stubs_link.title }}]({{ mocks_stubs_link.link }})
* [{{ test_double_link.title }}]({{ test_double_link.link }})
* [{{ uncle_bob_link.title }}]({{ uncle_bob_link.link }})

{% assign unit_testing_book = site.data.links | where: "id", 27 | first %}
{% assign growing_oo_book = site.data.links | where: "id", 28 | first %}
{% assign tdd_book = site.data.links | where: "id", 29 | first %}
### Books

* [{{ unit_testing_book.title }}]({{ unit_testing_book.link }}) by {{ unit_testing_book.author }}
* [{{ growing_oo_book.title }}]({{ growing_oo_book.link }}) by {{ growing_oo_book.author }}
* [{{ tdd_book.title }}]({{ tdd_book.link }}) by {{ tdd_book.author }}

---

## Questions?

<hr/>

---

## Thank you!

<hr/>
