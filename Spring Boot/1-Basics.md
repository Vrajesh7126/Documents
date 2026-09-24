# Spring Boot Startup Mechanism

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

flow:

```
main()
   ↓
SpringApplication.run()
   ↓
Create ApplicationContext
   ↓
Read configuration
   ↓
Component Scanning
   ↓
Auto-Configuration
   ↓
Create Beans + Dependency Injection
   ↓
Start Embedded Server
   ↓
Application Ready
```

1. **SpringApplication.run()**: Spring Boot uses `SpringApplication` to bootstrap the entire application.
2. **ApplicationContext is Created**: A container that manages all Spring Beans
3. **Configuration is processed**
4. **Component Scanning**: spring searches for classes such as `@Component`, `@Service`, `@Repository`, and `@Controller` and registers them as Bean definations**.
5. **Auto-Configuration**: Spring Boot checks your dependencies and configuration. For ex, if `spring-boot-starter-web` is on the classpath, it will automatically configure a `DispatcherServlet` and an `embedded Tomcat server` and `Spring MVC Infrastructure`.
6. **Beans are created**: Creates a Beans and performs dependency injection.
7. **Embedded server starts**
8. **Application Ready**: The application is fully started and ready to serve requests.

# @SpringBootApplication

1. **@Configuration**: Tells spring, "This class can contain Spring configuration/Bean definitions".
2. **@EnableAutoConfiguration**: Tells Spring Boot to "automatically configure your application based on the dependencies and configuration available."
3. **@ComponentScan**: Tells Spring "Search this package and its sub-packages for Spring components."

# Auto-Configuration Internals
- Spring Boot checks dependency,

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

This brings classes like:
- `DataSource.class`
- `JdbcTemplate.class`
...

**1. Class Path**

Now Spring can asks:

```java
@ConditionalOnClass(DataSource.class)
```

Meaning: "Only apply this configuration if `DataSource.class` is present on the classpath."

**2. Existing Beans**

Now suppose `DataSource.class` exists, Spring Boot asks another question:

```java
@ConditionalOnMissingBean(DataSource.class)
```

Meaning: "Has the developer already created a DataSource bean?"

If Developer does,

```java
@Bean
DataSource dataSource() {
    return new DataSource();
}
```

Then, Spring Boot doesn't create another one.

**3. Application Properties**

Now suppose Spring Boot decides, Okay I need to create a DataSource, but which database, which URL, which Username ?

you provide,

```
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=secret
```

Spring Boot reads these properties and uses them to create the `DataSource` bean.

**4. Conditions**

Spring Boot can also apply additional conditions based on the environment, properties, or custom conditions. For example:

```java
@ConditionalOnProperty(name = "spring.datasource.url", havingValue = "true")
```

Meaning: "Only create the DataSource bean if the property `spring.datasource.url` is defined as `true`."

```java
@ConditionalOnWebApplication
```

Meaning: "Only create the DataSource bean if the application is a web application."

```java
@ConditionalOnMissingClass("com.example.SomeClass")
```

Meaning: "Only create the DataSource bean if the class `com.example.SomeClass` is not present on the classpath."

```java
@ConditionalOnBean(DataSource.class)
```

Meaning: "Only create the DataSource bean if a `DataSource` bean is already present in the context."

# Component Scanning Internals
- Spring Boot internally search for classes annotated with `@Component`, `@Service`, `@Repository`, and `@Controller` within the base package and its sub-packages.
- Spring uses `ClassPathBeanDefinitionScanner` internally, it **doesn't create the actual object yet**, It creates a `BeanDefinition` containing metadata such as,
    - Class → EmployeeService
    - Scope → singleton
    - Lazy Initialization → false
    - Dependencies → ...
- Later, the **BeanFactory** uses that `BeanDefinition` to create the actual bean.

# Bean Creation & Bean Lifecycle

```java
@Component
class EmployeeService {

    @PostConstruct
    public void init() {
        System.out.println("EmployeeService initialized.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("EmployeeService destroyed.");
    }
}
```

Lifecycle:

```text
BeanDefinition
      ↓
Create object
      ↓
Dependency Injection
      ↓
Aware callbacks
      ↓
BeanPostProcessor → before
      ↓
@PostConstruct
      ↓
BeanPostProcessor → after
      ↓
Ready to use
      ↓
Application shutdown
      ↓
@PreDestroy
      ↓
Bean destroyed
```

