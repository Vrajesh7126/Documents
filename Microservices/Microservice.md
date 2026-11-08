# Microservices with Java & Spring Boot — Complete Revision Notes

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
- Highly cohesive (Different parts working together)
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
- Eventual consistency (Data across microservices may be temporarily inconsistent, but eventually becomes consistent)
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

Imagine you're building an E-commerce application using Spring Boot microservices.

### One customer, three services

1. Order Service : `Order` contains order ID, items, and order status.

2. Payment Service : `Order` means information needed to process a payment, such as the order ID and amount.

3. Inventory Service : `Order` means items and quantities that need to be reserved.

Although all three services deal with the same order, each service has its own model and business rules.

### So, what is Bounded Context?

Each service's boundary defines how it understands and manages its business concepts.

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

- `RestClient` : Sync call.

```java
User user = restClient.get()
              .uri("/users/1")
              .retrieve()
              .body(User.class)
```

- `WebClient` : Async call.

```java
Mono<User> user = webClient.get()
                    .uri("/users/1")
                    .retrieve()
                    .bodyToMono(User.class)
```

- `OpenFeign` : Call an API through an interface. Sync call.

```java
@FeignClient(name = "user-service")
public interface UserClient {
  @GetMapping("/users/{id}")
  User getUser(@PathVariable Long id);
}
```

### Disadvantages

