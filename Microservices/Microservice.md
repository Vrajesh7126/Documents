# Microservices with Java & Spring Boot — Complete Revision Notes

> Fast-paced but complete revision guide for Java/Spring Boot microservices interviews.
>
> **Core mental model:** Microservices are independently deployable services organized around business capabilities, communicating through APIs/events, with independent scaling, resilience, observability, and deployment.

---

## 1. What Is a Microservice?

A **microservice** is a small, independently deployable application/service that focuses on a specific business responsibility.

Example:

```text
E-commerce System

User Service          -> Users
Product Service       -> Products
Order Service         -> Orders
Payment Service       -> Payments
Notification Service  -> Email/SMS
Inventory Service     -> Stock
```

The important idea is not simply "small classes" or "small applications."

A good microservice is generally:

- Focused on a business capability
- Independently deployable
- Independently scalable
- Loosely coupled
- Highly cohesive
- Owned around a clear domain boundary
- Responsible for its own data

### Simple interview answer

> Microservices is an architectural style where an application is divided into independently deployable services, with each service responsible for a specific business capability.

---

# 2. Monolith vs Microservices

## Monolith

A monolithic application contains multiple business capabilities inside one deployable application.

```text
                 Monolithic Application
        +-----------------------------------+
        | User + Order + Payment + Inventory|
        +-----------------------------------+
                       |
                    One DB
```

### Problems that can appear as the system grows

- Large codebase
- Difficult deployments
- One component can affect the whole application
- Scaling one feature may require scaling the whole application
- Tight coupling between modules
- Large development teams can interfere with each other

## Microservices

```text
User Service       -> User DB
Order Service      -> Order DB
Payment Service    -> Payment DB
Inventory Service  -> Inventory DB
```

Each service can be developed, deployed, and scaled independently.

### Important nuance

Microservices are not automatically better than a monolith.

They introduce additional complexity:

- Network communication
- Distributed transactions
- Distributed debugging
- Deployment complexity
- Service discovery
- Observability
- Eventual consistency
- Failure handling

A modular monolith can be a better choice for some systems.

---

# 3. What Makes a Good Microservice?

Think:

```text
One business responsibility
        +
High cohesion
        +
Loose coupling
        +
Independent deployment
        +
Independent scaling
        +
Clear ownership
```

### Bad boundary

```text
PaymentService
    -> User
    -> Order
    -> Payment
    -> Email
    -> Inventory
```

### Better boundaries

```text
UserService       -> User functionality
OrderService      -> Order functionality
PaymentService    -> Payment functionality
NotificationService -> Notification functionality
InventoryService  -> Inventory functionality
```

Do not split services simply because a class becomes large.

Split around **business/domain boundaries**.

---

# 4. Bounded Context

A microservice often maps to a **bounded context** from Domain-Driven Design (DDD).

A bounded context defines:

> Where a particular domain model and its terminology have a specific meaning.

For example:

```text
Order Context
    Order
    OrderItem
    OrderStatus

Payment Context
    Payment
    Transaction
    PaymentStatus
```

The word "Customer" might have different meanings or data requirements in different contexts.

### Interview point

> Service boundaries should preferably follow business capabilities and domain boundaries rather than technical layers.

---

# 5. High Cohesion

**High cohesion** means related functionality stays together.

Good:

```text
Payment Service
    -> Make payment
    -> Refund
    -> Payment status
    -> Payment validation
```

Bad:

```text
Payment Service
    -> Payment
    -> User
    -> Inventory
    -> Email
```

Easy memory:

> High cohesion = things that belong together stay together.

---

# 6. Loose Coupling

**Loose coupling** means one service should know as little as possible about another service's internal implementation.

Bad:

```text
Order Service
      |
      +---- directly accesses ----> Payment DB
```

Better:

```text
Order Service
      |
      | API / Event
      v
Payment Service
      |
      v
Payment DB
```

Order Service should not depend on Payment Service's internal tables.

---

# 7. Database per Service

A common microservices principle is:

```text
User Service       -> User DB
Order Service      -> Order DB
Payment Service    -> Payment DB
```

Instead of:

```text
                Shared DB
              /    |    \
             /     |     \
          User   Order  Payment
```

### Why?

It gives services greater autonomy.

A service can:

- Change its schema independently
- Choose an appropriate data technology
- Deploy independently
- Control its own data

### Important

"Database per service" does not always mean every service must have a physically separate database server.

The important architectural rule is:

> Other services should not directly own or manipulate another service's data.

Access should happen through the owning service's API/events.

---

# 8. Polyglot Persistence

Different services may use different databases when there is a good reason.

Example:

```text
Order Service      -> PostgreSQL
Product Search     -> Elasticsearch
Cache              -> Redis
Analytics          -> Data Warehouse
```

Do not use different databases just for the sake of using different technologies.

---

# 9. How Microservices Communicate

There are two major styles.

## Synchronous Communication

Service A calls Service B and waits for a response.

```text
Order Service
      |
      | HTTP
      v
Payment Service
      |
      | response
      v
Order Service
```

Common technologies:

- REST
- gRPC
- HTTP clients
- OpenFeign

Spring Boot/Spring ecosystem examples:

- `RestClient`
- `WebClient`
- Spring Cloud OpenFeign

### Advantages

- Simple request/response model
- Immediate result
- Easy to understand

### Disadvantages

- Services become runtime-dependent on each other
- Latency accumulates
- Failure can propagate
- Cascading failures are possible

---

## Asynchronous Communication

Service A publishes a message/event and does not need an immediate response.

```text
Order Service
      |
      | OrderCreated
      v
    Kafka
    /   \
   v     v
Payment  Notification
```

Common technologies:

- Apache Kafka
- RabbitMQ
- Other messaging systems

### Advantages

- Loose runtime coupling
- Better resilience in some workflows
- Good for event-driven architecture
- Consumers can process independently

### Disadvantages

- More complex debugging
- Eventual consistency
- Duplicate messages must be handled
- Ordering/retry/error handling must be designed

---

# 10. REST API Basics

Spring Boot commonly exposes REST APIs using `@RestController`.

```java
@RestController
@RequestMapping("/orders")
public class OrderController {

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }
}
```

Example request:

```text
GET /orders/101
```

Example response:

```json
{
  "id": 101,
  "status": "CREATED"
}
```

### HTTP Methods

```text
GET     -> Read
POST    -> Create
PUT     -> Replace/update a resource
PATCH   -> Partial update
DELETE  -> Delete
```

---

# 11. HTTP Status Codes

Know these:

```text
200 OK
201 Created
204 No Content

400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
422 Unprocessable Content (where applicable)

500 Internal Server Error
502 Bad Gateway
503 Service Unavailable
504 Gateway Timeout
```

### 401 vs 403

**401 Unauthorized** generally means authentication is missing/invalid.

**403 Forbidden** generally means the user is authenticated but is not allowed to perform the operation.

---

# 12. API Gateway

Instead of the client directly calling every service:

```text
Client
  |----> User Service
  |----> Order Service
  |----> Payment Service
  |----> Product Service
```

Use:

```text
                 API Gateway
                     |
       +-------------+-------------+
       |             |             |
       v             v             v
     User          Order         Payment
```

The API Gateway is commonly the **single entry point** for external clients.

### Responsibilities can include

- Routing
- Authentication
- Authorization integration
- Rate limiting
- Request filtering
- CORS handling
- Header manipulation
- Logging/observability
- TLS termination
- Aggregation in some designs

### Spring option

**Spring Cloud Gateway**

---

# 13. Service Discovery

