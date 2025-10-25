---
layout: post
title: "HTTP API Deprecation Strategy"
tags: [versioning, best-practices, deprecation-strategy, api]
permalink: /blog/articles/http-api-deprecation-strategy/
description: "Comprehensive guide to plan and implement a deprecation strategy for HTTP APIs."
mathjax: false
---

Successfully migrating clients from old to new API versions requires a structured approach balancing technical execution with communication and support. A well-executed migration minimises disruption whilst ensuring all clients eventually adopt improved API versions.

## Monitoring API Version Adoption

Understanding how clients use different API versions is crucial for managing technical debt and planning deprecations. Without visibility into version adoption, you're making decisions blindly about when to sunset old versions or whether new versions meet client needs.

### Metrics to Track

Effective API version monitoring requires tracking multiple dimensions of usage:

1. **Request volume per version**: Total requests to each API version over time
2. **Unique clients per version**: Number of distinct consumers using each version
3. **Error rates by version**: Compare stability across versions to identify problematic releases
4. **Response times by version**: Performance characteristics may differ between versions
5. **Feature adoption**: Track usage of new endpoints or parameters introduced in newer versions

### Migration Dashboards

Create dedicated dashboards that visualise version adoption to inform migration decisions:

#### Key visualisations

- **Version distribution pie chart**: Percentage of traffic by version
- **Version adoption timeline**: Stacked area chart showing version usage over time
- **Client migration status**: Table showing which clients are on which versions
- **Deprecation readiness**: Percentage of clients still on deprecated versions

#### Decision criteria for sunsetting versions

- Less than 5% of total traffic uses the deprecated version
- Less than 3 active clients remain on the version
- All high-value clients have migrated
- At least 6 months notice provided since deprecation announcement
- Alternative versions (current + 1) remain available

### Version Analytics

Beyond operational metrics, track business and product analytics:

- **Feature utilisation**: Which new capabilities in v2 are actually being used?
- **Migration velocity**: How quickly do clients adopt new versions after release?
- **Deprecation compliance**: Client response time to deprecation notices
- **Support burden**: Correlation between version and support ticket volume

This data informs product decisions about which features to invest in and how to structure future API changes for easier adoption.

### The Migration Timeline

A typical API version migration follows a multi-phase approach spanning 12-18 months:

**Phase 1: Development (Months 1-3)**
- Design and implement new API version
- Create comprehensive migration documentation
- Build backward compatibility adapters where possible
- Develop contract tests covering version differences
- Prepare staging environments with both versions

**Phase 2: Beta Release (Months 4-5)**
- Release new version to selected beta clients
- Gather feedback on migration experience
- Refine documentation based on real migration attempts
- Identify and resolve unexpected compatibility issues
- Create migration tooling (scripts, validators, etc.)

**Phase 3: General Availability (Month 6)**
- Announce new version with detailed changelog
- Publish migration guides with code examples
- Offer migration office hours or consultation
- Monitor adoption metrics closely
- Address emerging issues rapidly

**Phase 4: Deprecation Notice (Month 9)**
- Formally deprecate old version with sunset date
- Send personalised migration notices to remaining clients
- Include usage statistics showing their dependency
- Offer dedicated support for complex migrations
- Begin returning deprecation warnings in API responses

**Phase 5: Sunset Preparation (Months 12-15)**
- Escalate communications to clients still on old version
- Offer extended support or custom migration assistance
- Implement strict rate limiting on deprecated endpoints
- Create migration incentives (if applicable)
- Finalise sunset date based on remaining adoption

**Phase 6: Sunset (Month 18)**
- Remove old version endpoints from production
- Redirect old version requests to migration documentation
- Monitor for unexpected dependencies
- Maintain emergency rollback capability for 30 days

### Migration Communication Plan

Effective communication is as critical as technical execution. Clients need advance notice, clear guidance, and ongoing support:

#### Announcement channels

- API changelog and release notes
- Developer newsletter or mailing list
- API status page with migration timeline
- Direct emails to active API clients
- In-dashboard notifications (if applicable)
- Deprecation headers in API responses

#### Documentation requirements

- High-level summary of changes
- Detailed endpoint-by-endpoint comparison
- Code examples showing before/after patterns
- Common migration pitfalls and solutions
- Automated migration tools or scripts
- FAQ addressing typical concerns

### Example Migration Guide Structure

#### Migrating from API v1 to v2

##### Overview
Version 2 introduces improved error handling, enhanced filtering capabilities,
and simplified authentication. Most endpoints remain compatible with minor
request/response format updates.

##### Breaking Changes

###### 1. Authentication
**v1 (Deprecated):**
```http
GET /api/v1/customers
X-API-Key: your-api-key
```

