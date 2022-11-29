
# Tools used for testing in Java

<hr/>

An introduction into popular tools used for testing Java applications

---

## Agenda

<hr/>

* [JUnit5 (Jupyter)](https://junit.org/junit5/docs/current/user-guide/)
    * APIs
    * Parameterized tests
* [Mockito](https://site.mockito.org/)
    * Mocks
    * Spys
    * Stubs
* [TestContainers](https://www.testcontainers.org/)
* [AssertJ](https://assertj.github.io/doc/)
    * Fluent assertions
* Questions
* Resources

---

## JUnit 5 - Jupyter

<hr/>

* JUnit5 Jupyter is a component of the JUnit5 framework 
* It provides a test engine for running tests on the JVM
* Provides a programming model and an extension model for writing tests

---

### JUnit5

<hr/>

#### General notes

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

### JUnit5

<hr/>

#### Popular APIs

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

#### JUnit5 Parameterized Tests

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

## [Mockito](https://site.mockito.org/)

<hr/>

Mockito is an open source testing framework for Java, that allows creation of mocks, stubs and spyes.

It can create mocks in several ways:

* Using a JUnit extension and annotations
* Creating mocks using static factory methods

---

### Using a JUnit extension

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

### Using static factory methods

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

### Mockito Argument Captor

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

## Types of Testing

<hr/>

### Value-based testing

### State-based testing

### Interaction-based testing

---

## Test objects [(doubles)](https://martinfowler.com/bliki/TestDouble.html)

<hr/>

Test doubles are objects that are used in tests to replace real objects, to model behaviour and state, to arrange the setup for testing.

### Mocks

### Spies

### Stubs

---

### Mocks

<hr/>

#### See Martin Fowler on [Mocks](https://martinfowler.com/articles/mocksArentStubs.html)

* Mocks are test objects that are used for *behaviour testing*
* Replace dependencies in tests and are used to assert for
    * Method calls (a certain method was called, or not)
    * Methods are called with expected values (using argument captors, or argument matchers)
* Should throw an exception when they receive a cal they don't expect
* Are part of the assertion cathegory

---

### Spies

<hr/>

* Another type of test objects, usually used for observing some behaviour
* They can record behaviour that should happen on a real object: how many times something happened

---

### Stubs

<hr/>

* Stubs are test objects that are used to return canned anwers in tests
* Should not respond outside the test case

---

## TestContainers

<hr/>

Testcontainers is a Java library that provides automatic lifecycle management to third party dependencies runing in Docker containers and that are used in testing.
Most common usecases are databases, AWS stack or messaging tools.

### Advantages

* Allows using in tests same version for dependencies as used in production
* The container(s) lifecycle management is handled by the library
* Containers start and stop automatically, when needed
* Configurable via modules
* Can specify which version from your dependency to run
* Integrates seamlessly with testing frameworks
* Requires an environment capable of running Docker containers

---

## AssertJ

<hr/>

AssertJ is an assertions library, providing a rich set of assertions, designed for readability and fluent code.

### Advantages

* Allows chaining assertion methods to have a fluent code
* Provides assertions for collections, exceptions, object fields, comparing field by field etc.
* Can be configured
* Provides an extension mechanism

---

### Fluent Assertions

<hr/>

* Simple assertions
```java
assertThat("The Lord of the Rings")
                            .isNotNull()   
                            .startsWith("The") 
                            .contains("Lord") 
                            .endsWith("Rings"); 
```
* Field by field recursive assertion
```java
assertThat(sherlock).usingRecursiveComparison()
                    .ignoringFields("name", "home.address.street")
                    .isEqualTo(moriarty);
```
* Exception assertion
```java
assertThatExceptionOfType(RuntimeException.class)
         .isThrownBy(() -> sut.executeAndThrow())
         .havingRootCause()
         .withMessage("root error");
```

---

## Questions?

<hr/>

---

## Resources

<hr/>