Problem:

```text
Order Service -> Where is Payment Service?
```

Hardcoding:

```text
http://10.10.2.15:8080
```

is fragile because service instances can move or scale.

Service discovery solves this.

```text
             Service Registry
             /      |       \
            /       |        \
         Order    Payment    User
```

Services register themselves, and clients/gateways can discover available instances.

Examples:

- Eureka
- Consul
- Kubernetes service discovery

### Interview answer

> Service discovery dynamically resolves the network location of service instances so clients do not need to hardcode hostnames and ports.

---

# 14. Client-Side vs Server-Side Discovery

## Client-side discovery

The client asks a registry and chooses an instance.

```text
Client -> Registry -> choose instance -> Service
```

## Server-side discovery

A router/load balancer handles discovery.

```text
Client -> Load Balancer -> Service instance
```

In Kubernetes, service discovery is commonly handled by the platform.

---

# 15. Load Balancing

Suppose Payment Service has three instances:

```text
Payment-1
Payment-2
Payment-3
```

Requests can be distributed:

```text
Request 1 -> Payment-1
Request 2 -> Payment-2
Request 3 -> Payment-3
```

This improves:

- Scalability
- Availability
- Resource utilization

### Types

**Client-side load balancing**

The client selects an instance.

**Server-side load balancing**

A load balancer selects an instance.

---

# 16. Configuration Management

Do not hardcode environment-specific values:

```java
String url = "http://localhost:8082";
```

Externalize configuration:

```properties
payment.service.url=http://payment-service
```

Possible sources:

- `application.properties`
- `application.yml`
- Environment variables
- Spring Cloud Config
- Kubernetes ConfigMap
- Kubernetes Secret
- Secret/configuration management platforms

### Why?

Development:

```text
payment.service.url=http://localhost:8082
```

Production:

```text
payment.service.url=http://payment-service
```

The application should not need code changes just to switch environments.

---

# 17. Spring Profiles

Spring Profiles allow environment-specific configuration.

Example:

```text
application.yml
application-dev.yml
application-test.yml
application-prod.yml
```

Run with:

```text
spring.profiles.active=prod
```

Use profiles for environment-specific behavior/configuration, while keeping secrets in a proper secret-management mechanism.

---

# 18. Synchronous HTTP Clients in Spring

## RestClient

Modern synchronous HTTP client in Spring.

Conceptually:

```text
Request -> Wait -> Response
```

Use when a straightforward blocking request/response model is appropriate.

---

## WebClient

Spring's HTTP client that supports reactive/non-blocking programming.

Conceptually:

```text
Request -> continue work -> response later
```

Do not simply say:

> WebClient is always faster.

Performance depends on workload, architecture, connection management, downstream behavior, and whether the application is actually using a non-blocking/reactive model effectively.

---

# 19. OpenFeign

Spring Cloud OpenFeign lets you define a declarative HTTP client.

Example:

```java
@FeignClient(name = "payment-service")
public interface PaymentClient {

    @GetMapping("/payments/{id}")
    Payment getPayment(@PathVariable Long id);
}
```

Then:

```java
paymentClient.getPayment(101);
```

### Advantage

You define the API as an interface instead of manually constructing every HTTP request.

---

# 20. REST vs gRPC

## REST

- HTTP-based
- Commonly JSON
- Easy for browsers/public APIs
- Widely understood

## gRPC

- Commonly HTTP/2
- Protocol Buffers
- Strongly typed contracts
- Efficient for service-to-service communication
- Supports streaming

Simple interview answer:

> REST is common for external/public APIs and general HTTP communication, while gRPC can be useful for efficient strongly typed internal service-to-service communication.

---

# 21. Kafka Basics

Kafka is commonly used for event-driven microservices.

Example:

```text
Order Service
     |
     | OrderCreated
     v
   Kafka
   /   \
  v     v
Payment Notification
```

Important Kafka concepts:

```text
Producer       -> Sends records
Consumer       -> Reads records
Topic          -> Logical stream/category
Partition      -> Ordered unit of a topic
Offset         -> Position of a record in a partition
Consumer Group -> Consumers cooperating to process a topic
Broker         -> Kafka server
```

---

# 22. Kafka Topic

Example:

```text
order-events
```

Events can be published to it:

```text
OrderCreated
OrderCancelled
OrderShipped
```

A topic is divided into partitions:

```text
order-events
  |
  +-- Partition 0
  +-- Partition 1
  +-- Partition 2
```

---

# 23. Kafka Partition and Ordering

Kafka guarantees ordering **within a partition**, not globally across all partitions.

If events for the same order must remain ordered, use a suitable key:

```text
key = orderId
```

Kafka can then route records with the same key to the same partition, preserving their order within that partition.

---

# 24. Kafka Consumer Group

Example:

```text
Topic: order-events

Consumer Group: payment-group

Consumer-1
Consumer-2
Consumer-3
```

Partitions are distributed among consumers in the group.

A partition is processed by only one consumer in a given consumer group at a time.

Different consumer groups can independently consume the same topic:

```text
order-events
   |
   +---- payment-group
   |
   +---- notification-group
   |
   +---- analytics-group
```

---

# 25. Kafka Consumer Offset

The offset represents a record's position in a partition.

Conceptually:

```text
Partition 0

Offset: 0  1  2  3  4
        A  B  C  D  E
```

The consumer tracks its progress.

Offsets are important for:

- Restart/recovery
- Reprocessing
- Delivery semantics

---

# 26. Kafka Delivery Semantics

Common concepts:

### At-most-once

A message may be lost, but is not normally processed more than once.

### At-least-once

A message is not intentionally lost, but duplicates can occur.

This is common in real-world systems.

### Exactly-once

Achieving true end-to-end exactly-once effects is more complicated than simply enabling one Kafka setting.

For business operations, idempotent consumers and transactional design are often still important.

---

# 27. Kafka Retry and Dead Letter Topic

If a consumer cannot process a message:

```text
Kafka
  |
  v
Consumer
  |
  X
  |
 Retry
  |
  X
  |
Dead Letter Topic
```

A **DLT/DLQ** can store messages that cannot be processed after configured attempts, allowing investigation or later recovery.

---

# 28. Event vs Command

A useful distinction:

### Event

> Something already happened.

Example:

```text
OrderCreated
PaymentCompleted
```

### Command

> Someone is asking a service to perform an action.

Example:

```text
CreateOrder
ProcessPayment
```

Events are often phrased in the past tense.

---

# 29. Event-Driven Architecture

Instead of direct calls:

```text
Order -> Payment
Order -> Notification
```

Order can publish an event:

```text
Order
  |
  | OrderCreated
  v
Event Broker
  |--------->
  |         Payment
  |
  +--------> Notification
```

This can reduce direct coupling between producers and consumers.

---

# 30. Resilience and Fault Tolerance

Microservices fail independently.

Suppose:

```text
Order Service -> Payment Service
```

Payment goes down.

If Order keeps waiting/retrying forever:

```text
Request
  |
Timeout
  |
Retry
  |
Timeout
  |
Retry
  |
More requests
  |
Cascading failure
```

Use resilience patterns:

```text
Timeout
Retry
Circuit Breaker
Bulkhead
Fallback
Rate Limiting
```

---

# 31. Timeout

Never allow a remote dependency to wait forever.

Example:

```text
Order -> Payment
          |
       2 seconds
          |
       Timeout
```

After the timeout, the caller takes an appropriate failure path.

---

# 32. Retry

Useful for transient failures.

```text
Request
   |
 Failed
   |
 Retry
   |
 Success
```

