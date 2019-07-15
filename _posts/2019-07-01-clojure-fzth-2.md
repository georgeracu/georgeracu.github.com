---
layout: post
title: "Clojure from zero to hero (part 2) - A bit of syntax"
date: 2019-07-01 07:00:00 +0000
tags: [programming, clojure]
mathjax: false
---

## Clojure syntax

As a short intro into Clojure's syntax, this programming language is a Lisp. This means that there's no semicolons (`;`) or indentation to signal line end. There's no curly brackets (`{}`) to signal the context of a class, function or method. Clojure keeps it simple and has only parentheses (`()`) to wrap the context of a function. Curly brackets are also known as as curly braces and are used to declare a map.

An example of declaring a Clojure function:

```clojure
(defn custom-addition [a b]
  (+ a b))  ;;try running this function in your REPL
```

Keep in mind the parentheses requirement, the above function can be called in the REPL as:

```clojure
(custom-addition 3 4)
7
```

## ⚠ New term alert: REPL

REPL is an abrevation for Read-Evaluate-Print-Loop and is an interactive environment where code is evaluated and provides instant feedback. Is a great tool to evaluate code but this doesn't replace testing. In the REPL you can test your code with custom values but do not use it as an excuse not to transfer those tests into proper unit test files. We will speak about testing in a later post.

The REPL can be started with:

`lein repl`

## Clojure namespaces

Clojure namespace acts as a way to separate code that belongs to the same domain logic. You can think of as Java's package declaration. A namespace is declared at the top of your file as:

```clojure
(ns com.georgeracu.site.posts.fzth.two)
;; then your Clojure code here
```

## ⚠ New symbol alert: `;`

In Clojure, semicolon is used to comment out a line of code. Other usage is using double semicolon to leave a comment for a line of code. Usually, code should be self explainable, you shouldn't have to add comments to make your code easier to read and understand. If that's the case then think again if you can remove that comment by renaming variables and functions or by extracting logic into a different function. If you still need to add a comment using double semicolons you can use it as:

```clojure
(custom-addition [a b]
  (+ a b)) ;;try running this function in your REPL
```

## ⚠ New concept alert: no `return` keyword

Some of us are used to specify a return statement if the function or method is not having a `void` or `unit` return type. In Clojure there's no need to use a return statement as the last line that gets executed in the function will be returned. If the function is not a pure function with data in data out and in its execution body calls other services we call it a function with `side effects`. We should get back on explaining more about pure functions and side effects later on.

## A code editor

So far we started using Clojure without the need to use a code editor. It's an easy start that showed how in less than five minutes you can create and run a Web project.
Next in our quest is to start writing some actual Clojure. After all, we are here to learn to code but also to write good Clojure.
In order to achieve our goal we need to find a code editor that can help us write Clojure code easier.
This is the task of the next article.
