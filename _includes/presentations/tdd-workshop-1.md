
# TDD

<hr/>

#### Test Driven Development Workshop

---

# Agenda

<hr/>

1. Setup
2. What is Test Driven Development?
3. TDD step by step with examples
4. Exercise
5. TDD with constraints
6. TDD on legacy code

---

## What is Test Driven Development?

<hr/>

Test Driven Development is a software development approach.
It involves an iterative approach to write clean, maintainable and robust software by writing the tests first.
Each iteration involves a set of practices.

</br>

### Write a failing test first and then the minimum code required to fix it.

---

## The TDD Cycle

<hr/>

Each iteration of TDD should follow these steps:

1. Red: write a failing test
2. Green: write code that makes the test pass (nothing more)
3. Refactor: refactor your code while the test(s) are green
4. Go to 1.

---

.center[

## The TDD Cycle

<hr/>

![TDD Cycle](/assets/img/red-green-refactor.png)
]

---

# TDD vs CDT

<hr/>

CDT - Code Driven Tests

* Why should I add tests if my code is working in production?
* When should I stop adding tests?
* Do we have enough test coverage?

TDD - Test Driven Development

* Stop writing code when your test passes
* Code coverage is there if you didn't write extra code
* Write a failing test to reproduce a bug

---

## Remember

<hr/>

### _Never write new functionality without writing a failing test first_

---

# TDD is not about test types

<hr/>

* New features cannot be expressed only by unit tests
* The TDD Cycle needs to be applied from a higher level
* Start with a failing Acceptance Test and then drill down into unit tests
* Keep writing failing unit tests, code that makes them green and refactor locally
* Once the Acceptance Test is green, then refactor at a higher level

---

## Properties of our tests

<hr/>

* Automated - run and result check automatically
* Thorough - test what's likely to break, use coverage tools to keep a consistent coverage
* Repeatable - should be independent from the environment and  produce the same result
* Independent - independent from the environment and from each other
* Professional - test code should be written and maintained at the same standards as production code

---

# TDD style used for practice

<hr/>

Mockist style:  makes use of mocks for dependencies

Outside-in: start from the outside and TDD your way in, mocking on the way down

Aka London style TDD

---

# Development requirements

<hr/>

* Java 17
* git
* Maven
* An active Internet connection
* A text editor or IDE (IntelliJ IDEA Community Edition, VS Code etc.)

---

## Practice

<hr/>

### Problem to solve

Compute the balance of an account:

* The account has a balance that starts at zero.
* The account supports deposit and withdrawal operations.
* The balance is updated when deposits or withdrawals occur.
* If withdrawals exceed the current balance, it should handle overdrafts or throw an exception.

---

## Write your first failing test

<hr/>

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {

    @Test
    void testInitialBalanceIsZero() {
        Account account = new Account();
        assertEquals(0, account.getBalance());
    }
}
```

Run the test. It will fail because the Account class and getBalance method don't exist yet.

---

# Exercise

<hr/>

#### Scenario: A client of our system creates a hotel

.text-muted[
Given a client for the system </br>
When I create a new hotel </br>
And the name of the hotel is "BooBoo" </br>
Then I should get back an object with the hotel created.
]

Clone this repo [shameless plug](https://github.com/georgeracu/spring-boot-demo-app) </br>
Checkout branch ```tdd-exercise-3```

---

# Constraints

<hr/>

### Mob programming

</br>

#### Designated driver

.fst-italic[
.text-muted[
Have a designated driver for the whole session. The driver is not allowed to drive on its own, just follow the instructions of the navigators. This is definitely not a fair distribution between the roles, but an experienced coder who knows its way through the IDE can really enable a small team to do some rapid prototyping. (And maybe the driver may ask a question from time to time).
]
]

Inspired from [kata-log](https://kata-log.rocks/mob-programming)