But don't retry blindly.

Retry may be appropriate for:

- Temporary network failure
- Transient service unavailable condition
- Short-lived infrastructure problem

Retry may be inappropriate for:

- Invalid request
- Authentication failure
- Validation failure
- Permanent business error

Use bounded retries and usually backoff/jitter.

---

# 33. Exponential Backoff

Instead of retrying immediately:

```text
Retry 1 -> 100ms
Retry 2 -> 200ms
Retry 3 -> 400ms
Retry 4 -> 800ms
```

Actual values depend on system requirements.

**Jitter** adds randomness to avoid many clients retrying at exactly the same time.

---

# 34. Circuit Breaker

Circuit Breaker prevents repeatedly calling an unhealthy dependency.

Example:

```text
Order -> Payment
          X
```

If Payment repeatedly fails:

```text
Order -> Circuit Breaker -> Payment
```

The circuit opens:

```text
Order -> Circuit Breaker -> DON'T CALL Payment
```

This allows the failing service time to recover and protects the caller.

---

# 35. Circuit Breaker States

```text
CLOSED
  |
  | failures exceed threshold
  v
OPEN
  |
  | wait for configured duration
  v
HALF-OPEN
  |
  +---- success ----> CLOSED
  |
  +---- failure ----> OPEN
```

### CLOSED

Requests flow normally.

### OPEN

Calls are rejected/short-circuited.

### HALF-OPEN

A limited number of test calls are allowed to determine whether the dependency recovered.

---

# 36. Resilience4j

A popular library for Spring Boot applications.

Common modules/patterns:

- Circuit Breaker
- Retry
- Rate Limiter
- Bulkhead
- Time Limiter

Conceptually:

```java
@CircuitBreaker(name = "paymentService")
public Payment pay(Order order) {
    // remote call
}
```

The exact configuration depends on the application.

---

# 37. Bulkhead Pattern

Think of a ship:

```text
+---------+---------+---------+
| Section | Section | Section |
+---------+---------+---------+
```

If one section floods, the entire ship should not necessarily sink.

In software:

```text
Payment requests -> limited resources
Order requests   -> separate/limited resources
```

If Payment becomes slow, it should not consume all application resources.

That is the **Bulkhead pattern**.

---

# 38. Fallback

If a dependency is unavailable, the application may provide an alternative response.

Example:

```text
Product Service
      |
Recommendation Service X
      |
Fallback -> return popular products
```

Fallback should be meaningful and safe.

Do not hide serious failures behind fake success responses.

---

# 39. Rate Limiting

Rate limiting controls how many requests are accepted in a period.

Example:

```text
100 requests/minute/client
```

If the client exceeds the limit:

```text
429 Too Many Requests
```

Useful for:

- Protecting services
- Preventing abuse
- Controlling traffic
- Protecting downstream dependencies

Rate limiting can be implemented at a gateway or service level.

---

# 40. Distributed Transactions

In a monolith:

```text
BEGIN TRANSACTION

Create Order
Update Payment
Update Inventory

COMMIT
```

If everything uses one database transaction, rollback can be straightforward.

In microservices:

```text
Order DB
Payment DB
Inventory DB
```

A normal local database transaction does not automatically span all of them.

This creates the distributed transaction problem.

---

# 41. Saga Pattern

A Saga breaks a distributed business transaction into a sequence of **local transactions**.

Example:

```text
Create Order
     |
     v
Process Payment
     |
     v
Reserve Inventory
```

If a later step fails, use a **compensating action**.

Example:

```text
Create Order       -> Success
Payment            -> Success
Inventory Reserve  -> Failure

Compensate:
Cancel/mark order accordingly
Refund payment
```

The exact compensation depends on business rules.

---

# 42. Saga Choreography

Services coordinate through events.

```text
Order Service
     |
 OrderCreated
     v
   Kafka
     |
     v
Payment Service
     |
PaymentCompleted
     v
   Kafka
     |
     v
Inventory Service
```

There is no single central coordinator.

### Advantage

- Less central orchestration

### Disadvantage

- Workflow can become difficult to understand
- Business flow is distributed across event handlers

---

# 43. Saga Orchestration

A central orchestrator controls the workflow.

```text
             Saga Orchestrator
              /      |       \
             v       v        v
          Order   Payment  Inventory
```

The orchestrator tells services what action to perform and handles failures/compensations.

### Advantage

- Centralized workflow visibility

### Disadvantage

- Orchestrator becomes an important component
- Can become too powerful if poorly designed

---

# 44. Eventual Consistency

With separate databases and asynchronous events, data may not become consistent immediately.

Example:

```text
Order = CREATED
Payment = PROCESSING
Inventory = NOT_RESERVED
```

A moment later:

```text
Order = CONFIRMED
Payment = SUCCESS
Inventory = RESERVED
```

The system becomes consistent eventually.

This is **eventual consistency**.

---

# 45. Idempotency

An operation is idempotent when repeating it produces the same final effect as executing it once.

This is especially important with:

- Payments
- Order creation
- Kafka consumers
- Retries

Example:

```text
Idempotency-Key: ABC123
```

Client accidentally sends the same payment request twice.

The service recognizes:

```text
ABC123 -> already processed
```

and does not charge twice.

### Important

Idempotency usually requires storing/looking up the idempotency key and designing the business operation accordingly.

---

# 46. Distributed Lock vs Idempotency

Do not automatically solve duplicate processing using distributed locks.

Often, idempotency is a better business-level solution.

Example:

```text
Payment request
   |
Idempotency key
   |
Already processed?
  / \
Yes  No
 |    |
Return  Process
result  + record key
```

---

# 47. Authentication vs Authorization

## Authentication

> Who are you?

Examples:

- Username/password
- JWT
- OAuth 2.0/OIDC
- Identity provider

## Authorization

> What are you allowed to do?

Example:

```text
USER  -> View orders
ADMIN -> View + cancel orders
```

Spring Security is commonly used.

---

# 48. JWT

JWT is commonly used for stateless authentication/authorization.

Typical flow:

```text
Client
  |
  | Login
  v
Identity Provider/Auth Service
  |
  | JWT
  v
Client
  |
  | Authorization: Bearer <token>
  v
API Gateway / Service
```

JWT commonly contains claims such as:

```text
subject/user identifier
roles/scopes
expiration
issuer
```

A service validates the token/signature and authorization rules.

---

# 49. OAuth 2.0 vs JWT

Do not treat these as the same thing.

**OAuth 2.0** is an authorization framework.

**JWT** is a token format.

OpenID Connect (OIDC) adds an identity layer on top of OAuth 2.0.

You can encounter:

```text
OAuth 2.0 / OIDC
        +
JWT access tokens
```

---

# 50. Service-to-Service Security

Microservices may also need authentication between services.

Possible approaches include:

- OAuth2 client credentials
- mTLS
- Signed service tokens
- Platform/service-mesh identity

Do not assume that internal traffic is automatically trusted.

---

# 51. Distributed Logging

A request can travel through many services:

```text
Gateway
   |
Order
   |
Payment
   |
Notification
```

If every service logs independently, debugging becomes difficult.

Use a **Correlation ID** or **Trace ID**.

Example:

```text
Trace ID = ABC123

Gateway       -> ABC123
Order Service -> ABC123
Payment       -> ABC123
Notification  -> ABC123
```

Now logs can be searched using the same identifier.

---

# 52. Centralized Logging

Instead of manually checking each server:

```text
Service A logs
Service B logs
Service C logs
```

send logs to a centralized logging system.

