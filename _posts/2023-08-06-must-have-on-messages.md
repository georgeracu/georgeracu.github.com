---
layout: post
title: "Must Have on Message Payload"
date: 2023-08-06 07:00:00 +0000
tags: [programming, message, asyncapi]
description: Essential guide to defining mandatory fields and metadata standards for message payloads in domain-driven messaging systems, covering consistency rules, structure requirements, and best practices for distributed communication.
mathjax: false
---

Having some strict rules on the _must, must not, should and should not_ will allow each domain to define messages that are consistent in metadata and structure and guarantees that the same metadata is present on each message that's published inside that domain.

In the context of this article, we will define and use:

* Publisher - the service, application, software component that's publishing a message.
* Consumer - a service, application, software component that's consuming a message.
* Message - a letter being sent via a messaging mechanism from a publisher to at least one consumer. We will not distinguish between _notification_, _command_ and _message_ as both _notification_ and _command_ are considered to be a _message_ in this context. 

### Must Have

#### Version

Message version should be defined using [semantic versioning](https://semver.org/). Given a version number MAJOR.MINOR.PATCH, increment the:

* MAJOR version when you make incompatible API changes
* MINOR version when you add functionality in a backward compatible manner
* PATCH version when you make backward compatible bug fixes

Additional labels for pre-release and build metadata are available as extensions to the MAJOR.MINOR.PATCH format.

Eg. `1.0.0`.

#### Schema

Location where to find the schema used to define and validate each message from our domain. Location _must_ point to the repository where we keep all our schemas for all messages in our domain. Location _should_ be an absolute path. File names with extension are also acceptable.

#### Data

This field is the "bag" holding business data that's shared by this message and that's of interest for consumers of this message. A message _must_ not be sent if this field is empty or null.

#### Id

A unique identifier for each message that's published. It _must_ be a random generated UUID v.4.

#### Correlation Id

A unique identifier that allows messages bellonging to the same business process to be grouped together. Eg. `order-placed`.

#### Source

The publisher of this message. It _must_ be the service, application or what other publisher this message might have. Eg. `order-confirmation-service`.

#### Type

The message type identified by namespace. It _must_ be a full namespace. Eg. `com.georgeracu.messages.order.confirmation.OrderPlaced`.

#### Created At

The Date Time when this message was created. It _must_ be in UTC, with nanosecond precision. It _must_ be populated when the message is initially created. If immutability is used, the initial value should be carried accross all copies of the initial object.

#### Published At

The Date Time when this message was published. It _must_ be in UTC, with nanosecond precision. It _must_ be populated before the message is published by the publisher.

#### Example Schema

#### Example Payload

```json
{
    "id": "5856bc07-735b-410a-baa4-b96d70745fd9",
    "version": "1.1.0",
    "schema": "",
    "correlationId": "order-placed",
    "source": "order-confirmation-service",
    "type": "com.georgeracu.messages.order.confirmation.OrderPlaced",
    "createdAt": "",
    "publishedAt": "",
    "data": {
        "customerId": "5856bc07-735b-410a-baa4-b96d70745fd9",
        "orderId": "5856bc07-735b-410a-baa4-b96d70745fd9"
    }
}
```


