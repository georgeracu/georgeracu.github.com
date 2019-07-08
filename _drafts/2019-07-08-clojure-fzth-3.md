---
layout: post
title: "Clojure from zero to hero (part 3) - IDE setup"
date: 2019-07-01 07:00:00 +0000
tags: [programming, clojure]
mathjax: false
---

## Integrated Development Environment (IDE)

Using an IDE for Clojure development raises controversy a few times. In the book [Clojure for the Brave and True](https://www.braveclojure.com/clojure-for-the-brave-and-true/), Emacs and Cider are the recommended tools. Other folks use IntelliJ IDEA and Coursive plugin or Visual Studio Code and Calva plugin or VIM and a few other plugins. I use SpaceMacs (Emacs with VIM keyboard shortcuts). The setup is better explained by [Practicalli](https://practicalli.github.io/spacemacs/install-spacemacs/). Please go ahead and pick whichever environment you feel the most comfortable with. I chose SpaceMacs because Emacs works really well integrated with Cider and I am familiar with the VIM keyboard shortcuts. Learning a new programming language (Clojure), a new programming paradigm (functional programming) and a new IDE (Emacs) is a very steep learning curve. I chose to flatten a little bit by using SpaceMacs.

## Our first custom endpoint

In the web application that we created in [episode 0]({{ site.baseurl }}{% post_url 2019-06-18-clojure-fzth-0 %}) we will start adding a custom endpoint. For now, all we want is to have a resource that returns JSON format every time when we go to `/temperature`. This resource we will hook it later with a sensor that will read temperature and humidity in a particular location.

For now we need not to worry about security, we need to write a bit of Clojure.

If you navigate to the root directory of your Pedestal application and list all the directories, you should get something similar with the image bellow. The directory we will focus at the moment is `test`. Before writing any code, we will write a failing test. Test Driven Development [TDD](https://www.jamesshore.com/Agile-Book/test_driven_development.html) is a rapid cycle of testing, coding, and refactoring.

![image](/assets/img/clojure-directory-structure.png)

From when we created the template Pedestal app, we already have a sample test file. If you run it in your favourite IDE, it should pass.

```shell
➜  pedestal-api lein test

lein test pedestal-api.service-test
INFO  io.pedestal.http  - {:msg "GET /", :line 80}
INFO  io.pedestal.http  - {:msg "GET /", :line 80}
INFO  io.pedestal.http  - {:msg "GET /about", :line 80}
INFO  io.pedestal.http  - {:msg "GET /about", :line 80}

Ran 2 tests containing 4 assertions.
0 failures, 0 errors.
➜  pedestal-api
```

### TDD step 1: red (writing a failing test)

Our new resource should respond with JSON format at any GET requests that reach `/temperature`. Let's go ahead and edit the file in `test/pedestal_api/service_test.cls` and let's add a new test. We can call it `temperature-page-test`. For now we just assert that we get an empty JSON object in the body when we do a GET request to `/temperature` and that content type from the server is `application/json`.

```clojure
(deftest temperature-page-test
  (is (.contains
       (:body (response-for service :get "/temperature"))
       "{}"))
  (is (=
       (:headers (response-for service :get "/temperature"))
       {"Content-Type" "application/json"})))
```

Running the above test should return two errors, for both `(is)` blocks. This is the first step in a TDD cycle, the red part, where we have a failing test, signalled by the colour red in most testing frameworks.

### TDD step 2: green (implementing the code and making the tests pass)

In ordere to make the test pass, we need to write some code. First stop is to write a handler function that will be able to get a `request` object as parameter and return the payload we expect. Navigate to `src/pedestal_api/service.clj` and create a new function:

```clojure
(defn temperature-page
  [request]
  (ring-resp/response "{}"))
```

Then we run our tests but they still fail. Is because we didn't tell to the router about our new resource. Next step is to add out new handler function for route `/temperature` to `routes`. Something along the lines of:

```clojure

```