Common technologies include:

- ELK/Elastic Stack
- OpenSearch
- Loki
- Cloud logging services

The exact stack depends on the organization.

---

# 53. Distributed Tracing

Logging answers:

> What was logged?

Tracing answers:

> Where did the request travel and how much time did each operation take?

Example:

```text
Request
  |
  +-- Gateway       20ms
  |
  +-- Order         50ms
  |
  +-- Payment      500ms  <-- slow
  |
  +-- Notification  30ms
```

Common technologies:

- OpenTelemetry
- Jaeger
- Zipkin
- Vendor tracing platforms

---

# 54. Metrics and Monitoring

Important metrics:

```text
Request count
Error rate
Latency
Throughput
CPU
Memory
Database connection usage
Kafka lag
```

A common ecosystem:

```text
Spring Boot Actuator
        |
     Metrics
        |
    Prometheus
        |
     Grafana
```

---

# 55. Spring Boot Actuator

Spring Boot Actuator provides production-oriented monitoring and management features.

Common endpoint:

```text
/actuator/health
```

Other endpoints may expose metrics and application information depending on configuration.

Do not expose sensitive actuator endpoints publicly without appropriate security.

---

# 56. Liveness vs Readiness

Especially important in container/Kubernetes environments.

## Liveness

> Is the application process alive?

If liveness fails, the platform may restart the container.

## Readiness

> Is the application ready to receive traffic?

If readiness fails, traffic should generally be removed from that instance while the process may continue running.

Example:

```text
Application running
     |
     +-- Liveness: OK
     |
     +-- Readiness: NOT READY
```

---

# 57. Containerization with Docker

Microservices are commonly packaged as containers.

```text
Order Service
     |
Docker Image
     |
Docker Container
```

Each service can run independently.

Example:

```text
Docker
 |
 +-- Order container
 +-- Payment container
 +-- User container
```

Containers provide packaging and isolation; they do not automatically solve distributed-system problems.

---

# 58. Kubernetes

Kubernetes commonly manages containers at scale.

It can help with:

- Deployment
- Scaling
- Service discovery
- Load balancing
- Self-healing
- Rolling updates
- Configuration
- Secrets
- Scheduling

Example:

```text
Order Service
     |
     +-- Pod 1
     +-- Pod 2
     +-- Pod 3
```

If a pod dies:

```text
Pod 1 -> X

Kubernetes
    |
creates/replaces pod
```

---

# 59. Pod, Deployment, Service

Basic Kubernetes terminology:

### Pod

Smallest deployable unit; commonly contains one application container.

### Deployment

Manages a desired number of pod replicas and rollout strategy.

### Service

Provides stable networking/discovery to a set of pods.

Conceptually:

```text
Client
  |
Kubernetes Service
  |
  +-- Pod 1
  +-- Pod 2
  +-- Pod 3
```

---

# 60. Horizontal vs Vertical Scaling

## Vertical Scaling

Increase resources for an instance:

```text
4 GB RAM -> 16 GB RAM
2 CPU    -> 8 CPU
```

## Horizontal Scaling

Increase number of instances:

```text
1 instance
    |
    v
3 instances
    |
    v
10 instances
```

Microservices commonly benefit from horizontal scaling because services can be scaled independently.

---

# 61. Stateless Services

A stateless service does not rely on local in-memory state to maintain a user's session between requests.

Bad for scaling:

```text
Request 1 -> Server A
             session in RAM

Request 2 -> Server B
             session missing
```

Better approaches:

```text
JWT
or
External session/state store
```

Then requests can reach any instance.

---

# 62. API Versioning

Suppose the existing API is:

```text
/api/v1/orders
```

A breaking change may require:

```text
/api/v2/orders
```

Versioning helps old clients continue working while new clients migrate.

Other versioning approaches exist, such as headers/media types, but URL versioning is easy to understand.

---

# 63. Backward Compatibility

Microservices often deploy independently.

Therefore, Service A may temporarily communicate with an older version of Service B.

Prefer backward-compatible changes.

Example:

Good:

```text
Add optional response field
```

Risky/breaking:

```text
Rename required field
Remove endpoint
Change meaning of existing field
```

This is especially important for event schemas.

---

# 64. Contract Testing

Contract testing verifies that a service provider and its consumers agree on an API/event contract.

Example:

```text
Consumer expects:
GET /payments/101

Provider promises:
response contains id + status
```

Tools/patterns can include consumer-driven contract testing such as Pact.

---

# 65. API Gateway vs Load Balancer

They are not exactly the same.

### Load Balancer

Main focus:

> Distribute traffic among service instances.

### API Gateway

Main focus:

> Provide an API entry point and handle routing plus cross-cutting concerns.

A gateway may itself use load balancing.

---

# 66. Service Mesh

A service mesh moves some service-to-service networking concerns into infrastructure.

It can provide:

- Traffic management
- mTLS
- Service identity
- Retries
- Observability
- Policy enforcement

Examples include:

- Istio
- Linkerd

Simple idea:

```text
Service A
   |
Sidecar/proxy
   |
Network
   |
Sidecar/proxy
   |
Service B
```

Do not introduce a service mesh unless the operational complexity is justified.

---

# 67. CAP Theorem

CAP stands for:

```text
C -> Consistency
A -> Availability
P -> Partition Tolerance
```

In a distributed system, when a network partition occurs, a system cannot simultaneously guarantee both perfect consistency and availability.

Partition tolerance is generally treated as necessary in distributed systems because network partitions can happen.

The practical discussion is about the trade-off between consistency and availability under partition.

---

# 68. Eventual Consistency vs Strong Consistency

## Strong Consistency

After a successful write, subsequent reads see the latest value according to the system's consistency model.

## Eventual Consistency

Different parts of the system may temporarily have different values, but they converge over time.

Microservices using asynchronous events frequently use eventual consistency.

---

# 69. Twelve-Factor Application Principles

The Twelve-Factor methodology provides guidance for cloud-native applications.

Important ideas to remember:

```text
Configuration -> environment
Stateless processes
Logs -> event streams
Disposable processes
Separate build/release/run
Explicit dependencies
Port binding
Dev/prod parity
```

For interviews, the core idea is:

> Applications should be easy to deploy, scale, configure, and operate in modern cloud environments.

---

# 70. CI/CD in Microservices

Each service can have its own pipeline.

```text
Code
  |
Build
  |
Unit Tests
  |
Integration Tests
  |
Security/Quality Checks
  |
Docker Image
  |
Deploy
```

Possible deployment strategies:

- Rolling deployment
- Blue-green deployment
- Canary deployment

---

# 71. Rolling Deployment

Gradually replace old instances:

```text
Old Old Old
   |
   v
New Old Old
   |
   v
New New Old
   |
   v
New New New
```

Usually avoids taking the whole service offline.

---

# 72. Blue-Green Deployment

Maintain two environments:

```text
Blue  -> Current version
Green -> New version
```

After validation, traffic switches:

```text
Blue  <- traffic

then

Green <- traffic
```

Rollback can be relatively quick by switching traffic back.

---

# 73. Canary Deployment

Release to a small percentage first:

```text
95% -> old version
5%  -> new version
```

If healthy:

```text
80% -> old
20% -> new
```

Eventually:

```text
0% -> old
100% -> new
```

---

# 74. Common Microservice Failure Scenario

Suppose:

```text
Client
  |
Gateway
  |
Order Service
  |
Payment Service
```

Payment becomes slow.

Without resilience:

