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

### ConcurrentHashMap
- Not allow null key or value.
- Why null key not allowed : null does not have hashcode(), and special handling for a null key whould complicated thread-safe design.
- Why null value not allowed : If map.get(null) retuns null, Is that means value is null or Key is absent ? so to remove this ambiguity, ConcurrentHashMap does not allows a null value.
- Thread Safe version of HashMap.
- Java 7 and below -> Segment Locking (Segment = Bunch of the bin)
- Java 8 and above -> CAS (Compare & swap) + Synchronized per bin locking
- Reads are non blocking.

### HashTable
- Thread safe.
- Entire Map gets locked (Every method synchronized)
- Not allows null key & value to simplify synchronization logic
- Default capacity = 11, Load facotr = 0.75

## CopyOnWriteArrayList
- Thread safe version of ArrayList.
- When you modify `add(), remove(), set()` it creates a new copy of an array that's why `Copy + Write = CopyOnWrite`, copy all elems and replace old array reference, O(n) complexty to copy the data.
- High memory usage, that's why garbage collection pressure.
- That's why no ConcurrentModificationException

```java
List<String> list = new ArrayList<>();

for(String s : list) {
    list.add("X"); // Throws ConcurrentModificationException
}

List<String> list = new CopyOnWriteArrayList<>();

for (String str : list) {
    list.add("ABC");    // works fine, because iteration use old ref array
}
```

- Disadvantage : Every write creates a full new array, so use when Read operations >>> Write Operations

```java
CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
list = [1, 2, 3];
list.add(4);

Old Array -> [1, 2, 3]      // Old readers continue safely using old array
New Array -> [1, 2, 3, 4]   // Reference switches to new array.
```

- When read operations `get(), iterator(), contains()` needs no locking

- Internally use :
```java
private transient volatile Object[] array;
// volatile : As soon as ArrayList modified, all threads immediately see latest version.
// Trinsient : Skip default Java serialization of this internal array; class handles serialization manually.
```

## Vector
- Lock on Read & Write.
- Lock on entire List

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
- `put()` stores data and return immediately & `transfer()` wait untill consumer arrives.
- `take()` waits if data is not available & `poll()` does not wait, returns null immedietly.
- Use CAS for synchronization.