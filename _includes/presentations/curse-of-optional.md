
# The curse of Optional

<hr/>

* What is Java's Optional?
* When to use it
* How to use it
* Anti-patterns

---

## Java's `Optional`

<hr/>

* Java's `Optional` made it to prime-time in Java 1.8
* It is _a container object which may or may not contain a non-null value_
* Other languages have a similar concept
    * `Maybe` -> see [Haskell](https://wiki.haskell.org/Maybe)
    * `Option` -> see [Scala](https://www.scala-lang.org/api/2.13.6/scala/Option.html)
* This is a [value-based class](https://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html)
* Has some helper methods to _look_ inside the container
    * `isPresent()` - returns if the value inside is not null
    * `get()` - returns the non-null value
* Some more helper methods for fluid coding and method chaining
    * `orElse(T other)` - get the value or `other` when value is missing
    * `orElseGet(Supplier<? extends T> other)`
    * `orElseThrow(Supplier<? extends X> exceptionSupplier)`
    * `map(Function<? super T,? extends U> mapper)`
    * `ifPresent(Consumer<? super T> consumer)`

---

## Java's Design Decisions around Optional

<hr/>

* A variable can represent two states simultaneously: has a _value_ or _empty_
* `null` is the source of evil: throws `NullPointerException` when not handled
* `Optional` actually says that this variable may or may not contain a value
* `null` values can mean several things:
    * This is default value for object types?
    * This is an expected value (see `Map.get()`)?
    * This is a valid value for a variable?
    * The method returning this value doesn't behave properly?
    * Imagine trying to answer these questions when debugging a NPE
* `Optional` types force the consumer to handle both cases: when there's a value and when the value is missing. There's no excuse for not doing so

---

## Why Java's `Optional`

<hr/>

* Usually, to handle `null` values in Java, one would do 

```java
public static boolean isExpectedValue(String other) {
    // I don't care about the value of `other`, it will never throw a NPE
    return EXPECTED_VALUE.equals(other);
}

@ParameterizedTest(name = "Should return {1} when value is \"{0}\"")
@CsvSource({",'false'","'','false'","'boo','false'","'blah','true'"})
void shouldMatchExpectedValue(String otherValue, boolean expected) {
    assertThat(App.isExpectedValue(otherValue)).isEqualTo(expected);
}

// will output: 'Should return false when value is "null"'
```

* Primitive types cannot be `null`
* Methods returning object types should always return an instance or `null`
* We need to check if the returned instance is `null` or not
* We cannot chain methods with null checks and have fluent code
* Basically allows writing code without the <s>loved</s> hated null check

---

## When to use Optional

<hr/>

* To return an object that might contain a value or not

```java
@Override
public Room execute(final Room request) {
    return RoomEntityToRoom.map(roomsRepository.save(RoomToRoomEntity.map(request)));
}
```
* To allow the caller of that function to continue a series of fluent calls

```java
@PostMapping
@ResponseBody
public ResponseEntity<RoomResponse> createRoom(@Validated RoomRequest request) {
    return Optional.ofNullable(request)
            .map(RoomRequestToRoom::map)
            .map(createRoomUseCase::execute)
            .map(RoomToRoomResponse::map)
            .map(response -> new ResponseEntity<>(response, HttpStatus.CREATED))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
}
```

---

## Another usage example

<hr/>

```java
public static String mayNotReturnAString() {
    final Random random = new Random();
    return random.nextInt() % 2 == 0 ? "Blah" : null;
}

public static Optional<String> maybeString() {
    final Random random = new Random();
    return random.nextInt() % 2 == 1 ? Optional.of("Boo") : Optional.empty();
}

public static void handleStrings() {
    if (null != mayNotReturnAString()) {
        System.out.printf("No Optional value is not null: %s%n", needsNullCheck);
    } else {
        System.out.println("No Optional value is null");
    }

    maybeString().ifPresentOrElse(
            s -> System.out.printf("Optional value is not null: %s%n", s),
            () -> System.out.println("Optional value is null"));
}
```

* Which usage is cleaner?

---

## When not to use Optional

### Method overloading

<hr/>

* When a parameter might be missing from a method: use method overloading

```java

```

---

## When not to use Optional

### When creating many objects in a short amount of time

<hr/>