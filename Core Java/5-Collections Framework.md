Index

- [List](#list)
    - [ArrayList](#arraylist)
        - [`remove()` Overloading](#remove-overloading)
        - [`ConcurrentModificationException`](#concurrentmodificationexception)
    - [LinkedList](#linkedlist)
    - [Vector](#vector)
    - [Stack](#stack)
    - [CopyOnWriteArrayList](#copyonwritearraylist)
    - [`Arrays.asList()`](#arraysaslist)
    - [`List.of()`](#listof)
    - [`Collections.unmodifiableList()`](#collectionsunmodifiablelist)

- [Set](#set)
    - [HashSet](#hashset)
    - [LinkedHashSet](#linkedhashset)
    - [TreeSet](#treeset)
    - [CopyOnWriteArraySet](#copyonwritearrayset)
    - [ConcurrentSkipListSet](#concurrentskiplistset)

- [Queue](#queue)
    - [LinkedList as Queue](#linkedlist-as-queue)
    - [ArrayDeque as Queue](#arraydeque-as-queue)
    - [PriorityQueue](#priorityqueue)
    - [ConcurrentLinkedQueue](#concurrentlinkedqueue)
    - [BlockingQueue](#blockingqueue)
        - [ArrayBlockingQueue](#arrayblockingqueue)
        - [LinkedBlockingQueue](#linkedblockingqueue)
        - [LinkedBlockingDeque](#linkedblockingdeque)
        - [PriorityBlockingQueue](#priorityblockingqueue)
        - [DelayQueue](#delayqueue)
        - [SynchronousQueue](#synchronousqueue)
        - [LinkedTransferQueue](#linkedtransferqueue)
        - [PriorityTransferQueue](#prioritytransferqueue)

- [Map](#map)
    - [HashMap](#hashmap)
    - [ConcurrentHashMap](#concurrenthashmap)
    - [ConcurrentSkipListMap](#concurrentskiplistmap)

- [Ordering](#ordering)
    - [1. Comparable](#1-comparable)
    - [2. Comparator](#2-comparator)
    - [Comparator / Comparable with TreeSet / TreeMap](#comparator--comparable-with-treeset--treemap)
    - [compareTo() / compare() Result](#compareto--compare-result)
    - [Comparable vs Comparator](#comparable-vs-comparator)

---
---

# List

- Maintain insertion order.
- Allow duplicate elements.
- Allow index based access.
- Allow `null` depending on the implementation (`List.of()` does not allow `null`).


## ArrayList
- Resizable array.
- Allows `null` elements.
- Not synchronized (not thread safe).
- Access complexity: O(1) for get and set operations.
- Insertion complexity: O(1) amortized, O(n) in worst case when resizing occurs.
- Deletion complexity: O(n) because elements need to be shifted after removal.

### `remove()` Overloading

`ArrayList` has two commonly used `remove()` methods:

```java
remove(int index)
remove(Object o)
```

- `list.remove(1)` → `int` → **remove by index**.
- `list.remove(Integer.valueOf(20))` → `Integer` object → **remove by value**.

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

The iterator detects this modification and throws `ConcurrentModificationException`.

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


## LinkedList
- Doubly linked list.
- Allows `null` elements.
- Not synchronized (not thread safe).
- Access complexity: O(n) for get and set operations.
- Insertion complexity: O(1) if inserting at the beginning or end, O(n) otherwise.
- Deletion complexity: O(1) if deleting from the beginning or end, O(n) otherwise.
- LinkedList implements `List`, `Deque`, `Queue` interfaces.


## Vector
- Resizable array.
- Allows `null` elements.
- Synchronized (thread safe).
- Access complexity: O(1) for get and set operations.
- Insertion complexity: O(1) amortized, O(n) in worst case when resizing occurs.
- Deletion complexity: O(n) because elements need to be shifted after removal.


## Stack
- Stack extends `Vector`.
- Follows LIFO (Last In First Out) principle.
- `Stack` is a legacy class, `Deque` interface is preferred for new implementations.


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

**Key point:** `CopyOnWriteArrayList` never modifies the currently published array. It creates and publishes a new array, allowing existing readers to safely continue with their old snapshot.

**Concurrent:** The word concurrent means the collection was modified while it was being iterated, not necessarily by another thread.


## `Arrays.asList()`

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


## `List.of()`

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


## Collections.unmodifiableList()

```java
List<Integer> list = new ArrayList<>();
list.add(1);
list.add(2);

List<Integer> unmodifiableList = Collections.unmodifiableList(list);
```

- Creates a **read-only view** of the original list.
- Cannot `add()`, `remove()`, or `set()`.
- Changes to the original list are reflected in the unmodifiable list.

```java
unmodifiableList.add(3);        // ❌ UnsupportedOperationException
unmodifiableList.set(0, 10);    // ❌ UnsupportedOperationException
list.add(3);                    // ✅ Modifies the original list
System.out.println(unmodifiableList); // [1, 2, 3]
```

---
---

# Set
- Does **not allow duplicate elements**.

## HashSet
- HashSet is backed by a **HashMap** internally.
- Does not preserve the insertion order.
- Allows one **null**.
- `HashSet` uses `equals()` and `hashCode()` to determine the duplicate element exist or not.

## LinkedHashSet
- LinkedHashSet is backed by a **LinkedHashMap** internally.
- `LinkedHashSet` uses `equals()` and `hashCode()` to determine the duplicate element exist or not.
- Uses `HashTable` + `Doubly Linked List` internally to maintain the insertion order.

## TreeSet

- TreeSet is backed by a **TreeMap** internally.
- Maintains the elements in **sorted order** because it internally uses **Red-Black Tree**.
- `TreeSet` uses `compareTo()` (or `Comparator`) to determine the order and to check for duplicates.
- Does not allow `null` elements. It gives `NullPointerException` while inserting an element, because it compares it with other elements using `Comparable` or `Comparator`. Java can not compare `null` with other values, so it throws `NullPointerException`.
- It stores `null` only if a custom `Comparator` is used that can handle `null` values.
- TreeSet determines the uniqueness based on **Comparator** or **Comparable**, not by `equals()`.

```java
TreeSet<String> set = new TreeSet<>(Comparator.comparingInt(String::length));

set.add("ABC");
set.add("XYZ");

System.out.println(set.size()); // 1
```

## CopyOnWriteArraySet
- CopyOnWriteArraySet is backed by a **CopyOnWriteArrayList** internally.
- Thread-Safe.
- Does not allow `null` elements.
- Suitable for scenarios where reads are more frequent than writes.
- While `add()`, it takes O(n) because it search for an element in the entire set and if not found, it creates a new copy of the array.
- While `remove()`, it takes O(n) because it searches for the element in the entire set and if found, it creates a new copy of the array without that element.
- While `contains()`, it takes O(n) because it searches for the element in the entire set. (Linear search due to it used `CopyOnWriteArrayList` internally).


## ConcurrentSkipListSet
- ConcurrentSkipListSet is backed by a **ConcurrentSkipListMap** internally.
- It uses **skip list** data structure internally to maintain the sorted order efficiently.
- Maintains the elements in **sorted order**.
- **Thread-Safe**.
- Does not allow `null` elements.
- `ConcurrentSkipListSet` uses `compareTo()` (or `Comparator`) to determine the order and to check for duplicates.

---
---

# Queue

- FIFO.

## LinkedList as Queue
- Internally uses a **doubly-linked list** to store the elements.
- Allows `null` elements.
- **Not thread-safe**.
- Commonly used methods:
  - `offer(E e)` : O(1).
  - `poll()` : O(1). Returns `null` if the queue is empty.
  - `peek()` : O(1). Returns `null` if the queue is empty.


## ArrayDeque as Queue
- Internally uses a **resizable circular array** to store the elements. It grows when the array becomes full.
- Does not allow `null` elements.
- **Not thread-safe**.
- Commonly used methods:
  - `offer(E e)` : O(1) amortized.
  - `poll()` : O(1) amortized. Returns `null` if the queue is empty.
  - `peek()` : O(1) amortized. Returns `null` if the queue is empty.


## PriorityQueue
- Elements are processed according to their priority.
- Internally uses a **min heap** by default to store the elements (Smallest element at the root).
- Does not allow `null` elements.
- **Not thread-safe**.
- Commonly used methods:
  - `offer(E e)` : O(log n).
  - `poll()` : O(log n). Returns `null` if the queue is empty.
  - `peek()` : O(1). Returns `null` if the queue is empty.


## ConcurrentLinkedQueue
- Internally uses a **linked list** to store the elements.
- **Thread safe**.
- Internally uses CAS (Compare and Swap) for thread-safe operations. CAS atomically updates a shared value only if it is still unchanged; if another thread changed it first, the CAS fails and the thread retries.
- Does not allow `null` elements to remove ambiguity whether a `null` element is returned or the element is not available.
- Commonly used methods:
  - `offer(E e)` : O(1).
  - `poll()` : O(1). Returns `null` if the queue is empty.
  - `peek()` : O(1). Returns `null` if the queue is empty.


## BlockingQueue

- **Thread-safe queue**, mainly used in Producer-Consumer problems.
- Internally **Reentrant Lock** + **Conditions (await() / signal())** were used.
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
- **Fixed size array**.
- **Single lock** because put() and take() is going to perform into a single array.

### LinkedBlockingQueue
- Linked list based queue.
- Size is **optionally bounded** (can be set during construction).
- **Separate put/take locks**, so both producer and consumer can put and take a data into the queue simultaniously.
    
### LinkedBlockingDeque
- LinkedList based deque (insertion and removal from both ends).
- **Single lock**.
- Mainly use for a task scheduling, Urgent task is added at head and normal task added at tail.

### PriorityBlockingQueue
- Elements sorted by priority.
- **Single lock** for thread safety.
- **Unbounded queue**, so `put()` does not block for capacity.
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
- When producer/consumer is not able to find a match, They calls `LockSupport.park()`
- When matching thread arrives, `LockSupport.unpark(thread)` calls.
- Internally `new SynchronousQueue()` uses TransferStack (LIFO), so last waiting thread gets matched first & `new SynchronousQueue(true)` uses TransferQueue (FIFO).
- `CachedThreadPool` uses `SynchronousQueue` because submitted task must be handed off directly to a worker thread without being queued.

### LinkedTransferQueue
- LinkedBlockingQueue + SynchronousQueue
- `put()` stores data and return immediately & `transfer()` wait untill consumer arrives(wait using park() & unpark()).
- `take()` waits if data is not available & `poll()` does not wait, returns null immedietly if data is not available.
- Use CAS for synchronization.

## PriorityTransferQueue
- Combines features of `PriorityBlockingQueue` and `LinkedTransferQueue`.
- Elements are ordered by priority.
- Supports direct handoff between producer and consumer threads.
- `transfer()` waits until a consumer receives the element.
- `put()` adds the element to the queue immediately.
- `take()` waits if no elements are available.
- Uses CAS for synchronization.

---
---

# Map

- Stored data as key-value pairs.

## HashMap
- Allows 1 null key(pair with null key store at bucket-0) and multiples of null values.
- Not Thread safe.
- Working : Key -> hashcode -> Decides buckets & equals used to compare an exact key.
- Default capacity = 16, Load factor = 0.75
- Size > capacity * Load Factor -> Resize occurs.
- If collision occurs, Linked List(O(n)) & If Bucket size > 8 and Table size >= 64, Treeify to Red black tree (O(log n)) in Java 8 & above.

Always use **immutable class as a key** or use a immutable fields of a class into the `hashCode()` and `equals()`

HashMap calls `equals()` if `hashCode()` matches.

#### equals and hashCode()
- If we create a HashMap with a key as a custom class and we did not override the `equals()` and `hashCode()` functions, then equals() compare memory address and hashCode() generates a value based on object's identity, so we should implement equals() and hashCode() if we have used a custom class as a HashMap's key.
- If two objects are equal (equals() returns true), they must return the same hashCode().
- Two different objects can have the same hashCode().
- Always use a **immutable objects** as HashMap key, it prevents modification in key once it will be inserted into the HashMap, because If we use mutable objects and someone modify the key after an insertion then we are going to find or remove the key from the HashMap then it will not found, because HashCode() will be different.
- This rule applies on `HashMap`, `HashSet`, `LinkedHashMap`, `LinkedHashSet`, `ConcurrentHashMap`


## LinkedHashMap
- Maintains **insertion order** or access order.
- **Linked list** + **Hash table**
- Allows 1 null key and multiple null values.
- Not Thread safe.


## TreeMap
- Keeps entries sorted by key.
- Internally uses a **Red-Black tree** for storing entries.
- Not Thread safe.
- Null key not allowed because it compares keys to find a position in tree, and `null` is not comparable. but multiple null values are allowed.


## HashTable
- Thread safe.
- Entire Map gets locked (Every method synchronized)
- Not allows null key & value to simplify synchronization logic
- Default capacity = 11, Load factor = 0.75


## ConcurrentHashMap
- Not allow null key or value.
- Why null key not allowed : Special handling for a null key would complicate thread-safe design.
- Why null value not allowed : Because in a multi-threaded environment, null would make it impossible to know whether a key is actually missing or another thread changed the map, so to avoid this ambiguity, null values are not allowed.
- **Thread Safe version of HashMap**.
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
Before Step 2 → null (the key is not visible yet)
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


## ConcurrentSkipListMap 
- **Thread safe**.
- **Sorted by key**.
- Implements a **skip list** data structure internally.

---
---

# Ordering

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
List<Employee> list = new ArrayList<>();

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
- The class can't / shouldn't be modified (e.g., third-party class).

---

## Comparator / Comparable with TreeSet / TreeMap

`TreeSet` and `TreeMap` need a way to compare keys/elements to maintain sorted order.

Without `Comparable` or `Comparator`,

```java
TreeSet<Employee> set = new TreeSet<>();
set.add(new Employee(1, "A"));
```

May result in,

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
