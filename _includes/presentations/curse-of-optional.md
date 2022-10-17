
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

* Suddenly a _null_ value becomes _empty_
    * When using something like `Optional.empty()`
    * This is an argument for semantics, some might say
    * `null` is the source of evil: throws `NullPointerException` when not handled

---

## Why Java's `Optional`

<hr/>

* Usually to handle `null` values in Java, one would do
