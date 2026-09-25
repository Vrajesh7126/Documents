# @ConfigurationProperties

Group of related configuration

```properties
// application.properties
app.name=MyApp
app.version=1.0.0
app.description=This is my application
```

```java
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String name;
    private String version;
    private String description;
}
```

# @Value

Simple property injection

```properties
// application.properties
app.name=MyApp

```

```java
@Value("${app.name}")
private String appName;
```

# @Profile

Profiles let you load different beans based on the active profile.

```java
@Profile("dev")
@Bean
DataSource devDataSource() {
    // return dev data source
}
```

This bean is created when `dev` is active as below:

```properties
spring.profiles.active=dev
```

When `dev` is not then injecting `DataSource` gives `NoSuchBeanDefinitionException`.

There could be multiples of the files:

```text
application.properties
application-dev.properties
application-prod.properties
```

When `spring.profiles.active=dev` is set, then `application-dev.properties` and `application.properties` are loaded. 

When `spring.profiles.active=prod` is set, then `application-prod.properties` and `application.properties` are loaded.

# Spring Bean name

```java
@Component
class PaymentService {
    
}
```

Bean name is `paymentService` by default. You can change it by:

```java
@Component("myPaymentService")
class PaymentService {
    
}
```

```java
@Bean
public PaymentService paymentService() {
    return new PaymentService();
}
```

Bean name is as same as method name `paymentService`, You can change it by:

```java
@Bean("myPaymentService")
public PaymentService paymentService() {
    return new PaymentService();
}
```

