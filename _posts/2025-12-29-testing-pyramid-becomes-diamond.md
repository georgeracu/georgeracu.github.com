---
layout: post
title: "Testing Microservices: When the Testing Pyramid Becomes a Diamond"
date: 2025-12-29
tags: [post, development, microservices, testing]
description: "When testing microservices and serverless functions, the classic approach of a testing pyramid becomes a diamond due to less business logic unit tests."
permalink: /blog/articles/when-testing-pyramid-becomes-diamond/
mathjax: false
---

## tl;dr

- The testing pyramid (many unit tests, fewer integration tests, minimal e2e) assumes monolithic architecture where business logic concentrates within a single service. This assumption breaks with microservices
- Microservices concentrate business logic at service boundaries, not within individual services. The unit-to-integration test ratio must reflect this: more business logic within a service means more unit tests; leaner services mean fewer
- The shape shifts based on architecture: pyramid for monoliths, balanced square for microservices, inverted diamond for serverless functions
- A defect caught during unit testing costs £100 and 5 minutes to fix. The same defect caught in production costs £2,000+ and weeks of incident response
- Start recognising when your pyramid has changed shape this week, then gradually shift testing investment toward integration tests

---

## The Pyramid Works If Your Architecture Matches Its Assumptions

The testing pyramid is everywhere. It's in books, courses, engineering standards. The rationale is straightforward: write many unit tests (fast and cheap), some integration tests (slower), minimal e2e tests (slow and expensive).

But here's what I've learned transitioning teams to microservices a few times already: the pyramid doesn't hold its shape. It doesn't break, it transforms. And if you try to force pyramid-shaped testing onto a microservices architecture, you'll waste engineering capacity writing unit tests that miss the defects that actually matter.

The pyramid emerged from a specific architectural reality: monolithic applications where most business logic lives in a single codebase, deployed as one unit. In that context, the shape makes perfect sense. The defects hide in the code. Unit tests find them fast and cheaply. The pyramid reflects where the risk actually is.

Microservices move the risk. And the testing shape must follow.

---

## Monoliths: Why the Pyramid Works

A monolith concentrates logic. When you're building a payment processing system, user authentication engine, and inventory management system within the same application, each domain contains substantial business logic.

A developer building the payment module writes functions that calculate payment amounts, handle refund logic etc. These functions contain real complexity: boundary conditions, edge cases, state management. Unit tests catch these issues fast: a miscalculation, an off-by-one error, a missing validation. The defects are in the code.

Integration tests then verify that the payment module talks correctly to the ledger, that the ledger writes accurate data, that the system doesn't double-charge. E2e tests exercise the full flow end-to-end.

The pyramid works because it reflects where defects hide. Most defects live in code logic. Unit tests are the right tool, and they're the most efficient investment.

![Testing Spectrum](/assets/img/posts/testing-pyramid/pyramid.svg)

---

## Microservices: The Boundaries Are Where Logic Lives Now

Microservices decompose the monolith. Instead of one application, you have many small applications, each focused on a narrow domain, each deployable independently.

Here's the critical shift: decomposing the monolith doesn't just redistribute code. It relocates where business logic concentrates.

In a monolith, payment processing logic lives in code. In a microservices system, the same logic is split: the payment service (orchestrating attempts), the billing service (calculating amounts), the ledger service (recording transactions). **Each service contains less logic individually. The business logic now lives at the boundaries and distributed between services.**

**Which defects are internal (caught by unit tests)?**

- Miscalculations in discount logic
- Missing validation on payment amounts
- Off-by-one errors in pagination
- Concurrency issues in state management
- Business rule violations

**Which defects are boundary issues (caught by integration tests)?**

- Payment service doesn't handle timeout from billing service, retries indefinitely, duplicates charges
- Ledger service receives events out of order (misconfigured queue, not platform limitation)
- Billing service returns unexpected error code that payment service doesn't handle
- Race condition between two services writing to the same resource

