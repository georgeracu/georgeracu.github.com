---
layout: post
title: "Clojure from zero to hero (part 3) - First endpoint"
date: 2019-07-08 07:00:00 +0000
tags: [programming, clojure]
mathjax: false
---

## Integrated Development Environment (IDE)

Using an IDE for Clojure development raises controversy a few times. In the book [Clojure for the Brave and True](https://www.braveclojure.com/clojure-for-the-brave-and-true/), Emacs and Cider are the recommended tools. Other folks use IntelliJ IDEA and Coursive plugin or Visual Studio Code and Calva plugin or VIM and a few other plugins. I use SpaceMacs (Emacs with VIM keyboard shortcuts), Cider and Clojure layer. The setup is better explained by [Practicalli](https://practicalli.github.io/spacemacs/install-spacemacs/). Please go ahead and pick whichever environment you feel the most comfortable with. I chose SpaceMacs because Emacs works realy well integrated with Cider and I am familiar with VIM's keyboard shortcuts. Learning a new programming language (Clojure), a new programming paradigm (functional programming) and a new IDE (Emacs) is a very steep learning curve. I chose to flatten a little bit by using SpaceMacs.

## Our first custom endpoint

In the web application that we created in [episode 0]({{ site.baseurl }}{% post_url 2019-06-18-clojure-fzth-0 %}) we will start adding a custom endpoint. For now, all we want is to have a resource that returns in JSON format every time when we go to `/temperature`. This resource we will hook it later with a sensor that will read temperature and humidity in a particular location.

For now we need not to worry about security, we need to write a bit of Clojure.

If you navigate to the root directory of your Pedestal application and list all the directories, you should get something similar with the image bellow. The directory we will focus at the moment is `test`. Before writing any code, we will write a failing test. Test Driven Development [TDD](https://www.jamesshore.com/Agile-Book/test_driven_development.html) is a rapid cycle of testing, coding, and code improvement.

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

### TDD step 1: Red (writing a failing test)

Our new resource should respond with JSON format at any GET requests that reaches `/temperature`. Let's go ahead and edit the file in `test/pedestal_api/service_test.cls` and let's add a new test. We can call it `temperature-page-test`. For now we just assert that we get a JSON object in the body when we do a GET request to `/temperature` and that content type from the server is `application/json`. The body should return a hardcoded zero Celsius and 32 Fahrenheit for now.

```clojure
(deftest temperature-page-test
  (is (=
       (:body (response-for service :get "/temperature"))
       "{\"celsius\":0,\"fahrenheit\":32}"))
  (is (true?
       (s/includes?
        (:headers (response-for service :get "/temperature")) "application/json;charset=UTF-8"))))
```

For the above assertion to work we need to import `[clojure.string :as s]`. It was added in Clojure 1.8.

Running the above test should return two errors, for both the body and the headers blocks. This is the first step in a TDD cycle, the red part, where we have a failing test, signalled by the colour red in most testing frameworks.

### TDD step 2: Green (implementing the code and making the tests pass)

In order to make the tests pass, we need to write some code. First stop is to write a handler function that will be able to get a `request` object as parameter and return the payload we expect. Navigate to `src/pedestal_api/service.clj` and create one new function:

```clojure
(defn temperature-page
  [request]
  (ring-resp/response {:celsius 0
                       :fahrenheit 32}))
```

Then we run our tests but they still fail. Is is because we didn't tell to the router about our new resource. Next step is to add our new handler function for route `/temperature` to `routes`. Something along the lines of:

```clojure
["/temperature" :get (conj [(body-params/body-params) http/json-body] `temperature-page)]
```

Next step is to run the new tests. I use SpaceMacs and Clojure layer, but it can be run from command line using `lein test` and the result should look like

```shell
Ran 3 tests containing 6 assertions.
0 failures, 0 errors.
```

Given that all our tests are passing we are free to move to the next step of TDD.

### TDD step 3: Refactor (improve code without breaking tests)

This step is about code improvements. Many times we hack something to make the test(s) pass and is not all the times the best solution. After making all the tests pass, we can go back and improve our code by renaming functions and data, extracting code into smaller units, moving code around into better named names spaces.

Before starting changing code, we need to add our changes to git. In case we do too many changes and don't keep track of them, we have a clean slate to start over our improvements.

```shell
➜  pedestal-api git:(master) ✗ git status

modified:   src/pedestal_api/service.clj
modified:   test/pedestal_api/service_test.clj

➜  pedestal-api git:(master) ✗ git add .
➜  pedestal-api git:(master) ✗ git commit -m "Added a new endpoint /temperature that returns hardcoded values as JSON"
```

Now that we have our code versioned locally we can start improving it. Given that we use a vector to do the conjoin operation, it looks like a good candidate to be extracted into a `def`. For now we can call the new def `common-json-interceptors` to be similar with the one called `common-interceptors`. This def needs to be declared before the `routes` such that it can be used.

```clojure
;; returns a JSON response encoding any map sent to it as valid
;; JSON object into the body and adds the correct headers
(def common-json-interceptors [(body-params/body-params) http/json-body])
```

And the route now can use the new def:

```clojure
["/temperature" :get (conj common-json-interceptors `temperature-page)]
```

Given that we have tests to cover our code, we are confident that we didn't brake our code by making these changes. Running the tests again, should yeld the same result as above:

```shell
➜  pedestal-api git:(master) ✗ lein test

lein test pedestal-api.service-test
INFO  io.pedestal.http  - {:msg "GET /", :line 80}
INFO  io.pedestal.http  - {:msg "GET /", :line 80}
INFO  io.pedestal.http  - {:msg "GET /about", :line 80}
INFO  io.pedestal.http  - {:msg "GET /about", :line 80}
INFO  io.pedestal.http  - {:msg "GET /temperature", :line 80}
INFO  io.pedestal.http  - {:msg "GET /temperature", :line 80}

Ran 3 tests containing 6 assertions.
0 failures, 0 errors.
```

Once we finish improving our code, amend the previous commit, add a more descriptive "why" message (if needed) and push to `master`.

Looking forward to the next section.
