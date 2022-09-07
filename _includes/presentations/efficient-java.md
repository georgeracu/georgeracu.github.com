
# Efficient Java

<hr/>

#### Writing better code in Java

By better code I mean a few things:

* Immutability
* Value objects
* Builder pattern

---

# Immutable Java - why?

<hr/>

Why do we want immutable objects?

* Avoid side effects on long-lived objects (invisible state changes)
* Shorter object life span means fewer places where state can change
* Guaranteed thread safety (we don't know how our objects are being used by other classes)
* The flow of data is simplified (no public methods to change internal state after object creation)
* Functional programming relies on many mathematical concepts, and one of them is that operands don't change value
* Easier to debug and trace changes

---

# Immutable Java - how?

<hr/>

We can express immutability through a few methods:

* Immutable objects
* Final classes and final variables
* Immutable collections (still an object)
* Records

To have truly immutable code, we need to use all above, together.

Any non-immutable or non-modifiable object will make our life harder.

Java started with few immutable classes, like `String` and started adding more in newer JDK releases: `BigInteger` etc.

---

# Immutable Objects

<hr/>

Easy to accomplish following a few simple rules:

* Only private fields on objects
* No setters on objects
* No void methods with side effects (changing value for a field)
* Field instantiation only through constructors
* No use of reflection to change object fields
* Defensive copies

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

Java is renowned for verbosity and not loved by many for the same reason

Impossible to change an `immutable object` (**reflection** not considered here), therefore we create a new one

[Lombok](https://projectlombok.org/) to the rescue with `@Builder` annotation

Creating new objects is cheap in resource consumption (for our use case)

The Garbage Collector (GC) works great to cleanup short lived objects

Very little extra resource utilization for having more objects

Large and very large objects should be carefully designed

Code structures generating many objects (loops, recursion etc.) can use many resources

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

<hr>

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

---

# Final classes

<hr />

A class that's declared as `final` has an important message to send: `not open for inheritance`.

In previous examples all classes are marked with `final`.

By default, classes should be final.

When we intend to let classes open for inheritance, we should document it. Joshua Bloch explains this term very well in his excellent book [Effective Java, 3rd edition](https://www.amazon.co.uk/Effective-Java-Joshua-Bloch-ebook/dp/B078H61SCH/ref=sr_1_1?_encoding=UTF8&keywords=Effective+Java&qid=1649185785&s=digital-text&sr=1-1) in item 19.

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

A variable that has been declared as `final` and has been inialized **cannot** have this reference changed.

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

* A final reference to an unmodifiable collection cannot be changed
* An unmodifiable collection cannot be modified
* Objects inside an unmodifiable collection can be changed, if they are not immutable

```java
// elements of an unmodifiable collection cannot be changed
public static String someMethod() {
    
    final List<String> someList = Collections.unmodifiableList(List.of("Mom"));

    /* 
    * this operation is will throw an error: 
    * cannot assign a value to final variable someList
    */
    someList = Collections.unmodifiableList(List.of("Dad"));

    return someList.get(0);
}
```

---

# Java Records

<hr />

Java Records made it through in Java and they are supposed to reduce a lot of the boilerplate code.

They provide an object with:

* getters
* toString()
* hashCode() and equals()
* A public constructor with all class fields

Some might argue that this functionality can be accomplished by using [Lombok](https://projectlombok.org/), or, on a more drastic note, by switching to [Kotlin](https://kotlinlang.org/). I would agree that Java needs to evolve, and this a sign that things are getting better.

I will focus only on the advantages brought for immutability: no `setters` on an object

```java
public Record Hotel(String name, Address address){}
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

* final class that extends `java.lang.Record`
* getter methods
* toString(), equals() and hashCode()
* a public constructor with two String arguments

---

# Combining Records with Builder pattern

When using immutable objects it becomes pretty hard to change a field and to create a new object with the rest of the fields having the same values. The [Builder pattern](https://en.wikipedia.org/wiki/Builder_pattern) can be used by using [Lombok](https://projectlombok.org/)'s annotation `@Builder(toBuilder=true)`.

```java
public Record Hotel(String name, Address address){
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

_WIP_

---

# References

<hr/>

* [Effective Java, 3rd edition](https://www.amazon.co.uk/Effective-Java-Joshua-Bloch-ebook/dp/B078H61SCH/ref=sr_1_1?_encoding=UTF8&keywords=Effective+Java&qid=1649185785&s=digital-text&sr=1-1)
* [Java Concurrency in Practice](https://jcip.net/)
* [Functional Programming for Java Developers](https://www.oreilly.com/library/view/functional-programming-for/9781449312657/)
* [Functional Programming in Java](https://www.manning.com/books/functional-programming-in-java)
* [Design Patterns: Elements of Reusable Object-Oriented Software](https://www.oreilly.com/library/view/design-patterns-elements/0201633612/)