**The key principle: The testing ratio must reflect the logic distribution. The more business logic within a service, the more unit tests. The leaner the service, the fewer unit tests and more integration tests.**

A microservice that implements complex domain logic should still have substantial unit test coverage. A microservice that's primarily orchestration should emphasise integration tests.

As I've watched teams transition to microservices, the pattern is consistent: they initially write unit tests at the same ratio they did in the monolith. They mock all dependencies. The test suite runs quickly. Then, in staging or production, bugs appear. Bugs the fast unit tests didn't catch. Bugs that only surface when multiple services interact.

The unit tests were passing. The architecture was breaking.

![Testing Spectrum](/assets/img/posts/testing-pyramid/tower.svg)

---

## Serverless: The Apex of This Shift

Serverless functions take this to its logical extreme. A Lambda function typically handles one thing: an HTTP request, a message from an event queue, a scheduled task. **The business logic within the function is often trivial, only a few lines of code. The real logic is orchestration.**

However, this varies. A Lambda function implementing complex pricing logic needs substantial unit testing. A Lambda that reads a queue, calls a database, and publishes to another queue is primarily orchestration and should emphasize integration tests.

**The testing shift applies to the degree the function is orchestration-heavy.** A 90% orchestration / 10% logic function should have that ratio reflected in testing. A 30% orchestration / 70% logic function should have a different ratio.

I've reviewed serverless codebases where the function itself is 50 lines of code and the integration surface is 500 lines. Testing in isolation tells you nothing about whether it'll work when deployed.

**Integration tests become first-class citizens. Unit tests become a smaller proportion of your overall testing strategy. They're less likely to catch the defects that matter most in distributed systems, but they're not useless.**

![Testing Spectrum](/assets/img/posts/testing-pyramid/diamond.svg)

---

## Recognising When Your Pyramid Has Changed Shape

How do you know if you're in a microservices context where the pyramid shape matters? Here are the signals:

**Unit tests pass, but production bugs still emerge regularly.** If your unit test coverage is high (>80%), tests are passing, but you're still finding bugs in production, the unit tests aren't testing what matters. The defects are at the boundaries.

**Integration tests are significantly slower than you'd like.** In a pyramid-optimised monolith, integration tests run in minutes. In microservices, 30-45 minutes is typical. Why? Because they're orchestrating multiple services, setting up test data, talking to real or containerised external systems. This slowness is informative: integration is complex and important in your architecture.

**Your development and staging environments are fragile.** If integration test environments are unreliable, require manual setup, break frequently, that's a signal. In a monolith, a single environment suffices. In microservices, you need reliable test environments that realistically reflect production.

**Your release process is unpredictable.** Tests pass, you deploy, integration issues surface in staging. If releases are unpredictable, look at integration test coverage, that's most likely where the gaps are.

---

## The Cost of Misalignment

Research from IBM's Systems Sciences Institute, supported by studies from UC Irvine and companies like Google and Netflix, establishes clear cost multipliers. **A defect found during design costs approximately £100 to fix. During unit testing: £100-300. During integration testing: £500-1,000. In production: £2,000-10,000+.**

These ratios emerged from waterfall-era studies and apply even in continuous deployment contexts. Why? Because even with rapid deployment, the cognitive overhead of debugging production issues, coordinating incident response, and potential business impact dwarf the cost of development-stage fixing.

In continuous deployment, you deploy faster, but you don't change the fundamental economics: catching defects early is cheaper than catching them late.

**If you're investing in unit tests that don't catch real defects, you won't discover problems until later stages**, and the cost escalates rapidly.

![Testing Spectrum](/assets/img/posts/testing-pyramid/testing-spectrum.svg)

---

## When Not to Shift the Pyramid

**Some defects remain internal logic issues, even in microservices.** A service implementing complex business logic (insurance premium calculation, portfolio optimisation, pricing algorithms) should still have substantial unit test coverage. Unit tests catch logic errors. Integration tests catch boundary errors.

