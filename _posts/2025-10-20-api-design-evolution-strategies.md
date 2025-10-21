---
layout: post
title: "HTTP API Design and Evolution"
tags: [versioning, best-practices, design-patterns, microservices, api]
permalink: /blog/articles/api-design-evolution-strategies/
description: "Comprehensive guide to designing and evolving APIs for long-term success, covering versioning strategies, backward compatibility, and patterns for resilient interface design."
mathjax: false
---

Well-designed APIs are the backbone of modern software systems, enabling integration, scalability, and evolution. This post explores strategies for designing APIs that can evolve gracefully whilst maintaining backward compatibility, focusing on practical patterns and implementation strategies using Java and Spring Boot.

## Why API Design Matters

APIs are contracts between systems. Once published, they become dependencies for clients who integrate with your service. Poor API design decisions made early can haunt teams for years, requiring painful migrations or forcing you to maintain multiple versions indefinitely.

The challenge is to build an API that can adapt to tomorrow's requirements without breaking existing integrations. This requires thoughtful design decisions around structure, versioning, error handling, and documentation.

## API Design Fundamentals

### The Resource-Oriented Approach

{% assign rest_link = site.data.links | where: "id", 64 | first %}
{% assign maturity_link = site.data.links | where: "id", 65 | first %}

REST APIs should model business concepts as resources with clear hierarchies and relationships. Each resource should have a canonical URL, and operations should map to HTTP verbs semantically. This creates intuitive, discoverable APIs that developers can understand without extensive documentation. Roy Fielding's [original REST principles]({{ rest_link.link }}) emphasise hypertext-driven design, whilst the [Richardson Maturity Model]({{ maturity_link.link }}) provides a practical framework for evaluating REST API design.

#### Key principles include:

{% assign http_spec = site.data.links | where: "id", 63 | first %}
{% assign http_status = site.data.links | where: "id", 68 | first %}

- **Resource naming**: Use plural nouns (`/customers`, not `/getCustomers`)
- **Hierarchy**: Express relationships through URL structure (`/customers/{id}/orders`)
- **HTTP verbs**: GET for retrieval, POST for creation, PUT for replacement, PATCH for updates, DELETE for removal
- **Status codes**: Use appropriate [HTTP status codes]({{ http_spec.link }}) (201 for creation, 404 for not found, [422]({{ http_status.link }}) for business rule violations)
- **Idempotency**: Ensure PUT, DELETE, and GET operations are idempotent

### RESTful API Design Example

Consider a customer management API. The resource hierarchy and operations should be immediately clear from the URL structure:

```
POST   /api/v1/customers              # Create a new customer
GET    /api/v1/customers              # List customers (with pagination)
GET    /api/v1/customers/{id}         # Retrieve specific customer
PATCH  /api/v1/customers/{id}         # Update customer
DELETE /api/v1/customers/{id}         # Deactivate customer
GET    /api/v1/customers/{id}/orders  # Get customer's orders
```

```yaml
openapi: 3.0.3
paths:
  /api/v1/customers:
    post:
      summary: Create a new customer
      responses:
        '201':
          description: Customer created successfully
          headers:
            Location:
              description: URI of the created customer resource
              schema:
                type: string
```

**Key aspects of this design:**

1. **Location header**: The 201 response includes a `Location` header pointing to the newly created resource (`/api/v1/customers/{id}`)
2. **Pagination**: List endpoints support `page`, `size`, and `sort` parameters with sensible defaults
3. **Sub-resources**: Related resources are accessed through hierarchical paths (`/customers/{id}/orders`)
4. **Soft deletes**: DELETE returns 204 but marks the customer as inactive rather than removing data

### Using Java Records for DTOs

{% assign records_link = site.data.links | where: "id", 66 | first %}
{% assign lombok_link = site.data.links | where: "id", 40 | first %}
{% assign validation_link = site.data.links | where: "id", 62 | first %}

Modern Java (16+) introduced [Records]({{ records_link.link }}), which provide immutable data carriers perfect for API requests and responses. Records automatically generate constructors, accessors, `equals()`, `hashCode()`, and `toString()` methods, reducing boilerplate whilst enforcing immutability.

