## Table of Contents

- [What is Optional?](#what-is-optional)
- [Why Was Optional Introduced?](#why-was-optional-introduced)
- [Creating Optional Objects](#creating-optional-objects)
  - [Optional.of()](#1-optionalof)
  - [Optional.empty()](#2-optionalempty)
  - [Optional.ofNullable()](#3-optionalofnullable)
- [Checking Whether a Value Exists](#checking-whether-a-value-exists)
  - [isPresent()](#ispresent)
  - [isEmpty()](#isempty-java-11)
  - [ifPresent()](#ifpresent)
- [Getting Values](#getting-values)
  - [get()](#get)
  - [orElse()](#orelse)
  - [orElseGet()](#orelseget)
  - [orElseThrow()](#orelsethrow)
- [Transforming Values with map()](#transforming-values-with-map)
- [Why flatMap() Exists](#why-flatmap-exists)
- [Real Spring Data JPA Usage](#real-spring-data-jpa-usage)
- [Best Practices](#best-practices)
- [Common Mistakes](#common-mistakes)
- [Quick Reference Table](#quick-reference-table)
- [Most Asked Interview Questions](#most-asked-interview-questions)
- [Golden Rules](#golden-rules)
- [One-Line Summary](#one-line-summary)

# Java Optional - Complete Revision Guide

## What is Optional?

`Optional<T>` is a container object introduced in Java 8 that may contain:

* A value (`present`)
* No value (`empty`)

It is mainly used to reduce accidental `NullPointerException` and make code more expressive.

---

## Why Was Optional Introduced?

Before Java 8:

```java
User user = findUser(id);

if(user != null) {
    System.out.println(user.getName());
}
```

Problem:

```java
user.getName(); // NullPointerException if user is null
```

Developers frequently forgot null checks.

Optional makes the possibility of missing values explicit.

```java
Optional<User> user = findUser(id);
```

Now it is immediately clear that:

> A user may or may not be found.

---

# Creating Optional Objects

## 1. Optional.of()

Use when you are 100% sure the value is not null.

```java
Optional<String> name = Optional.of("Vrajesh");
```

### If null is passed

```java
Optional.of(null);
```

Throws:

```java
NullPointerException
```

### Use Case

```java
String name = "Vrajesh";

Optional<String> opt = Optional.of(name);
```

---

## 2. Optional.empty()

Creates an empty Optional.

```java
Optional<String> name = Optional.empty();
```

Meaning:

> No value exists.

### Example

```java
Optional<User> user = Optional.empty();
```

---

## 3. Optional.ofNullable()

Use when the value may be null.

```java
String name = getName();

Optional<String> opt = Optional.ofNullable(name);
```

### If value exists

```java
name = "Vrajesh";
```

Result:

```java
Optional[Vrajesh]
```

### If value is null

```java
name = null;
```

Result:

```java
Optional.empty
```

### Most Commonly Used

Most database queries, APIs, and user inputs can return null, so this is the most commonly used creation method.

---

# Checking Whether a Value Exists

## isPresent()

Returns true if a value exists.

```java
Optional<String> name = Optional.of("Vrajesh");

name.isPresent(); // true
```

---

## isEmpty() (Java 11+)

Returns true if no value exists.

```java
Optional<String> name = Optional.empty();

name.isEmpty(); // true
```

---

## ifPresent()

Executes code only if a value exists.

```java
Optional<String> name = Optional.of("Vrajesh");

name.ifPresent(System.out::println);
```

Output:

```text
Vrajesh
```

If Optional is empty, nothing happens.

---

# Getting Values

## get()

Returns the value.

```java
Optional<String> name = Optional.of("Vrajesh");

String value = name.get();
```

### Danger

```java
Optional.empty().get();
```

Throws:

```java
NoSuchElementException
```

### Recommendation

Avoid direct use of `get()` whenever possible.

---

## orElse()

Returns a default value if Optional is empty.

```java
Optional<String> name = Optional.empty();

String value = name.orElse("Guest");
```

Output:

```text
Guest
```

---

## orElseGet(supplier)

Takes `Supplier` as an input.

Similar to `orElse()`, but creates the default value only when needed.

```java
String value = opt.orElseGet(() -> createDefaultUser());
```

### Why?

```java
opt.orElse(createDefaultUser());
```

`createDefaultUser()` executes even if the Optional already contains a value.

With:

```java
opt.orElseGet(() -> createDefaultUser());
```

The method executes only if Optional is empty.

---

## orElseThrow()

Throws an exception if no value exists.

```java
User user = opt.orElseThrow();
```

### Custom Exception

```java
User user = opt.orElseThrow(
    () -> new RuntimeException("User not found")
);
```

Very common in Spring applications.

---

# Transforming Values with map()

`map()` transforms the value inside an Optional.

## Example

```java
Optional<String> name = Optional.of("vrajesh");

Optional<String> upper = name.map(String::toUpperCase);
```

Result:

```java
Optional[VRAJESH]
```

---

## Real Example

```java
Optional<User> user = repo.findById(1);

Optional<String> name = user.map(User::getName);
```

### What Happens?

```text
Optional<User>
        ↓
      map()
        ↓
Optional<String>
```

---

## Multiple Transformations

```java
repo.findById(1)
    .map(User::getName)
    .map(String::toUpperCase)
    .orElse("Unknown");
```

---

# Why flatMap() Exists

## The Problem

Suppose:

```java
class User {
    Optional<Address> getAddress() {
        ...
    }
}
```

Now:

```java
user.map(User::getAddress);
```

Result:

```java
Optional<Optional<Address>>
```

This creates nested Optional objects.

---

## Solution: flatMap()

```java
Optional<Address> address =
    user.flatMap(User::getAddress);
```

Result:

```java
Optional<Address>
```

No nesting.

---

## Rule

### Use map()

When method returns:

```java
T -> R
```

Example:

```java
User -> String
```

---

### Use flatMap()

When method returns:

```java
T -> Optional<R>
```

Example:

```java
User -> Optional<Address>
```

---

## Real Example

```java
repo.findById(1)
    .flatMap(User::getAddress)
    .map(Address::getCity)
    .orElse("Unknown");
```

---

# Real Spring Data JPA Usage

Spring Data JPA:

```java
Optional<User> user = userRepository.findById(id);
```

Common pattern:

```java
User user =
    userRepository.findById(id)
                  .orElseThrow(
                      () -> new RuntimeException("User not found")
                  );
```

---

# Best Practices

## ✅ Use Optional as a Return Type

Good:

```java
public Optional<User> findUser(int id)
```

Meaning:

> User may or may not exist.

---

## ✅ Return Optional.empty()

Good:

```java
return Optional.empty();
```

Bad:

```java
return null;
```

---

## ✅ Use map(), flatMap(), orElse(), orElseThrow()

These methods make code cleaner and safer.

---

# Common Mistakes

## ❌ Using Optional as a Field

Bad:

```java
class User {
    Optional<String> name;
}
```

Good:

```java
class User {
    String name;
}
```

---

## ❌ Using Optional as a Method Parameter

Bad:

```java
public void save(Optional<String> name)
```

Good:

```java
public void save(String name)
```

---

## ❌ Calling get() Directly

Bad:

```java
User user = opt.get();
```

If Optional is empty:

```java
NoSuchElementException
```

---

## ❌ Using isPresent() + get()

Bad:

```java
if(opt.isPresent()) {
    User user = opt.get();
}
```

This behaves almost like old-style null checking.

Prefer:

```java
opt.ifPresent(...)
```

or

```java
opt.map(...)
```

or

```java
opt.orElse(...)
```

---

## ❌ Returning null from an Optional Method

Bad:

```java
public Optional<User> findUser() {
    return null;
}
```

Good:

```java
return Optional.empty();
```

---

## ❌ Using Optional Inside Collections

Avoid:

```java
List<Optional<User>>
```

Prefer:

```java
List<User>
```

An empty list already means no data.

---

# Quick Reference Table

| Method                       | Purpose                               |
| ---------------------------- | ------------------------------------- |
| `Optional.of(value)`         | Create Optional with non-null value   |
| `Optional.ofNullable(value)` | Create Optional that may contain null |
| `Optional.empty()`           | Create empty Optional                 |
| `isPresent()`                | Check if value exists                 |
| `isEmpty()`                  | Check if value is absent              |
| `ifPresent()`                | Execute code if value exists          |
| `get()`                      | Get value (use carefully)             |
| `orElse()`                   | Return default value                  |
| `orElseGet()`                | Lazily create default value           |
| `orElseThrow()`              | Throw exception if absent             |
| `map()`                      | Transform value                       |
| `flatMap()`                  | Transform value returning Optional    |

---

# Most Asked Interview Questions

## Difference Between of() and ofNullable()

| Method         | Null Allowed |
| -------------- | ------------ |
| `of()`         | No           |
| `ofNullable()` | Yes          |

---

## Difference Between orElse() and orElseGet()

### orElse()

```java
opt.orElse(createUser());
```

Fallback object is created immediately.

---

### orElseGet()

```java
opt.orElseGet(() -> createUser());
```

Fallback object is created only if needed.

---

## Difference Between map() and flatMap()

| Method      | Returns                       |
| ----------- | ----------------------------- |
| `map()`     | `Optional<R>`                 |
| `flatMap()` | `Optional<R>` without nesting |

### Use map()

```java
User -> String
```

### Use flatMap()

```java
User -> Optional<Address>
```

---

# Golden Rules

✅ Use Optional primarily as a return type.

✅ Use `Optional.empty()` instead of returning null.

✅ Prefer `map()`, `flatMap()`, `orElse()`, and `orElseThrow()`.

✅ Use `orElseGet()` when default value creation is expensive.

❌ Avoid Optional fields.

❌ Avoid Optional parameters.

❌ Avoid direct use of `get()`.

❌ Never return null from a method returning Optional.

---

# One-Line Summary

> Optional is a container that explicitly represents the presence or absence of a value, helping write safer, cleaner, and more expressive Java code while reducing NullPointerException issues.
