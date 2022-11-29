
# Write code fit for testing

<hr/>

* Start with Test Driven Development
* Refactor existing code to make it easier to test
* Find code seams

---

## Start with TDD

<hr/>

* Starting with a test-first approach will naturally guide your code architecture to be easy to test
* It will avoid constructs like
    * Field injection
    * Creating objects using constructors and setters
* It will _force_ us to:
    * Replace field injection with constructor injection
    * Add `equals()` and `hashCode()` on objects when we need to compare equality
    * Use the [construction builder](https://martinfowler.com/dslCatalog/constructionBuilder.html) pattern for a more fluent syntax

---

## First Example

<hr/>

### First user story

Given a hotel owner
When I query the API for all my hotels
Then I should get a list with hotels

---

## Using an outside-in approach

<hr/>



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

Martin Fowler on Object mothers

---

## EasyRandom

<hr/>

---

## Lesson learned

<hr/>

We are the creators of our code and we are finally responsible to make our life easier and of others following us in working on our code.

We are the ones in charge of writing simpler code that easier to test, to refactor, to understand and to read.
