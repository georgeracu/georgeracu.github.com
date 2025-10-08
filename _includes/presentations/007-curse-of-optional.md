
# The curse of Optional

<hr/>

* What is Java's Optional?
* When to use it
* How to use it
* Anti-patterns

---

## Java's `Optional`

<hr/>

{% assign haskell_link = site.data.links | where: "id", 48 | first %}
{% assign scala_link = site.data.links | where: "id", 49 | first %}
{% assign value_based_link = site.data.links | where: "id", 50 | first %}

- Java's `Optional` made it to prime-time in Java 1.8
- It is _a container object which may or may not contain a non-null value_
- Other languages have a similar concept
    - `Maybe` -> see [{{ haskell_link.title }}]({{ haskell_link.link }})
    - `Option` -> see [{{ scala_link.title }}]({{ scala_link.link }})
- This is a [{{ value_based_link.title }}]({{ value_based_link.link }})
- Has some helper methods to _look_ inside the container
    - `isPresent()` - returns if the value inside is not null
    - `get()` - returns the non-null value
- Some more helper methods for fluid coding and method chaining
    - `orElse(T other)` - get the value or `other` when value is missing
    - `orElseGet(Supplier<? extends T> other)`
    - `orElseThrow(Supplier<? extends X> exceptionSupplier)`
    - `map(Function<? super T,? extends U> mapper)`
    - `ifPresent(Consumer<? super T> consumer)`

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
    String needsNullCheck = mayNotReturnAString();
    if (null != needsNullCheck) {
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
// Using Optional as parameter
public void processUser(Optional<String> name) {
    // Forces caller to wrap values in Optional
}

// Method overloading
public void processUser(String name) {
    // Implementation with name
}

public void processUser() {
    // Implementation without name (default behavior)
}
```

---

## When not to use Optional

<hr/>

### Performance-Critical Scenarios

```java
// Creating many Optional objects in loops
public List<String> processItems(List<String> items) {
    return items.stream()
        .map(item -> Optional.ofNullable(item)
            .map(String::toUpperCase)
            .orElse("DEFAULT"))
        .collect(Collectors.toList());
}

// Direct null checks for performance-critical code
public List<String> processItems(List<String> items) {
    return items.stream()
        .map(item -> item != null ? item.toUpperCase() : "DEFAULT")
        .collect(Collectors.toList());
}
```

---

## When not to use Optional

<hr/>

### As class fields

```java
// Optional as instance fields
public class User {
    private Optional<String> middleName = Optional.empty();

    public Optional<String> getMiddleName() {
        return middleName;
    }
}

// Use null for absent fields
public class User {
    private String middleName; // Can be null

    public Optional<String> getMiddleName() {
        return Optional.ofNullable(middleName);
    }
}
```

---

## Common Anti-Patterns

<hr/>

### Anti-Pattern 1: Using get() without checking

```java
// Can throw NoSuchElementException
public String processValue(Optional<String> value) {
    return value.get().toUpperCase(); // Can throw!
}

// Always check or provide alternative
public String processValue(Optional<String> value) {
    return value.map(String::toUpperCase).orElse("UNKNOWN");
}
```

### Anti-Pattern 2: isPresent() + get()

```java
// Verbose: Defeats the purpose of Optional
if (optional.isPresent()) {
    doSomething(optional.get());
}

// Concise: Use ifPresent()
optional.ifPresent(this::doSomething);
```

---

## Optional Best Practices

<hr/>

### 1. Use Optional for return types only

```java
// Return Optional from methods that might not find a result
public Optional<User> findUserById(Long id) {
    return userRepository.findById(id);
}

// Don't use Optional for parameters
public void updateUser(Optional<User> user) { ... }
```

### 2. Prefer orElseThrow() for required values

```java
// Clear intent: This value must exist
User user = findUserById(id)
    .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
```

### 3. Chain operations fluently

```java
// Elegant: Chain transformations
public Optional<String> getUserDisplayName(Long userId) {
    return findUserById(userId)
        .map(User::getProfile)
        .map(Profile::getDisplayName)
        .filter(name -> !name.isBlank());
}
```

---

## Advanced Optional Patterns

<hr/>

### Combining Optionals

```java
public Optional<Address> getFullAddress(Long userId) {
    Optional<User> user = findUserById(userId);
    Optional<Profile> profile = user.map(User::getProfile);

    return user.flatMap(u -> profile.map(p ->
        new Address(u.getCity(), p.getStreet())));
}
```

### Optional with Streams

```java
public List<String> getActiveUserEmails() {
    return users.stream()
        .map(User::getEmail)        // Optional<String>
        .filter(Optional::isPresent) // Filter present values
        .map(Optional::get)         // Extract values
        .collect(Collectors.toList());
}

// Or better with flatMap (Java 9+)
public List<String> getActiveUserEmails() {
    return users.stream()
        .map(User::getEmail)
        .flatMap(Optional::stream)  // Java 9+
        .collect(Collectors.toList());
}
```

---

## Optional vs Null: When to Use What

<hr/>

### Use Optional When:
- Method might legitimately not find a result
- You want to encourage proper null handling
- Building fluent APIs
- Working with streams and functional programming

### Use Null When:
- Performance is critical (tight loops)
- Representing absent fields in data classes
- Working with legacy APIs that expect null
- Simple internal logic where Optional adds no value

---

## Testing with Optional

<hr/>

```java
@Test
void shouldReturnEmptyWhenUserNotFound() {
    Optional<User> result = userService.findByEmail("notfound@example.com");

    assertThat(result).isEmpty();
}

@Test
void shouldReturnUserWhenFound() {
    User expectedUser = new User("john@example.com");
    when(userRepository.findByEmail("john@example.com"))
        .thenReturn(Optional.of(expectedUser));

    Optional<User> result = userService.findByEmail("john@example.com");

    assertThat(result)
        .isPresent()
        .get()
        .extracting(User::getEmail)
        .isEqualTo("john@example.com");
}
```

---

## Performance Considerations

<hr/>

### Memory Overhead
- Optional objects have ~16 bytes overhead
- Consider null for high-frequency, low-level operations
- Measure performance in your specific use case

### Allocation Pressure
```java
// High allocation in hot paths
for (int i = 0; i < 1_000_000; i++) {
    Optional.ofNullable(getValue(i))  // Creates many objects
        .map(this::transform)
        .orElse(defaultValue);
}

// Better for performance-critical code
for (int i = 0; i < 1_000_000; i++) {
    String value = getValue(i);
    result = value != null ? transform(value) : defaultValue;
}
```

---

## Key Takeaways

<hr/>

### Do
- Use Optional for return types that might be empty
- Chain operations with map(), flatMap(), filter()
- Use orElse(), orElseGet(), orElseThrow() appropriately
- Prefer ifPresent() over isPresent() + get()

### Don't
- Optional as method parameters
- Optional as class fields
- Calling get() without checking isPresent()
- Using Optional for performance-critical paths

### Remember
Optional is a tool for **API design**, not a replacement for all null checks!

---

## References

<hr/>

{% assign effective_java_link = site.data.links | where: "id", 41 | first %}
{% assign optional_blog_link = site.data.links | where: "id", 51 | first %}

* [{{ effective_java_link.title }}]({{ effective_java_link.link }}) - Item 55: Return optionals judiciously
* [{{ optional_blog_link.title }}]({{ optional_blog_link.link }})
* Java 8+ Documentation on Optional class

---

## Questions?

<hr/>

### Thank You :-)
