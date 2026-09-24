# Application Startup Optimization & Startup Internals

Spring Boot startup time is mainly affected by:

```text
Configuration loading
      ↓
Component scanning
      ↓
Auto-configuration
      ↓
Bean creation
      ↓
Embedded server startup
```

## Solutions

### 1. Lazy Initialization

Create a bean when requested instead of at application startup.

```properties
spring.main.lazy-initialization=true
```

### 2. Reduce unnecessary auto-configuration

Exclude auto-configuration classes that are not needed to speed up startup.

```java
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

### 3. Reduce unnecessary component scanning

### 4. Startup measurement

Spring Boot provides `BufferingApplicationStartup` which can record startup steps.

We can inspect the start time, end time, duration, startup step details at the endpoint `/actuator/startup`.

### 5. Important optimization principle

Don't blindly enable lazy initialization or exclude configurations.

First identify the expensive startup work.