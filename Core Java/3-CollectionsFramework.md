# Collection Framework
---
## HashMap vs ConcurrentHashMap vs HashTable

### HashMap
- Implementation of the Map Interface.
- Allows 1 null key(pair with null key store at bucket-0) and multiples of null values.
- Not Thread safe.
- Working : Key -> hashcode -> Decides buckets & equals used to compare an exact key.
- Default capacity = 16, Load factor = 0.75
- Size > capacity * Load Factor -> Resize occurs.
- If collision occurs, Linked List(O(n)) & If Bucket size > 8 and Table size >= 64, Treeify to Red black tree (O(log n)) in Java 8 & above.

Always use **immutable class as a key** or use a immutable fields of a class into the `hashCode()` and `equals()`

HashMap calls `equals()` if `hashCode()` matches.

#### equals and hashCode()
- If we create a HashMap with a key as a custom class and we did not override the equals() and hashCode() functions, then equals() compare memory address and hashCode() generates a value based on object's identity, so we should implement equals() and hashCode() if we have used a custom class as a HashMap's key.
- If two objects are equal (equals() returns true), they must return the same hashCode().
- Two different objects can have the same hashCode().
- Always use a **immutable objects** as HashMap key, it prevents modification in key once it will be inserted into the HashMap, because If we use mutable objects and someone modify the key after an insertion then we are going to find or remove the key from the HashMap then it will not found, because HashCode() will be different.
- This rule applies on `HashMap`, `HashSet`, `LinkedHashMap`, `LinkedHashSet`, `ConcurrentHashMap`

### ConcurrentHashMap
- Not allow null key or value.
- Why null key not allowed : Special handling for a null key whould complicated thread-safe design.
- Why null value not allowed : Because in a multi-threaded environment, null would make it impossible to know whether a key is actually missing or another thread changed the map, so to avoid this ambiguity, null values are not allowed.
- Thread Safe version of HashMap.
- Java 7 and below -> Segment Locking (Segment = Bunch of the bin)
- Java 8 and above -> CAS (Compare & swap) + Synchronized per bin locking (If the bucket is empty (null), use CAS. Otherwise, use bin locking for put() operations).
- Reads are non blocking, why :

```java
// Suppose the map contains:
1 -> A

// Now Thread 1 executes:
map.put(2, B);
```
- Step 1: Create the new node (2, B) completely in memory (No other thread can see it yet.)
- Step 2: Link the new node into the bucket (Now other threads can see it.)
- At the same time, Thread 2 calls:
```java
map.get(2);
```

There are only two possibilities:

```java
Before Step 2 → null (the key isn't visible yet)
After Step 2 → "B"
```
- Because The bucket reference is volatile.
- It can never see something like:
```java
2 -> ?
// or
2 -> partially created object
```

- because the node is published only after it is fully constructed.

### HashTable
- Thread safe.
- Entire Map gets locked (Every method synchronized)
- Not allows null key & value to simplify synchronization logic
- Default capacity = 11, Load facotr = 0.75

## HashSet vs TreeSet

`HashSet` allows `null` value, `TreeSet` does not

`TreeSet` gives `NullPointerException`. while insert an element, because it compare it with other elements using `Comparable` or `Comparator`. Java can not compare `null` with other values, so it throws `NullPointerException`.

`TreeSet` store `null`, if you provide `Comprator` which knows how to handle `null`.

```java
TreeSet<String> set = new TreeSet<>(Comparator.nullsFirst(String::compareTo));

// or nullsLast()
```

`HashSet` uses hashing.
`TreeSet` uses sorting/comparasion.

# Comparable vs Comparator

Both are used for **ordering/sorting objects**, especially with `Collections.sort()`, `TreeSet`, `TreeMap`, and `PriorityQueue`.

## Why?

Java knows how to compare types like `Integer`, but for a custom class:

```java
class Employee {
    int id;
    String name;
}
```

Java doesn't know whether to sort by `id`, `name`, salary, etc.

---

## 1. Comparable

Use `Comparable` when a class has **one natural/default ordering**.

```java
class Employee implements Comparable<Employee> {

    int id;
    String name;

    @Override
    public int compareTo(Employee e) {
        return Integer.compare(this.id, e.id);
    }
}
```

Now:

```java
Collections.sort(list);
```

uses `compareTo()`.

### Used by

- `Collections.sort()`
- `Arrays.sort()`
- `TreeSet`
- `TreeMap`
- `PriorityQueue` when no `Comparator` is provided

---

## 2. Comparator

Use `Comparator` when you need **custom or multiple sorting orders**.

```java
Comparator<Employee> byName =
    Comparator.comparing(Employee::getName);

Collections.sort(list, byName);
```

