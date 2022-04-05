
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
