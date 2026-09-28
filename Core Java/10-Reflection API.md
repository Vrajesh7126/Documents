# Table of Contents

1. [What is Reflection?](#1-what-is-reflection)
2. [Why Reflection Was Introduced](#2-why-reflection-was-introduced)
3. [Reflection API Components](#3-reflection-api-components)
4. [Class Class (`java.lang.Class`)](#4-class-class-javalangclass)
   - [What is Class?](#what-is-class)
   - [Getting Class Object](#getting-class-object)
   - [Common Methods](#common-methods)
   - [Create Object Dynamically](#create-object-dynamically)

5. [Field Reflection (`java.lang.reflect.Field`)](#5-field-reflection-javalangreflectfield)
   - [Get Field](#get-field)
   - [Read Value](#read-value)
   - [Modify Value](#modify-value)
   - [Why `setAccessible(true)`?](#why-setaccessibletrue)

6. [Method Reflection (`java.lang.reflect.Method`)](#6-method-reflection-javalangreflectmethod)
   - [Get Method](#get-method)
   - [Execute Method](#execute-method)
   - [Method With Parameters](#method-with-parameters)
   - [Method Returning Value](#method-returning-value)

7. [Constructor Reflection (`java.lang.reflect.Constructor`)](#7-constructor-reflection-javalangreflectconstructor)
   - [Get Constructor](#get-constructor)
   - [Create Object](#create-object)
   - [Constructor With Parameters](#constructor-with-parameters)

8. [Dynamic Invocation (`Method.invoke()`)](#8-dynamic-invocation-methodinvoke)
   - [Normal Method Call](#normal-method-call)
   - [Dynamic Method Call](#dynamic-method-call)
   - [Why Important?](#why-important)

9. [Annotation Processing](#9-annotation-processing)
   - [`isAnnotationPresent()`](#check-annotation-exists)
   - [`getAnnotation()`](#read-annotation)
   - [Retention Policy Requirement](#retention-policy-requirement)

10. [Reflection and Spring Internals](#10-reflection-and-spring-internals)
    - [`@Component`, `@Service`, `@Repository`](#component--service--repository)
    - [`@Autowired`](#autowired)
    - [`@GetMapping`](#getmapping)

11. [Reflection and Hibernate Internals](#11-reflection-and-hibernate-internals)
    - [`@Entity`](#entity)
    - [Why JPA Requires No-Arg Constructor](#why-jpa-requires-no-arg-constructor)

12. [Reflection Workflow](#12-reflection-workflow)

13. [Advantages](#13-advantages)

14. [Disadvantages](#14-disadvantages)

15. [Common Interview Questions](#15-common-interview-questions)

16. [Reflection Cheat Sheet](#16-reflection-cheat-sheet)

17. [Final Summary](#final-summary)

---

# 1. What is Reflection?

Reflection is a Java feature that allows a program to:

- Inspect classes at runtime
- Inspect methods at runtime
- Inspect fields at runtime
- Inspect constructors at runtime
- Access annotations
- Create objects dynamically
- Invoke methods dynamically

In simple words:

> Reflection allows Java code to examine and manipulate itself while the application is running.

---

## Normal Java

```java
Employee emp = new Employee();
emp.display();
```

Everything is known at compile time.

---

## Reflection

```java
Class<?> cls = Class.forName("Employee");
```

The class is discovered at runtime.

---

# 2. Why Reflection Was Introduced

Sometimes a framework does not know:

- Which class will be used
- Which method will be called
- Which field will be accessed

until the application starts.

Reflection solves this problem.

---

## Real World Examples

### Spring

```java
@Service
class UserService {
}
```

Spring discovers and creates the object automatically.

---

### Hibernate

```java
@Entity
class Employee {
}
```

Hibernate automatically maps database rows to objects.

---

### JUnit

```java
@Test
public void loginTest() {
}
```

JUnit automatically discovers and executes test methods.

---

# 3. Reflection API Components

Reflection mainly works through four classes.

| Class | Purpose |
|---------|----------|
| Class | Information about class |
| Field | Information about variables |
| Method | Information about methods |
| Constructor | Information about constructors |

---

# 4. Class Class (`java.lang.Class`)

## What is Class?

Every class loaded into JVM has a corresponding Class object.

Example:

```java
class Employee {
}
```

JVM internally creates:

```java
Class<Employee>
```

This object contains metadata about the class.

---

## Getting Class Object

### Method 1

```java
Class<Employee> cls = Employee.class;
```

---

### Method 2

```java
Employee emp = new Employee();

Class<?> cls = emp.getClass();
```

---

### Method 3

```java
Class<?> cls =
        Class.forName("Employee");
```

Used heavily by frameworks.

---

## Common Methods

### Get Class Name

```java
cls.getName();
```

---

### Get Simple Name

```java
cls.getSimpleName();
```

---

### Get Package

```java
cls.getPackage();
```

---

### Get Parent Class

```java
cls.getSuperclass();
```

---

### Get Interfaces

```java
cls.getInterfaces();
```

---

## Create Object Dynamically

```java
Object obj =
    cls.getDeclaredConstructor()
       .newInstance();
```

Equivalent to:

```java
new Employee();
```

---

# 5. Field Reflection (`java.lang.reflect.Field`)

Field represents a class variable.

---

## Example

```java
class Employee {
    private String name;
}
```

---

## Get Field

```java
Field field =
    Employee.class
            .getDeclaredField("name");
```

---

## Read Value

```java
field.setAccessible(true);

Object value =
        field.get(employee);
```

---

## Modify Value

```java
field.setAccessible(true);

field.set(employee, "Vrajesh");
```

---

## Why setAccessible(true)?

Allows Reflection to access private members.

```java
private String name;
```

can still be accessed.

---

## Common Methods

| Method | Purpose |
|----------|----------|
| getName() | Field name |
| getType() | Field type |
| get() | Read value |
| set() | Write value |
| setAccessible() | Access private field |

---

# 6. Method Reflection (`java.lang.reflect.Method`)

Method represents a class method.

---

## Example

```java
class Employee {

    public void display() {
        System.out.println("Hello");
    }
}
```

---

## Get Method

```java
Method method =
    Employee.class
            .getDeclaredMethod("display");
```

---

## Execute Method

```java
method.invoke(employee);
```

Output:

```text
Hello
```

---

## Method With Parameters

```java
Method method =
    Employee.class
            .getDeclaredMethod(
                "greet",
                String.class
            );

method.invoke(employee, "Vrajesh");
```

---

## Method Returning Value

```java
Object result =
        method.invoke(calc, 10, 20);
```

---

## Common Methods

| Method | Purpose |
|----------|----------|
| getName() | Method name |
| getReturnType() | Return type |
| getParameterTypes() | Parameters |
| invoke() | Execute method |
| setAccessible() | Access private method |

---

# 7. Constructor Reflection (`java.lang.reflect.Constructor`)

Constructor represents a class constructor.

---

## Example

```java
class Employee {

    public Employee() {
    }
}
```

---

## Get Constructor

```java
Constructor<Employee> constructor =
        Employee.class
                .getDeclaredConstructor();
```

---

## Create Object

```java
Employee emp =
        constructor.newInstance();
```

---

## Constructor With Parameters

```java
Constructor<Employee> constructor =
        Employee.class
                .getDeclaredConstructor(
                        String.class
                );

Employee emp =
        constructor.newInstance("Vrajesh");
```

---

## Common Methods

| Method | Purpose |
|----------|----------|
| getName() | Constructor name |
| getParameterCount() | Parameter count |
| getParameterTypes() | Parameter types |
| newInstance() | Create object |

---

# 8. Dynamic Invocation (`Method.invoke()`)

This is the heart of Reflection.

---

## Normal Method Call

```java
employee.display();
```

Method known at compile time.

---

## Dynamic Method Call

```java
Method method =
        Employee.class
                .getDeclaredMethod(
                        "display");

method.invoke(employee);
```

Method selected at runtime.

---

## Why Important?

Frameworks usually don't know which method will execute until runtime.

---

## Real Examples

### Spring MVC

```java
@GetMapping("/users")
public String users() {
}
```

Spring eventually executes:

```java
method.invoke(controller);
```

---

### JUnit

```java
@Test
public void loginTest() {
}
```

JUnit executes:

```java
method.invoke(testObject);
```

---

### Event Listener

```java
@EventListener
public void handle(Event e) {
}
```

Spring executes:

```java
method.invoke(bean, event);
```

---

# 9. Annotation Processing

Reflection can detect and read annotations.

---

## Check Annotation Exists

```java
cls.isAnnotationPresent(
        Service.class);
```

---

## Read Annotation

```java
GetMapping mapping =
        method.getAnnotation(
                GetMapping.class);
```

---

## Read Annotation Value

```java
mapping.value();
```

Output:

```text
/users
```

---

# Important Methods

| Method | Purpose |
|----------|----------|
| isAnnotationPresent() | Check annotation |
| getAnnotation() | Read annotation |

---

# Retention Policy Requirement

Reflection can read annotations only when:

```java
@Retention(RetentionPolicy.RUNTIME)
```

is used.

Example:

```java
@Retention(RetentionPolicy.RUNTIME)
@interface MyService {
}
```

---

# 10. Reflection and Spring Internals

---

## @Component / @Service / @Repository

```java
@Service
class UserService {
}
```

Spring:

```text
Scan Package
    ↓
Find Annotation
    ↓
Create Object
    ↓
Store Bean
```

---

## @Autowired

```java
@Autowired
private UserService service;
```

Spring:

```text
Find Field
    ↓
Find Annotation
    ↓
Locate Bean
    ↓
Inject Bean
```

Internally:

```java
field.set(controller, serviceObj);
```

---

## @GetMapping

```java
@GetMapping("/users")
```

Spring:

```text
Find Method
    ↓
Read URL
    ↓
Store Mapping
```

Request arrives:

```text
GET /users
```

Spring:

```java
method.invoke(controller);
```

---

# 11. Reflection and Hibernate Internals

---

## Entity

```java
@Entity
class Employee {
}
```

Hibernate:

```text
Find @Entity
    ↓
Register Entity
    ↓
Create Object
    ↓
Populate Fields
```

---

## Database Row

```text
id = 1
name = Vrajesh
```

Hibernate:

```java
Employee emp =
        constructor.newInstance();

field.set(emp, value);
```

---

## Why JPA Requires No-Arg Constructor

Hibernate creates objects like:

```java
constructor.newInstance();
```

Therefore every entity must have:

```java
public Employee() {
}
```

(or protected)

---

# 12. Reflection Workflow

Most frameworks follow this pattern:

```text
Load Class
      ↓
Check Annotation
      ↓
Create Object
      ↓
Inject Dependencies
      ↓
Invoke Methods
```

Detailed flow:

```text
Class
  ↓
Field
  ↓
Method
  ↓
Constructor
  ↓
Annotation
  ↓
invoke()
```

---

# 13. Advantages

✅ Dynamic behavior

✅ Plugin architecture

✅ Dependency Injection

✅ ORM frameworks

✅ Annotation processing

✅ Testing frameworks

---

# 14. Disadvantages

❌ Slower than normal calls

❌ Breaks encapsulation

❌ Harder to debug

❌ Harder to maintain

❌ Can access private members

---

# 15. Common Interview Questions

### What is Reflection?

Ability to inspect and manipulate classes, fields, methods, constructors, and annotations at runtime.

---

### Why is Reflection used?

To support dynamic behavior when class information is not known until runtime.

---

### What is the entry point of Reflection?

```java
Class
```

---

### Difference Between

```java
Employee.class
```

and

```java
Class.forName("Employee")
```

| Employee.class | Class.forName() |
|----------|----------|
| Compile-time | Runtime |
| Type-safe | Dynamic |

---

### Difference Between

```java
getField()
```

and

```java
getDeclaredField()
```

| getField() | getDeclaredField() |
|----------|----------|
| Public only | All fields |
| Includes parent classes | Current class only |

---

### Why is Reflection slower?

Because method lookup and access checks happen at runtime.

---

### Which Reflection method actually executes a method?

```java
method.invoke(...)
```

---

### Which methods are used for annotation processing?

```java
isAnnotationPresent()
```

```java
getAnnotation()
```

---

# 16. Reflection Cheat Sheet

## Get Class

```java
Employee.class
```

```java
obj.getClass()
```

```java
Class.forName("Employee")
```

---

## Get Field

```java
Field field =
    cls.getDeclaredField("name");
```

---

## Read Field

```java
field.get(obj);
```

---

## Write Field

```java
field.set(obj, value);
```

---

## Get Method

```java
Method method =
    cls.getDeclaredMethod("display");
```

---

## Execute Method

```java
method.invoke(obj);
```

---

## Get Constructor

```java
Constructor<?> c =
    cls.getDeclaredConstructor();
```

---

## Create Object

```java
c.newInstance();
```

---

## Check Annotation

```java
cls.isAnnotationPresent(
        Service.class);
```

---

## Read Annotation

```java
cls.getAnnotation(
        Service.class);
```

---

# Final Summary

Reflection allows Java programs and frameworks to:

- Discover classes
- Create objects
- Read fields
- Modify fields
- Discover methods
- Invoke methods
- Process annotations

at runtime.

This is the foundation of:

- Spring Framework
- Spring MVC
- Spring Dependency Injection
- Hibernate/JPA
- JUnit
- Many custom frameworks

> Reflection = "Examining and manipulating Java classes, methods, fields, constructors, and annotations at runtime."