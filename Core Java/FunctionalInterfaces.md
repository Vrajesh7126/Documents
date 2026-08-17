# Java Functional Interfaces

## Index

1. [Function](#1-function)
2. [Predicate](#2-predicate)
3. [Consumer](#3-consumer)
4. [Supplier](#4-supplier)
5. [Combined Example](#5-combined-example)
6. [Quick Summary](#6-quick-summary)

# Java Stream API

## Index

- [What is Stream API?](#what-is-stream-api)
- [filter()](#filter)
- [map()](#map)
- [flatMap()](#flatmap)
- [reduce()](#reduce)
- [collect()](#collect)
- [groupingBy()](#groupingby)
- [partitioningBy()](#partitioningby)
- [Parallel Streams](#parallel-streams)
- [Combined Example](#combined-example)
- [Quick Summary](#quick-summary)

---

# 1. Function

### Purpose

Takes an input and returns an output.

### Syntax

```java
Function<Input, Output>
```

### Example

```java
Function<Integer, Integer> square = x -> x * x;

System.out.println(square.apply(5));
```

### Output

```text
25
```

### Real Use Case

Transform data.

```text
5 → 25
10 → 100
```

---

# 2. Predicate

### Purpose

Checks a condition and returns true or false.

### Syntax

```java
Predicate<Input>
```

### Example

```java
Predicate<Integer> isEven = x -> x % 2 == 0;

System.out.println(isEven.test(4));
System.out.println(isEven.test(3));
```

### Output

```text
true
false
```

### Real Use Case

Filtering data.

```text
4 → true
3 → false
```

---

# 3. Consumer

### Purpose

Consumes a value and performs an action.

Returns nothing.

### Syntax

```java
Consumer<Input>
```

### Example

```java
Consumer<Integer> print = x -> System.out.println(x);

print.accept(4);
```

### Output

```text
4
```

### Real Use Case

Printing, logging, sending notifications, etc.

```text
Input → Action → No Return
```

---

# 4. Supplier

### Purpose

Supplies a value.

Takes no input.

### Syntax

```java
Supplier<Output>
```

### Example

```java
Supplier<String> value = () -> "Vrajesh Vaghasiya";

System.out.println(value.get());
```

### Output

```text
Vrajesh Vaghasiya
```

### Real Use Case

Generate or provide data on demand.

```text
No Input → Output
```

---

# 5. Combined Example

Suppose we have:

```java
List<Integer> list = Arrays.asList(
    1,2,3,4,5,6,7,8,9,10
);
```

## Step 1: Predicate

Keep only even numbers.

```java
.filter(isEven)
```

Result:

```text
2, 4, 6, 8, 10
```

---

## Step 2: Function

Square each number.

```java
.map(square)
```

Result:

```text
4, 16, 36, 64, 100
```

---

## Step 3: Consumer

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

## Complete Flow

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

## Easy Way To Remember

```text
Function  → Convert something
Predicate → Check something
Consumer  → Use something
Supplier  → Give something
```

### Stream Flow

```java
list.stream()
    .filter(isEven)   // Predicate
    .map(square)      // Function
    .forEach(print);  // Consumer
```

```text
Filter → Transform → Consume
```


---

## What is Stream API?

### Purpose

Process collections in a clean and readable way.

### Without Stream

```java
for (Integer n : list) {
    System.out.println(n);
}
```

### With Stream

```java
list.stream()
    .forEach(System.out::println);
```

### Think

```text
Collection
    ↓
 Filter
    ↓
 Transform
    ↓
 Result
```

---

## filter()

### Purpose

Keep only matching elements.

### Uses

Predicate

### Example

```java
List<Integer> nums = List.of(1,2,3,4,5,6);

nums.stream()
    .filter(x -> x % 2 == 0)
    .forEach(System.out::println);
```

### Output

```text
2
4
6
```

### Think

```text
Keep what you want.
```

---

## map()

### Purpose

Transform one value into another.

### Uses

Function

### Example

```java
nums.stream()
    .map(x -> x * x)
    .forEach(System.out::println);
```

### Output

```text
1
4
9
16
25
36
```

### Think

```text
Convert data (one -> one)
```

```text
2 → 4
3 → 9
4 → 16
```

---

## flatMap()

### Purpose

Flatten nested collections.

### Example Data

```java
[
 [1,2],
 [3,4]
]
```

### Desired Result

```java
[1,2,3,4]
```

### Example

```java
List<List<Integer>> list = List.of(
    List.of(1,2),
    List.of(3,4)
);

list.stream()
    .flatMap(l -> l.stream())
    .forEach(System.out::println);
```

### Output

```text
1
2
3
4
```

### Think

```text
Remove nesting.
```

# map() vs flatMap()

```java
const numbers = [1, 2, 3];

const result = numbers.map(x => [x, x * 2]);

console.log(result);
// [[1, 2], [2, 4], [3, 6]]
// result is an array of arrays
```

```java
const numbers = [1, 2, 3];

const result = numbers.flatMap(x => [x, x * 2]);

console.log(result);
// [1, 2, 2, 4, 3, 6]
// nested arrays are flattened into a single array.
```

### Common Use case :

```java
const sentences = ["Hello world", "How are you"];

const words1 = sentences.map(s => s.split(" "));
console.log(words1);
// [["Hello", "world"], ["How", "are", "you"]]

const words2 = sentences.flatMap(s => s.split(" "));
console.log(words2);
// ["Hello", "world", "How", "are", "you"]
```

---

## reduce()

### Purpose

Combine many values into one value.

### Example

```java
int sum = nums.stream()
              .reduce(0, (a, b) -> a + b);

System.out.println(sum);
```

### Output

```text
21
```

### Think

```text
Many → One
```

---

## collect()

### Purpose

Convert Stream result into a List, Set, Map, etc.

### Example

```java
List<Integer> evenNumbers =
        nums.stream()
            .filter(x -> x % 2 == 0)
            .collect(Collectors.toList());
```

### Result

```text
[2, 4, 6]
```

### Think

```text
Stream → Collection
```

---

## groupingBy()

### Purpose

Group data based on a property.

### Example

```java
List<String> names =
        List.of("Raj", "Ram", "Amit", "Ankit");

Map<Integer, List<String>> result =
    names.stream()
         .collect(
             Collectors.groupingBy(String::length)
         );
```

### Result

```text
3 → [Raj, Ram]
4 → [Amit]
5 → [Ankit]
```

### Think

```text
Create groups.
```

---

## partitioningBy()

### Purpose

Split data into only 2 groups.

### Example

```java
Map<Boolean, List<Integer>> result =
    nums.stream()
        .collect(
            Collectors.partitioningBy(
                x -> x % 2 == 0
            )
        );
```

### Result

```text
true  → [2,4,6]
false → [1,3,5]
```

### Think

```text
Divide into 2 buckets.
```

---

## Parallel Streams

### Purpose

Process data using multiple threads.

### Normal Stream

```java
list.stream()
```

### Parallel Stream

```java
list.parallelStream()
```

### Example

```java
list.parallelStream()
    .forEach(System.out::println);
```

### Benefit

```text
Can be faster for large data.
```

### Caution

Order may change.

### Think

```text
stream()         = One worker

parallelStream() = Multiple workers
```

---

## Combined Example

```java
List<Integer> nums =
        List.of(1,2,3,4,5,6,7,8,9,10);

List<Integer> result =
        nums.stream()
            .filter(x -> x % 2 == 0)
            .map(x -> x * x)
            .collect(Collectors.toList());

System.out.println(result);
```

### Flow

```text
1,2,3,4,5,6,7,8,9,10
            ↓
         filter
            ↓
      2,4,6,8,10
            ↓
           map
            ↓
   4,16,36,64,100
            ↓
         collect
            ↓
 [4,16,36,64,100]
```

### Output

```text
[4,16,36,64,100]
```

---

## Quick Summary

| Method | Purpose |
|----------|----------|
| stream() | Start Stream |
| filter() | Keep matching data |
| map() | Transform data |
| flatMap() | Remove nesting |
| reduce() | Many → One |
| collect() | Stream → Collection |
| groupingBy() | Create groups |
| partitioningBy() | Create 2 groups |
| parallelStream() | Use multiple threads |

---

## Easy Way To Remember

```text
filter()        → Keep
map()           → Convert
flatMap()       → Flatten
reduce()        → Combine
collect()       → Store
groupingBy()    → Group
partitioningBy()→ Split into 2 groups
parallelStream()→ Process faster
```

### Typical Stream Flow

```java
list.stream()
    .filter(...)
    .map(...)
    .collect(...);
```

```text
Filter → Transform → Collect
```