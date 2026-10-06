# Functional Programming Basics

Functional Programming is a programming style where we build programs using functions. 

It focuses on what to do rather than how to do it.

## 1. Lamdba (Function as Behavior)

A function repersent as a some behavior/action.

```java
Runnable runnable = () -> System.out.println("Hello, World!");
```

## 2. Function as Value (can be assigned to a variable, passed as an argument, or returned from another function)

```java
// square is a First-Class Function bcoz it can be assigned to a variable, passed as an argument, or returned from another function
Function<Integer, Integer> square = x -> x * x;

// assigned to a variable
System.out.println(square.apply(5));

// pass a function as an argument
// calculate is a Higher-Order Function bcoz it takes a function as an argument
static int calculate(int value, Function<Integer, Integer> function) {
    return function.apply(value);
}

// return from another function
// getSquareFunction is a Higher-Order Function bcoz it returns a function
static Function<Integer, Integer> getSquareFunction() {
    return x -> x * x;
}
```

## 3. Declarative Programming

Focus on what to do rather than how to do it.

```java
// Imperative Programming
for (String item : list) {
    if(item.length() > 0) {
        System.out.println(item);
    }
}

// Functional Programming (Declarative way)
list.stream()
    .filter(item -> item.length() > 0)
    .forEach(System.out::println);
```

## 4. Immutability

FP does not modify existing data, it creates new data instead.

```java
List<Integer> numbers = List.of(1, 2, 3);

List<Integer> squaredNumbers = numbers.stream()
                                        .map(x -> x * x)
                                        .toList();

// numbers remains unchanged
```

## 5. Pure Function

A pure function is a function that, given the same input, will always return the same output and has no side effects.

```java
Function<Integer, Integer> square = x -> x * x;
```

# Functional Interfaces

Interface with only one abstract method.

It could be implemented as a lambda expression.

`@FunctionalInterface` allows only one abstract method in the interface. If we declare an another abstract method, it gives a compilation error.

```java
@FunctionalInterface
interface MyFunctionalInterface {
    void execute();
}
```

`@FunctionalInterface` can have default method because we can add an additional behaviour without breaking existing implementations.

`@FunctionalInterface` can have static method because we can add utility methods related to the interface.

```java
@FunctionalInterface
interface MyFunctionalInterface {
    void execute();

    default void defaultMethod() {

    }

    static void staticMethod() {

    }
}
```

`@FunctionalInterface` can inherit from another interface as long as it does not add another abstract method.

```java
@FunctionalInterface
interface ParentInterface {
    void parentMethod();
}

@FunctionalInterface
interface ChildInterface extends ParentInterface {
    // No additional abstract method, still a functional interface

    void childMethod(); // ❌ 2 abstract methods are not allowed.
}
```

Functional interface with 1 abstract method + `Object` class's method is still considered a functional interface.

```java
// It is still considered as a functional interface
@FunctionalInterface
interface MyFunctionalInterface {
    void execute();

    // Object class's equal method is allowed
    boolean equals(Object obj);

    // Object class's hashCode method is allowed
    int hashCode();

    // Object class's toString method is allowed
    String toString();
} 
```

## Built-in Functional Interfaces

