
# Centralised Management For Message Schemas 

<hr />

A centralized place for message schema management in a software company is a dedicated system, repository, or service where all message schemas used across different services and applications are stored, managed, and versioned. 

This central repository serves as the single source of truth for all message schemas and promotes consistency, reusability, and maintainability.

---

## Why a Centralised System?

<hr />

* Having a centralized place for message schema management promotes efficiency, reliability, and maintainability in a software company's architecture and operations.
* General characteristics of a centralised message schema management system:
---

### Schema Storage

<hr />

* A place to store message schemas, usually in various formats like JSON, XML, or YAML.
* Such a tool can be GitHub, GitLab, BitBucket etc.

---

### Versioning

<hr />

* Support for versioning to track changes and manage schema evolution over time.
* Enables teams to manage changes to message schemas effectively and ensure backward compatibility.
* Such tools can be `git` for source code versioning and GitHub for hosting our repository.
* Semantic versioning can be used to track evolution of the message payload over time. Eg. `1.1.0`.

---

### Validation

<hr />

* Centralized schema management allows for consistent validation of incoming and outgoing messages.
* Tools and libraries exist to validate messages against the defined schemas for compliance and data integrity.

---

### Documentation

* Centralized schema management provides a single point of reference for documentation, making it easier for developers to understand message structures and use them correctly.
* A single repository provides comprehensive documentation for all message schemas, facilitating understanding and usage.
* Comprehensive documentation for each schema to aid developers in understanding and using them correctly.
* AsyncAPI allows a reach description for each message and channel.

---

### Integration

<hr />

* When integrating with external partners or third-party systems, a centralized schema repository streamlines data exchange.
* A centralised schema management system reduces integration complexities.

---

### Scalability

<hr />

* As the company grows and introduces more services, having a central repository for message schemas streamlines the onboarding process and ensures new services and messages adhere to existing standards.

---

### Maintenance

<hr />

* Centralization makes it easier to update and maintain message schemas, as changes can be made in one place and propagated across all relevant services.

---

### Communication

<hr />

* It simplifies communication between different teams by providing a clear and standardized format for messages, making it easier to understand and process data.

---

### Consistency

<hr />

* It ensures that all teams and services within the company use the same message schema, promoting consistency and preventing data discrepancies.

---

## How to Create a Centralised Management System?

<hr />

* By following these steps, you can create an efficient and centralized place for message schema management in your software company, fostering consistency, collaboration, and scalability across your systems.

---

### Define Requirements

<hr />

* Understand the needs of your software company. Identify the types of messages, data formats, and standards that need to be managed centrally.
* Define the _must_, _must not_, _should_ and _should not_.
* A list of _must_ can be: _Each message *must* have a set of predefined metadata_.

---

### Choose a Schema Format

<hr />

* Decide on a schema format that suits your company's requirements. Common choices include JSON Schema, Protocol Buffers, Avro, or XML Schema.

---

### Choose a Schema Standard

<hr />

* A common standard is AsyncAPI, that's based on OpenAPI. Supports JSON, YAML and Avro format.

---

### Setup a Version Control System

<hr />

* Use a version control system (e.g., Git) to manage message schema files. This ensures proper versioning and easy collaboration among team members.

---

### Design the Schema Repository

<hr />

* Create a dedicated repository or directory within your version control system to store all message schema files.

---

### Organise Schemas

<hr />

* Organize the schemas into logical folders or categories based on message types or domains for easier management and maintenance.
* Organising by domains will allow defining domain boundaries easier and will allow duplicate message names as they belong to different domains.

---

### Establish Governance and Access Control

<hr />

* Define roles and permissions for accessing and modifying the schemas to ensure data integrity and security.
* Usually the system hosting your repository allows role based access such that only owners can modify schemas they own and they can only propose changes to other schemas they don't own.
* Changes can be proposed via pull requests or merge requests.

---

### Develop Validation Tools

<hr />

* Create validation tools or libraries that allow developers to validate incoming and outgoing messages against the centralized schemas.
* Several open source tools exist that allow message payload validation against JSON schema.

---

### Implement CI/CD Pipelines

<hr />

* Set up continuous integration and continuous deployment (CI/CD) pipelines to automate schema validation and deployment processes.
* GitLab and GitHub both offer hosting for `git` repositories and CI/CD pipelines that can be configured to provide schema validation and automatic deployment/delivery.

---

### Documentation

<hr />

* Provide comprehensive documentation for each schema, including usage guidelines and examples to facilitate understanding and adoption.
* Certain tools allow to do in-browser validation for message payloads.

---

### Integrate with Services

<hr />

* Update existing services and applications to integrate with the centralized schema management system. Ensure that all services use the validated schemas.

---

### Handling Schema Evolution

<hr />

* Plan for schema evolution to accommodate changes in data requirements over time. 
* Consider backward compatibility and deprecation strategies.
* Start adding schemas for messages that already exist and don't have a schema already.
* Start evolving existing messages to comply with the mandatory fields required by the new system.

---

### Train Teams

<hr />

* Conduct training sessions to familiarize development teams with the centralized schema management process and its importance.

---

### Monitor and Maintain

<hr />

* Continuously monitor the usage of schemas and regularly review and update them to reflect changing business needs.

---


