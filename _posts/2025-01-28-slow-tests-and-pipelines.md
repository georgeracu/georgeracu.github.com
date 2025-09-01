---

layout: post
title: The Cost of Slow Pipelines - A Tale of Wasted Time
permalink: /blog/articles/the-cost-of-slow-pipelines-a-tale-of-wasted-time/
tags: [post, accidental-complexity, lean, flow, test, slow, pipeline, ai]
mathjax: false
description: A real life story on how reducing time spent on building and running slow tests and pipelines will speed up Software Development and Innovation and, improve Developer Experience.
---

## Daily Practice  

Imagine you’re a developer named Sam, starting your day with a strong cup of coffee and a long list of tickets to tackle. You fire up your IDE, make the changes, and run your **first local build** to ensure everything works as expected. While the build churns for **~10 minutes**, you glance at your inbox, scroll through Slack, and lose focus. Finally, the build completes. You make a small adjustment and run the build again. **Another 10 minutes pass**.  

By the time you’ve created your pull request (PR), you’ve already spent **~20 minutes waiting** for local builds alone. Your CI/CD pipeline will add another **20 minutes** before the tests confirm whether your code is ready to merge. Multiply this by **4 PRs per day**, across a team of 5 developers, and the numbers paint a grim picture.  

Let’s crunch those numbers and get a better picture of who's stealing our time.  

### Daily Time Spent Waiting  

1. **Local Builds**:  
   - **2 builds per day per developer** × **10 minutes per build** × **5 developers** = **100 minutes (1.67 hours) per day**.  
2. **CI/CD Pipelines**:  
   - **4 pull requests per day** × **20 minutes per pipeline** = **80 minutes (1.33 hours) per day**.  

**Total Daily Waiting Time** = **100 + 80 = 180 minutes (3 hours) per day**.  

Initially this doesn't seem much for a team of 5 developers.

### The Monthly Toll  

Fast forward a month, and here’s how much time Sam’s team has spent waiting:  

- **3 hours per day** × **5 working days** × **4 weeks** = **60 hours per month**.  
- For a team of 5 developers, that’s **~300 developer-hours per month** wasted.  

To put it another way: **300 hours** is like losing a developer for almost two months each year while waiting for slow pipelines.  

And this isn’t just about time—it’s about momentum. Every time Sam stops to wait for a build, it’s like pulling the brakes on a train. The wheels of creativity grind to a halt, and getting back up to speed takes even longer.  

### What if there's a better world?  

Now imagine a world where Sam’s local builds are optimized to run in **half the time**—from **~10 minutes** down to **~5 minutes per build**. Here’s how the story changes:  

1. **Local Builds**:  
   - **2 builds per day per developer** × **5 minutes per build** × **5 developers** = **50 minutes per day** (down from 100 minutes).  

2. **CI/CD Pipelines**: No change, still **80 minutes per day**.  

**New Total Daily Waiting Time** = **50 + 80 = 130 minutes (2.17 hours) per day**.  

**Time Saved Daily** = **180 minutes - 130 minutes = 50 minutes (0.83 hours) per day**.  

Over a month, this saves **~16.68 hours per developer** and **~83.4 hours for the whole team**—the equivalent of two weeks of focused, productive work.  

## The Problem with End-to-End Tests: A Rocky Road  

Sam knows the value of tests; after all, testing helps catch bugs before they reach production. But not all tests are created equal. End-to-end (E2E) tests, while comprehensive, are like the rocky paths of the testing world: slow, brittle, and difficult to maintain.  

Here’s why:  

### 1. **Complexity Rules the Setup**  

To run a single E2E test, you need to replicate the entire user journey. Imagine testing a checkout flow:  

- The application must be fully deployed (often across multiple microservices).  
- Databases like MongoDB must be prepped with realistic data.  
- External dependencies—like AWS services—need to be simulated or live.  

Now contrast that with a **unit test**, which runs entirely in-memory, isolated from the world. A unit test is like solving a math problem on a sheet of paper: simple and self-contained.  

### 2. **The Slowness and Fragility of E2E Tests**  

End-to-end tests are inherently slow. Each test interacts with the entire stack, which means delays from network calls, database queries, and external services. A single E2E test might take **1–2 minutes**, and if you’ve got a suite of 50 tests, your CI/CD pipeline could take over an **hour** to run.  

What’s worse, E2E tests are often brittle. Small changes in one part of the system—like the UI layout or database schema—can break multiple tests. Debugging these failures is like searching for a needle in a haystack, sapping time and energy from Sam’s day.  

## A Better Way: The Swiss Cheese Model  

To avoid these pitfalls, Sam’s team adopts the **Swiss cheese model** for testing—a concept drawn from books like *“Clean Code”* by Robert C. Martin and *“Working Effectively with Legacy Code”* by Michael Feathers. The idea is simple: no single layer of testing is perfect, but by combining multiple layers, you can catch most issues without relying too heavily on any one approach.  

Here’s how Sam’s team layers their tests:  

1. **Unit Tests**: Cover individual components and logic (e.g., does a method calculate taxes correctly?). These tests are the fastest and easiest to maintain.  

2. **Integration Tests**: Validate how components work together (e.g., does the Spring Boot service interact correctly with MongoDB?). Tools like **TestContainers** make it easy to spin up lightweight, isolated environments for these tests.  

3. **End-to-End Tests**: Focus only on critical workflows, like a user logging in or completing a purchase. By limiting the number of E2E tests, the team avoids the trap of bloated, fragile test suites.  

This layered approach balances speed and coverage. As *“The Pragmatic Programmer”* by Andy Hunt and Dave Thomas explains, “Test deliberately, and test efficiently.”

## Sam’s Optimized Day  

With faster local builds, a smarter approach to testing, and fewer fragile E2E tests, Sam’s day transforms. Waiting for builds no longer interrupts their flow, and the team’s productivity soars. Over the course of a month, the team saves over **83 hours**, reclaiming valuable time to focus on innovation instead of frustration.  

### Final Thoughts  

The numbers don’t lie: slow builds and pipelines are a productivity drain. By adopting faster local builds, tools like **LocalStack** and **TestContainers**, and the **Swiss cheese model** of testing, you can save hours every day and months every year.  

As *“Clean Architecture”* by Robert C. Martin emphasizes, “The goal of software architecture is to minimize the human resources required to build and maintain the required system.” Faster pipelines do just that—they free your team to do their best work.  

So the next time you’re waiting for a pipeline to finish, ask yourself: *How much more could we achieve with faster tests?*  

---

## References

- [Bob C. Martin, *Clean Code: A Handbook of Agile Software Craftsmanship*](https://amzn.to/4ioMG4w).