- Services become runtime-dependent on each other
- Latency accumulates (total response time increases as a request passes through multiple microservices)
- Failure can propagate (One service's failure affects a caller service)
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
- Good for event-driven architecture (EDA is a design where a microservices communicates by publisher-consumer model instead of directly calls each others)
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

- **Routing** : Route a request o a microservice based on rule such as URL.
- **Authentication**
- **Authorization**
- **Rate limiting**
- **Request filtering**
- **CORS handling** : API Gateway controls which browser based frontend can call the microservice through the API Gateway.
- **Header manipulation**
- **Logging/Observability**
- **TLS termination** : API Gateway receives HTTPS request and it decrypt it and send HTTP request to the service.
- **Aggregation** : Combine a result from the multiples of the microservice and give a single response to the caller.

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

- **Eureka** : maintains a registry of service instances and their addresses.
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
  |------------> Payment
  |
  +------------> Notification
```

This can reduce direct coupling between producers and consumers.

---

# 30. Resilience and Fault Tolerance

**Resilience :** Ability to recover from failure.

For example, If Order service calls Payment service and Payment service is down, Order service should retries request or provide a fallback.

Use resilience patterns:

```text
Timeout
Retry
Circuit Breaker
Bulkhead
Fallback
Rate Limiting
```

**Resilience4j :** A popular library for implementing resilience patterns in Java/Spring Boot applications.

**Fault Tolerance :** Ability to continue working despite failures.

For example, Another payment service instances automatically take over if one instance fails.

---

# 31. Timeout

**Problem :** Payment Service takes 30 seconds to respond, and Order Service keeps waiting.

**Solution :** Set a maximum waiting time, for example 3 seconds.

```java
webClient.create()
    .get()
    .uri("http://payment-service/pay")
    .retrieve()
    .bodyToMono(Payment.class)
    .timeout(Duration.ofSeconds(3))
    .onErrorResume(TimeoutException.class, e -> {
        // handle timeout, maybe return fallback
        return Mono.empty();
    });
```

---

# 32. Retry

**Problem :** Payment Service temporarily fails because of a network issue or a transient error.

**Solution :** Retry the request a limited number of times.

Using Resilience4j:

```java
@Retry(name = "paymentService")
public Payment pay(Order order) {
    // remote call
}
```

Configuration example:

```yaml
resilience4j.retry:
  instances:
    paymentService:
      maxAttempts: 3
      waitDuration: 1s
```

Use bounded retries and usually backoff/jitter.

Configuration:

```yaml
resilience4j:
  retry:
    instances:
      paymentService:
        max-attempts: 3
        wait-duration: 1s
        enable-exponential-backoff: true
        exponential-backoff-multiplier: 2 # Waiting will be increase multiply by 2
        enable-randomized-wait: true  # Add randomness
        randomized-wait-factor: 0.5 # Add randomness up to 50% of the wait duration
```

```
Base delay    Possible randomized range
1 second	    0.5–1.5 seconds
2 seconds	    1–3 seconds
4 seconds	    2–6 seconds
8 seconds	    4–12 seconds
```

---

# 34. Circuit Breaker

**Problem :** Payment Service is continuously failing. Order Service keeps calling it, wasting resources

**Solution :** Circuit Breaker temporarily stops requests to Payment Service.

## Circuit Breaker States

```text
CLOSED : Request are allowed
  |
  | failures exceed threshold
  v
OPEN : Request are blocked
  |
  | wait duration expires
  v
HALF-OPEN : Test Requests are allowed
  |
  +---- success ----> CLOSED
  |
  +---- failure ----> OPEN
```

**Implementation :**

```java
@CircuitBreaker(name = "paymentService", fallbackMethod = "fallbackPay")
public Payment pay(Order order) {
    // remote call
}
```

**Configuration :**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        slidingWindowSize: 10 # Number of calls to consider for calculating the failure rate
        failureRateThreshold: 50  # Percentage of failures that will trip the circuit breaker
        waitDurationInOpenState: 10s  # Time to wait before transitioning from OPEN to HALF-OPEN
        permittedNumberOfCallsInHalfOpenState: 2  # Number of test calls allowed in HALF-OPEN state
```

# 35. Fallback

**Problem :** Payment Service is unavailable, and Order Service cannot complete the payment request.

**Solution :** Provide an alternative response instead of allowing the request to fail.

**Implementation :**

```java
public PaymentResponse fallbackPay(PaymentRequest request, Throwable ex) {
    return new PaymentResponse("Payment service is currently unavailable. Please try again later.");
}
```

---

# 37. Bulkhead Pattern

**Problem :** Payment Service is slow. Hundreds of Order Service requests are waiting for payment responses and consuming all available threads.

**Solution :** Limit the number of concurrent calls to Payment Service.

**Implementation :**

```java
@Bulkhead(name = "paymentService", type = Bulkhead.Type.SEMAPHORE)
public PaymentResponse pay(PaymentRequest request) {
    // remote call
}
```

**Configuration :**

```yaml
resilience4j:
  bulkhead:
    instances:
      paymentService:
        maxConcurrentCalls: 10  # Limit to 10 concurrent calls
        maxWaitDuration: 0s      # No waiting; reject immediately if limit is reached
```

---

# 39. Rate Limiting

**Problem :** Order Service sends 1,000 payment requests per second, overwhelming Payment Service.

**Solution :** Limit how many requests can be made within a time period.

**Implementation :**

```java
@RateLimiter(name = "paymentService")
public PaymentResponse pay(PaymentRequest request) {
    // remote call
}
```

**Configuration :**

```yaml
resilience4j:
  ratelimiter:
    instances:
      paymentService:
        limitForPeriod: 100  # Allow 100 requests
        limitRefreshPeriod: 1m # Per minute
        timeoutDuration: 0s    # No waiting; reject immediately if limit is reached
```

If the client exceeds the limit:

```text
429 Too Many Requests
```

# 40. Health Check

**Problem :** One Payment Service instance crashes, but requests are still being sent to it.

**Solution :** Use **Spring Boot Actuator** and Kubernetes health probes.

**Add Actuator :**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Kubernetes checks the instance's health and can stop routing traffic to an unhealthy instance. For it, need to provide the configuration at Kubernetes.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service

spec:
  replicas: 3

  selector:
    matchLabels:
      app: order-service

  template:
    metadata:
      labels:
        app: order-service

    spec:
      containers:
        - name: order-service
          image: order-service:latest

          ports:
            - containerPort: 8080

          # Every 10 seconds kubernetes checks whether Order Service is alive, If it repeatedly fails, Kubernetes restarts the container
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            periodSeconds: 10

          # Every 5 seconds Kubernetes checks whether Order Service is ready to receive traffic, If readiness fails, Kubernetes removes that Pod from the Service's available traffic endpoints.
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            periodSeconds: 5
```

---

# 40. Distributed Transactions

Distributed Transaction is a transactions which involves a multiples of services and DB, where all operations must be successed together or be handled consistently if something fails.

If multiples of services participate in one business operation, we should maintain data consistency.

## Saga Pattern

Provides Eventual Consistency.

Instead of one global transaction, each service excutes its own local transaction.

If a later step fails, previously completed steps are compensated using a *Compensation action**.

```
Create Order (Success)
        ↓
Process Payment (Success)
        ↓
Reserve Inventory (Fail)
        ↓
[Compansation Actions]
        ↓
Refund Payment (Compensated)
        ↓
Cancel Order (Compensated)
```

## Saga Choreography

Saga Choreography is a way of implementating distributed transactions where microservices communicates through events, and each service independently decides what action to perform when it receives an event.

```text
Order Service (Create Order)
  Publishes: OrderCreated
          ↓
Payment Service (Process Payment)
  Publishes: PaymentCompleted
          ↓
Inventory Srevice (Reerve Inventory Failed)
  Publishes: InventoryReservationFailed
          ↓
Payment Service (Refund Payment)
  Publishes: PaymentRefunded
          ↓
Order Service (Cancels Order)
  Publishes: OrderCancelled
```

## Saga Orchestration

A central orchestrator controls the workflow.

```text
             Saga Orchestrator
              /      |       \
             v       v        v
          Order   Payment  Inventory
```

The orchestrator tells services what action to perform and handles failures/compensations.

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

# 45. Strong Consistency

Strong Consistency means once a write operation succeeds, every subsequent read must return that latest value (or a newer value), by any of the replication.

It increase latency and reduce availability during network failure.

## Synchronous Replication

Methods to achieve Strong Consistency.

```
Application
Withdraw ₹2,000
    ↓
Node A (Primary)
Updates balance to ₹8,000
    ↓
Node B (Replica)
Confirms replication of ₹8,000
    ↓
Write Acknowledged
```

---

# 45. Idempotency

Idempotancy is the property of an operation where executing it multiple times produce the same result as executing it once.

**Problem :**

For example, If Customer places an order worth $5000.
  1. Order Service sends a payment request.
  2. Payment Service successfully deducts ₹5,000.
  3. Due to a network issue, Order Service doesn't receive the response.
  4. Order Service retries the same payment request.
  5. Without idempotency, Payment Service might deduct another ₹5,000!

**Solution :** Use **Idempotancy key**.

```
Order Service (Send Payment request with key: ORDER-101-PAYMENT)
      ↓
Payment Request
First Request:
  Key doesn't exist.
  Process payment: ₹5,000.
  Store key and payment result.

Retry Request:
  Key exist
  Don't process payment again.
  Return previously stored result.
```

**Impleentation :**

```java
public PaymentResponse makePayment(
        PaymentRequest request, String key) {

    Payment payment = repository.findByIdempotencyKey(key);

    if (payment != null) {
        return payment.getResponse();
    }

    // Process payment
    PaymentResponse response = processPayment(request);

    // Store payment and key
    savePayment(key, response);

    return response;
}
```

**Important Implementation details :**

Add a **UNIQUE constraints** on the idempotancy key.

Handle concurrent request so two identical request cannot process payment simultaneoudly. (Using Distributed locking)

Validate same key will not be useful with a different payment request.

---

# 46. Distributed Lock

Distributed Lock is a mechanism that ensures only one service instance or process can access or modify a shared resource at a time, even when multiple instances are running.

For example before processind payment, an instance must aquire a lock.

```
OrderService
-------------------------
Instance-1 (Lock aquired)
Instance-2 (Waiting)
Instance-3 (Waiting)
        ↓
Distributed Lock Manager (Redis)
        ↓
Instance 1 processes payment
Release a lock
```

**Implementation :**

Add Dependency :

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>3.20.0</version>
</dependency>
```

```java
public class PaymentService {
  private final RedissonClient redissonClient;

  public void processPayment(Long orderId) {
    // getLock: Creates or retrieves a lock for a particular order
    RLock lock = redissonClient.getLock("payment-lock-" + orderId);

    try {
      // attempt to aquire a lock (wait to 5 seconds to aquire, and if aquire hold for 30 seconds)
      boolean acquired = lock.tryLock(5, 30, TimeUnit.SECONDS);

      if (!acquired) {
          throw new RuntimeException("Payment is already being processed");
      }

      // process payment
    }
    catch (InterruptedException e) {
    }
    finally {
      if (lock.isHeldByCurrentThread()) {
          lock.unlock();
      }
  }
}
```

---

# Pending (will be covered later)
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

Distributed Logging is a mechanism for collecting, storing, and analyzing logs generated by multiple microservices in one centralized location.

```
Order Service, Payment Service, Inventory Service
                    ↓
  Log Collector (Fluentd/Fluent Bit or Grafana Alloy)
                    ↓
Centralized logging system (ElasticSearch or Loki)
        Collects and stores logs
                    ↓
        Kibana or Grafana
  Search, Filter and visualize logs
```

**EFK Stack** : Elastic Search + Fluentd + Kibana

**ELK Stack** : Elastic Search + Logstash + Kibana

Fluentd and Logstash perform similar log collection, but they are different tools.

**Grafana Loki stack** : Grafana + Loki

## Correlation ID.

Correlation ID is a unique ID assigned to an incoming request and passed through all microservices.

It helps us identify which logs belong to the same request.

For example, If payment fails you can search a log using **Correlation ID** and see the complete request journey across all services.

**Implementation :**

1. Generate a Unique ID at API Gateway or at first service.
2. Pass it to downstream services through an HTTP header, such as X-Correlation-ID.
3. Fetch Correlation-ID and store it at MDC (Mapped Diagnostic Context), as MDC is available to the logger so logger can print it into the log.
4. We can see logs at ElasticSearch/Loki.

# 53. Distributed Tracing

Distributed Tracing is a technique used to track the complete journey of a single request across multiple microservices.

## Trace ID

Complete journey of a request across services.

## Span ID

One individual operation within that journey.

For a single request, Trace ID is same but Span ID vary acorss a different operations.

**Use case :**

    Identify how much time each operation took.
    Identify which service is slow.
    Where an error occured.

**Tools :**

Micrometer Tracing: Instruments in Spring Boot.

OpenTelemetry: Standard for collecting and propagating traces.

ZipKin: UI for a Tracing.

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

---

# 54. Metrics and Monitoring

Metrics is numerical measurements of your app's health & performance.

Monitoring is continuously observing those metrics to dleect a problem.

Important metrics:

```text
CPU
Memory
Request count
Error rate
Latency
Throughput
Database connection usage
Kafka lag
```

A common ecosystem:

```text
Spring Boot Application
(Expose Application metrics)
            ↓
        Prometheus
(Collect & stores metrics)
            ↓
        Grafana
(Display dashboard & Graph)
```

---

# 56. Liveness vs Readiness

Use in container/Kubernetes environments.

## Liveness

Checks whether the application is still alive or stuck.

If it fails, **Kubernetes restarts** the container.

Example, Application is **deadlocked**.

## Readiness

Checks whether the application is ready to receive traffic.

If it fails, Kubernetes stops sending traffic to that Pod and **remove a pod**. it doen't restart the container.

Example: Application is **starting up** or temporarily unable to connect to a required dependency.

---

# 60. Horizontal vs Vertical Scaling

## Vertical Scaling

Increase resources for an instance.

## Horizontal Scaling

Increase number of instances.

Microservices commonly benefit from horizontal scaling because services can be scaled independently.

---

# 61. Stateless Services

A stateless service doesn't store user/session/request-specific state inside memory.

Every request can be handled by any available instance without depending on previous requests.

Suport **Horizontal scaling**.

How to achieve a Statelessness ?
    
    Store data in shared DB.
    Store session in Redis, not in local memory.
    Use JWT.

---

# 62. API Versioning

If we update a new version of an API, old should be work as it is because old client may use it.

**Methods to implement it :**

**URI Versioning :** `api/v1/users`

**Query Parameters :** `api/users?version=1`

**Header Versioning :** `X-API-Version: 1`

---

# 63. Backward Compatibility

Backward Compatibility means introducing new changes without breaking existing clients or systems that depend on the old behavior.

For example,

Old Response:

```json
{
    "id": 101,
    "name": "Vrajesh"
}
```

New Response:

```json
{
    "id": 101,
    "name": "Vrajesh"
    "email": "vrajesh@gmail.com"
}
```

Existing clients still work because old fields remain unchanged.

Backward compitability could be break if:

    Removing an existing API field.
    Rename it.
    Changing field's data type.
    Making an Optional field mandatory.

---

# 66. Service Mesh (Skipped)

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

The CAP Theorem indicate that distributed DB can gaurantee at most two properties during network partition.

**Consistency :** Every read gets the latest successfully written value.

**Availability :** Every request receives a response, even if the response may contain stale data.

**Partition Tolerance :** It means a Distributed system continues working even when network communication between some of its nodes or services breaks.

Suppose DB has 2 replicas:

    Node A stores balance = ₹1,000.
    Node B stores balance = ₹1,000.

Now the network connection between them breaks.

Node A receives a withdrawal request and updates the balance to ₹500. Node B still has ₹1,000.

What should system do ?

**Option 1: CP**

Rejects a request on Node B until consistency can be restored.

**Option 2: AP**

Allow Node B to respond with ₹1,000, even though it is stale.

**Note :** Partition tolerance is generally treated as necessary in distributed systems because network partitions can happen.

---

# 69. Twelve-Factor Application Principles

The 12 factor App is a methodology for building modern, scalable, maintainable, cloud-native and microservice application.

| # | Principle | Simple Meaning |
|---|---|---|
| 1 | Codebase | One codebase tracked in Git, with multiple deployments. |
| 2 | Dependencies | Explicitly declare dependencies (Maven/Gradle). |
| 3 | Config | Store configuration outside code using environment variables. |
| 4 | Backing Services | Treat databases, Kafka, Redis as attached resources. |
| 5 | Build, Release, Run | Separate building, configuration/release, and execution. |
| 6 | Processes | Run the application as stateless processes. |
| 7 | Port Binding | Application exposes itself through a port. |
| 8 | Concurrency | Scale by running multiple application instances. |
| 9 | Disposability | Start quickly and shut down gracefully. |
| 10 | Dev/Prod Parity | Keep development and production environments similar. |
| 11 | Logs | Treat logs as event streams; let infrastructure collect them. |
| 12 | Admin Processes | Run maintenance tasks as separate processes. |

---

# 70. CI/CD in Microservices (Skipped)

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

# 71. Rolling Deployment (Skipped)

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

# 72. Blue-Green Deployment (Skipped)

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

# 73. Canary Deployment (Skipped)

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

# 76. Graceful Degradation

Graceful Degradation means when a **service or dependency** fails, the application continues providing its core functionality while temporarily disabling non-essential features.

Example :

Suppose E-Commerce has:

```text
Order Service
Payment Service
Recommendation Service
```

Now, Recommendation Service is down. Instead of making the entire application unavailable, 

    Users can still browse products.
    Users can still place orders.

How to achieve it :
    
**Fallback :** Show default recommendation instead.

**Circuit Breaker :** Stop repeatedly calling a failing service.

**Timeout :** Avoid waiting indefinitely.

**Cached Data :** Serve previously stored data when appropriate.

---

# 77. Retry Storm

A Retry Storm happens when **multiple services repeatedly retry failed requests**, creating excessive traffic and making an already struggling service even worse.

How to prevent it:

| Technique           | Purpose                                    |
| ------------------- | ------------------------------------------ |
| Exponential Backoff | Increase waiting time between retries      |
| Jitter              | Add randomness to retry delays             |
| Retry Limit         | Restrict maximum retry attempts            |
| Circuit Breaker     | Temporarily stop calling failing services  |
| Timeout             | Prevent requests from waiting indefinitely |
| Rate Limiting       | Restrict incoming request volume           |

---

# 79. Database Migration (Skipped)

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

Caching means storing frequently accessed data temporarily in a faster storage location.

Cache stores data as a Key-Value pair.

### Types of Caching

| Type              | Description                                    |
| ----------------- | ---------------------------------------------- |
| In-Memory Cache   | Stores data in application memory              |
| Distributed Cache | Shared cache across multiple instances (Redis) |
| Browser Cache     | Stores resources in the browser                |

## Common Caching Strategies

**Cache-Aside :** Application checks cache first; on a miss, fetches from DB and updates cache. (Achieve using `@Cacheable`)

**Read-Through :** Cache itself automatically fetches data from the DB on a miss.

**Write-Through :** Writes to cache and DB together.

**Write-Behind :** Writes to cache first, then asynchronously updates the DB.

## Caching Annotations

### @Cacheable

Checks cache first; executes method only on cache miss.

```java
@Cacheable(value = "products", key = "#id")
public Product getProduct(Long id) {
    return productRepository.findById(id).orElseThrow();
}
```

### @CacheEvict

Removes entries from cache.

```java
@CacheEvict(value = "products", key = "#id")
public void updateProduct(Long id, Product product) {
    productRepository.save(product);
}
```

### @CachePut

Always executes the method and updates the cache.

```java
@CachePut(value = "users", key = "#user.id")
public User updateUser(User user) {
    return userRepository.save(user);
}
```

### @Caching

Combine multiple caching annotations on one method.

```java
@Caching(
    evict = {
        @CacheEvict(value = "users", key = "#id"),
        @CacheEvict(value = "userList", allEntries = true)
    }
)
public void deleteUser(Long id) {
    userRepository.deleteById(id);
}
```

## Caching Challanges

### Cache Invalidation

Cache Invalidation means removing or updating cached data when the original database data changes.

### Cache Stampede

Cache Stampede happens when a popular cache entry expires for a **single key** and many requests simultaneously try to fetch the same data from the database.

**Prevention :** Use **Distributed locking** so only one request can fetch the data from DB and store into the cache.

### Cache Penetration

Cache Penetration happens when requests repeatedly ask for data that doesn't exist in the DB.

**Preventation :** Store negative results into the cache (e.g., product not found)

### Cache Avalanche

Cache Avalanche happens when many cache entries with a **different keys** expire simultaneously, causing a large number of requests to hit the database at once.

**Preventation :** 
    
    Add a random TTL (TTL Jitter).
    Avoid setting identical expiration times.
    Apply rate limiting. 

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

Problem: You need to update your DB and publish an event.

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

**Solution :**

Store the event in the same local database transaction (Outbox table):

```text
BEGIN TRANSACTION

Update Order
Insert Outbox Event

COMMIT
```

Then a separate process publishes outbox events to Kafka by reading from the Outbox table.

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

---

# 85. Transactional Inbox / Idempotent Consumer

Prevents a microservice from procesing the same Kafka event multiple times.

**Solution :** Use an idempotent consumer strategy.

**Example :**

Payment Service maintains an `inbox` table.

| event_id | event_type   | status    |
| -------- | ------------ | --------- |
| EVT-101  | OrderCreated | PROCESSED |

When an event arrives:
1. Check whether its event_id already exists in Inbox.
2. If it exists, skip processing.
3. Otherwise, insert the event ID.
4. Process the business operation.
5. Mark it processed.

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