```text
Order requests
     |
     v
Payment
     |
Slow
     |
Threads/resources get consumed
     |
Order becomes unhealthy
     |
Gateway sees failures
```

Better:

```text
Order
  |
Timeout
  |
Circuit Breaker
  |
Retry only where appropriate
  |
Fallback/alternative flow
```

This prevents one dependency from taking down the whole system.

---

# 75. Cascading Failure

A failure in one service causes failures in dependent services.

Example:

```text
Payment Service
      X
      |
Order Service
      X
      |
Gateway
      X
      |
Clients
```

Ways to reduce cascading failures:

- Timeouts
- Circuit breakers
- Bulkheads
- Rate limiting
- Backpressure
- Queueing
- Graceful degradation
- Appropriate retries

---

# 76. Graceful Degradation

When a non-critical dependency fails, provide reduced functionality instead of failing everything.

Example:

```text
Recommendation Service -> DOWN

Product page still works
but recommendations are unavailable.
```

This is better than:

```text
Recommendation Service -> DOWN
        |
Entire Product Page -> DOWN
```

---

# 77. Retry Storm

A dangerous situation:

```text
100 clients
   |
dependency fails
   |
all clients retry immediately
   |
200 requests
   |
more overload
   |
more failures
```

Avoid using:

- Unbounded retries
- Immediate retries
- Excessively high retry counts

Use:

- Bounded retries
- Exponential backoff
- Jitter
- Circuit breaker
- Appropriate timeouts

---

# 78. Distributed Debugging

In a monolith:

```text
One application
```

In microservices:

```text
Gateway
  -> Service A
      -> Service B
          -> Kafka
              -> Service C
```

Debugging requires:

- Correlation/trace IDs
- Centralized logs
- Metrics
- Distributed tracing
- Clear error responses
- Good dashboards

---

# 79. Database Migration

Each service should ideally own its schema and migrations.

Tools commonly used in Java/Spring applications:

- Flyway
- Liquibase

Example:

```text
Order Service
   |
Order DB
   |
Flyway migrations
```

Do not manually modify production schemas without a controlled migration strategy.

---

# 80. Caching

Caching can reduce latency and database load.

Example:

```text
Client
  |
Product Service
  |
Redis Cache
  |
If miss:
  |
Database
```

Common cache technology:

- Redis
- Caffeine
- Distributed/cloud caches

Important concepts:

- Cache-aside
- TTL
- Cache invalidation
- Cache stampede
- Consistency

---

# 81. Cache-Aside Pattern

Typical flow:

```text
Read
 |
Cache?
 / \
Yes No
 |   |
Return DB
       |
       v
     Cache
       |
       v
     Return
```

The application controls loading data into the cache.

---

# 82. Cache Stampede

Suppose a popular cache entry expires.

Thousands of requests all see a cache miss:

```text
Cache expired
     |
1000 requests
     |
1000 DB calls
     |
DB overloaded
```

Possible protections:

- Request coalescing
- Staggered expiration
- Locks where appropriate
- Refresh-ahead
- Good TTL strategy

---

# 83. Microservices and Transactions

Inside one service, normal Spring transactions are still useful:

```java
@Transactional
public void createOrder(...) {
    // local database transaction
}
```

But `@Transactional` does **not magically create a distributed transaction across independent services**.

For cross-service business workflows, consider patterns such as Saga and transactional outbox.

---

# 84. Transactional Outbox Pattern

Problem:

You need to update your DB and publish an event.

Bad:

```text
1. Update DB -> success
2. Publish Kafka event -> failure
```

Now DB changed but event was not published.

Or:

```text
1. Publish event -> success
2. DB update -> failure
```

Now event says something happened when the DB says it did not.

### Outbox solution

Store the event in the same local database transaction:

```text
BEGIN TRANSACTION

Update Order
Insert Outbox Event

COMMIT
```

Then a separate process publishes outbox events to Kafka.

```text
Order DB
  |
  +-- orders table
  |
  +-- outbox table
           |
           v
      Outbox Publisher
           |
           v
         Kafka
```

This helps achieve reliable event publication.

---

# 85. Transactional Inbox / Idempotent Consumer

A consumer may receive the same event more than once.

Use an idempotent consumer strategy.

Conceptually:

```text
Receive event
    |
Already processed?
   / \
 Yes  No
  |    |
Ignore Process
       |
Record event ID
```

This is especially useful with at-least-once delivery.

---

# 86. Saga vs Two-Phase Commit

### Two-Phase Commit (2PC)

Attempts to coordinate a distributed transaction across participants.

It can be expensive and introduces coordination/availability concerns.

### Saga

Uses local transactions plus compensating actions.

For many microservice business workflows, Saga is more commonly discussed.

Simple interview answer:

> In microservices, Saga is often preferred for long-running business workflows because it avoids requiring one distributed ACID transaction across all services.

---

# 87. Service Ownership

A service team should ideally own:

```text
Code
API
Database/schema
Deployment
Monitoring
Operational responsibility
```

This improves autonomy.

---

# 88. Shared Libraries

Shared libraries can be useful for:

- Common logging
- Security utilities
- API models in carefully controlled cases
- Common infrastructure utilities

But overusing shared libraries can create coupling.

Bad:

```text
All services depend on one huge shared library.
```

Now a small change may require coordinated upgrades everywhere.

---

# 89. Shared Database Anti-Pattern

If every service directly uses the same tables:

```text
Order Service ----\
Payment Service ----> Shared DB
User Service ------/
```

then database schema changes can break many services.

This reduces service autonomy.

---

# 90. Distributed Monolith

A common interview concept.

A system may be deployed as multiple services but still behave like a monolith.

Example:

```text
Service A
   |
must call B
   |
B must call C
   |
C must call D
```

All services must be deployed together and cannot work independently.

That is effectively a **distributed monolith**.

### Warning signs

- Tight synchronous dependency chains
- Shared database
- Coordinated deployments
- Shared release cycles
- Strong coupling
- Services cannot operate independently

---

# 91. Microservice Granularity

Too few services:

```text
Huge Service
```

Too many services:

```text
100 tiny services
```

Both can be problematic.

The goal is a meaningful business boundary.

A good interview answer:

> Service size should be determined by business capability, cohesion, coupling, team ownership, and operational cost rather than an arbitrary number of classes or lines of code.

---

# 92. API Composition

Sometimes a client needs data from multiple services.

Example:

```text
Product Page
  |
  +-- Product Service
  +-- Inventory Service
  +-- Recommendation Service
```

A gateway/BFF/API composition layer may aggregate the responses.

This avoids forcing the client to know every backend service.

---

# 93. Backend for Frontend (BFF)

Different clients may need different API shapes.

```text
Mobile App -> Mobile BFF
Web App    -> Web BFF
             |
             v
       Backend Services
```

A BFF is a client-specific backend layer.

---

# 94. Service-to-Service Timeout Design

Suppose:

```text
Gateway timeout = 3 seconds
Order timeout   = 2 seconds
Payment timeout = 5 seconds
```

This is poorly aligned because a downstream timeout is longer than its caller's timeout.

A good timeout budget should be designed across the call chain.

Example:

```text
Gateway
  3 sec
   |
Order
  2 sec
   |
Payment
  1.5 sec
```

Actual values depend on business and infrastructure requirements.

---

# 95. HTTP Connection Pooling

Repeatedly creating network connections is expensive.

HTTP clients commonly use connection pools so connections can be reused.

This can improve:

- Latency
- Resource usage
- Throughput

Important when building high-throughput service-to-service calls.

---

# 96. Backpressure

Backpressure means slowing down producers when consumers cannot keep up.