For more complex scenarios requiring builder patterns, [Lombok's]({{ lombok_link.link }}) `@Builder` annotation can be applied to Records (requires Lombok 1.18.20+), providing convenient object construction whilst maintaining immutability. The `toBuilder = true` option enables creating modified copies of existing records.

Benefits of using Records for API DTOs:
- **Immutability**: Thread-safe by default, preventing accidental modifications
- **Concise**: Significantly less code than traditional Java beans
- **Type-safe**: Compile-time validation of field access
- **Validation support**: Works seamlessly with [Bean Validation]({{ validation_link.link }}) annotations
- **Builder support**: Lombok integration for complex construction patterns

```java
import lombok.Builder;

// Concise request DTO with validation and builder support
@Builder(toBuilder = true)
public record CreateCustomerRequest(
    @NotBlank @Size(max = 50) String firstName,
    @NotBlank @Size(max = 50) String lastName,
    @Email @NotBlank String email,
    @Valid @NotNull AddressRequest address,
    Map<String, String> metadata
) {}

// Update requests use Optional for partial updates
@Builder(toBuilder = true)
public record UpdateCustomerRequest(
    Optional<String> firstName,
    Optional<String> email,
    Optional<AddressRequest> address
) {}
```

Records with builder support provide the best of both worlds: immutability with convenient construction. The compact constructor can provide default values, whilst the canonical constructor handles validation automatically through annotations.

## API Versioning Strategies

Versioning is inevitable. Business requirements change, new features emerge, and technical improvements demand breaking changes. The question isn't whether to version your API, but how to do it in a way that minimises disruption to clients whilst allowing your service to evolve.

### Choosing a Versioning Strategy

There are three primary approaches to API versioning, each with distinct trade-offs:

1. **URI versioning** (`/v1/customers` vs `/v2/customers`)
2. **Header versioning** (`Accept-Version: 2.0`)
3. **Content negotiation** (`Accept: application/vnd.company+json; version=2`)

The "best" approach depends on your context. URI versioning is explicit and cache-friendly but clutters URLs. Header versioning keeps URLs clean but is less discoverable. Content negotiation is RESTful but complex for clients.

For most teams, **URI versioning offers the best balance of simplicity and explicitness**. It's immediately visible in logs, easy to test with curl, and works seamlessly with HTTP caching.

### URI Versioning

URI versioning embeds the version number directly in the URL path. This makes the version explicit in every request, simplifying debugging and monitoring. When you see `/v1/orders` versus `/v2/orders` in logs, you immediately know which API version handled the request.

The main advantage is clarity. Clients explicitly choose their version, browsers cache responses correctly per version, and API gateways can route traffic based on URL patterns. The downside is URL proliferation, as you end up maintaining multiple endpoints that conceptually represent the same resource.

```yaml
openapi: 3.0.3
info:
  title: Order API - URI Versioning
  version: 2.0.0
  description: API demonstrating URI-based versioning strategy

paths:
  # Version 1 API
  /api/v1/orders:
    post:
      summary: Create an order (V1)
      operationId: createOrderV1
      tags:
        - orders-v1
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateOrderRequestV1'
      responses:
        '201':
          description: Order created successfully
          headers:
            Location:
              schema:
                type: string
              description: URI of the created order
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/OrderResponseV1'

  # Version 2 API with enhanced features
  /api/v2/orders:
    post:
      summary: Create an order with enhancements (V2)
      operationId: createOrderV2
      tags:
        - orders-v2
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateOrderRequestV2'
      responses:
        '201':
          description: Order created successfully with V2 features
          headers:
            Location:
              schema:
                type: string
              description: URI of the created order
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/OrderResponseV2'

# Further content omitted for brevity
```

### Header-Based Versioning

Header-based versioning keeps URLs clean by placing version information in HTTP headers like `Accept-Version` or custom headers. This maintains a single canonical URL per resource whilst allowing different versions to coexist.

The advantage is cleaner URLs and better alignment with REST principles, as the resource identifier remains constant regardless of representation version. However, header-based versioning is less visible in logs, harder to test with simple tools like curl, and can complicate HTTP caching since cache keys must consider headers.

```yaml
openapi: 3.0.3
info:
  title: Order API - Header-Based Versioning
  version: 2.0.0
  description: API demonstrating header-based versioning using Accept-Version header

paths:
  /api/orders:
    post:
      summary: Create an order (version determined by Accept-Version header)
      operationId: createOrder
      tags:
        - orders
      parameters:
        - name: Accept-Version
          in: header
          description: API version to use (1.0 or 2.0, defaults to 1.0)
          required: false
          schema:
            type: string
            enum: ['1.0', '2.0']
            default: '1.0'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              oneOf:
                - $ref: '#/components/schemas/CreateOrderRequestV1'
                - $ref: '#/components/schemas/CreateOrderRequestV2'
      responses:
        '201':
          description: Order created successfully
          content:
            application/json:
              schema:
                oneOf:
                  - $ref: '#/components/schemas/OrderResponseV1'
                  - $ref: '#/components/schemas/OrderResponseV2'
        '400':
          description: Invalid request or unsupported version
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ApiError'
              examples:
                unsupportedVersion:
                  summary: Unsupported API version
                  value:
                    code: UNSUPPORTED_VERSION
                    message: API version 3.0 is not supported
                    supportedVersions: ['1.0', '2.0']

components:
  schemas:
    ApiError:
      type: object
      properties:
        code:
          type: string
        message:
          type: string
        supportedVersions:
          type: array
          items:
            type: string
```

### Content Type Versioning

Content negotiation uses custom media types to specify API versions through the `Accept` and `Content-Type` headers. Instead of `application/json`, clients request `application/vnd.company.order.v2+json`. This is the most RESTful approach, as it treats different versions as different representations of the same resource.

This approach is theoretically elegant but practically complex. Clients must understand media type syntax, server implementations become more sophisticated, and debugging requires inspecting headers. Most teams find the added complexity outweighs the theoretical benefits, making this approach less common in practice.

```yaml
openapi: 3.0.3
info:
  title: Order API - Content Type Versioning
  version: 2.0.0
  description: API demonstrating versioning through custom media types

paths:
  /api/orders:
    post:
      summary: Create an order (version determined by Content-Type)
      operationId: createOrderByContentType
      tags:
        - orders
      requestBody:
        required: true
        content:
          application/vnd.company.order.v1+json:
            schema:
              $ref: '#/components/schemas/CreateOrderRequestV1'
          application/vnd.company.order.v2+json:
            schema:
              $ref: '#/components/schemas/CreateOrderRequestV2'
      responses:
        '201':
          description: Order created successfully
          content:
            application/vnd.company.order.v1+json:
              schema:
                $ref: '#/components/schemas/OrderResponseV1'
            application/vnd.company.order.v2+json:
              schema:
                $ref: '#/components/schemas/OrderResponseV2'
        '415':
          description: Unsupported media type
```

## Backward Compatibility Patterns

Breaking changes force clients to update their code, coordinate deployments, and potentially experience downtime. The cost of breaking compatibility grows exponentially with the number of clients. A single breaking change might require coordinating updates across dozens of services, each with their own deployment schedules and testing requirements.

The principle of backward compatibility is simple: existing clients should continue working without modification when you deploy a new API version. This doesn't mean you can never make breaking changes, but it does mean you need strategies to introduce them gradually whilst supporting existing integrations.

### Additive Changes

The safest way to evolve an API is through additive changes: introduce new fields, endpoints, or capabilities without modifying existing behaviour. Clients that don't use the new features are unaffected, whilst those that need them can adopt incrementally.

When adding fields to responses, ensure they're optional and don't break existing parsing logic. New fields should have sensible defaults so older clients can safely ignore them. Similarly, new request fields should be optional with default values that maintain previous behaviour.

```java
import lombok.Builder;

// Original Customer Response (V1) as Record
@Builder(toBuilder = true)
public record CustomerResponseV1(
    String id,
    String firstName,
    String lastName,
    String email
) {}

// Enhanced Customer Response (V2) - Backward Compatible as Record
@Builder(toBuilder = true)
public record CustomerResponseV2(
    // Existing fields - unchanged
    String id,
    String firstName,
    String lastName,
    String email,

    // New optional fields
    String phoneNumber,
    AddressResponse address,
    CustomerPreferences preferences,

    // New computed fields
    CustomerSegment segment,
    LocalDateTime lastLoginAt
) {}

// Backward-compatible service implementation
```

### Field Deprecation Strategy

Sometimes fields need to be removed or renamed. Rather than deleting them immediately, mark them as deprecated and maintain them alongside their replacements for at least one major version cycle. This gives clients time to migrate whilst receiving clear signals about which fields will be removed.

Use `@Deprecated` annotations in code and mark fields as deprecated in your OpenAPI specification. Include migration guidance in API documentation explaining which fields replace deprecated ones. Monitor usage metrics to understand when it's safe to remove deprecated fields—ideally when they represent less than 1% of total API traffic.

```java
import lombok.Builder;

// Gradual field deprecation using Record with builder
@Builder(toBuilder = true)
public record OrderResponse(
    String orderId,

    // Deprecated field - marked for removal in next major version
    @Deprecated
    @JsonProperty("order_number") // Legacy name
    @Schema(description = "Deprecated: Use orderId instead", deprecated = true)
    String orderNumber,

    String customerId,
    BigDecimal totalAmount,

    // New preferred field
    @JsonProperty("order_status")
    OrderStatus status,

    // Legacy field with migration support
    @Deprecated
    @JsonProperty("status_code")
    @Schema(description = "Deprecated: Use status instead", deprecated = true)
    String statusCode
) {
    // Factory method that maintains compatibility
    public static OrderResponse fromOrder(Order order) {
        return OrderResponse.builder()
            .orderId(order.getId())
            .orderNumber(order.getId()) // Map to deprecated field for compatibility
            .customerId(order.getCustomerId())
            .totalAmount(order.getTotalAmount())
            .status(order.getStatus())
            .statusCode(order.getStatus().getCode()) // Legacy format
            .build();
    }
}
```

### Request Transformation

When introducing breaking changes to request structures, provide adapter layers that transform old request formats into new ones. This allows you to maintain a single implementation internally whilst supporting multiple API versions externally.

Adapters should apply sensible defaults for new required fields, map renamed fields to their new locations, and validate that the transformation preserves the client's intent. This pattern works well when you need to maintain v1 endpoints whilst rolling out v2 with enhanced capabilities.

```java
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Request adapter for backward compatibility
@Component
@RequiredArgsConstructor
public class OrderRequestAdapter {

    public CreateOrderRequestV2 adaptFromV1(CreateOrderRequestV1 v1Request) {
        // Records with builder support enable cleaner adaptation
        return CreateOrderRequestV2.builder()
            .customerId(v1Request.customerId())
            .items(adaptItems(v1Request.items()))
            .priority(OrderPriority.NORMAL)              // Default priority
            .deliveryOptions(DeliveryOptions.standard()) // Default delivery options
            .paymentMethod(PaymentMethod.defaultMethod()) // Default payment method
            .build();
    }

    private List<OrderItemRequestV2> adaptItems(List<OrderItemRequestV1> v1Items) {
        return v1Items.stream()
            .map(item -> OrderItemRequestV2.builder()
                .productId(item.productId())
                .quantity(item.quantity())
                .customization(Collections.emptyMap())  // Default customization
                .giftWrap(false)                        // Default gift wrap
                .build())
            .collect(Collectors.toList());
    }
}
```

## Error Handling and Status Codes

Poor error handling creates frustration and support burden. When errors occur, clients need to understand what went wrong, why it happened, and how to fix it. Generic error messages like "Bad Request" or "Internal Server Error" provide no actionable information.

Well designed error responses include error codes for programmatic handling, human-readable messages for debugging, and context about what failed. This enables clients to implement proper retry logic, display meaningful messages to users, and resolve issues without contacting support.

### Comprehensive Error Response Design

A standardised error response structure should include multiple levels of detail. Error codes enable clients to handle specific scenarios programmatically. Messages provide human-readable explanations for developers debugging issues. Field-level errors help users correct validation problems in forms.

The error response should also include timestamps for correlation with logs, request paths for context, and metadata with additional diagnostic information. This rich error structure transforms opaque failures into debuggable issues.

### Custom Business Exceptions

Business rule violations differ from technical errors. When a customer tries to place an order exceeding their credit limit, that's not a validation error or server error, that's a business rule violation that clients need to handle differently.

Create custom exception types for business rules, each carrying relevant context. An insufficient inventory exception should include the requested quantity, available quantity, and product identifier. This allows clients to present specific, actionable messages to users rather than generic error screens.

## Documentation and Discovery

APIs without documentation are unusable. Even well-designed, RESTful APIs need comprehensive documentation explaining resources, operations, request formats, response structures, error codes, and authentication requirements.

OpenAPI specifications provide machine-readable contracts that generate interactive documentation, client SDKs, and validation tooling. Invest in keeping your OpenAPI specs accurate and comprehensive—they're the single source of truth for your API contract.

### OpenAPI Specification

{% assign openapi_link = site.data.links | where: "id", 57 | first %}
{% assign swagger_link = site.data.links | where: "id", 58 | first %}
{% assign redoc_link = site.data.links | where: "id", 59 | first %}

[OpenAPI]({{ openapi_link.link }}) (formerly Swagger) specifications describe your entire API in a machine-readable format. This enables automatic generation of documentation, client libraries, server stubs, and validation logic. More importantly, it creates a contract that both API providers and consumers can reference.

Maintain your OpenAPI specs as code alongside your implementation. Include detailed descriptions for every operation, comprehensive examples for request and response bodies, and document all possible error responses. Tools like [Swagger UI]({{ swagger_link.link }}) and [ReDoc]({{ redoc_link.link }}) transform these specifications into interactive documentation where developers can explore and test your API directly.

```yaml
openapi: 3.0.3
info:
  title: Customer Management API
  version: 2.0.0
  description: Comprehensive API for managing customer data and relationships
  contact:
    name: API Support
    email: api-support@company.com
    url: https://company.com/support
  license:
    name: MIT
    url: https://opensource.org/licenses/MIT

servers:
  - url: https://api.company.com
    description: Production server
  - url: https://staging-api.company.com
    description: Staging server

security:
  - BearerAuth: []

paths:
  /api/v2/customers:
    post:
      summary: Create a new customer
      description: Creates a new customer account with the provided information
      operationId: createCustomer
      tags:
        - Customer Management
      requestBody:
        description: Customer creation request
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateCustomerRequest'
      responses:
        '201':
          description: Customer created successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CustomerResponse'
              examples:
                customerCreationResponse:
                  summary: Customer creation response
                  value:
                    id: cust_123456
                    firstName: John
                    lastName: Doe
                    email: john.doe@example.com
                    status: ACTIVE
                    createdAt: '2023-01-15T10:30:00Z'
        '400':
          description: Invalid request data
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ApiError'
        '409':
          description: Customer with email already exists
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ApiError'

    get:
      summary: Search customers
      description: Search customers using various criteria with pagination support
      operationId: searchCustomers
      tags:
        - Customer Management
      parameters:
        - name: page
          in: query
          description: Page number
          schema:
            type: integer
            default: 0
        - name: size
          in: query
          description: Page size
          schema:
            type: integer
            default: 20
        - name: sort
          in: query
          description: Sort field
          schema:
            type: string
            default: createdAt
      responses:
        '200':
          description: Search completed successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PagedCustomerSummaryResponse'
        '400':
          description: Invalid search criteria
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ApiError'

# Content excluded for brevity
```

### API Testing and Validation

{% assign spring_contract_link = site.data.links | where: "id", 60 | first %}
{% assign pact_link = site.data.links | where: "id", 61 | first %}

Contract testing ensures your API implementation matches its specification. Rather than testing implementation details, contract tests verify that requests and responses conform to the documented schema. This catches breaking changes before they reach production.

Tools like [Spring Cloud Contract]({{ spring_contract_link.link }}) and [Pact]({{ pact_link.link }}) enable consumer-driven contract testing, where client expectations drive API behaviour. When clients define what they need from your API, you can validate that your implementation satisfies those contracts. This creates a safety net that prevents accidental breaking changes whilst allowing safe evolution of your API.

```java
@SpringBootTest
@AutoConfigureTestDatabase
class CustomerApiContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateCustomerSuccessfully() throws Exception {
        CreateCustomerRequest request = CreateCustomerRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .phoneNumber("+1234567890")
            .address(AddressRequest.builder()
                .street("123 Main St")
                .city("Anytown")
                .postalCode("12345")
                .country("US")
                .build())
            .customerType(CustomerType.INDIVIDUAL)
            .build();

        mockMvc.perform(post("/api/v2/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"))
            .andExpect(jsonPath("$.email").value("john.doe@example.com"))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(header().exists("Location"));
    }

    @Test
    void shouldReturnValidationErrorForInvalidEmail() throws Exception {
        CreateCustomerRequest request = CreateCustomerRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .email("invalid-email")
            .build();

        mockMvc.perform(post("/api/v2/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("email"))
            .andExpect(jsonPath("$.fieldErrors[0].message").value("Email must be valid"));
    }
}
```

## Best Practices Summary

1. **Design for Evolution**: Use extensible patterns from the start
2. **Version Strategically**: Choose versioning strategy based on client needs
3. **Maintain Compatibility**: Implement additive changes when possible
4. **Document Comprehensively**: Provide clear, executable documentation
5. **Handle Errors Gracefully**: Consistent error responses with actionable information
6. **Test Thoroughly**: Include contract testing and backward compatibility tests
7. **Monitor Performance**: Implement caching and monitoring strategies
8. **Have a Deprecation Strategy**: As defined in this [post](/blog/articles/http-api-deprecation-strategy/)

## Conclusion

Successful API design requires balancing immediate functionality needs with long-term evolution requirements. By implementing thoughtful versioning strategies, maintaining backward compatibility, and providing comprehensive documentation, APIs can serve as stable foundations for growing systems.

The key is planning for change from the beginning whilst maintaining simplicity and usability for API consumers.
