
# Efficient Java

<hr/>

#### Writing better code in Java

---

# What is better?

By better code I mean a few things:

* Immutability
* Value objects
* Builder pattern

---

# Immutable Java

<hr/>

We can express immutability through a few methods:

* Immutable objects
* Final classes and final variables
* Immutable collections (still an object)
* Records (Java 17+)

---

# Immutable Objects

<hr/>

Easy to accomplish following a few simple rules:

* Only private fields on objects
* No setters on objects
* No void methods with side effects (changing value for a field)
* Field instantiation only through constructors
* No use of reflection to change private fields

---

# Why immutable objects

<hr/>

* No unwanted side-effects from changing values on the fly
* Promoting functional programming concepts in OOP

`Example`

```java
public class Hotel {

    private final String name;
    private final Address address;

    public Hotel(final String name, final Address address) {
        this.name = name;
        this.hotel = hotel;
    }

    public String getName() { return this.name; }

    public Address getAddress() { return this.address; }
}
```

---

# How to change object `hotel`

<hr/>

Java is renowned for verbosity and not loved by many for the same reason

There is no easy way to change an `immutable object`

[Lombok](https://projectlombok.org/) to the rescue with `@Builder`

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

Assuming that we need to change a hotel's address, we can do the following

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

A class that's final has an important message to send: `not open for extension`.

In previous examples all classes are marked with `final`.

By default, classes should be final.

When we intend to let classes open for inheritance, we should document it. Joshua Bloch explains this term very well in his excellent book [Effective Java, 3rd edition](https://www.amazon.co.uk/Effective-Java-Joshua-Bloch-ebook/dp/B078H61SCH/ref=sr_1_1?_encoding=UTF8&keywords=Effective+Java&qid=1649185785&s=digital-text&sr=1-1) in item 19.