**Don't shift the pyramid for a service that's legitimately logic-heavy.** Shift it for services that are primarily orchestration.

**Also consider: are microservices the right choice for us?** If the testing overhead and integration complexity are substantial concerns, and your domain logic is concentrated and stable, a monolith might be more pragmatic. Microservices bring operational complexity, including testing complexity. That trade-off should be explicit. Serverless functions bring even more complexity than microservices.

---

## The Testing Pyramid vs. Testing Trophy

The "testing trophy" (popularized by Kent C. Dodds) argues for distributions: more integration tests than unit tests, fewer e2e tests. In monoliths, this might be overcorrection. In microservices and serverless, the trophy doesn't go far enough. **You often need integration tests to outnumber unit tests significantly**, and the shape continues evolving as your service becomes leaner.

---

## Defining Integration Tests for Microservices

Integration tests exist on a spectrum:

**In CI/CD pipelines:** Use Docker containers, TestContainers, and WireMock. Spin up real databases and message queues. Mock external domain dependencies (third-party APIs). Fast enough to run on every commit (~5-15 minutes for a reasonable suite).

**In dev environments:** Use deployed artefacts. Real internal domain resources (your other services, your databases). Mock external dependencies. Closer to production, discovers realistic failure modes.

**In staging:** Closer to production-like conditions. More real services talking to each other. This is where you catch the integration issues that survive pipeline testing.

This staged approach balances feedback speed (pipeline tests) with realism (dev/staging tests).

---

## The Bottom Line

The testing pyramid worked because it reflected where defects hide in monolithic applications: in code logic. Microservices move defect risk to the boundaries, in the integrations between services, the interfaces with external systems.

The pyramid doesn't fail. It transforms. And if you try to keep it pyramid-shaped in a microservices context, you're not following best practices, you're fighting your architecture.

The question isn't "should we follow the testing pyramid?" The question is "where do defects actually hide in our system?" Answer that honestly, and your testing strategy and its shape will follow.

Links to related posts:

- <a href="/blog/articles/the-cost-of-testing-late/" target="_blank" rel="noopener noreferrer">The Hidden Cost of Testing Late</a>
- <a href="/blog/articles/the-cost-of-slow-pipelines-a-tale-of-wasted-time/" target="_blank" rel="noopener noreferrer">The Cost of Slow Pipelines - A Tale of Wasted Time</a>

---

## References

{% assign cohn_agile = site.data.links | where: "id", 102 | first %}
- <a href="{{ cohn_agile.link }}" target="_blank" rel="noopener noreferrer">{{ cohn_agile.author }}, _{{ cohn_agile.title }}_</a>

{% assign test_pyramid = site.data.links | where: "id", 103 | first %}
- <a href="{{ test_pyramid.link }}" target="_blank" rel="noopener noreferrer">{{ test_pyramid.author }}, _{{ test_pyramid.title }}_</a>

{% assign microservice_testing = site.data.links | where: "id", 104 | first %}
- <a href="{{ microservice_testing.link }}" target="_blank" rel="noopener noreferrer">{{ microservice_testing.author }}, _{{ microservice_testing.title }}_</a>

{% assign building_microservices_newman = site.data.links | where: "id", 105 | first %}
- <a href="{{ building_microservices_newman.link }}" target="_blank" rel="noopener noreferrer">{{ building_microservices_newman.author }}, _{{ building_microservices_newman.title }}_</a>

{% assign continuous_delivery = site.data.links | where: "id", 98 | first %}
- <a href="{{ continuous_delivery.link }}" target="_blank" rel="noopener noreferrer">{{ continuous_delivery.author }}, _{{ continuous_delivery.title }}_</a>

{% assign ibm_defect_cost = site.data.links | where: "id", 90 | first %}
- <a href="{{ ibm_defect_cost.link }}" target="_blank" rel="noopener noreferrer">{{ ibm_defect_cost.author }}, _{{ ibm_defect_cost.title }}_</a>