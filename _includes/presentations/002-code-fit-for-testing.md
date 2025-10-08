
# Write code fit for testing

<hr/>

* Why?
* Start with Test Driven Development (TDD)
* Refactor existing code to make it easier to test
* Find code seams
* Work with legacy code
* Tests should be improved every day

---

## Why?

<hr/>

.fst-italic[
It is interesting to note that having automated tests primarily created and maintained either by QA or an outsourced party is not correlated with IT performance.

The theory behind this is that when developers are involved in creating and maintaining acceptance tests, there are two important effects. First, the code becomes more testable when developers write tests. This is one of the main reasons why test-driven development (TDD) is an important practice‚ as it forces developers to create more testable designs. Second, when developers are responsible for the automated tests, they care more about them and will invest more effort into maintaining and fixing them.
]

{% assign accelerate_link = site.data.links | where: "id", 35 | first %}
.footnote[.red[*] See [{{ accelerate_link.title }}]({{ accelerate_link.link }})]

---

## What about QAs and Testers?

<hr/>

.fst-italic[
None of this means that we should be getting rid of testers. Testers serve an essential role in the software delivery lifecycle, performing manual testing such as exploratory, usability, and acceptance testing, and helping to create and evolve suites of automated tests by working alongside developers.
]

{% assign accelerate_link = site.data.links | where: "id", 35 | first %}
.footnote[.red[*] See [{{ accelerate_link.title }}]({{ accelerate_link.link }})]

---

## Start with TDD

<hr/>

* Starting with a test-first approach will naturally guide your code architecture to be easy to test
* It will avoid constructs like
    * Field injection (considered an anti-pattern)
    * Creating objects using setters
* It will _force_ us to:
    * Replace field injection with constructor injection
    * Add `equals()` and `hashCode()` on objects when we need to compare equality
{% assign builder_link = site.data.links | where: "id", 36 | first %}
    * Use the [construction builder]({{ builder_link.link }}) pattern for a more fluent syntax
    * Think in small increments and small units (small methods and classes)

---

## First Example

<hr/>

### First user story

**Feature:** Hotel Management System<br/>
**Scenario:** A hotel owner can see all rooms<br/>
Given a hotel owner<br>
When I query the API for all my rooms<br>
Then I should get a list with all rooms

---

## Using an outside-in approach

<hr/>

* We start fleshing out each layer at a time (remember the Swiss cheese analogy on layered testing)
* We start with minimal code needed for lower layers, just enough to make the code compile and the tests pass for the current layer under test
* Never add more than needed, we try to minimize the amount of code that we write
* When moving to a lower layer, follow the same approach:
    * Write a test first
    * Add code enough to make that test pass
    * Add code for lower level enough to make it compile
    * Mock behaviour and state on lower level
    * Refactor when your test is green
    * Repeat

---

## Service architecture driven by tests

<hr/>

### Apply Wishful Thinking

.fst-italic[
.text-muted[
Before implementing a component you write some of the code that actually uses it.<br/>
This way you discover what functions with what parameters you really need, which leads to a very good interface.<br/>
You will also have some good test code for your component.
]
]

{% assign wishful_link = site.data.links | where: "id", 37 | first %}
.footnote[.red[*] See [{{ wishful_link.title }}]({{ wishful_link.link }})]

---

### How an outside-in test might look like

<hr/>

```java
@WebMvcTest(RoomsController.class)
@ActiveProfiles("integration-test")
class RoomsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetRoomsUseCaseImpl getRoomsUseCase;

    @Test
    @WithMockUser(authorities = {"rooms:read"})
    void shouldGetAllRoomsWhenHavingPermissions() throws Exception {
        // arrange
        final Room room = Room.builder().name("Some name").build();
        when(getRoomsUseCase.execute()).thenReturn(Collections.singletonList(room));

        // act
        this.mockMvc.perform(get("/api/v1/rooms"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"name\":\"Some name\"}]"));
    }
}
```

---

### What do we need to compile the code above?

<hr/>

* A controller class called `RoomsController`
    * A method capable to handle an incoming GET request on `/api/v1/rooms`
    * That method should return a 200.OK status on success
    * The response should be in JSON format
    * That method should be secured and allow only certain authorities to call it
    * Inside the controller we should call another class called `GetRoomsUseCaseImpl`

---

### Example controller code

<hr/>

```java
@Controller
@RequestMapping("/api/v1/rooms")
public class RoomsController {

    private final GetRoomsUseCase getRoomsUseCase;

    public RoomsController(final GetRoomsUseCase getRoomsUseCase) {
        this.getRoomsUseCase = getRoomsUseCase;
    }

    @GetMapping
    @ResponseBody
    public List<RoomResponse> getRooms() {
        return getRoomsUseCase.execute().stream().map(RoomToRoomResponse::map).toList();
    }
}
```

---

### The use case minimal implementation

<hr/>

For this layer we write enough code just to make it compile. No logic should be added at this level. Logic will be added using a test-first approach when we get to test this layer.

#### Minimal requirements