Descending:

```java
Comparator<Employee> byIdDesc =
    Comparator.comparing(Employee::getId).reversed();
```

Multiple fields:

```java
Comparator<Employee> comparator =
    Comparator.comparing(Employee::getName)
              .thenComparing(Employee::getId);
```

Useful when:
- Multiple sorting rules are needed.
- The class cannot/shouldn't be modified (e.g., third-party class).

---

## TreeSet / TreeMap

`TreeSet` and `TreeMap` need a way to compare keys/elements to maintain sorted order.

Without `Comparable` or `Comparator`:

```java
TreeSet<Employee> set = new TreeSet<>();
set.add(new Employee(1, "A"));
```

May result in:

```text
ClassCastException
```

because Java doesn't know how to compare `Employee` objects.

Provide either:

```java
class Employee implements Comparable<Employee>
```

or:

```java
TreeSet<Employee> set =
    new TreeSet<>(Comparator.comparing(Employee::getName));
```

---

## `compareTo()` / `compare()` Result

Both return:

```text
Negative → first object comes before second
Zero     → considered equal for ordering
Positive → first object comes after second
```

Example:

```java
Integer.compare(this.id, other.id);
```

```text
this.id   other.id   Result
1         3          Negative
3         3          0
5         3          Positive
```

> Prefer `Integer.compare()` instead of `this.id - other.id` because subtraction can overflow.

---

## Comparable vs Comparator

| Comparable | Comparator |
|---|---|
| `java.lang` | `java.util` |
| Implemented inside the class | Usually defined outside the class |
| Natural/default ordering | Custom ordering |
| Usually one natural order | Multiple possible orders |
| `compareTo()` | `compare()` |

**Note:** `equals()` and `compareTo()` should be consistent. If `compareTo(...) == 0` then ideally `equals(...) == true`

**Note:** Never implement `compareTo()` by subtracting numbers, it may gives overflow, use `Integer.compare(this.id, e.id)`

---
---

## ArrayList

### `remove()` Overloading

`ArrayList` has two commonly used `remove()` methods:

```java
remove(int index)
remove(Object o)
```

With `List<Integer>`, this can cause confusion:

```java
List<Integer> list = new ArrayList<>();
list.add(10);
list.add(20);
list.add(30);

list.remove(1);                    // Removes element at index 1 → 20
list.remove(Integer.valueOf(20));  // Removes element with value 20
```

- `list.remove(1)` → `int` → **remove by index**.
- `list.remove(Integer.valueOf(20))` → `Integer` object → **remove by value**.

---

### `Arrays.asList()`

```java
Integer[] arr = {1, 2, 3};

List<Integer> list = Arrays.asList(arr);
```

- Creates a **fixed-size List backed by the original array**.
- Cannot `add()` or `remove()`.
- Can modify existing elements using `set()`.

```java
list.set(0, 10);
```

Changes both:

```text
List → [10, 2, 3]
Array → [10, 2, 3]
```

And:

```java
arr[1] = 20;
```

Changes both:

```text
Array → [10, 20, 3]
List  → [10, 20, 3]
```

> `Arrays.asList()` and the original array share the **same underlying array**.

If you need a resizable `ArrayList`:

```java
List<Integer> list = new ArrayList<>(Arrays.asList(arr));
```

Now the `ArrayList` has its **own backing array**, so changes are independent of `arr`.

---

### `List.of()`

```java
List<Integer> list = List.of(1, 2, 3);
```

- Creates an **immutable list**.
- Cannot `add()`, `remove()`, or `set()`.
- Does **not allow `null`**.

```java
list.add(4);       // ❌ UnsupportedOperationException
list.set(0, 10);   // ❌ UnsupportedOperationException
List.of(1, null);  // ❌ NullPointerException
```

---

### `new ArrayList<>()`

```java
List<String> list = new ArrayList<>();

list.add("A");
list.add("B");
list.add("C");
```

- Fully **mutable/resizable**.
- Supports `add()`, `remove()`, and `set()`.
- Allows `null`.

---

### `ConcurrentModificationException`

```java
List<String> list = new ArrayList<>();

list.add("A");
list.add("B");
list.add("C");

for (String s : list) {

    if (s.equals("B")) {
        list.remove(s);  // ❌ ConcurrentModificationException
    }
}
```

Output:

```text
Exception in thread "main"
java.util.ConcurrentModificationException
```

The enhanced `for` loop internally uses an `Iterator`:

```java
Iterator<String> it = list.iterator();

while (it.hasNext()) {
    String s = it.next();
    // ...
}
```

When:

```java
list.remove(s);
```

