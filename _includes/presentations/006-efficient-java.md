
# Better Java

<hr/>

#### Writing better code in Java

By better code I mean a few things:

* Immutability
* Value objects
* Builder pattern
* How and when to use Optional
* Simpler code

---

## Why this topic?

<hr/>

* We need to remember basics before doing advanced
* Focus too much on the big picture
* Simpler, safer code starts with basic stuff

---

# Immutable Java - why?

<hr/>

Why do we want immutable objects?

* Avoid side effects on long-lived objects (invisible state changes)
* Shorter object life span means fewer places where state can change
* Guaranteed thread safety (we don't know how our objects are being used by other classes)
* The flow of data is simplified (no public methods to change internal state after object creation)
* Functional programming relies on many mathematical concepts, and one of them is that operands don't change values

`2 + 3 = 5 -> 5 is a new value, 2 and 3 are still the same`

* Easier to debug and trace changes

---

# Immutable Java - how?

<hr/>

We can express immutability through a few methods:

* Immutable objects
* Final classes and final variables
* Immutable collections (still an object)
* Records

To have truly immutable code, we need to use all of above, together.

Any non-immutable or non-modifiable object will make our life harder.

Java started with a few immutable classes, like `String` and started adding more in newer JDK releases: `BigInteger` etc.

---

# Immutable Objects

<hr/>

Easy to accomplish following a few simple rules:

* Only private fields on objects
* No setters on objects
* No void methods with side effects (changing value for a field)
* Field instantiation only through constructors
* No use of reflection to change object fields
* Use defensive copies

---

# Why immutable objects

<hr/>

* No unwanted side-effects from changing values on the fly
* Promoting functional programming concepts in OOP

`Example`

```java
public final class Hotel {

    private final String name;
    private final Address address;

    public Hotel(final String name, final Address address) {
        this.name = name;
        this.address = address;
    }

    public String getName() { return this.name; }

    public Address getAddress() { return this.address; }
}
```

---

# Immutable objects and resources

<hr/>

* Java is renowned for verbosity and not loved by many for the same reason

* Impossible to change an `immutable object` (**reflection** not considered here), therefore we create a new one

{% assign lombok_link = site.data.links | where: "id", 40 | first %}
* [{{ lombok_link.title }}]({{ lombok_link.link }}) to the rescue with `@Builder` annotation

* Creating new objects is cheap in resource consumption (for our use case)

* The Garbage Collector (GC) works great to cleanup short lived objects (they don't leave eden space)

* Very little extra resource utilization for having more objects

* Large and very large objects should be carefully designed

* Code structures generating many objects (loops, recursion etc.) can use many resources

---

# Using Lombok's builder

<hr/>

`Example`

```java
// imports omitted for brevity

@Builder(toBuilder=true)
public class Hotel {

    // The content of the class remains the same as before

}
// How to use it
{
    var myHotel = Hotel.builder()
        .name("George's fancy hotel")
        .build();

    // I sold my hotel
    var notMyHotel = myHotel.toBuilder()
        .name("Jim's hotel")
        .build();
}
```

---

# Using an immutable object

<hr/>

Changing internal state of an immutable object is impossible

Assuming that we need to change a hotel's address, we need to create a new `Hotel` object with the new address and all other fields of the existing one that we are updating

```java
private final Hotel awesomeHotel =
    new Hotel("My Awesome hotel", Address.defaultAddress());

public Hotel moveAddress(final Hotel hotel, final Address address) {
    return hotel.toBuilder()
                    .address(address)
                    .build();
}

private final Address newAddress =
    new Address("ugly city", "ugly street");

private final Hotel uglyHotel = moveAddress(awesomeHotel, newAddress);
```

---

# Pitfalls

<hr/>

One might say that object Hotel is not completely immutable

In honesty, that's true if we assume that object Address is mutable

Let's define our `Address` class first

```java
public final class Address {
    private final String city;
    private final String street;

    public Address(final String city, final String street) {
        this.city = city;
        this.street = street;
    }

    public String getCity() { return this.city; }
    public String getStreet() { return this.street; }
    // This setter makes the class mutable
    public void setCity(final String city) { this.city = city; }

    public static Address defaultAddress() {
        return new Address("awesome city", "awesome street");
    }
}
```

---

# Immutable or not?

<hr/>

Consider this code

```java
private final Hotel awesomeHotel =
    new Hotel("My Awesome hotel", Address.defaultAddress());

private final Address originalAddress = awesomeHotel.getAddress();
originalAddress.setCity("Ugly city");
```

Even if we didn't change anything directly on the `hotel` object, we could change it indirectly by changing one of the values held.

This behaviour can be fixed if we make all objects immutable.

To fix our example, we need to remove all setters from class `Address`.

Think about searching for all places that are referencing a variable to understand where it was changed

---

# Final classes

<hr />

A class that's declared as `final` has an important message to send: `not open for inheritance`.

In previous examples all classes are marked with `final`.

By default, classes should be final (Kotlin got the point).

When we intend to let classes open for inheritance, we should document it. {% assign effective_java_link = site.data.links | where: "id", 41 | first %}
Joshua Bloch explains this term very well in his excellent book [{{ effective_java_link.title }}]({{ effective_java_link.link }}), item 19.

```java
/*
* This class declaration will throw a compilation error as class "Address"
* is declared as final and other classes cannot inherit from it
*/
public class AddressExtension extends Address {
    // content omitted for brevity
}
```

---

# Final variables

<hr />

A variable that's declared as `final` can be instantiated only once:

* In a constructor if this is a field in a class
* At the time of declaration if this is a field in a class or a variable
* Fields declared as `final` and instantiated in constructor cannot be changed through setters (!)

A variable that has been declared as `final` and has been instantiated **cannot** have this reference changed.

The content of an instance that's referenced by a `final` variable **can** change.

```java
private final Hotel boringHotel =
    new Hotel("My Boring hotel", Address.defaultAddress());

/*
* When trying to make a boring hotel awesome, will throw a compilation error as variable
* "boringHotel" cannot be re-assigned
*/
boringHotel = new Hotel("My Awesome hotel", Address.defaultAddress());
```

---

# Unmodifiable collections

<hr />

* For a long time, Java had (and still has) mutable collections
* Recently there was a shift to introduce _immutable_ collections and then they were renamed to be _unmodifiable_
* Unmodifiable collections cannot have their elements removed, added or replaced
* Elements of unmodifiable collections can have their internal state changed

```java
List<String> someList = Collections.unmodifiableList(List.of("Mom"));

// this operation will throw an "UnsupportedOperationException"
someList.set(0, "Dad");

// this operation is allowed
someList = Collections.unmodifiableList(List.of("Dad"));
```

---

# Reference an unmodifiable collection

<hr />

* A *final* reference to an unmodifiable collection cannot be changed
* An unmodifiable collection cannot be modified
* Objects inside an unmodifiable collection can be changed, if they are not immutable

```java
// elements of an unmodifiable collection cannot be changed
public static String someMethod() {

    final List<String> someList = Collections.unmodifiableList(List.of("Mom"));

    /*
    * this operation will throw an error:
    * cannot assign a value to final variable someList
    */
    someList = Collections.unmodifiableList(List.of("Dad"));

    return someList.get(0);
}
```

---

# Java Records

<hr />

Java Records made it to Java after a long time and they are supposed to reduce a lot of the boilerplate code.

A Java Record provides:

* `getters`
* `toString()`
* `hashCode()` and `equals()`
* A public constructor with all fields

{% assign lombok_link = site.data.links | where: "id", 40 | first %}
{% assign kotlin_link = site.data.links | where: "id", 46 | first %}
Some might argue that this functionality can be accomplished by using [{{ lombok_link.title }}]({{ lombok_link.link }}), or, on a more drastic note, by switching to [{{ kotlin_link.title }}]({{ kotlin_link.link }}). I would agree that Java needs to evolve, and this is a sign that things are getting better.

Let's focus on the advantages brought for immutability: no `setters` on an object

```java
public record Hotel(String name, Address address) {}
```

How simple is that?

---

# What is a Record under the hoods?

<hr />

For those interested in what the compiler does on a Record, run the following command in your terminal:

```sh
echo "public record Address(String city, String street){}" >> Address.java && javac Address.java && javap -c Address.class
```

In your terminal you should see compiler's magic:

* a final class that extends `java.lang.Record`
* getter methods
* `toString()`, `equals()` and `hashCode()`
* a public constructor with two String arguments

---

## Combining Records with the Builder pattern

{% assign builder_pattern_link = site.data.links | where: "id", 47 | first %}
{% assign lombok_link = site.data.links | where: "id", 40 | first %}
When using immutable objects it becomes pretty hard to change a field and to create a new object with the rest of the fields having the same values. The [{{ builder_pattern_link.title }}]({{ builder_pattern_link.link }}) can be used by using [{{ lombok_link.title }}]({{ lombok_link.link }})'s annotation `@Builder(toBuilder=true)`.

```java
public Record Hotel(String name, Address address) {

    @Builder(toBuilder=true)
    public Hotel() {
        // constructor required for @Builder annotation
    }
}

// usage

var myHotel = new Hotel("Awesome Hotel", new Address());
var renamedHotel = myHotel.toBuilder.name("Marvelous Hotel").build();
```

---

# Defensive copies

<hr/>

* Do not provide direct access to mutable fields via accessors
* Use defensive copies in field instantiation: constructors

### Example: Defensive Copying

```java
public final class Hotel {
    private final String name;
    private final List<Room> rooms;

    public Hotel(String name, List<Room> rooms) {
        this.name = name;
        // Defensive copy to prevent external modification
        this.rooms = Collections.unmodifiableList(new ArrayList<>(rooms));
    }

    public List<Room> getRooms() {
        // Return defensive copy to prevent external modification
        return new ArrayList<>(rooms);
    }
}
```

**Why defensive copies?**
* Prevents external code from modifying internal state
* Maintains true immutability even with mutable field types
* Essential when dealing with collections or mutable objects

---

## The Builder Pattern

<hr/>

* Used when dealing with many constructor arguments
* Used when not using value objects and dealing with many primitives

```java
@AllArgsConstructor
public class Address {
    private final String firstLine;
    private final String secondLine;
    private final String street;
    private final String city;
}

var address = new Address("first line", "second line", "London Rd", "Colchester");
var anotherAddress = new Address("first line", "second line", "Colchester", "London Rd");
```

* What's the problem here? What about an alternative?

```java
var address = Address.builder()
                        .firstLine("first line")
                        .secondLine("second line")
                        .street("London Rd")
                        .city("Colchester")
                        .build();
```

---

## An example of a builder

<hr/>

```java
public final class Hotel {
    private final String name;
    private Hotel(final String name) {
        this.name = name;
    }
    public static Builder builder() {
        return new Builder();
    }
    public String getName() {
        return name;
    }
    public static class Builder {
        private String name;
        public Builder name(final String name) {
            this.name = name;
            return this;
        }
        public Hotel build() {
            return new Hotel(name);
        }
    }
}
```

---

## An example using Lombok

<hr/>

```java
// as a class
@Builder(toBuilder=true)
public class Hotel {
    private final String name;
    // The content of the class omitted for brevity
}

// as a record
@Builder(toBuilder=true)
public record Hotel(String name) {
    // Note: Records with @Builder require careful setup
}
```

---

## When NOT to Use Immutability

<hr/>

### Performance-Critical Scenarios

```java
// Inefficient for frequent modifications
StringBuilder result = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    String temp = result.toString(); // Creates new String
    result = new StringBuilder(temp + i); // Inefficient
}

// Use mutable types for accumulation
StringBuilder result = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    result.append(i); // Modifies existing buffer
}
```

### When Working with Large Data Sets

* Copying large objects can be expensive
* Consider using immutable wrappers around mutable internals
* Use lazy evaluation patterns when appropriate

---

## Best Practices Summary

<hr/>

### Do This

* Make classes `final` by default
* Use `final` for all fields
* Provide no setters
* Use defensive copying for mutable fields
* Prefer composition over inheritance
* Use builder pattern for complex objects

### Avoid This

* Public mutable fields
* Setters on immutable objects
* Sharing mutable references
* Deep object hierarchies
* Premature optimisation concerns

---

## Modern Java Features for Immutability

<hr/>

### Text Blocks (Java 15+)

```java
public record ErrorMessage(String code, String details) {
    public String toJson() {
        return """
            {
                "errorCode": "%s",
                "details": "%s"
            }
            """.formatted(code, details);
    }
}
```

### Pattern Matching (Java 17+)

```java
public sealed interface Result permits Success, Failure {
    record Success(String value) implements Result {}
    record Failure(String error) implements Result {}
}

public String processResult(Result result) {
    return switch (result) {
        case Success(var value) -> "Success: " + value;
        case Failure(var error) -> "Error: " + error;
    };
}
```

---

# References

<hr/>

{% assign effective_java_link = site.data.links | where: "id", 41 | first %}
{% assign concurrency_link = site.data.links | where: "id", 42 | first %}
{% assign fp_dev_link = site.data.links | where: "id", 43 | first %}
{% assign fp_java_link = site.data.links | where: "id", 44 | first %}
{% assign design_patterns_link = site.data.links | where: "id", 45 | first %}

* [{{ effective_java_link.title }}]({{ effective_java_link.link }})
* [{{ concurrency_link.title }}]({{ concurrency_link.link }})
* [{{ fp_dev_link.title }}]({{ fp_dev_link.link }})
* [{{ fp_java_link.title }}]({{ fp_java_link.link }})
* [{{ design_patterns_link.title }}]({{ design_patterns_link.link }})

---

## Questions?

<hr/>

### Thank You :-)