**v2 (Current):**
```http
GET /api/v2/customers
Authorization: Bearer your-jwt-token
```

**Migration:** Exchange your API key for a JWT using the `/auth/token` endpoint.

###### 2. Error Response Format

**v1 (Deprecated):**
```json
{"error": "Customer not found"}
```

**v2 (Current):**
```json
{
  "code": "CUSTOMER_NOT_FOUND",
  "message": "Customer with ID 12345 not found",
  "timestamp": "2024-01-15T10:30:00Z",
  "path": "/api/v2/customers/12345"
}
```

**Migration:** Update error parsing to use structured error codes instead of message strings.

##### New Capabilities

- Pagination on all list endpoints
- Filtering via query parameters
- Partial updates using PATCH
- Webhook notifications

##### Backwards Compatibility

These endpoints remain unchanged:
- GET /customers/{id}
- POST /customers

##### Migration Checklist

- [ ] Update authentication to use JWT
- [ ] Update error handling logic
- [ ] Add pagination support to list operations
- [ ] Test in staging environment
- [ ] Update monitoring and logging
- [ ] Deploy to productions

##### Supporting Parallel Versions

During migration, support both versions simultaneously with minimal code duplication.

This approach ensures a single source of business logic whilst supporting multiple API contracts during transition.

### API Version Lifecycle Diagram

Understanding the complete lifecycle of an API version helps teams plan releases, manage deprecations, and communicate timelines effectively. Below is a visual representation of the typical version lifecycle:

```
API Version Lifecycle
======================

         │
         ▼
    ┌─────────┐
    │  V1.0   │ ◄── Initial Release
    │ Release │     - Full support
    └────┬────┘     - Active development
         │          - All clients on v1
         │
    [6 months]
         │
         ▼
    ┌─────────┐
    │  V2.0   │ ◄── New Version Released
    │ Release │     - V1 remains fully supported
    └────┬────┘     - V2 in beta/GA
         │          - Clients begin migrating
         │
         ├──────────────────┐
         │                  │
         ▼                  ▼
    ┌─────────┐        ┌─────────┐
    │  V1.0   │        │  V2.0   │
    │Supported│        │ Active  │
    └────┬────┘        └────┬────┘
         │                  │
    [3 months]              │
         │                  │
         ▼                  │
    ┌─────────┐             │
    │  V1.0   │ ◄── Deprecation Announced
    │Deprecated│    - Sunset date set (12 months)
    └────┬────┘    - Warnings added to responses
         │         - Migration support offered
         │              │
    [6 months]          │
         │              │
         ▼              ▼
    ┌─────────┐    ┌─────────┐
    │  V1.0   │    │  V2.0   │
    │Deprecated│   │ Active  │ ◄── Majority of traffic
    └────┬────┘    └────┬────┘     on v2 (>80%)
         │              │
    [3 months]          │
         │              │
         ▼              │
    ┌─────────┐         │
    │  V1.0   │ ◄── Sunset Warning (30 days)
    │ Sunset  │    - Rate limiting applied
    │ Warning │    - Direct client outreach
    └────┬────┘    - Final migration push
         │              │
    [1 month]           │
         │              │
         ▼              │
    ┌─────────┐         │
    │  V1.0   │ ◄── Version Sunset
    │ Removed │    - Endpoints return 410 Gone
    └─────────┘    - Redirect to migration docs
                        │
                   [12 months]
                        │
                        ▼
                   ┌─────────┐
                   │  V3.0   │ ◄── Next Version Released
                   │ Release │     - Cycle repeats
                   └─────────┘     - V2 remains supported


Version States Legend:
======================
┌──────────┐
│  Active  │  Full support, active development, new features
└──────────┘

┌──────────┐
│Supported │  Maintenance mode, bug fixes only, no new features
└──────────┘

┌──────────┐
│Deprecated│  Discouraged use, sunset date announced, limited support
└──────────┘

┌──────────┐
│  Sunset  │  No longer available, returns errors or redirects
└──────────┘


Timeline Characteristics:
=========================
- Active Support Period:     12-18 months
- Deprecation Period:        6-12 months
- Sunset Warning Period:     1-3 months
- Total V1 Lifespan:         18-24 months
- Parallel Version Support:  12-15 months (V1 + V2 simultaneously)
```

### Best Practices

- Announce deprecation minimum 6 months before sunset
- Maintain at least 2 versions simultaneously (current + previous)
- Never sunset with >5% traffic remaining on old version
- Provide automated migration tools where possible
- Monitor adoption metrics throughout lifecycle
- Communicate timeline changes immediately

This lifecycle ensures clients have adequate time to migrate whilst preventing indefinite support of outdated versions. Adjust timelines based on your client base, complexity of changes, and organisational capacity for supporting parallel versions.
