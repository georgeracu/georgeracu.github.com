---
layout: post
title: "Store"
date: 2019-07-30 07:00:00 +0000
tags: [programming, architecture, sqlite3]
mathjax: false
---

Given that our bare metal hardware is quite limited in memory, disk and CPU power we can run a full flavour database server like PostgreSQL or MySQL but this will leave us with very few resources to run our JAR. Some people might suggest to have a dedicated database server where we run it and this is a good suggestion and in this case means that we need to buy more hardware.

On the other hand, it means that we many more features that we don't actually need. All we need is a simple database that is capable of storing sensor readings and in case we need to replay any events, we can read from it. Given our needs are quite simple and primitive, SQLite3 is a perfect candidate for our use case. Is small, fast as `fopen()` can be and supports CRUD operations. Also requires far fewer server resources and can run on the same device as the JAR, which means that our services will have their own database and not be tight on a shared database server.

When taking decisions on software and hardware we are tempted to choose solutions that have many advantages but we tend to ignore the drawbacks. In this case drawbacks would be dedicated hardware for the database(s) and managing availability, discovery and connectivity to them.

As an event store (the purpose of at least one of the databases) there is a solution specially built for this case []() but we are not going to use it as it cannot be run on our hardware platform.

## Install sqlite3

Now that we are happy to use SQLite3 as our event store is time to install it on the little Raspberry Pi.

`sudo apt-get install sqlite3`

And done, we have an SQLite3 server ready to serve our needs.

## Create a database for the raw events

``` bash
sqlite3 raw_events.db
create table events (id integer, body text, date_received text, ip_address text, sensor_id text);
```

## Drawbacks of SQLite3

### Memory card corruption

SQLite is just a file that seats on our disk. Given that we run our database on an SD card (the default disk for a Raspberry Pi) we expose ourselves to memory card corruption, which might make our db inaccessible. For now we will leave it as it is until we finish our [walking skeleton](). On our list of todos there is an item to make the database resilient to memory card corruption.

### Speed

SQLite3 is as fast as `fopen()` gets. This is good enough for now.