Example:

```text
Producer -> 1 million events/sec
Consumer -> 10,000 events/sec
```

Without controls, queues can grow indefinitely.

Possible mechanisms:

- Bounded queues
- Rate limiting
- Reactive backpressure
- Consumer scaling
- Kafka partition scaling

---

# 97. Bulk Processing

For large event volumes, consumers may process messages in batches.

Benefits can include:

- Better throughput
- Fewer network/database calls

But batching may increase latency.

Choose based on business requirements.

---

# 98. Health Check Dependency Design

Do not make a liveness check fail just because a temporary downstream service is unavailable unless the application truly cannot function.

A common distinction:

```text
Liveness:
"Is my process alive?"

Readiness:
"Can I safely receive traffic?"
```

Readiness may consider important dependencies.

---

# 99. Security at Multiple Layers

Microservice security should not rely only on the API Gateway.

Possible layers:

```text
Client
  |
TLS
  |
API Gateway
  |
Authentication
  |
Authorization
  |
Service
  |
Service-to-service authentication
  |
Database credentials/secrets
```

Use least privilege.

---

# 100. Secrets Management

Do not store:

```text
DB_PASSWORD=secret123
```

in source control.

Use:

- Environment/secret management
- Kubernetes Secrets
- Cloud secret managers
- Vault
- Appropriate IAM mechanisms

Secrets should be rotated where appropriate.

---

# 101. Observability

Observability usually includes three major signals:

```text
Logs
Metrics
Traces
```

### Logs

What happened?

### Metrics

How much/how often/how fast?

### Traces

Where did the request go?

Together:

```text
Logs + Metrics + Traces
          =
      Observability
```

---

# 102. Important Spring Boot Microservice Components

A Java/Spring Boot microservice project may commonly use:

```text
Spring Boot
Spring Web
Spring Data JPA
Spring Security
Spring Validation
Spring Actuator

Spring Cloud Gateway
Spring Cloud OpenFeign
Spring Cloud LoadBalancer
Spring Cloud Config

Kafka / Spring for Apache Kafka
Resilience4j

Docker
Kubernetes
```

Do not assume every project uses all of them.

---

# 103. Typical Spring Boot Layering

Inside a service:

```text
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
Database
```

Example:

```java
@RestController
class OrderController {

    @GetMapping("/orders/{id}")
    public OrderDto get(@PathVariable Long id) {
        return orderService.get(id);
    }
}
```

Then:

```text
Controller
 -> Service
 -> Repository
 -> DB
```

Keep business logic primarily in the service/domain layer rather than controllers.

---

# 104. DTO vs Entity

Do not automatically expose JPA entities directly from APIs.

Prefer:

```text
Database Entity
      |
      v
Service
      |
      v
DTO
      |
      v
API Response
```

Benefits:

- API contract is independent of DB structure
- Avoid accidental exposure of fields
- Better versioning
- Better control over serialization

---

# 105. Validation

Spring Boot commonly uses Bean Validation:

```java
public record CreateOrderRequest(
    @NotNull Long customerId,
    @NotEmpty List<Long> productIds
) {}
```

Controller:

```java
@PostMapping
public OrderDto create(@Valid @RequestBody CreateOrderRequest request) {
    return orderService.create(request);
}
```

Invalid input should be handled consistently.

---

# 106. Global Exception Handling

Use `@RestControllerAdvice` to standardize API errors.

Conceptually:

```java
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<?> handle(OrderNotFoundException ex) {
        // return consistent error response
    }
}
```

A consistent error model is important in distributed APIs.

---

# 107. API Error Response

Example:

```json
{
  "timestamp": "2026-08-18T10:30:00Z",
  "status": 404,
  "code": "ORDER_NOT_FOUND",
  "message": "Order was not found",
  "traceId": "abc123"
}
```

Avoid exposing internal stack traces to clients.

---

# 108. Testing Microservices

Important test levels:

```text
Unit Tests
Integration Tests
Contract Tests
Component Tests
End-to-End Tests
```

### Unit Test

Tests one unit in isolation.

### Integration Test

Tests integration with real/managed dependencies.

### Contract Test

Verifies service API/event contracts.

### End-to-End Test

Tests the complete business flow.

Do not rely only on end-to-end tests because they can be slow and brittle.

---

# 109. Testcontainers

In Java, Testcontainers can run real infrastructure dependencies in containers during tests.

Examples:

```text
PostgreSQL
Kafka
Redis
```

This can make integration tests closer to production behavior than mocked dependencies.

---

# 110. Distributed System Testing

Test failure scenarios, not only happy paths.

Examples:

```text
Payment service down
Kafka unavailable
Database unavailable
Network timeout
Duplicate event
Delayed event
Out-of-order event
Invalid message
High traffic
```

---

# 111. Common Interview Scenario: Payment Failure

Question:

> Order service calls Payment service, but Payment service is down. What will you do?

Good answer:

```text
1. Set a timeout
2. Use limited retries for transient failures
3. Use exponential backoff/jitter
4. Use a circuit breaker
5. Avoid blocking resources indefinitely
6. Return an appropriate response/state
7. Consider asynchronous payment processing
8. Use Saga/compensation if the business workflow requires it
9. Monitor/log/trace the failure
```

Do not simply say:

> "Retry until it works."

---

# 112. Common Interview Scenario: Duplicate Kafka Message

Question:

> What if Kafka sends the same event twice?

Answer:

> Design the consumer to be idempotent.

Example:

```text
Event ID = 123

Have I processed event 123?
    |
   Yes -> Ignore/return safely
    |
    No -> Process + record event ID
```

---

# 113. Common Interview Scenario: Service Is Slow

Question:

> Payment service takes 30 seconds. What do you do?

Answer:

```text
Timeout
+
Circuit Breaker
+
Monitoring
+
Tracing
+
Appropriate retry policy
+
Bulkhead/resource isolation
```

Then investigate the root cause using metrics and traces.

---

# 114. Common Interview Scenario: Traffic Suddenly Increases

Possible approach:

```text
Load Balancing
      +
Horizontal Scaling
      +
Caching
      +
Async processing
      +
Rate Limiting
      +
Database optimization
```

Kubernetes can help with horizontal scaling depending on the deployment.

---

# 115. Common Interview Scenario: Need Reliable DB Update + Kafka Event

Answer:

> Consider the Transactional Outbox Pattern.

```text
DB transaction:
    Update Order
    Insert Outbox Event
          |
        Commit
          |
    Outbox Publisher
          |
        Kafka
```

This avoids the simple dual-write failure problem.

---

# 116. Common Interview Scenario: Order + Payment + Inventory

Answer:

> Use a Saga-style workflow if the operation spans multiple services.

```text
Create Order
    |
Process Payment
    |
Reserve Inventory
```

Failure:

```text
Inventory fails
    |
Compensate
    |
Refund Payment
    |
Cancel/adjust Order
```

Exact compensation depends on business rules.

---

# 117. Common Interview Scenario: Why Kafka Instead of REST?

Possible answer:

> Kafka is useful when the producer does not need an immediate response, when multiple consumers need the same event, when asynchronous processing is desirable, or when buffering and independent consumer scaling are useful.

Do not say Kafka should replace REST everywhere.

---

# 118. Common Interview Scenario: Why Not One Database?

Possible answer:

> A shared database creates tight coupling around schemas and data ownership. Database-per-service improves autonomy and allows services to evolve independently, although it also introduces distributed consistency and transaction complexity.

This shows balanced understanding.

---

