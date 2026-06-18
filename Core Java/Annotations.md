# Table of Contents

- [1. What is an Annotation?](#1-what-is-an-annotation)
- [2. Why Annotations?](#2-why-annotations)
- [3. Built-in Annotations](#3-built-in-annotations)
  - [@Override](#override)
  - [@Deprecated](#deprecated)
  - [@SuppressWarnings](#suppresswarnings)
  - [@FunctionalInterface](#functionalinterface)
  - [@SafeVarargs](#safevarargs)
- [4. How Annotations Work Internally](#4-how-annotations-work-internally)
- [5. Meta Annotations](#5-meta-annotations)
  - [@Target](#target)
  - [@Retention](#retention)
  - [@Documented](#documented)
  - [@Inherited](#inherited)
- [6. What is @interface?](#6-what-is-interface)
- [7. Custom Annotations](#7-custom-annotations)
- [8. Annotation Elements (Attributes)](#8-annotation-elements-attributes)
  - [Default Values](#default-values)
  - [value() Shortcut](#value-shortcut)
  - [Arrays](#arrays)
  - [Nested Annotations](#nested-annotations)
- [9. Repeatable Annotations](#9-repeatable-annotations)
- [10. Composed Annotations (Spring)](#10-composed-annotations-spring)
- [11. Compile-Time vs Runtime Processing](#11-compile-time-vs-runtime-processing)
- [12. Interview Revision](#12-interview-revision)

---

# 1. What is an Annotation?

An annotation is **metadata about code**.

It provides extra information to:

- Compiler
- JVM
- Reflection
- Frameworks (Spring, Hibernate, JUnit, Lombok)

Example:

```java
@Override
public String toString() {
    return "Hello";
}
```

Annotation = Information, not business logic.

---

# 2. Why Annotations?

Before annotations:

```java
// This is a test method
public void testLogin() {
}
```

Comments cannot be understood by frameworks.

With annotations:

```java
@Test
public void testLogin() {
}
```

Frameworks can automatically detect and process it.

---

# 3. Built-in Annotations

## @Override

Verifies method overriding.

```java
@Override
public void display() {
}
```

### Why?

Compiler catches override mistakes.

---

## @Deprecated

Marks old API.

```java
@Deprecated
public void oldMethod() {
}
```

Compiler shows warning when used.

---

## @SuppressWarnings

Hides compiler warnings.

```java
@SuppressWarnings("unchecked")
```

Common values:

```java
@SuppressWarnings("unused")
@SuppressWarnings("unchecked")
@SuppressWarnings("deprecation")
```

---

## @FunctionalInterface

Marks Functional Interface.

```java
@FunctionalInterface
interface Calculator {
    int add(int a, int b);
}
```

Compiler ensures exactly one abstract method.

Used with Lambdas.

---

## @SafeVarargs

Suppresses generic varargs warnings.

```java
@SafeVarargs
public static <T> void print(T... values) {
}
```

Mostly used in framework/library code.

---

# 4. How Annotations Work Internally

Flow:

```text
Write Annotation
       ↓
Compile
       ↓
Stored in .class File
       ↓
JVM Loads Class
       ↓
Reflection Reads Annotation
       ↓
Framework Takes Action
```

Example:

```java
@Service
class UserService {
}
```

Spring scans classes:

```java
cls.isAnnotationPresent(Service.class);
```

If found:

```text
Create Bean
```

### Important

Annotation itself does nothing.

Framework reads it and decides what to do.

---

# 5. Meta Annotations

Meta Annotation = Annotation applied on another annotation.

Example:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface MyAnnotation {
}
```

---

## @Target

Defines where annotation can be used.

```java
@Target(ElementType.METHOD)
```

Common values:

| ElementType | Usage |
|------------|--------|
| TYPE | Class |
| METHOD | Method |
| FIELD | Field |
| CONSTRUCTOR | Constructor |
| PARAMETER | Parameter |
| ANNOTATION_TYPE | Annotation |

---

## @Retention

Defines annotation lifetime.

### SOURCE

```java
@Retention(RetentionPolicy.SOURCE)
```

Compiler only.

Example:

```java
@Override
```

---

### CLASS

```java
@Retention(RetentionPolicy.CLASS)
```

Stored in class file.

Not available through Reflection.

---

### RUNTIME

```java
@Retention(RetentionPolicy.RUNTIME)
```

Available through Reflection.

Required by Spring.

---

## @Documented

Includes annotation in JavaDocs.

```java
@Documented
```

Mostly used by framework/library developers.

---

## @Inherited

Allows child class to inherit parent annotation.

```java
@Inherited
```

Works only for class inheritance.

Does NOT work for:

- Methods
- Fields
- Interfaces

---

# 6. What is @interface?

Used to create custom annotations.

```java
@interface Author {
}
```

Similar to:

```java
class User {
}
```

or

```java
interface Vehicle {
}
```

---

Internally:

```java
@interface Author {
}
```

behaves like:

```java
interface Author extends Annotation {
}
```

---

# 7. Custom Annotations

## Define

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Author {
    String name();
}
```

---

## Use

```java
@Author(name = "Vrajesh")
class Book {
}
```

---

## Read

```java
Author author =
    Book.class.getAnnotation(Author.class);

System.out.println(author.name());
```

Output:

```text
Vrajesh
```

---

# 8. Annotation Elements (Attributes)

Elements are values inside annotations.

```java
@interface Author {
    String name();
    int version();
}
```

Usage:

```java
@Author(
    name = "Vrajesh",
    version = 1
)
```

---

## Default Values

```java
@interface Author {

    String name();

    int version() default 1;
}
```

Usage:

```java
@Author(name = "Vrajesh")
```

Result:

```text
version = 1
```

---

## value() Shortcut

Special Java feature.

```java
@interface Author {
    String value();
}
```

Normal:

```java
@Author(value = "Vrajesh")
```

Shortcut:

```java
@Author("Vrajesh")
```

Both are identical.

### Real Spring Examples

```java
@Component("userService")
```

=

```java
@Component(value = "userService")
```

---

## Arrays

```java
@interface Roles {
    String[] value();
}
```

Usage:

```java
@Roles({"ADMIN", "USER"})
```

---

## Nested Annotations

```java
@interface Author {
    String name();
}

@interface BookInfo {
    Author author();
}
```

Usage:

```java
@BookInfo(
    author = @Author(name = "Vrajesh")
)
```

---

## Allowed Return Types

```java
int
long
double
boolean
String
Class
Enum
Annotation
Array of above
```

Not Allowed:

```java
List
Map
Object
```

---

# 9. Repeatable Annotations

Allows same annotation multiple times.

## Define

```java
@Repeatable(Roles.class)
@interface Role {
    String value();
}
```

Container:

```java
@interface Roles {
    Role[] value();
}
```

---

## Use

```java
@Role("ADMIN")
@Role("USER")
class UserService {
}
```

Internally:

```java
@Roles({
    @Role("ADMIN"),
    @Role("USER")
})
```

---

# 10. Composed Annotations (Spring)

Annotation built using other annotations.

---

## @Service

Simplified:

```java
@Component
public @interface Service {
}
```

Usage:

```java
@Service
class UserService {
}
```

---

## @Repository

```java
@Component
public @interface Repository {
}
```

---

## @Controller

```java
@Component
public @interface Controller {
}
```

---

## @RestController

```java
@Controller
@ResponseBody
public @interface RestController {
}
```

Usage:

```java
@RestController
```

Equivalent:

```java
@Controller
@ResponseBody
```

---

## @GetMapping

```java
@RequestMapping(method = RequestMethod.GET)
public @interface GetMapping {
}
```

Usage:

```java
@GetMapping("/users")
```

Equivalent:

```java
@RequestMapping(
    value = "/users",
    method = RequestMethod.GET
)
```

---

# 11. Compile-Time vs Runtime Processing

## Compile-Time Processing (APT)

Example:

```java
@Getter
@Setter
class User {
}
```

Lombok generates code during compilation.

Flow:

```text
Source
   ↓
Compiler
   ↓
Lombok
   ↓
Generated Code
   ↓
.class
```

---

## Runtime Processing

Example:

```java
@Service
class UserService {
}
```

Spring reads annotation using Reflection after application starts.

Flow:

```text
Application Starts
       ↓
Reflection
       ↓
Find Annotation
       ↓
Take Action
```

---

## Lombok vs Spring

| Feature | Lombok | Spring |
|----------|----------|----------|
| Processing Time | Compile Time | Runtime |
| Uses Reflection | No | Yes |
| Generates Code | Yes | No |
| Creates Objects | No | Yes |
| Example | @Getter | @Service |

---

# 12. Interview Revision

## What is an Annotation?

Metadata about code.

---

## What does @Override do?

Verifies method overriding.

---

## Most Important Meta Annotation?

```java
@Retention(RetentionPolicy.RUNTIME)
```

Without Runtime retention, Spring cannot read annotations.

---

## Why can we write?

```java
@Component("userService")
```

instead of:

```java
@Component(value = "userService")
```

Because of the special:

```java
String value();
```

shortcut.

---

## How does Spring detect @Service?

Using Reflection.

```java
cls.isAnnotationPresent(Service.class);
```

---

## Difference Between Lombok and Spring Annotations?

Lombok:

```text
Compile Time
```

Spring:

```text
Runtime
```

---

# One-Minute Revision

- Annotation = Metadata about code.
- `@interface` creates custom annotation.
- `@Target` = Where annotation can be used.
- `@Retention` = How long annotation survives.
- `RUNTIME` retention is required for Reflection.
- Reflection reads annotations.
- Spring heavily relies on annotations.
- `value()` provides shortcut syntax.
- `@Repeatable` allows multiple same annotations.
- Composed annotations build annotations from annotations.
- Lombok works at Compile Time.
- Spring works at Runtime.
- Annotation itself does nothing; compiler/framework must read it.

---

# End