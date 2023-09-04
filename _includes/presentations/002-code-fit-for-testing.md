
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

None of this means that we should be getting rid of testers. Testers serve an essential role in the software delivery lifecycle, performing manual testing such as exploratory, usability, and acceptance testing, and helping to create and evolve suites of automated tests by working alongside developers.
]

[Accelerate](https://amzn.to/3XPnyK0) - on high performing teams.

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
    * Use the [construction builder](https://martinfowler.com/dslCatalog/constructionBuilder.html) pattern for a more fluent syntax
    * Think in small increments and small units (small methods and classes)

---

## First Example

<hr/>

### First user story

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

### Apply [wishful thinking](https://wiki.c2.com/?WishfulThinking)

.fst-italic[
.text-muted[
Before implementing a component you write some of the code that actually uses it. This way you discover what functions with what parameters you really need, which leads to a very good interface. You will also have some good test code for your component. 
]
]

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
        sut.createHotel()

        // assert
    }
}
```

---

## Object Mothers

<hr/>

Martin Fowler on Object mothers:

_An object mother is a kind of class used in testing to help create example objects that you use for testing._

From [martinfowler.com](https://martinfowler.com/bliki/ObjectMother.html)

This type of object factories are very useful when you need to generate objects for test. 

A library that helps with creating objects populated with random data based on field type is [EasyRandom](https://github.com/j-easy/easy-random)
---

## Lessons learned

<hr/>

We are the creators of our code and we are finally responsible to make our life easier and of others following us in working on our code.

We are the ones in charge of writing simpler code that's easier to test, to refactor, to understand and to read.
