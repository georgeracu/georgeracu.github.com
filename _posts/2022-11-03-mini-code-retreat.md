---
layout: post
title: Facilitating a mini Code Retreat
permalink: /blog/articles/facilitate-mini-code-retreat/
tags: [java, design pattern, clean code, tdd, code retreat]
mathjax: false
description: A short description on facilitating an event like the Global Day of Code Retreat
---

This is a virtual event. We need to make sure that participants have access to a laptop and that they have a working web camera.

We need to put participants into a learning mindset, were our work constraints are left behind and we focus on the practice of writing software.

The agenda is structured for a session that lasts for 4 hours, with a lunch break of 30 minutes. An agenda for a day structured for 8 hours will have a longer lunch break and longer session.

## Agenda

* Welcome participants
* Group introduction
* Introduce the hosts of the event and write their name down
* Read out the important bits of our Code of Conduct
* Layout the schedule for the rest of the day
* Have everything written down into a shared space that anyone can access: Miro etc.

## Introducing the workshop

* This is an autodidactic workshop, I am here just to provide guidance
* Establish learning goals
    * Practice TDD
    * Mentor other developers
    * Try new languages and paradigms
* Topic: Reduce the Cost of Change in our code
* Topic: [Four Rules of Simple Design](https://www.martinfowler.com/bliki/BeckDesignRules.html)
    * Passes al tests
    * Expresses intent: clear, expressive, and consistent
    * No logic duplication
    * Minimal methods, classes, and modules (no superfluous abstractions)
* Topic: Coderetreat format
    * We shouldn't try to finish the problem, and why that's important
    * At the end of the session we will ask people to delete their code and standup. We cannot do this at work :-)
* Topic: [Conway's Game of Life](https://en.wikipedia.org/wiki/Conways_Game_of_Life)
* Encourage participants to have the courage to experiment

## Tooling and setup

* Using [VSCode](https://code.visualstudio.com/download) with [Live Share](https://marketplace.visualstudio.com/items?itemName=MS-vsliveshare.vsliveshare) extension for live pair programming
    * Alternatively, [VSCode for web](https://vscode.dev) can be used
* Using [Miro](https://miro.com/) to share the agenda with our participants and to capture feedback from retrospectives
* Have a video feed always on where all participants can join
* Have pairs work in separate huddles via Slack and the facilitator can jump in and out from the pairing session


## Test-Driven Development

* Activity: ask participants to scale their experience with TDD by picking a number between 0 and 5, where 0 is no experience and 5 is "I do it every day religiously"
    * This needs to be done in a safe environment, and should not be questioned

### TDD rules

* Write production code only to pass a failing unit test
* Write no more of a unit test than sufficient to fail (compilation failures are failures)
* Write no more production code than necessary to pass the one failing unit test

### The TDD Cycle

* Red: write a failing test
* Green: write code that makes the test pass (nothing more)
* Refactor: refactor your code while the test(s) are green
* Repeat

## Pair-Programming

### [Rules](https://www.cprime.com/resources/blog/etiquette-for-pair-programming/)

* Pay Attention and Be Engaged
* Program Out Loud
* Encourage Vulnerability and Discourage Judgement
* Thicken Your Skin a Little
* Be Humble and Willing to Try Things
* Remind Each Other About Standards and Agreements
* Be a Navigator, Not a Backseat Driver

### [Styles](https://martinfowler.com/articles/on-pair-programming.html)

* Driver and navigator
* Ping pong
* Strong-style pairing

## Coderetreat session rules

* Do not put time pressure: a visible timer or inform participants of how long the session is

## Schedule of the day

* 11:00 AM getting started
* 11:05 AM agenda and introductions
* 11:30 AM First session
* 12:00 PM First retrospective
* 12:10 PM Break (including lunch)
* 12:30 PM Second session
* 01:00 PM Second retrospective
* 01:10 PM Break
* 01:20 PM Third session
* 01:50 PM Third retrospective
* 02:00 PM Break
* 02:10 PM Fourth session
* 02:40 PM Fourth retrospective
* 02:50 PM Wrap-up
* 03:00 PM Goodbyes

### Sessions

* First session: setup and problem introduction
    * People make sure that they have their environments ready
    * Participants make themselves familiar with the Game of Life
    * Make it clear that participants should delete their code after each session
* Second session: verbs instead of nouns
    * Appropriate data structures around the problem
    * [Primitive obsession](https://wiki.c2.com/?PrimitiveObsession)
    * Verbs instead of nouns: every class name and variable name needs to be a verb (`CreatesCellGeneration`, `AppliesRuleNumberOne`)
* Third session: heavy exploration of abstractions 
    * Polymorphism vs boolean flags
    * Explore abstractions and stay away from primitives
* Fourth session: constraints
    * No if statements
    * No loops
    * Small methods (1 - 5 lines of code)
    * No language primitives
    * TDD as if you meant it
    * Max 2 minutes session

### Retrospective

* Facilitators should _facilitate_ the sessions not lecture the participants
* Ask questions, let participants discover things on their own
* Get everyone involved
* Who found it difficult to delete their code? Why it was difficult?

### Closing circle

* Everyone should answer three simple questions:
    * What, if anything, did you learn today?
    * What, if anything, surprised you today?
    * What, if anything, will you do differently in the future?
* Ask about feedback about the session

## Resources

* [Facilitators](https://www.coderetreat.org/facilitators/facilitation/)
* [Lessons learned](https://alexbolboaca.ro/coderetreat/how-to-organize-a-code-retreat)
