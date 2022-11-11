
# Like a Global Day of Code Retreat

<hr/>

## Welcome everyone :-)

* Agenda
* Tools
* Problem

---

## Code of Conduct

<hr/>

Please take a few minutes to read the [community code of conduct](
https://communitycodeofconduct.com/)

By continuing with the session you agree with the code of conduct

---

## Agenda

<hr/>

* Welcome participants
* Group introduction
* Introduce the hosts of the event and write their name down
* Read out the important bits of our Code of Conduct
* Layout the schedule for the rest of the day

---

## Introducing the workshop

* This is an autodidactic workshop, I am here just to provide guidance
* Do not attempt to finish solving the problem, we are here to learn
* Establish learning goals
    * Practice TDD
    * Mentor other developers
    * Try new languages and paradigms
* Multiple sessions where we will try to solve the problem using TDD and pair programming and following the 4 rules of simple design
* After each session we will have to **delete our code**. I am serious about this
* Be kind and friendly with your pair during the day

---

## Workshop rules

<hr/>

* Have the IDE installed on your computer and the Live Share plugin
    * Alternatively you can use the browser version
* At the end of each session please delete all your code

---

## Tools

<hr/>

* Using [VSCode](https://code.visualstudio.com/download) with [Live Share](https://marketplace.visualstudio.com/items?itemName=MS-vsliveshare.vsliveshare) extension for live pair programming
    * Alternatively, [VSCode for web](https://vscode.dev) can be used
* Using [Miro](https://miro.com/) to share the agenda with our participants and to capture feedback from retrospectives
* [Maven](https://maven.apache.org/install.html) to build our project
* Java 17
* Starting project [here](https://github.com/georgeracu/global-day-of-code-retreat.git)
* Have a video feed always on where all participants can join
* Have pairs work in separate huddles via Slack and the facilitator can jump in and out from the pairing session

---

## Pair programming [rules](https://www.cprime.com/resources/blog/etiquette-for-pair-programming/)

<hr/>

* Pay Attention and Be Engaged
* Program Out Loud
* Encourage Vulnerability and Discourage Judgement
* Thicken Your Skin a Little
* Be Humble and Willing to Try Things
* Remind Each Other About Standards and Agreements
* Be a Navigator, Not a Backseat Driver

---

## Pair programming [styles](https://martinfowler.com/articles/on-pair-programming.html)

<hr/>

* Driver and navigator
    * Driver communicates while typing and changing code
    * Navigator supports the driver and keeps an eye on the code base
    * Roles should be switched regularly
* Ping pong
    * One person writes a failing test
    * The other person writes the code that will make it pass.
    * Then change roles

---

## TDD

<hr/>

### TDD rules

* Write production code only to pass a failing unit test
* Write no more of a unit test than sufficient to fail (compilation failures are failures)
* Write no more production code than necessary to pass the one failing unit test

### The TDD Cycle

* Red: write a failing test
* Green: write code that makes the test pass (nothing more)
* Refactor: refactor your code while the test(s) are green
* Repeat

---

## The Problem: [Conway's Game of Life](https://en.wikipedia.org/wiki/Conways_Game_of_Life)

<hr/>

The universe of the Game of Life is an infinite, two-dimensional orthogonal grid of square cells, each of which is in one of two possible states, live or dead (or populated and unpopulated, respectively). Every cell interacts with its eight neighbours, which are the cells that are horizontally, vertically, or diagonally adjacent. At each step in time, the following transitions occur:

* Any live cell with fewer than two live neighbours dies, as if by underpopulation.
* Any live cell with two or three live neighbours lives on to the next generation.
* Any live cell with more than three live neighbours dies, as if by overpopulation.
* Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.

---

## Rules condensed

<hr/>

These rules, which compare the behavior of the automaton to real life, can be condensed into the following:

* Any live cell with two or three live neighbours survives.
* Any dead cell with three live neighbours becomes a live cell.
* All other live cells die in the next generation. Similarly, all other dead cells stay dead.

The initial pattern constitutes the _seed_ of the system. The first generation is created by applying the above rules simultaneously to every cell in the seed, live or dead; births and deaths occur simultaneously, and the discrete moment at which this happens is sometimes called a tick. Each generation is a pure function of the preceding one. The rules continue to be applied repeatedly to create further generations. 

---

## Example

<hr/>

![Example](https://upload.wikimedia.org/wikipedia/commons/1/12/Game_of_life_toad.gif)

---

## Topic: [The Cost of Change](https://martinfowler.com/articles/is-quality-worth-cost.html)

<hr/>

_When I talk to software developers who have been working on a system for a while, I often hear that they were able to make progress rapidly at first, but now it takes much longer to add new features. Every new feature requires more and more time to understand how to fit it into the existing code base, and once it’s added, bugs often crop up that take even longer to fix. The code base starts looking like a series of patches covering patches, and it takes an exercise in archaeology to figure out how things work. This burden slows down adding new features—to the point that developers wish they could start again from a blank slate._

— Martin Fowler, [Refactoring](https://martinfowler.com/books/refactoring.html)

_The fundamental role of internal quality is that it lowers the cost of future change. But there is some extra effort required to write good software, which does impose some cost in the short term._

— Martin Fowler, [Visualizing The Impact Of Internal Quality](https://martinfowler.com/articles/is-quality-worth-cost.html#VisualizingTheImpactOfInternalQuality)

---

## Topic: [Four Rules of Simple Design](https://www.martinfowler.com/bliki/BeckDesignRules.html)

<hr/>

* Passes al tests
* Reveals intention: clear, expressive, and consistent
* No logic duplication
* Minimal methods, classes, and modules (no superfluous abstractions)

The rules are in priority order, so "passes the tests" takes priority over "reveals intention"

---

## First session

<hr/>

* Participants should pick a pair
* Participants should make sure their setup is working
* They should familiarize with the problem
* First attempt at solving the problem

---

## Second session

<hr/>

Applying first constraint:

* Verbs instead of nouns: every class name and variable name needs to be a verb (`CreatesCellGeneration`, `AppliesRuleNumberOne`)

---

## Third session

<hr/>

A different constraint:

* Polymorphism vs boolean flags: no usage of boolean flags
* Explore abstractions and stay away from primitives. See [Primitive obsession](https://wiki.c2.com/?PrimitiveObsession)

---

## Fourth session

<hr/>

* Verbs instead of nouns
* No if statements
* No loops
* Small methods (max 5 lines of code)
* No language primitives

---

## Retrospective

<hr/>

* Facilitators should _facilitate_ the sessions not lecture the participants
* Ask questions, let participants discover things on their own
* Get everyone involved
* Who found it difficult to delete their code? Why it was difficult?

---

## Closing circle

<hr/>

* Everyone should answer three simple questions:
    * What, if anything, did you learn today?
    * What, if anything, surprised you today?
    * What, if anything, will you do differently in the future?
* Ask about feedback about the session

---

## Resources

* [Facilitators](https://www.coderetreat.org/facilitators/facilitation/)
* [Lessons learned](https://alexbolboaca.ro/coderetreat/how-to-organize-a-code-retreat)
