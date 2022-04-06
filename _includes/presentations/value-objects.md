
# Value Objects in Java

<hr />

Value objects are useful concepts to avoid using primitives
to represent domain objects.

They are usually found around small concepts like bank accounts,
geographical coordinates, ranges, points, phone numbers etc.

---

# Strings are good

<hr />

Java has a few primitive types that are used everywhere to hold values.

While holding value is a good thing, having meaningful domain concepts around these values is not that easy.

Take for example a vehicle Vehicle Identification Number [VIN](https://en.wikipedia.org/wiki/Vehicle_identification_number).

Consider the following simplistic example to add validation at construction time

```java
public record Vehicle(String vin) {
    public Vehicle(final String vin) {
        // not quite my responsibility to validate a VIN
        if (VinValidator.isValid(vin)) {
            this.vin = vin;
        } else {
            throw new InvalidVINException("VIN length should be 17 characters");
        }
    }
}
```

---

# VIN Validator

<hr />

Our simplistic VIN validator

```java
public class VinValidator {
    private VinValidator() {
        // nothing to see here
    }

    public static boolean isValid(final String vin) {
        return vin.length() == 17;
    }
}
```

Can you see any issues there?

One might say that we can put VIN validation inside the Vehicle class and we keep data and behavior together.

---

# Evolving our Vehicle and VIN

<hr />

Consider the following:

* The vehicle will get more fields
* VIN validation will to add more conditions
* VIN follows ISO 3779 standard and its components have meaning
* What if we want to make VIN object "smarter"? Eg. tell us country, manufacturer etc.

Looking at the conditions above, a String can still hold the value, but who will hold the behavior?

This is a good point to start introducing Value Objects

---

# VIN as a Value Object

<hr />

Consider this code snippet

```java
public record VehicleIdentificationNumber(String value) {
    public VehicleIdentificationNumber(final String value) {
        validateLengthOrThrow(value);
        this.value = value;
    }

    private void validateLengthOrThrow(final String input) {
        if (input != null && input.length() != 17) {
            throw new InvalidVINException("VIN length should be 17 characters");
        }
    }
}

public record Vehicle(VehicleIdentificationNumber vin) {}
public record ServiceRecord(VehicleIdentificationNumber vin, UUID customerId) {}
```

* We added validation in the same place with the data that's validating
* We are consistent in throwing the same error at initialization time
* We encapsulated properties related to a VIN inside that object

---

# Evolving VIN

<hr />

Given that we respect a standard in our VIN record, let's get the WMI (World Manufacturer Identifier) out of any VIN

```java
public record VehicleIdentificationNumber(String value) {
    // previous code omitted for brevity

    public String getWorldManufacturerIdentifier() {
        return value().substring(0,3);
    }
}
```

Because we validated when we built the record we know that we will always have a value with a non-null value, with 17 characters.

---

# References

< hr/>

[Martin Fowler](https://www.martinfowler.com/bliki/ValueObject.html) dedicated an entire post for Value Objects, mostly talking about them being implemented in JavaScript.
