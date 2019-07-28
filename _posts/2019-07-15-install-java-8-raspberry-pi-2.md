---
layout: post
title: "Running a Clojure Pedestal application on Raspberry Pi model B revision 2"
date: 2019-07-15 07:00:00 +0000
tags: [programming, clojure, raspberrypi, pedestal]
mathjax: false
---

## Compiling a fat JAR (uberjar) from the Pedestal application

Given that we already have a working application with an endpoint (`/temperature`) we now can deploy it and run it. Thinking on where this app should be run, I noticed that I have sitting around a Raspberry Pi Model B Rev 2. For those who might ask what processing power this little device has, this is fitted with 512MB RAM and an ARM CPU running at 700MHz. Not a very powerful device, but good enough for our little Clojure app.

If you have a Raspberry Pi sitting around and you want to find out what version it is, just type in the command line:

```bash
pi@raspberrypi:~ $ cat /proc/device-tree/model
```

And you should see something similar on your screen:

```bash
Raspberry Pi Model B Rev 2pi@raspberrypi:~ $
```

On the development machine, just go to the root folder of your Pedestal application and run:

```bash
➜  pedestal-api git:(master) lein uberjar
Compiling pedestal-api.server
INFO  org.eclipse.jetty.util.log  - Logging initialized @20621ms to org.eclipse.jetty.util.log.Slf4jLog
Created /Users/x/Projects/clojure/pedestal-api/target/pedestal-api-0.0.1-SNAPSHOT.jar
Created /Users/x/Projects/clojure/pedestal-api/target/pedestal-api-0.0.1-SNAPSHOT-standalone.jar
```

And the output should be a JAR file in `target/pedestal-api-0.0.1-SNAPSHOT.jar`. This file contains everything that is needed for the app to run on a system capable of running Java 8 applications.

## Install Java 8 on Raspberry Pi Model B Rev 2

In this case, the CPU architecture restricts our options in which JRE (Java Runtime Environment) to install. I struggled a little bit until I managed to find a version that works on Raspbian Buster Lite (my OS on Raspberry Pi).

Let's start clean and remove any traces of the OpenJDK:
```bash
sudo apt-get purge openjdk-8-jre-headles
```
Now let's install Java 8:
```bash
sudo apt-get install openjdk-8-jre-headless
sudo apt-get install openjdk-8-jre
```

In the end the output should show Java 8 installed
```bash
java -version
openjdk version "1.8.0_212"
OpenJDK Runtime Environment (build 1.8.0_212-8u212-b01-1+rpi1-b01)
OpenJDK Client VM (build 25.212-b01, mixed mode)
```

## SSH into the Raspberry Pi

Until we will setup continuous deployments onto our little server (Raspberry Pi) we will do it by hand. We just ssh into the box and copy the uber JAR.

### Enable SSH onto Raspberry Pi

My distro of Raspbian comes with SSH disabled by default. This is a headless distro and all you need to do to enable SSH at boot time is to place a file named `ssh` onto the boot partition of the SD card. No file content is required and no extension. Raspbian will pickup the file at boot time and enable SSH.

More information can be found into [official documentation](https://www.raspberrypi.org/documentation/remote-access/ssh/).

## Copy the uber JAR via SSH

To copy the uber JAR we will be using `scp` (scp -- secure copy (remote file copy program)).

```bash
scp target/pedestal-api-0.0.1-SNAPSHOT-standalone.jar pi@192.168.1.151:/home/pi/clojure-jars
```

Arguments passed to the `scp` command:
* File to copy: target/pedestal-api-0.0.1-SNAPSHOT-standalone.jar
* Destination: username@host:location

## Start the Pedestal app on the Raspberry Pi

To start the Clojure app we need to SSH into the server and run the JAR as any Java archive.

```bash
➜  pedestal-api git:(master) ✗ ssh pi@192.168.1.151
pi@raspberrypi:~ $ java -jar clojure-jars/pedestal-api-0.0.1-SNAPSHOT-standalone.jar
```

It might seem that the command is stuck, but be patient. We need to reminder that this is a less powerful device and it needs some time to start. After the server starts, you should see something like this into your terminal:

```bash
pi@raspberrypi:~ $ java -jar clojure-jars/pedestal-api-0.0.1-SNAPSHOT-standalone.jar
INFO  org.eclipse.jetty.util.log  - Logging initialized @93062ms to org.eclipse.jetty.util.log.Slf4jLog

Creating your server...
INFO  org.eclipse.jetty.server.Server  - jetty-9.4.z-SNAPSHOT; built: 2019-04-29T20:42:08.989Z; git: e1bc35120a6617ee3df052294e433f3a25ce7097; jvm 1.8.0_212-8u212-b01-1+rpi1-b01
INFO  o.e.j.server.handler.ContextHandler  - Started o.e.j.s.ServletContextHandler@40e9cd{/,null,AVAILABLE}
INFO  o.e.jetty.server.AbstractConnector  - Started ServerConnector@ce284d{HTTP/1.1,[http/1.1, h2c]}{localhost:8080}
INFO  org.eclipse.jetty.server.Server  - Started @97551ms
```

This means that your server is started and is ready to accept connections.

## Test the app via `curl`

At this stage, the application is not accessible outside of the Raspberry Pi box, so a way to test it is to use `curl` (curl - transfer a URL).

```bash
pi@raspberrypi:~ $ curl localhost:8080/temperature
{"celsius":0,"fahrenheit":32}
```

## What we achieved so far

* We packaged a Pedestal web service into an uber JAR
* We installed Java8 on Raspberry Pi Model B Rev 2
* We enabled SSH onto the Raspberry Pi and we connected to it
* We copied the uber JAR onto the server via `scp`
* We started the web service and tested via `curl`