* A class called `GetRoomsUseCaseImpl`
    * This class should have a method with signature `public List<Room> execute()`
    * Remember, minimal code just to have our code compile: method signatures and return types
    * The use-case will be fleshed out when we get to test this layer

```java
public final class GetRoomsUseCaseImpl {

    public List<Room> execute() {
        return null;
    }
}
```

---

## Finding Seams in Legacy Code

<hr/>

A **seam** is a place where you can alter behaviour in your programme without editing in that place.

### Types of Seams:

* **Object seams** - Places where you can substitute one object for another
* **Compile seams** - Places where you can swap in different implementations at compile time
* **Link seams** - Places where you can substitute different libraries or modules

---

### Example: Adding a seam

<hr/>

#### Before - tightly coupled, hard to test

```java
public class OrderService {
    public void processOrder(Order order) {
        EmailSender.sendConfirmation(order.getCustomerEmail());
    }
}
```

#### After - with seam, easy to test

```java
public class OrderService {
    private final EmailSender emailSender;

    public OrderService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void processOrder(Order order) {
        emailSender.sendConfirmation(order.getCustomerEmail());
    }
}
```

---

## Working with legacy code

<hr/>

### Example of a service

```java
@Component
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private MessagePublisher messagePublisher;

    public void createHotel(final Hotel hotel) {
        var entity = HotelToHotelEntity.map(hotel);
        hotelRepository.save(entity);
        messagePublisher.publish(hotel);
    }
}
```

---

## Example test

<hr/>

```java
@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @MockBean
    private HotelRepository hotelRepository;
    @MockBean
    private MessagePublisher messagePublisher;
    @InjectMocks
    private HotelService sut;

    @Test
    void shouldPersistAndPublish() {
        // arrange

        // act
        final Hotel hotel = HotelMother.aHotel();
        sut.createHotel(hotel);

        // assert
        verify(hotelRepository).save(any(HotelEntity.class));
        verify(messagePublisher).publish(hotel);
    }
}
```

---

## Test Data Builders vs Object Mothers

<hr/>

### Test Data Builder Pattern

```java
public class HotelBuilder {
    private String name = "Default Hotel";
    private String address = "Default Address";
    private int stars = 3;

    public HotelBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public HotelBuilder withStars(int stars) {
        this.stars = stars;
        return this;
    }

    public Hotel build() {
        return new Hotel(name, address, stars);
    }
}
```

Usage: `Hotel hotel = new HotelBuilder().withName("Luxury Hotel").withStars(5).build();`

---

## Object Mothers

<hr/>

Martin Fowler on Object mothers:

_An object mother is a kind of class used in testing to help create example objects that you use for testing._

{% assign object_mother_link = site.data.links | where: "id", 38 | first %}
From [{{ object_mother_link.title }}]({{ object_mother_link.link }})

This type of object factories are very useful when you need to generate objects for test.

{% assign easy_random_link = site.data.links | where: "id", 39 | first %}
A library that helps with creating objects populated with random data based on field type is [{{ easy_random_link.title }}]({{ easy_random_link.link }})

---

### Object Mother Example

<hr/>

```java
public class HotelMother {
    public static Hotel aHotel() {
        return Hotel.builder()
                .name("Grand Hotel")
                .address("123 Main Street")
                .stars(4)
                .build();
    }

    public static Hotel aLuxuryHotel() {
        return aHotel().toBuilder()
                .name("Luxury Resort")
                .stars(5)
                .build();
    }

    public static Hotel aBudgetHotel() {
        return aHotel().toBuilder()
                .name("Budget Inn")
                .stars(2)
                .build();
    }
}
```

---

## Test Naming Conventions

<hr/>

### Good test names tell a story

```java
@Test
void test1() { ... }

@Test
void testCreateHotel() { ... }
```

#### Better naming

```java
@Test
void shouldReturnEmptyListWhenNoHotelsExist() { ... }

@Test
void shouldThrowExceptionWhenHotelNameIsNull() { ... }

@Test
void shouldPublishMessageWhenHotelIsCreatedSuccessfully() { ... }
```

**Pattern:** `should[ExpectedBehaviour]When[StateUnderTest]`

This makes tests self-documenting and easier to understand when they fail.

---

## Practical Refactoring Techniques

<hr/>

### Replace `new` with constructor injection:

```java
// Before
public class EmailService {
    public void sendWelcome(User user) {
        SmtpClient client = new SmtpClient(); // Hard to test
        client.send(user.getEmail(), "Welcome!");
    }
}

// After
public class EmailService {
    private final SmtpClient smtpClient;

    public EmailService(SmtpClient smtpClient) {
        this.smtpClient = smtpClient;
    }

    public void sendWelcome(User user) {
        smtpClient.send(user.getEmail(), "Welcome!");
    }
}
```

---

## Lessons learned

<hr/>

We are the creators of our code and we are finally responsible to make our life easier and of others following us in working on our code.

We are the ones in charge of writing simpler code that's easier to test, to refactor, to understand and to read.
