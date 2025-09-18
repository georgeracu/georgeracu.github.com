---
layout: post
title: A Collection of Steps to Follow when Developing Distributed Software Systems
permalink: /blog/articles/steps-to-do-when-developing-distributed-software-systems/
tags: [post, distributed-systems, software-development]
mathjax: false
description: This is a collection of topics and how-to's that I gathered during years of software development. Practices and processes to follow when developing distributed software systems and not only.
---

## Why do we need such a list of topics?

Building distributed software systems is inherently complex. Unlike monolithic applications where components communicate through in-memory calls, distributed systems introduce network communication, partial failures, eventual consistency, and coordination challenges that can catch even experienced developers off guard.

This collection serves as a practical checklist - a set of battle-tested practices that help teams avoid common pitfalls and build systems that are reliable, maintainable, and scalable from day one. Rather than learning these lessons through painful production incidents, teams can proactively adopt these patterns and processes.

The goal isn't perfection from the start, but establishing a foundation that supports iterative improvement and reduces the likelihood of architectural debt that becomes expensive to fix later.

## What kind of software have I built where I used these patterns?

In this post I am focussing on distributed software systems - applications composed of multiple services that communicate over a network to deliver business value. This includes:

- Microservices architectures: where business capabilities are decomposed into independent services
- Event-driven systems: that react to and produce events across service boundaries
- Cloud-native applications: designed to leverage cloud platform capabilities
- API-first systems: where services expose well-defined interfaces

These systems share common characteristics: they're deployed across multiple processes/machines, they communicate over unreliable networks, they need to handle partial failures gracefully, and they require coordination between autonomous components.

The practices in this guide apply whether you're building a greenfield system or evolving an existing monolith towards a more distributed architecture. Brownfield development applies as well, where you might have missed some of these steps when you started development and you are already in production and fixing issues.

## Topics to discuss

- Default hosting platform is cloud (choose your poison: AWS, Azure, GCP etc.)
- All code is hosted in git
- Use a cloud hosting tool for your git repository: GitHub, GitLab etc.
- Building and deploying code is automated through CI/CD, not manually by Ops (use GitLab pipelines, GitHub Actions, ArgoCD, FluxCD etc.)
- The application runs on local development computers by mocking external dependencies
- Infrastructure as Code not click ops
- Code quality and code coverage monitoring (SonarQube/SonarCloud)
- Linting and code style enforcement
- AI coding assistant adoption
- Health checks and readiness probes are mandatory (see Kubernetes probes for readiness and liveness)
- Structured logging with proper log levels and formats
- API contracts are explicitly defined (OpenAPI specs, AsyncAPI specs)
- Monitoring and alerting infrastructure (DataDog, NewRelic etc.)
- Automated testing at multiple levels:
  - Unit testing
  - Component integration testing
  - End-to-end integration testing
- Configuration is externalised and environment-specific (see Helm and Helm files)
- Secrets management is automated (Hashicorp Vault, Mozilla SOPS etc.)
- Security scanning is automated in CI/CD pipelines (SAST and DAST scanners)
- Schema registry with contract validation and client generation
- Run books and play books for operational procedures
- Database migrations are automated and version-controlled (no manual interventions)
- Observability is built-in: distributed tracing, metrics collection
- Circuit breakers and retry policies are implemented for resilience (see Resilience4J)
- Service discovery and load balancing are automated
- Graceful shutdown handling is implemented
- Resource limits and quotas are defined and enforced
- Dependency management and vulnerability scanning is automated (see Dependabot, RenovateBot etc.)
- Documentation is code-adjacent and kept up-to-date
- API versioning strategy is defined and consistently applied (see SemVer)
- Data backup and disaster recovery procedures are automated
- Performance testing is integrated into the development cycle (see Gatling etc.)
- Capacity planning is data-driven with proper metrics
- Error handling and dead letter queues are implemented
- Service mesh or similar networking infrastructure is considered for service-to-service communication
- Performance optimisation based on metrics and system monitoring
- Security hardening practices
- Architectural evolution through continuous analysis of system usage and performance behaviour

## Implementation Priorities

Not everything needs to be implemented from day one, even though the earlier you add them, the better it is later. Depending on the type of the project, if an MVP is not required and you know what you are building (these cases exist) then you start with setting the foundation right and allow for the development teams to iterate faster. When you don't know exactly what you are building, or you don't have the right financing in place, you might start with the minimum implementation and build from there. Here's a suggested order when you don't know what you are building (you need an MVP for testing your market-fit):

### 1. Preparing for the Walking Skeleton

- Git repository with cloud hosted git repo: GitHub, GitLab etc.
- CI/CD either with the git hosting provider (GitLab pipelines, GitHub Actions etc. or using a dedicated CI/CD server or a hybrid approach);
- Local development environment;
- Code quality and code coverage monitoring: SonarQube/SonarCloud
- Testing strategy
  - Unit tests: JUnit5
  - Integration tests: TestContainers, Docker, LocalStack etc.
  - Architectural tests using ArchUnit
- Linting and code style
- AI coding assistant adoption
- Use a "monolith first" pattern, as described by {% assign link = site.data.links | where: "id", 10 | first %}[{{ link.author }} in {{ link.title }}]({{ link.link }})
- Don't build a distributed monolith (one of the very few "don't do it" items)

### 2. A basic solid foundation

- Basic health checks;
- Structured logging;
- Infrastructure as Code basics;
- API contracts (OpenAPI, AsyncAPI)

### 3. Getting ready for production

- Monitoring and alerting: DataDog, NewRelic etc.
- Automated testing
  - Unit testing
  - Component integration testing
  - e2e integration testing
- Configuration management: application configuration files with variable replacement and separated by environment (Helm and Helm files etc.);
- Secrets management: Hashicorp Vault, Mozilla SOPS etc.
- Basic security scanning: SAST and DAST scanners;
- API contracts (schema registry with contract validation and client generation);
- Run books and play books;
- Automatic database migrations;

### 4. Operational maturity (stabilisation phase)

- Distributed tracing
- Circuit breakers
- Service contracts
- Database migrations
- Disaster recovery

### 5. Evolution and continuous improvement (Kaizen)

- Performance optimisation: requires good metrics and system monitoring in place
- Security hardening
- Documentation updates
- Capacity planning refinements
- Architectural evolution: continuous analysis of the system usage and performance behaviour, coupled with product evolution

### 6. Nice to have

- Automatic dependency management (upgrades): see Dependabot or RenovateBot

### 7. Development practices

- Do trunk based development;
- Have 3 development environments: `development`, `staging` and `production`;
- Have a dedicated performance testing environment that mimicks production;
- Do code reviews (human and AI assisted);
- Treat test code as production code;
- Use commit templates: make sure that your work item number is referenced in every commit (Jira task, GitHub issue etc.)
- Use `Co-authored-by` when doing pair programming or using an AI coding assistant;
- Use semantic versioning as described in [SemVer](https://semver.org/);

Future blog posts will explain each phase in more detail.