# 119. Common Interview Scenario: What Is a Distributed Monolith?

Answer:

> A distributed monolith is technically split into multiple services but the services remain tightly coupled through shared databases, synchronized deployments, or long synchronous dependency chains, so they lose most of the independence expected from microservices.

---

# 120. Common Interview Scenario: What Is Circuit Breaker?

Answer:

> Circuit breaker protects a service from repeatedly calling an unhealthy dependency. After repeated failures it opens the circuit and fails fast; after a wait it moves to half-open and allows limited test calls.

---

# 121. Common Interview Scenario: What Is Saga?

Answer:

> Saga manages a distributed business transaction as a sequence of local transactions. If a later step fails, compensating actions undo or logically compensate for earlier successful steps.

---

# 122. Common Interview Scenario: What Is Idempotency?

Answer:

> An idempotent operation can be safely repeated without causing additional unintended business effects. It is especially important for retries, payments, and at-least-once event processing.

---

# 123. Common Interview Scenario: What Is Service Discovery?

Answer:

> Service discovery allows a service to dynamically find available instances of another service instead of relying on hardcoded hostnames and ports.

---

# 124. Common Interview Scenario: What Is API Gateway?

Answer:

> API Gateway provides a common entry point for clients and can perform routing and cross-cutting concerns such as authentication integration, rate limiting, filtering, and observability.

---

# 125. Common Interview Scenario: How Do You Monitor Microservices?

Answer:

```text
Actuator
+
Metrics
+
Centralized Logs
+
Distributed Tracing
+
Dashboards
+
Alerts
```

Example stack:

```text
Spring Boot Actuator
       |
Prometheus
       |
Grafana

OpenTelemetry
       |
Tracing backend
```

---

# 126. Common Interview Scenario: How Do You Secure Microservices?

Answer:

```text
TLS
+
Spring Security
+
OAuth2/OIDC
+
JWT or other access tokens
+
Authorization/scopes/roles
+
Service-to-service authentication
+
Secrets management
+
Least privilege
```

---

# 127. Common Interview Scenario: How Do You Deploy Microservices?

Answer:

> Each service can be packaged as an independently deployable artifact, commonly a container image, and deployed through a CI/CD pipeline. Kubernetes is often used for orchestration, scaling, service discovery, health checks, and rolling deployments.

---

# 128. Common Interview Scenario: What Happens When a Pod Dies?

With Kubernetes:

```text
Pod dies
   |
Deployment desired replicas != actual
   |
Kubernetes schedules replacement
```

A Kubernetes Service continues routing traffic to healthy ready pods.

---

# 129. Common Interview Scenario: What Is Readiness?

Answer:

> Readiness indicates whether an instance is currently ready to receive traffic.

If readiness fails:

```text
Instance still running
       |
Removed from traffic
```

---

# 130. Common Interview Scenario: What Is Liveness?

Answer:

> Liveness indicates whether the application process is alive enough to continue running. A failed liveness probe can cause the platform to restart the container.

---

# 131. Microservice Architecture Example — E-commerce

Complete example:

```text
                         CLIENT
                            |
                            v
                      API GATEWAY
                            |
             +--------------+--------------+
             |              |              |
             v              v              v
        User Service   Order Service   Product Service
             |              |              |
          User DB        Order DB       Product DB
                            |
                            |
                     OrderCreated Event
                            |
                            v
                          KAFKA
                       /    |     \
                      /     |      \
                     v      v       v
                Payment  Inventory Notification
                 Service   Service     Service
                    |        |            |
               Payment DB Inventory DB   Email/SMS
```

Cross-cutting:

```text
Security       -> OAuth2/JWT/Spring Security
Gateway        -> Spring Cloud Gateway
Discovery      -> Kubernetes/Eureka/etc.
HTTP Calls     -> RestClient/WebClient/OpenFeign
Events         -> Kafka
Resilience     -> Resilience4j
Config         -> External configuration
Monitoring     -> Actuator + Metrics
Tracing        -> OpenTelemetry
Deployment     -> Docker + Kubernetes
Consistency    -> Saga / Events / Outbox
```

---

# 132. Full Order Flow Example

Customer places an order:

```text
1. Client
      |
2. API Gateway
      |
3. Order Service
      |
4. Validate request
      |
5. Save Order = CREATED
      |
6. Insert OrderCreated into Outbox
      |
7. Commit local DB transaction
      |
8. Outbox Publisher -> Kafka
      |
9. Payment Service consumes event
      |
10. Payment succeeds
      |
11. PaymentCompleted event
      |
12. Inventory consumes event
      |
13. Inventory reserved
      |
14. InventoryReserved event
      |
15. Order Service updates order state
      |
16. Notification Service sends confirmation
```

Possible failure:

```text
Inventory reservation fails
       |
       v
Compensating action
       |
       v
Refund Payment
       |
       v
Cancel/adjust Order
```

This is a practical Saga-style workflow.

---

# 133. Microservices Design Checklist

When designing a microservice system, ask:

```text
1. What is the business capability?
2. What is the service boundary?
3. Who owns the data?
4. Is the service independently deployable?
5. Does it need synchronous or asynchronous communication?
6. How is service discovery handled?
7. How is traffic routed?
8. How is authentication handled?
9. How are timeouts configured?
10. What happens if a dependency fails?
11. Is retry safe?
12. Do we need idempotency?
13. How is distributed consistency handled?
14. Do we need Saga?
15. Do we need an outbox?
16. How are logs correlated?
17. How are metrics collected?
18. How is tracing done?
19. How is the service deployed?
20. How does it scale?
21. How are secrets managed?
22. How are APIs versioned?
23. How are contracts tested?
24. How are database migrations managed?
25. How do we roll back?
```

---

# 134. The Most Important Concepts to Memorize

If you have only a few minutes before the interview, remember:

```text
Microservice
Monolith
Business Capability
Bounded Context
High Cohesion
Loose Coupling
Database per Service
REST
gRPC
Kafka
API Gateway
Service Discovery
Load Balancing
Configuration
Circuit Breaker
Timeout
Retry
Exponential Backoff
Bulkhead
Rate Limiting
Fallback
Saga
Choreography
Orchestration
Eventual Consistency
Idempotency
Transactional Outbox
JWT
OAuth2/OIDC
Spring Security
Actuator
Metrics
Centralized Logging
Correlation ID
Distributed Tracing
OpenTelemetry
Docker
Kubernetes
Horizontal Scaling
Liveness
Readiness
CI/CD
Rolling Deployment
Blue-Green
Canary
```

---

# 135. One-Line Definitions — Rapid Revision

