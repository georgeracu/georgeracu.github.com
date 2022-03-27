
# TDD

## Test Driven Development Workshop

---

# Agenda

1. Setup
2. What is Test Driven Development?
3. TDD with constraints
4. TDD on legacy code

---

# Setup your development requirements

* Java 17
* git
* Maven
* An active Internet connection
* A text editor or IDE (IntelliJ IDEA Community Edition, VS Code etc.)

---

# What is Test Driven Development (TDD)?

## Introduction to TDD

---

# Test Driven Development (TDD)

Also known as _Test-First Programming_ in Extreme Programming (XP)

Advantages:

* Write self-testing code
* Think about the interface to the code first

---

# The TDD Cycle

* Red: write a failing test
* Green: write code that makes the test pass (nothing more)
* Refactor: refactor your code while the test(s) are green
* Repeat

---

.center[
# The TDD Cycle
.img-450h[
![TDD Cycle](/assets/img/red-green-refactor.png)
]
]

---

# TDD vs CDT

CDT - Code Driven Tests

.smaller[
* Why should I add tests if my code is working in production?
* When should I stop adding tests?
* Do we have enough test coverage?
]

TDD - Test Driven Development

.smaller[
* Stop writing code when your test passes
* Code coverage is there if you didn't write extra code
* Write a failing test to reproduce a bug
]

---

# Exercise

.smaller[
    _"Create a new API endpoint that will serve GET requests to tell the time by responding with a JSON object that contains a field called `timestamp` with the value at the time of the call"_
]