1. [Function](#1-function)
2. [Predicate](#2-predicate)
3. [Consumer](#3-consumer)
4. [Supplier](#4-supplier)
5. [Combined Example](#5-combined-example)
6. [Quick Summary](#6-quick-summary)

### 1. Function

#### Purpose

Takes an input and returns an output.

#### Syntax

```java
Function<Input, Output>
```

#### Example

```java
Function<Integer, Integer> square = x -> x * x;

System.out.println(square.apply(5));
```

#### Output

```text
25
```

#### Real Use Case

Transform data.

```text
5 → 25
10 → 100
```

---

### 2. Predicate

#### Purpose

Checks a condition and returns true or false.

#### Syntax

```java
Predicate<Input>
```

#### Example

```java
Predicate<Integer> isEven = x -> x % 2 == 0;

System.out.println(isEven.test(4));
System.out.println(isEven.test(3));
```

#### Output

```text
true
false
```

#### Real Use Case

Filtering data.

```text
4 → true
3 → false
```

---

### 3. Consumer

#### Purpose

Consumes a value and performs an action.

Returns nothing.

#### Syntax

```java
Consumer<Input>
```

#### Example

```java
Consumer<Integer> print = x -> System.out.println(x);

print.accept(4);
```

#### Output

```text
4
```

#### Real Use Case

Printing, logging, sending notifications, etc.

```text
Input → Action → No Return
```

---

### 4. Supplier

#### Purpose

Supplies a value.

Takes no input.

#### Syntax

```java
Supplier<Output>
```

#### Example

```java
Supplier<String> value = () -> "Vrajesh Vaghasiya";

System.out.println(value.get());
```

#### Output

```text
Vrajesh Vaghasiya
```

#### Real Use Case

Generate or provide data on demand.

```text
No Input → Output
```

---

### 5. Combined Example

Suppose we have:

```java
List<Integer> list = Arrays.asList(
    1,2,3,4,5,6,7,8,9,10
);
```

#### Step 1: Predicate

Keep only even numbers.

```java
.filter(isEven)
```

Result:

```text
2, 4, 6, 8, 10
```

---

#### Step 2: Function

Square each number.

```java
.map(square)
```

Result:

```text
4, 16, 36, 64, 100
```

---

#### Step 3: Consumer

Print each value.

```java
.forEach(print)
```

Output:

```text
4
16
36
64
100
```

---

### Complete Flow

```text
1,2,3,4,5,6,7,8,9,10
            │
            ▼
     Predicate
      (isEven)
            │
            ▼
    2,4,6,8,10
            │
            ▼
      Function
       (square)
            │
            ▼
  4,16,36,64,100
            │
            ▼
      Consumer
       (print)
            │
            ▼
Prints output
```

---

# 6. Quick Summary

| Interface | Method | Input | Output | Purpose |
|------------|----------|---------|----------|----------|
| Function<T,R> | apply() | Yes | Yes | Transform |
| Predicate<T> | test() | Yes | boolean | Check condition |
| Consumer<T> | accept() | Yes | No | Perform action |
| Supplier<T> | get() | No | Yes | Supply value |

---

### Stream Flow

```java
list.stream()
    .filter(isEven)   // Predicate
    .map(square)      // Function
    .forEach(print);  // Consumer
```

---

## Lambda

Short way to provide the implementation of a functional interface.

### Syntax

```java
// Expression lambda (single statement)
(parameters) -> expression

// Block lambda (multiple statements)
(parameters) -> {
    // multiple statements can be added here
}
```

In java, we either infer(અનુમાન લગાવવું) a param type or explicitly declare it.

```java
// explicit type
// We provide Integer x
Predicate<Integer> isEven = (Integer x) -> x % 2 == 0;

// inferred type
// Compiler infers the type of x from the generic type of the Predicate
Predicate<Integer> isOdd = x -> x % 2 != 0;
```

Variable used in lambda expression should be final or effectively final.

Problem If it will be change after being used in a lambda expression, it will cause a compilation error.

```java
static Runnable create() {
    int x = 10;

    Runnable r = () -> System.out.println(x);

    x = 20;

    return r;
}

Runnable r = create();
r.run();    // When it will be called then should it print 10 or 20?
// Due to this ambiguity, it will cause a compilation error.
// So java doesn't allow to modifying a variable used in a lambda expression, because lamdba may execute after the method create() has been finished.
```

`this` inside lambda refers to the object of the **enclosing class**.

```java
class Employee {
    String name = "Vrajesh";

    void printName() {
        Runnable r = () -> System.out.println(this.name);
        r.run();
    }
}

new Employee().printName(); // Vrajesh
```

In anonymous inner class, `this` refers to the instance of the anonymous class itself, not the enclosing class.

**Note :** Anonymous inner class means it has **No name**, **Declared and instantiated at the same time** & **Used when you need a one time implementation** of a class or interface.

```java
class Employee {
    String name = "Vrajesh";

    void printName() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println(this.name);  // Gives compilation error because `this` refers to the anonymous inner class, which doesn't have a `name` field.
            }
        };
        r.run();
    }
}

new Employee().printName();
```

### Limitations of Lambda Expressions

1. Needs Functional Interface to be implemented.
2. Cannot modify local variables used in lambda expressions.
3. Lambda doesn't have it's own `this` reference.
4. Lamdba doesn't have it's own `super` reference.
5. Can throw Unchecked exceptions but Checked exception depends on the functional interface's method signature.
    - `Runnable`'s method `run()` doesn't throw any checked exceptions, so their lambda expressions cannot throw checked exceptions.
    - We can create our own functional interface that allows throwing checked exceptions.

    ```java
    @FunctionalInterface
    interface Task {
        void execute() throws IOException;
    }

    Task task = () -> {
        throw new IOException();
    }
    ```

---
---


# Method References

Method reference is a shorter way to write a lamdba when it **only calls an existing method**.

```syntax
class/Object :: methodName
```

## 4 Types

### Static Method

```java
Function<Integer, Integer> func = Math::abs;
```

### Instance Method of a Particular Object

```java
String str = "hello";
Function<String, Integer> func = str::length;
```

### Instance Method of an Arbitrary Object

Arbitrary Object means **any object of that type**.
In below example, `String::length` refers to the `length` method of any `String` object passed to the function.

```java
Function<String, Integer> func = String::length;
```

### Constructor Reference

```java
Supplier<Employee> supplier = Employee::new;

// Parameterized constructor reference with 1 parameter (String)
Function<String, Employee> func = Employee::new;

// Parameterized constructor reference with 2 parameters (String, int)
BiFunction<String, Integer, Employee> func2 = Employee::new;

// Parameterized constructor reference with 3 parameters (String, int, double)
TriFunction<String, Integer, Double, Employee> func3 = Employee::new;
```

A Generic Constructor Reference is when the class itself is generic.

```java
class Box<T> {
    T value;

    Box(T value) {
        this.value = value;
    }
}

// Generic constructor reference
Function<String, Box<String>> genericFunc = Box::new;
           ↑            ↑
        Input       Return type
```

## Composition Methods

### 1. Function.andThen()

```syntax
f.andThen(g)

f -> g;
```

```java
Function<Integer, Integer> multiply = x -> x * 2;

Function<Integer, Integer> add = x -> x + 3;
```

```java
Function<Integer, Integer> combined = multiply.andThen(add);

System.out.println(combined.apply(5)); // (5 * 2) + 3 = 13
```

### 2. Function.compose()

```syntax
f.compose(g)

g -> f;
```

```java
Function<Integer, Integer> combined2 = multiply.compose(add);

System.out.println(combined2.apply(5)); // (5 + 3) * 2 = 16
```

### 3. Predicate.and()

```syntax
p.and(q)

p & q;
```

```java
Predicate<Integer> isPositive = x -> x > 0;

Predicate<Integer> isEven = x -> x % 2 == 0;
```

```java
Predicate<Integer> combined = isPositive.and(isEven);

System.out.println(combined.test(4)); // true
```

### 4. Predicate.or()

```syntax
p.or(q)

p | q;
```

```java
Predicate<Integer> combinedOr = isPositive.or(isEven);

System.out.println(combinedOr.test(5)); // true
```

### 5. Predicate.negate()

```syntax
Predicate<Integer> negated = p.negate();
```

```java
Predicate<Integer> isNegative = isPositive.negate();
```

### Consumer.andThen()

```syntax
c.andThen(d);

c -> d;
```

```java
Consumer<String> print = x -> System.out.println("Print: " + x);

Consumer<String> log = x -> System.out.println("Log: " + x);
```

```java
Consumer<String> combinedConsumer = print.andThen(log);

combinedConsumer.accept("Hello");
```
