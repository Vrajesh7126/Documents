- When the request comes to the Tomcat, tomcat itself assign a virtual thread to the request instead of platform thread, and when the request waits for DB/API calls, JVM temporarily removes the virtual thread from the carrier thread so the carrier thread can serve another requests.

- lb:// stands for Load Balancer. It tells the API Gateway to route the request to a service by its name instead of a fixed IP/port. The gateway discovers the available instances of that service and load-balances requests among them.

# JAR :
- How do you hide internal code?

Use:

public

for APIs you want consumers to use.

Use:

private

or package-private (no modifier)

for internal implementation.

- Since Java 9, you can use:

```java
module com.vrajesh.mathutils {
    exports com.vrajesh.api;
}
```

Now only :
```java
com.vrajesh.api
```

- is visible outside the JAR.
- Even if internal classes are public, packages not exported cannot be used by consumers. This is stronger than access modifiers.

WebClient

A Spring HTTP client where developers manually build and execute REST calls.

OpenFeign

A declarative HTTP client where developers define an interface, and Spring automatically generates the REST communication code.

# Fault Tolerance
- "Even if something fails, the system can still handle it gracefully."

