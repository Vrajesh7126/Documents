
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

## Other Notes

Streams are lazy.

Intermediate operations (map, filter, peek, etc.) don't execute until a terminal operation is invoked.

`peek()` is primarily for debugging/inspection, not for business logic or side effects.

Always remember: **No terminal operation = No stream execution**

# Stream Execution Flow

```text
Stream created
      │
      ▼
map()
      │
      ▼
filter()
      │
      ▼
peek()
      │
      ▼
Nothing executes yet!
      │
      ▼
Terminal operation (forEach, collect, count, ...)
      │
      ▼
Entire pipeline executes
```

- peek() is for debugging.

# Collectors.toList() and Stream.toList()

## Collectors.toList()
```java
List<String> list = Stream.of("A", "B", "C")
                          .collect(Collectors.toList());

list.add("D");

System.out.println(list);

// Output : [A, B, C, D]
```

It returns modifiable list.

## Stream.toList() (Java 16+)

```java
List<String> list = Stream.of("A", "B", "C")
                          .toList();

list.add("D");

// Output :
// Exception in thread "main"
// java.lang.UnsupportedOperationException
```

returned list is unmodifiable.

If you need a modifiable list

```java
List<String> list = new ArrayList<>(
    Stream.of("A", "B", "C").toList()
);


list.add("D");   // Works
```                  