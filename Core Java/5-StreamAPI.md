Streams are lazy.

Intermediate operations (map, filter, peek, etc.) don't execute until a terminal operation is invoked.

peek() is primarily for debugging/inspection, not for business logic or side effects.

Always remember: No terminal operation = No stream execution.

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
List<String> list = new ArrayList<>(
    Stream.of("A", "B", "C").toList()
);


list.add("D");   // Works