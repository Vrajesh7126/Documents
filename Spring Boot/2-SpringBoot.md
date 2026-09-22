# DispatcherServlet

It is a front controller of spring MVC.

```text
HTTP Request
     ↓
Tomcat
     ↓
DispatcherServlet
     ↓
HandlerMapping
     ↓
Controller
     ↓
Service
     ↓
Response
```

```java
@GetMapping("/employees")
public List<Employee> getEmployees() {
    return service.getEmployees();
}
```

**HandlerMapping :** which controller and method handles by `/employee`.

`HandlerMapping` finds `employeeController.getEmployees()`.

`HandlerAdapter` calls `employeeController.getEmployees()`.

`HandlerExceptionResolver` handles any exceptions thrown during the processing of the request.


# Filter vs Interceptor vs AOP

**Filter :**
- Servlet's feature.
- Runs before the request reaches the DispatcherServlet.

**Interceptor :**
- Spring MVC's feature.
- Runs before and after the controller methods.

**AOP :**
- Works around Spring-managed Bean methods.

# Spring AOP
- Used to add an extra behaviour around methods.

**AOP :** The feature want to run.
**Proxy :** The mechanism Spring can use to achieve it.