| Concept | One-line meaning |
|---|---|
| Microservice | Independently deployable service focused on a business capability |
| Monolith | One deployable application containing multiple capabilities |
| Bounded Context | Domain boundary where a model has a defined meaning |
| High Cohesion | Related functionality stays together |
| Loose Coupling | Services depend minimally on each other's internals |
| Database per Service | Service owns and controls its data |
| API Gateway | Common entry point for clients |
| Service Discovery | Finds service instances dynamically |
| Load Balancer | Distributes traffic among instances |
| REST | HTTP-based request/response API style |
| gRPC | Strongly typed RPC framework commonly using HTTP/2 and Protobuf |
| Kafka | Distributed event streaming platform |
| Producer | Sends Kafka records |
| Consumer | Reads Kafka records |
| Topic | Logical Kafka stream/category |
| Partition | Ordered unit within a Kafka topic |
| Offset | Record position in a partition |
| Consumer Group | Consumers cooperating to process a topic |
| Timeout | Stops waiting after a limit |
| Retry | Attempts a failed operation again |
| Backoff | Waits progressively longer between retries |
| Circuit Breaker | Stops calls to a failing dependency |
| Bulkhead | Isolates resources to limit failure impact |
| Rate Limiting | Controls request rate |
| Fallback | Alternative behavior when dependency fails |
| Saga | Sequence of local transactions + compensations |
| Choreography | Services coordinate through events |
| Orchestration | Central coordinator manages workflow |
| Eventual Consistency | Data converges over time |
| Idempotency | Repetition does not cause unintended additional effects |
| Outbox | Stores DB change + event in one local transaction |
| JWT | Token format often used for access tokens |
| OAuth2 | Authorization framework |
| OIDC | Identity layer built on OAuth2 |
| Authentication | Who are you? |
| Authorization | What can you do? |
| Actuator | Spring Boot monitoring/management features |
| Metrics | Numerical measurements of system behavior |
| Trace | Request journey across services |
| Correlation ID | Identifier used to connect logs for one request |
| Docker | Container packaging/runtime technology |
| Kubernetes | Container orchestration platform |
| Pod | Kubernetes execution unit |
| Deployment | Manages desired pod replicas/rollouts |
| Service | Stable Kubernetes networking endpoint |
| Liveness | Is the process alive? |
| Readiness | Is the instance ready for traffic? |
| Horizontal Scaling | Add more instances |
| Vertical Scaling | Give an instance more resources |
| Rolling Deployment | Gradually replace old instances |
| Blue-Green | Switch traffic between two environments |
| Canary | Gradually expose new version to users |

---

# 136. Fast Interview Answer Template

When asked a scenario question, structure your answer:

```text
1. Identify the problem
2. Identify the failure/consistency requirement
3. Choose communication style
4. Add timeout
5. Add retry only if safe
6. Add circuit breaker if dependency failure is possible
7. Add idempotency if duplicate processing is possible
8. Use Saga for distributed business transactions
9. Use Outbox for reliable DB + event publishing
10. Add logging, metrics, and tracing
11. Explain scaling/deployment
12. Mention trade-offs
```

This makes your answer sound practical rather than theoretical.

---

# 137. Top 15 Questions to Practice Before the Interview

## Q1. What are microservices?

Answer:

> Microservices is an architecture where an application is split into independently deployable services, each focused on a business capability.

## Q2. What are the advantages?

```text
Independent deployment
Independent scaling
Team autonomy
Fault isolation
Technology flexibility
Smaller codebases
```

Also mention the costs:

```text
Network complexity
Distributed transactions
Observability
Deployment complexity
Eventual consistency
```

## Q3. How do services communicate?

> REST/gRPC for synchronous communication and Kafka/RabbitMQ for asynchronous communication.

## Q4. What is API Gateway?

> Single external entry point for routing and cross-cutting concerns.

## Q5. What is Service Discovery?

> Dynamic discovery of service instances.

## Q6. Why database per service?

> To maintain data ownership and reduce coupling.

## Q7. What is Circuit Breaker?

> Protects a service by stopping calls to an unhealthy dependency after repeated failures.

## Q8. What is Saga?

> A sequence of local transactions with compensating actions for distributed business workflows.

## Q9. What is Idempotency?

> Repeated execution has the same intended business effect as a single execution.

## Q10. What is Kafka?

> A distributed event streaming platform commonly used for asynchronous communication and event-driven architecture.

## Q11. How do you handle service failure?

> Timeout, appropriate retry/backoff, circuit breaker, bulkhead, fallback, and monitoring.

## Q12. How do you secure microservices?

> Spring Security, OAuth2/OIDC, JWT or other access tokens, TLS, service-to-service authentication, authorization, and secret management.

## Q13. How do you monitor them?

> Logs, metrics, traces, Actuator, Prometheus/Grafana, OpenTelemetry, centralized logging, and alerts.

## Q14. How do you deploy?

> Independently through CI/CD, commonly packaged as containers and orchestrated by Kubernetes.

## Q15. How do you handle distributed transactions?

> Prefer local transactions per service and use patterns such as Saga and transactional outbox where appropriate.

---

# 138. Final Mental Map

Memorize this architecture:

```text
                              CLIENT
                                |
                                v
                         +--------------+
                         | API GATEWAY  |
                         +--------------+
                                |
          +---------------------+---------------------+
          |                     |                     |
          v                     v                     v
   +-------------+       +-------------+       +-------------+
   | User Service|       |Order Service|       |Product Svc |
   +-------------+       +-------------+       +-------------+
          |                     |                     |
       User DB               Order DB             Product DB
                                |
                                | OrderCreated
                                v
                           +---------+
                           |  KAFKA  |
                           +---------+
                            /   |    \
                           /    |     \
                          v     v      v
                     Payment Inventory Notification
                     Service  Service   Service
                        |        |         |
                    Payment   Inventory   Email/SMS
                       DB        DB

Cross-cutting:
------------------------------------------------------------
Security       -> Spring Security / OAuth2 / OIDC / JWT
Gateway        -> Spring Cloud Gateway
Discovery      -> Kubernetes / Eureka / Consul
HTTP Client    -> RestClient / WebClient / OpenFeign
Messaging      -> Kafka
Resilience     -> Timeout / Retry / Circuit Breaker / Bulkhead
Consistency    -> Saga / Outbox / Idempotency
Monitoring     -> Actuator / Metrics / Prometheus / Grafana
Tracing        -> OpenTelemetry
Logging        -> Centralized logs + Correlation/Trace ID
Deployment     -> Docker / Kubernetes
Scaling        -> Horizontal scaling
Configuration  -> Externalized config / Secrets
------------------------------------------------------------
```

---

# 139. Final 30-Second Interview Summary

If the interviewer asks:

> "Explain your understanding of microservices."

A strong concise answer is:

> Microservices is an architecture where the application is split into independently deployable services aligned with business capabilities. Each service ideally owns its data and communicates with other services using synchronous APIs such as REST/gRPC or asynchronous messaging such as Kafka. In Spring Boot, we commonly use Spring Web, Spring Security, Spring Cloud Gateway, OpenFeign, Actuator, and resilience tools such as Resilience4j. For distributed concerns, we need service discovery, load balancing, timeouts, retries, circuit breakers, observability, and secure communication. For distributed business transactions, we can use Saga and transactional outbox patterns, while idempotency helps handle retries and duplicate messages. Services are commonly containerized with Docker and deployed/scaled using Kubernetes.

---

# 140. Final Revision Strategy

For a quick revision, study in this order:

```text
Priority 1:
Microservice vs Monolith
Service boundaries
REST
Kafka
API Gateway
Database per Service

Priority 2:
Service Discovery
Load Balancing
Timeout
Retry
Circuit Breaker
Bulkhead
Idempotency

Priority 3:
Saga
Choreography
Orchestration
Transactional Outbox
Eventual Consistency

Priority 4:
Spring Security
OAuth2/JWT
Actuator
Logging
Tracing
Metrics

Priority 5:
Docker
Kubernetes
Pods
Deployments
Liveness
Readiness
Scaling
CI/CD

Priority 6:
Caching
BFF
Service Mesh
Contract Testing
CAP
Twelve-Factor
Distributed Monolith
```

## Final Rule

Do not try to memorize only definitions.

For each major concept, be able to answer:

```text
What is it?
Why do we need it?
What problem does it solve?
Give a simple example.
What happens when it fails?
What are the trade-offs?
```

That is the level at which microservices interview discussions usually become practical rather than purely theoretical.