is called, the collection is structurally modified **without informing the iterator**.

The iterator detects this modification and throws:

```text
ConcurrentModificationException
```

### Correct Way: `Iterator.remove()`

Use the iterator itself to remove the element:

```java
Iterator<String> it = list.iterator();

while (it.hasNext()) {

    String s = it.next();

    if (s.equals("B")) {
        it.remove();  // ✅ Safe
    }
}
```

- `it.remove()` removes the element that was **most recently returned by `next()`**.
- The iterator updates its internal state, so no `ConcurrentModificationException` occurs.

### Quick Comparison

```text
Arrays.asList()
→ Fixed size
→ Can set()
→ Backed by original array
→ Allows null

List.of()
→ Immutable
→ Cannot add/remove/set
→ Does NOT allow null

new ArrayList<>()
→ Resizable + mutable
→ Can add/remove/set
→ Allows null
```

---
---

## CopyOnWriteArrayList

- Thread-safe version of `ArrayList`.
- On every `add()`, `remove()`, `set()`:
  - Creates a **new array**.
  - Copies old elements → modifies new array → replaces old array reference.
  - Copying takes **O(n)** time.
- Because every write creates a new array:
  - Higher memory usage.
  - More GC pressure.
  - Best when **Read Operations >>> Write Operations**.
- Readers don't need locking.
- Writers use a **lock** to make the copy-and-replace operation thread-safe.
- Because the existing array is never modified, readers can safely continue using it.

```java
List<String> list = new ArrayList<>();

for (String s : list) {
    list.add("X"); // ❌ ConcurrentModificationException
}

List<String> list = new CopyOnWriteArrayList<>();

for (String str : list) {
    list.add("ABC"); // ✅ Safe
}
```

### How Write Works

```text
Current array
[A, B, C]
    ↓
Acquire write lock
    ↓
Create new array
[A, B, C, D]
    ↓
Modify new array
    ↓
array = newArray
    ↓
Release lock
```

Old array remains unchanged:

```text
Old Array → [A, B, C]       // Existing readers can continue
New Array → [A, B, C, D]    // New/current array
```

### Snapshot Iteration

- `iterator()` takes a **snapshot/reference of the current array when the iterator is created**.
- If another thread modifies the list afterward, the existing iterator continues using the old array.

```text
Initial:

array → [A, B, C]

Thread 1 creates iterator
iterator → [A, B, C]


Thread 2 writes:

array → [X, B, C, D]


Thread 1:
iterator → [A, B, C]       // Still sees old snapshot

New iterator:
iterator → [X, B, C, D]    // Sees new array
```

- Therefore, changes made after iterator creation are **not visible to that iterator**.

### `iterator()` vs `get()`

```text
iterator()
    ↓
Snapshot of array at iterator creation
    ↓
Does NOT see later modifications

get(index)
    ↓
Reads current array
    ↓
Sees latest published array
```

### Internal Array

```java
private transient volatile Object[] array;
```

- `private` → accessed internally by `CopyOnWriteArrayList`.
- `transient` → internal array isn't serialized directly; the class handles serialization itself.
- `volatile` → makes the **latest array reference visible to other threads**.

> `volatile` makes the reference visible; it does **not** make the entire write operation thread-safe. The write lock provides that.

### Reader + Writer

```text
Initial:
array → [A, B, C]

Thread 1 iterator → [A, B, C]

Thread 2:
    lock
      ↓
    copy
      ↓
    modify → [X, B, C, D]
      ↓
    array = newArray
      ↓
    unlock

Result:

Thread 1 iterator → [A, B, C]
Current array     → [X, B, C, D]
```

### Core Idea

```text
READ
 ↓
Read current/snapshot array
 ↓
No lock


WRITE
 ↓
Acquire lock
 ↓
Copy array
 ↓
Modify new array
 ↓
Publish new array reference
 ↓
Release lock
```

**Key point:** `CopyOnWriteArrayList` never modifies the currently published array. It creates and publishes a new array, allowing existing readers to safely continue with their old snapshot.

**Concurrent:** The word concurrent means the collection was modified while it was being iterated, not necessarily by another thread.

## Vector
- Lock on Read & Write.
- Lock on entire List

# Queue
```java
Queue (Interface)
│
├── PriorityQueue (Class)
├── ConcurrentLinkedQueue (Class)
├── Deque (Interface)
│     ├── ArrayDeque (Class)
│     └── LinkedList (Class)
│
└── BlockingQueue (Interface)
      ├── ArrayBlockingQueue
      ├── LinkedBlockingQueue
      ├── PriorityBlockingQueue
      └── DelayQueue
```

