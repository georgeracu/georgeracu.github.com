---
layout: post
title: "System diagrams and architecture"
date: 2019-07-28 07:00:00 +0000
tags: [programming, architecture]
mathjax: false
---

## System description

## Components

### Data collector (Producer)

Data collector is a microcontroller capable of connecting to the WiFi that will read temperature and humidity and publish them as events using MQTT ()

#### Hardware

* ESP 8266 to collect and publish data
* DHT 22 temperature and humidity sensor
* MQ 135 air quality sensor
* MQTT client for ESP 8266

#### Libraries

* DHT.h (https://github.com/adafruit/DHT-sensor-library)
* MQ135.h (https://github.com/GeorgK/MQ135)
* MQTT PubSubClient.h (https://github.com/knolleary/pubsubclient)

### Data normalizer (Consumer)

Responsible for receiving sensory data from outside world and mapping it into an agreed format to be consumed later into the boundaries of the system. Acts as an adaptor the format of the data from sensors.

* Raspbery Pi Model B Rev 2 (the beefed up server running this service)
* Clojure based Pedestal web service (an uber JAR)
* MQTT Client (http://clojuremqtt.info/)
* Database client ()

### Web API (data feed for UI)

* Raspbery Pi Model B Rev 2
* Kotlin based DropWizard Web API
* Database client

### UI (presentation layer)

* Raspbery Pi Model B Rev 2
* ClojureScript based Reagent application

### Persistence layer

* SQLite 3

## Technologies used

* Clojure
* Pedestal
* ClojureScript
* Reagent
* Kotlin
* DropWizard
* MQTT client
* SQLite 3
* Mosquitto MQTT Broker

## Tasks

### Producer

[] Create a sketch to publish events with readings from temperature and humidity sensor
[] Agree on event topics between producer and consumer

### Consumer

[] Setup MQTT consumer and start consuming events
[] Consume events from sensors by translating them into normalised events
[] Publish normalised events and store raw events into the events store

### Web API

[] Setup the Web API layer project
[] Consume normalised events by translating them into domain schema
[] Start reading from the store and expose it as a REST endpoint

### MQTT Broker

[] Setup MQTT Broker

### Store

[] Setup persistence store (SQLite3)
[] Make store resilient to SD card corruption, where possible

### UI

[] Setup the UI project
[] Start reading in the UI from the Web API endpoint

## Interactions