**1. BeanDefinition exists**

Component scanning already discovered

```java
@Component
class EmployeeService
```

and register its `BeanDefinition`.

**2. Object is created** : **BeanFactory** creates an Object.

**3. Dependency Injection** : The **BeanFactory** injects dependencies into the created object.

**4. Initialization callbacks** : Spring executes `@PostConstruct`.

**5. BeanPostProcessor** : Spring allows `BeanPostProcessor` to modify/process the beans. Spring uses it for `@Autowired`, `@Value`, `@Async`, `@Transactional`, AOP proxies.

**6. Bean is Ready**

**7. Shutdown** : Spring calls `@PreDestroy` and destroys the bean.

**Note :** For a **Prototype** bean, Spring created the object but generally does not manage its destruction, so you shouldn't rely on `@PreDestroy` for prototype cleanup.

We can create a class implementing `BeanPostProcessor` to customize the initialization and destruction of beans. For example:

```java
@Component
class UserService { }

@Component
class OrderService { }

@Component
class PaymentService { 
    
    @Value("${payment.api-key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        System.out.println("PaymentService initialized with API key: " + apiKey);
    }
}
```

`MyProcessor` itself is registered as a Spring bean, its methods are called for every bean that Spring creates in that `ApplicationContext`.

```java
@Component
class MyProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) {
        System.out.println("Before: " + name);

        if (bean instanceof PaymentService service) {
            // validate before @PostConstruct
            // Can validate @Autowired, @Value
            if (service.getApiKey() == null) {
                throw new RuntimeException("API key is missing");
            }
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        System.out.println("After: " + name);

        // Can create proxies or wrap the bean with additional behaviour
        // Spring use this to implements @Async, @Transactional, AOP proxies

        return bean;
    }
}
```

# Dependency Injection Internals

`BeanFactory` performs dependency injection.

`AbstractAutowireCapableBeanFactory` is responsible for creating and initializing beans.

`AutowiredAnnotationBeanPostProcessor` is specific `BeanPostProcessor` whose role is to process the `@Autowired` annotation.

```java
interface PaymentService { }

@Service
class StripePaymentService implements PaymentService { }

@Service
class PaypalPaymentService implements PaymentService { }
```

```java
@Autowired
PaymentService paymentService;
```

Result: `NoUniqueBeanDefinitionException`.

Solution: `@Qualifier` above the injection point or `@Primary` above the bean definition.

# BeanFactory vs ApplicationContext

```text
BeanFactory
    ↓
Basic IoC / DI container


ApplicationContext
    ↓
BeanFactory +
    ├── Application Events
    ├── Internationalization (MessageSource)
    ├── Resource Loading
    ├── Automatic BeanPostProcessor registration
    ├── Automatic BeanFactoryPostProcessor registration
    ├── Application lifecycle integration
    └── Parent/Child context support
```

# FactoryBean
`FactoryBean` is a special Spring interface used when you want to control how a Bean is created.

```java
@Component
class MyFactoryBean implements FactoryBean<Employee> {

    @Override
    public Employee getObject() {
        return new Employee();
    }

    @Override
    public Class<?> getObjectType() {
        return Employee.class;
    }
}
```

```java
Employee employee = context.getBean("myFactory", Employee.class);
```

result: an instance of `Employee` created by `MyFactoryBean`.

```java
MyFactoryBean myFactoryBean = context.getBean("&myFactory", MyFactoryBean.class);
```

result: an instance of `MyFactoryBean` itself, not the `Employee` it creates.

# @configuration & Proxying

```java
@Configuration
class AppConfig {
    @Bean
    EmployeeRepository repository() {
        return new EmployeeRepository();
    }

    @Bean
    EmployeeService service() {
        return new EmployeeService(repository());
    }
}
```

When `service()` calls `repository()`, Spring intercepts the call and give the same `EmployeeRepository` instance.

```text
service()
    ↓
repository()
    ↓
Spring proxy intercepts
    ↓
existing Repository Bean
```

Without this mechanism, Spring could create another object.

```java
@Configuration(proxyBeanMethods = false)
```

This disables the method interception, use it when `@Bean` method don't directly call each other and not need the singleton-preserving behavior.