## PriorityQueue

## ConcurrentLinkedQueue

## BlockingQueue

- Thread-safe queue, mainly used in Producer-Consumer problems.
- Used in ExecutorService.

| Method           | Behavior               |
| ---------------- | ---------------------- |
| `put()`          | waits if full (If queue full → thread blocks)         |
| `take()`         | waits if empty (If queue empty → thread blocks)        |
| `offer()`        | returns false if full  |
| `poll()`         | returns null if empty  |
| `offer(timeout)` | waits for limited time |
| `poll(timeout)`  | waits for limited time |

Example :

```java
BlockingQueue<Integer> queue =
        new ArrayBlockingQueue<>(2);

Thread producer = new Thread(() -> {
    try {
        queue.put(1);
        queue.put(2);
        queue.put(3); // waits because full
    } catch (Exception e) {}
});

Thread consumer = new Thread(() -> {
    try {
        Thread.sleep(2000);

        System.out.println(queue.take());
    } catch (Exception e) {}
});

producer.start();
consumer.start();
```

- Internally Reentrant Lock + Conditions (await() / signal()) were used.

```java
Queue Full              |Queue Empty
------------------------|------------------------
Producer -> put()       |Consumer -> take()
          ↓             |          ↓
      queue full        |      queue empty
          ↓             |          ↓
    notFull.await()     |    notEmpty.await()
          ↓             |          ↓
Consumer -> take()      |Producer -> put()
          ↓             |          ↓
    notFull.signal()    |    notEmpty.signal()
          ↓             |          ↓
Producer wakes up       |Consumer wakes up
```

### ArrayBlockingQueue
- Fixed size array.
- Single lock because put() and take() is going to perform into a single array.

### LinkedBlockingQueue
- Linked list based queue.
- Separate put/take locks, so both producer and consumer can put and take a data into the queue simultaniously.
- `put()` blocks if queue is full.
- `take()` blocks if queue is empty.

### LinkedBlockingDeque
- LinkedList based deque (insertion and removal from both ends).
- Single lock.
- Mainly use for a task scheduling, Urgent task is added at head and normal task added at tail.

### PriorityBlockingQueue
- Elements sorted by priority.
- Single lock for a thread safety.
- Unbounded queue, so `put()` does not block for capacity.
- `take()` blocks if queue is empty.

### DelayQueue
- Elements become available only after delay time expires
- It internally uses Priority Queue (Unbounded), sorted by remaining delay time.
- `take()` calls, if delayed expired, return element, if Delay not expired, consumer thread waits until time finish.
- Every object inside the DelayQueue must tell `How much delay is remaining?` so must be implemented by `Delayed`, which implements `getDelay()` (Returns remaining delay) & `compareTo()` (Used for sorting).
- While calling `take()`, Leader thread does `condition.awaitNanos(delay)`, means sleep until timeout happens & JVM timer wakes thread automatically after delay. Then after timeout, Thread wakes up and checks, Is head element delay finished? If yes, `take()` returns element.
- Whenever queue is accessed, `ReentrantLock` were used while `put()` and `take()`.
- When queue is empty and calls `take()`, then consumer does `condition.await()`.
- Suppose current head has a 30 sec of timeout and producer modify to 5 sec, then queue does `condition.signal()` to wake leader.

### SynchronousQueue
- A queue with Capacity = 0, it designed for direct thread-to-thread handoff.
- When `put()` calls and no consumer is waiting, Producer creates a waiting node containing Thread reference & Data & Producer sleeps using `park()`. Now consumer arrives, finds waiting producer node, take a data & wakes producer using `unpark()`.
- When `take()` calls, and no producer available, consumer creates a waiting node, Consumer sleeps using `park()`, Now producer arrives, finds waiting consumer node, give data directly and wakes consumer using `unpark()`.
- `LockSupport` is a static class provides `park()` and `unpark()`.
- Use CAS when multiples of consumers come up with a producer node and vice-versa.
- When producer/consumer is not able to find a match, This sleep is `LockSupport.park()`
- When matching thread arrives, `LockSupport.unpark(thread)` calls.
- Internally `new SynchronousQueue()` uses TransferStack (LIFO), so last waiting thread gets matched first & `new SynchronousQueue(true)` uses TransferQueue (FIFO).
- `CachedThreadPool` uses `SynchronousQueue`.

### LinkedTransferQueue
- LinkedBlockingQueue + SynchronousQueue
- `put()` stores data and return immediately & `transfer()` wait untill consumer arrives(wait using park() & unpark()).
- `take()` waits if data is not available & `poll()` does not wait, returns null immedietly if data is not available.
- Use CAS for synchronization.