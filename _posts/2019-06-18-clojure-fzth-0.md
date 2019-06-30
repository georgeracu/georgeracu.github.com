---
layout: post
title: "Clojure from zero to hero (0) - creating a Pedestal app"
date: 2019-06-18 07:00:00 +0000
tags: [programming, clojure]
mathjax: false
---

```clojure
(ns com.georgeracu.site.posts.fzth.zero)
```

## Install Leiningen and create your first app

### [Clojure](https://clojure.org/)

_Clojure is a dynamic, general-purpose programming language, combining the approachability and interactive development of a scripting language with an efficient and robust infrastructure for multithreaded programming._

### Leiningen

As these folks are saying on their website: _If you come from the Java world, Leiningen could be thought of as "Maven meets Ant without the pain". For Ruby and Python folks, Leiningen combines RubyGems/Bundler/Rake and pip/Fabric in a single tool._

Basically is a dependency management tool with built in capabilities to build the Clojure project and to generate Clojure apps from templates.

#### Install [Leiningen](https://leiningen.org/)

#### Ubuntu 19.04

{% highlight bash %}
sudo apt install leiningen
{% endhighlight %}

#### Homebrew

```bash
brew install leiningen
```

If you have a different distro or a different operating system, go to Leiningen's instalation instructions and you should find instructions on how to do it.

### [Pedestal](https://pedestal.io)

_Pedestal is a set of libraries that we use to build services and applications. It runs in the back end and can serve up whole HTML pages or handle API requests._

### First Clojure project

#### Create a new web API from a template

```bash
lein new pedestal-service pedestal-api
```

#### Go to the new location and tell Leiningen to download and install all the dependencies for the project

```bash
cd pedestal-api
lein deps
```

#### Run the application using Leiningen

```bash
lein run
```

Navigate to http://localhost:8080 and you should get the very popular greeting "Hello world".

### Grab a drink to celebrate

Done, you have a running web API using Clojure, Pedestal and Leiningen.
