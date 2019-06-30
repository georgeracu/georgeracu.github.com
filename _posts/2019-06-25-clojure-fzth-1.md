---
layout: post
title: "Clojure from zero to hero (1) - explaining project.clj"
date: 2019-06-25 07:00:00 +0000
tags: [programming, clojure]
mathjax: false
---

```clojure
(ns com.georgeracu.site.posts.fzth.one)
```

### Dependencies

This area declares which dependencies are required for the project. From the version of Clojure to the version of the libraries used to put together the app. In this case, is a simple file where we use only Pedestal, Jetty for web server and Logback and SLF4J for logging.

For those who want to use a different web server, there is an option to enable Mutant or Tomcat. Just uncomment the line for the desired web server and comment the one for Jetty. There cannot be two active web servers at the same time.

### Lein

Lein version declares which version of Leiningen we use to build our project. Think of it like Maven or Gradle.

### Resource paths

Being a web app we need some directories to store our config files and static resources. These section helps to inform Leiningen where to look for them when building our project. We should place all our config files in `config` and static resources in `resources`.

### Profiles

These section of the file describes what profiles are available for the command `lein`. You can use `lein run-dev` to start the app in development mode or `lein uberjar` to package the app in a fat jar, bundled with all dependencies required to run it as a JAR file. After packaging in a fat JAR, the Clojure app can be run from command line with:
`java -jar target/pedestal-api-0.0.1-SNAPSHOT-standalone.jar`
The server should start and run at `http://localhost:8080`.

### Full project.clj file

```clojure
(defproject pedestal-api "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [io.pedestal/pedestal.service "0.5.7"]

                 ;; Remove this line and uncomment one of the next lines to
                 ;; use Immutant or Tomcat instead of Jetty:
                 [io.pedestal/pedestal.jetty "0.5.7"]
                 ;; [io.pedestal/pedestal.immutant "0.5.7"]
                 ;; [io.pedestal/pedestal.tomcat "0.5.7"]

                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.26"]
                 [org.slf4j/jcl-over-slf4j "1.7.26"]
                 [org.slf4j/log4j-over-slf4j "1.7.26"]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  ;; If you use HTTP/2 or ALPN, use the java-agent to pull in the correct alpn-boot dependency
  ;:java-agents [[org.mortbay.jetty.alpn/jetty-alpn-agent "2.0.5"]]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "pedestal-api.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.7"]]}
             :uberjar {:aot [pedestal-api.server]}}
  :main ^{:skip-aot true} pedestal-api.server)
```
