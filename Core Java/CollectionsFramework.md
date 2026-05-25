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
- When you modify `add(), remove(), set()` it creates a new copy of an array that's why `Copy + Write = CopyOnWrite`, copy all elems and replace old array reference, O(n) complexty to copy the data
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

- Thread-safe queue.
