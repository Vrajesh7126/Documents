## Table of Contents

- [What is Optional?](#what-is-optional)
- [Creating Optional Objects](#creating-optional-objects)
- [Checking Values](#checking-values)
- [Getting Values](#getting-values)
- [Transforming Values](#transforming-values)
- [Best Practices & Common Mistakes](#best-practices--common-mistakes)
- [Quick Reference Table](#quick-reference-table)
- [Interview Questions](#interview-questions)
- [One-Line Summary](#one-line-summary)


## What is Optional?

`Optional<T>` (Java 8+) is a container that may or may not hold a value (`present`/`empty`). It makes the possibility of a missing value explicit in the API, reducing accidental `NullPointerException`.

```java
// Before: easy to forget null check → NPE on user.getName()
User user = findUser(id);

// After: explicit that a value may be absent
Optional<User> user = findUser(id);
```

---

# Creating Optional Objects

| Method | When to Use | Null Input Behavior |
|---|---|---|
| `Optional.of(value)` | Value is guaranteed non-null | Throws `NullPointerException` |
| `Optional.ofNullable(value)` | Value may be null (most common – DB/API/user input) | Returns `Optional.empty()` |
| `Optional.empty()` | Explicitly represent "no value" | N/A |

```java
Optional<String> a = Optional.of("Vrajesh");        // must be non-null
Optional<String> b = Optional.ofNullable(getName()); // safe for nullable values
Optional<User> c = Optional.empty();                 // no value
```

---

# Checking Values

```java
Optional<String> name = Optional.of("Vrajesh");

name.isPresent();               // true if value exists
name.isEmpty();                 // true if no value (Java 11+)
name.ifPresent(System.out::println); // runs only if present, does nothing otherwise
```

---

# Getting Values

| Method | Behavior |
|---|---|
| `get()` | Returns value; throws `NoSuchElementException` if empty — **avoid direct use** |
| `orElse(default)` | Returns default if empty; **default is always evaluated eagerly** |
| `orElseGet(supplier)` | Returns default if empty; **supplier runs lazily**, only when needed |
| `orElseThrow(supplier)` | Throws custom exception if empty (common in Spring) |

```java
String value = name.orElse("Guest");                 // "Guest" built eagerly, even if present
String value2 = name.orElseGet(() -> createDefault()); // built only when Optional is empty

User user = repo.findById(id)
                 .orElseThrow(() -> new RuntimeException("User not found"));
```

> Prefer `orElseGet()` over `orElse()` when the default value is expensive to create.

---

# Transforming Values

## map() — for `T -> R`

Transforms the contained value; no-op if empty.

```java
Optional<String> name = repo.findById(1).map(User::getName);

repo.findById(1)
    .map(User::getName)
    .map(String::toUpperCase)
    .orElse("Unknown");
```

## flatMap() — for `T -> Optional<R>`

Avoids nested `Optional<Optional<R>>` when the mapping function itself returns an `Optional`.

```java
// user.map(User::getAddress) would give Optional<Optional<Address>>
Optional<Address> address = user.flatMap(User::getAddress);

repo.findById(1)
    .flatMap(User::getAddress)
    .map(Address::getCity)
    .orElse("Unknown");
```

Common Spring Data JPA pattern:

```java
User user = userRepository.findById(id)
                           .orElseThrow(() -> new RuntimeException("User not found"));
```

---

# Best Practices & Common Mistakes

✅ Use `Optional` as a **return type** only — e.g. `Optional<User> findUser(int id)`.

✅ Return `Optional.empty()` instead of `null`.

✅ Prefer `map()`, `flatMap()`, `orElse()`, `orElseThrow()` over manual checks.

✅ Use `orElseGet()` when the fallback is expensive to compute.

❌ Don't use `Optional` as a class **field** or **method parameter** — use plain types instead.

❌ Don't call `get()` directly, or use `isPresent()` + `get()` (defeats the purpose — same as null checks). Prefer `ifPresent()`, `map()`, or `orElse()`.

❌ Never return `null` from a method whose return type is `Optional`.

❌ Avoid `List<Optional<User>>` — an empty `List<User>` already conveys "no data".

---

# Quick Reference Table

| Method | Purpose |
| --- | --- |
| `Optional.of(value)` | Create with non-null value |
| `Optional.ofNullable(value)` | Create that may hold null |
| `Optional.empty()` | Create empty |
| `isPresent()` / `isEmpty()` | Check presence/absence |
| `ifPresent()` | Execute code if present |
| `get()` | Get value (use carefully) |
| `orElse()` | Return default (eager) |
| `orElseGet()` | Return default (lazy) |
| `orElseThrow()` | Throw if absent |
| `map()` | Transform value (`T -> R`) |
| `flatMap()` | Transform, avoiding nesting (`T -> Optional<R>`) |

---

# Interview Questions

**`of()` vs `ofNullable()`** — `of()` rejects null (NPE); `ofNullable()` accepts null and becomes empty.

**`orElse()` vs `orElseGet()`** — `orElse()` builds the fallback eagerly (always); `orElseGet()` builds it lazily (only if empty).

**`map()` vs `flatMap()`** — `map()` wraps the result in `Optional`; `flatMap()` expects the mapper to already return an `Optional`, avoiding double-wrapping.

---

# One-Line Summary

> Optional explicitly represents the presence or absence of a value, helping write safer, cleaner code while reducing `NullPointerException` — use it as a return type, never as a field, parameter, or null substitute.
