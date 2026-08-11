# Multithreading
---
## Basic

- Every Java program starts with one thread
- Need to override run() method.
- Java supports single Inheritance, so Runnable is used most.

### start() vs run()

- `start()` -> JVM create a new Thread and call run() internally. It transit the thread into a state where it can be scheduled for execution.
- `run()` -> Normal method call.

## Thread Lifecycle

**NEW** : Thread object created but not started
```java
Thread t = new Thread(); // NEW
```

**RUNNABLE** : Ready to run, waiting for the CPU
```java
t.start();  // RUNNABLE
```

**RUNNING** : Start executing when run() executes
```java
run();  // RUNNING
```

**BLOCKED** : Waiting to aquire a monitor lock
```java
synchronized(obj){

}
//or
lock.lock();
```

**WAITING** : Thread wait until another thread wakes it.
```java
wait(); // Waiting
join(); // Wait until another thread finish it's execution
```

**TIMED_WAITING** : Thread waits for a specific time
```java
// sleep for a sec
sleep(1000);

// wait for the 1 second or 
// another thread calls join() or 
// another thread interrupts it
wait(1000);

// wait for 1 sec or 
// another thread finish it's execution if execution time is less than 1 sec or 
// some another thread interrupt it
join(1000);
```

**TERMINATED** : Thread finish it's execution
```java
run();  // Execution over
```

### start() vs run()
- `start()` : Create a new thread, JVM calls run()
- `run()` :  Executed in current thread

- call start() twice, it gives `IllegalThreadStateException` exception, Because one thread object can start only once.

### sleep()

`sleep()` : Static method of a Thread class, not a t.sleep()
- must handle `InterruptedException` while using sleep()
- Why InterruptedException? : Another thread may interrupt sleeping thread.

- `NOTES` : Sleep does not release a lock, Wait release a lock.

```java
try {
    Thread.sleep(1000);
}
catch(InterruptedException e) {
}
```

### interrupt()
- Used to signal a thread. Stop waiting/sleeping/joining.

```java
t.interrupt();
```

- If a thread is not inside the sleep(), wait() or join(), then calling `thread.interrput()` does not throw Interrupted exception, instead java sets interrupt flag true.

```java
// we can check something like this
while(!Thread.currentThread().isInterrupted()){
    // do some work...
}
```
- When Java throws `InterruptedException`, it clears the thread's interrupted status. `interrupted` checks current thread & clear the status.

### join()

- One thread waits for another thread to finish.
- `join(2000)` : It waits maximum 2 seconds, If execution not finish then it keeps running in background.
- `join()` uses `wait()` internally. When the thread finishes executing `run()`, JVM internally calls `notifyAll()` on that thread object, which wakes up the thread waiting in `join()`.

### Daemon Thread
- Background thread (Ex. Garbage Collector).
- If only daemon threads remains, JVM exits.
```java
t.setDaemon(true);
t.start();
```

### Concurrency
- 1 CPU core switches between tasks very quickly.

### Parallelism
- Tasks literally run simultaneously on multiple CPU cores.

## Synchronization

- Allow only one thread at a time to access critical code.
- If exception occurs, lock automatically released.

### synchronized method

```java
class CriticalPart{
    int count = 0;

    public synchronized void increment() {
        count++;
    }
}
```

- Every Java object has Monitor Lock / Intrinsic Lock, synchronized uses that lock.
- When thread enters method Lock acquired, when thread exit Lock released.


### Synchronized Block

- Instead of whole method

```java
// Current object becomes a lock
synchronized(this) {
    count++;
}
```

### Object lock
- Why to use Object use even Iif I have a synchronized method or synchronized (this) block : 

```java
// creates a separate private lock object
Object lock = new Object();
synchronized(lock) {
    count++;
}
```

#### Importance of the Object lock :

```java
// BAD : even If print and increment will not affect each others, we have used the same object lock
class Counter{
    count = 0;
    public synchronized void print(){
        System.out.println(counter);
    }

    public void increment(){
        synchronized (this){
            counter++;
        }
    }
}

// GOOD : Use seperate lock, because they are not affect each others
class Counter{
    private final Object countLock = new Object();

    public synchronized void print(){
        System.out.println(counter);
    }

    public void increment(){
        synchronized (counterLock){
            counter++;
        }
    }
}
```

### Static Synchronization

- Lock applied on Class Object instead of object instance.

```java
public static synchronized void test() {
}
```

## Thread Communication Methods

```java
wait()
notify()
notifyAll()
```
- Must be called inside the synchronized method, otherwise throws `IllegalMonitorStateException` exception.
- Used for inter thread communication (One thread communicating with another thread)
- Part of java.lang.Object (Not a Thread class)

### wait()

- Thread **releases lock** and waits.
- There is a logical reason behind to release a lock while call wait() is, If wait() is release a lock then another thread can call notify and wait()'s ahead execution will be happens, otherwise it will not.
- wait() must be inside the synchronized block.
- Thread goes to **WAITING** state.
- Can wake up a WAITING thread of **notify()**, **notifyAll()** & **interrupt()** (thread gets `InterruptedException`)
- wait(5000) (Timeout into the thread)
- Spurious Wakeup (rarely JVM wakes up a WAITING thread, so we have used while (condition) {wait(); })

### notify() 

- Wakes one waiting thread.
- If multiples of WAITING thread, JVM decides which thread to wake.
- Thread goes to **RUNNABLE** state.
- notify() does NOT Immediately Run Thread, become eligible to compete for the lock, it makes a thread to it only moves thread from WAITING to BLOCKED/RUNNABLE. Actual execution depends on scheduler, lock availability.

### notifyAll()

- Wakes all waiting Thread.
- Then compete for the lock.
- One gets lock first, others wait again.

### interrupt()

- Another thread interrupts the waiting thread.
- If the thread was inside wait(), it immediately wakes up & throws `InterruptedException`.

### Producer-Consumer problem

- This is for a single producer-consumer problem.

```java
class Buffer {
    private int data;
    private boolean hasData = false;

    public synchronized void produce(int value) throws InterruptedException {

        while (hasData) {   // prevent from the suspious wakeup
            wait();
        }

        data = value;
        hasData = true;

        System.out.println("Produced: " + value);

        notify();
    }

    public synchronized void consume() throws InterruptedException {

        while (!hasData) {   // prevent from the suspious wakeup
            wait();
        }

        System.out.println("Consumed: " + data);

        hasData = false;

        notify();
    }
}

public class Main {

    public static void main(String[] args) {

        Buffer buffer = new Buffer();

        Thread producer = new Thread(() -> {

            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.produce(i);
                }
            } catch (Exception e) {
            }

        });

        Thread consumer = new Thread(() -> {

            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.consume();
                }
            } catch (Exception e) {
            }

        });

        producer.start();
        consumer.start();
    }
}
```

## Condition
- Use with `ReentrantLock`
- Instead of having one waiting queue per object, you can create a multiple waiting queues.
- Priblem with `wait()` and `notify()` :

```java
Object lock = new Object();

synchronzed(lock){
    lock.wait();
}
```

```java
Object(lock)
     |
     +-- Waiting Queue
         Producer-1
         Producer-2
         Consumer-1
         Consumer-2
```

- When you call `lock.notify()`, we can not choose weather it's producer or consumer.

- Solution : `Condition`

```java
Lock lock = new ReentrantLock();

Condition notFull = lock.newCondition();
Condition notEmpty = locl.newCondition();
```

```java
ReentrantLock
      |
      +-- notFull Queue
      |      Producer-1
      |      Producer-2
      |
      +-- notEmpty Queue
             Consumer-1
             Consumer-2
```

- Now you can wakeup exactly what we want :

```java
notEmpty.signal();  // Wake a consumer

notFull.signal();   // Wake a producer
```

- Another Example :

```java
Lock lock = new ReentrantLock();

Condition foodReady = lock.newCondition();
Condition tableFree = lock.newCondition();

boolean isFoodReady = false;
boolean isTableFree = false;

// Customer Thread
lock.lock();

try {

    while (!isFoodReady) {
        foodReady.await();
    }

    while (!isTableFree) {
        tableFree.await();
    }

    System.out.println("Customer starts eating");

} finally {
    lock.unlock();
}

// Chef Thread
lock.lock();

try {

    isFoodReady = true;

    foodReady.signal(); // wake food waiters

} finally {
    lock.unlock();
}

// Manager Thread
lock.lock();

try {

    isTableFree = true;

    tableFree.signal(); // wake table waiters

} finally {
    lock.unlock();
}
```


## Deadlock

- Two threads waiting forever for each other.

Example:
```java
synchronized(lock1) {
    synchronized(lock2) {

    }
}
```

```java
synchronized(lock2) {
    synchronized(lock1) {

    }
}

```

### Condition required on Deadlock

- Mutual Exclusion : 
    - Means only one thread can access the lock.
    - Without mutual exclusion, there would be no waiting, no Deadlock.
- Hold and Wait : 
    - Thread holds one lock and waiting for an another lock.
- No Preemption : 
    - Can not forcefully take lock away from the thread.
    - Only thread that owns a lock can release it. 
- Circular Wait : 
    - A thread holds lock-1 and waiting for lock-2 and another thread holds lock-2 and waiting for lock-1.

### How to prevent a deadlock

- Lock Ordering : Always acquire locks in same order.
- Minimize Nested Locks
- Use Timeout Locks : lock.tryLock(5, TimeUnit.SECONDS)
- Avoid Unnecessary Synchronization

### Livelock

- Not blocked, But still unable to progress

```java
import java.util.concurrent.locks.ReentrantLock;

public class MyClass {
    static ReentrantLock lock1 = new ReentrantLock();
    static ReentrantLock lock2 = new ReentrantLock();

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            while (true) {
                if (lock1.tryLock()) {
                    System.out.println("T1 got lock1");

                    if (lock2.tryLock()) {
                        System.out.println("T1 got a lock2");
                        break;
                    } else {
                        System.out.println("T1 releasing lock1");
                        lock1.unlock();
                    }
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (true) {
                if (lock2.tryLock()) {
                    System.out.println("T2 got lock2");

                    if (lock1.tryLock()) {
                        System.out.println("T2 got lock1");
                        break;
                    } else {
                        System.out.println("T2 releasing lock2");
                        lock2.unlock();
                    }
                }
            }
        });

        t1.start();
        t2.start();
    }
}
```

### Starvation
- Never gets a resource.
- High prority threads always runs, low priority thread waits forever.
- Solution = Fair lock.

### Thread safety problems

- Visibility Problem : One thread updates variable, another thread doesn't see latest value.
- Solved using : volatile, synchronized, Atomic classes
- Immutable Objects : Best thread-safe objects, it's not changed after creation

### Fail-Fast vs Fail-Safe
- Fail-Fast : Stop immediately on problem instead of continuing with the invalid data. Ex : ArrayList stops work when add during iteration
- Fail-Safe : Instead of stopping when something change, Continue safely without affecting the current operation. Ex : CopyOnWriteArrayList even if add during iteration

### ThreadLocal
- Each thread gets separate variable copy.

## Lock Interface (Advanced Synchronization)

- Old -> synchronized
- New -> ReentrantLock

- Manually need to lock and unlock.
- Reentrant means same thread can acquire same lock multiple times.

```java
Lock lock = new ReentrantLock();

lock.lock();

try {
    count++;
}
finally {
    lock.unlock();
}
```

- Same thread can acquire same lock multiple times, But must unlock same number of times.
- Instead of waiting forever, use tryLock(). If lock unavailable, returns false.

```java
if(lock.tryLock()) {
    try {
        // critical code
    } finally {
        lock.unlock();
    }
}
```

Timed Lock -> Wait max 5 seconds.
```java
lock.tryLock(5, TimeUnit.SECONDS);
```

Fair Lock : Threads get lock in FIFO order. synchronized has no fairness option.

```java
new ReentrantLock(true)
```

Interruptible Lock : Used when Thread is waiting for lock, but we want ability to cancel/interupt that waiting.

- With normal lock() when thread.interrupt() calls, waiting still continues.
- with lockInterruptibly(), interruptible while waiting for lock, throws InterruptedException.

```java
lock.lockInterruptibly();
```

## Fair Lock

- First waiting thread gets lock first, prevent starvation.

```java
new ReentrantLock(true);
```

## ReadWriteLock
- Used when Reads are frequent, Writes are rare.

```java
ReadWriteLock lock = new ReentrantReadWriteLock();

lock.readLock().lock();     // Read lock, Multiple readers allowed simultaneously
lock.writeLock().lock();    // Write lock, Only one writer allowed
```

- `await()` equivalent to `wait()`
- `signal()` equivalent to `notify()`
- `signalAll()` equivalent to `notifyAll()`
- Better than wait/notify because multiple conditions possible

```java
import java.util.concurrent.locks.*;

public class Main {

    static Lock lock = new ReentrantLock();
    static Condition shopOpen = lock.newCondition();

    static boolean isOpen = false;

    public static void main(String[] args) {

        // Customer
        new Thread(() -> {
            lock.lock();

            try {
                while (!isOpen) {
                    System.out.println("Customer waiting...");
                    shopOpen.await(); // wait
                }

                System.out.println("Customer entered shop");

            } catch (InterruptedException e) {
                e.printStackTrace();

            } finally {
                lock.unlock();
            }
        }).start();

        // Shopkeeper
        new Thread(() -> {

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {}

            lock.lock();

            try {
                isOpen = true;

                System.out.println("Shop opened");

                shopOpen.signal(); // wake customer

            } finally {
                lock.unlock();
            }

        }).start();
    }
}
```

## Semaphore
- Means, How many threads can access resource simultaneously.

```java
Semaphore semaphore = new Semaphore(2); // 2 Threads allowed to use a resource simultaniously.

Runnable task = () -> {
    try {
        semaphore.acquire();
        System.out.println(Thread.currentThread().getName() + " running");

        Thread.sleep(2000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    } finally {
        semaphore.release();
    }
};
```

## CountDownLatch
- Used when, One or more threads wait until tasks complete.
- Once 0, latch is finished, cannot reuse it.
- `await()` puts the thread into a waiting/blocking state, and another thread can interrupt it using `thread.interrupt()`, and it throws `InterruptedException`

```java
latch.countDown();  // Reduce count by 1
latch.await();      // Wait until count becomes 0
```

- Example :
```java
import java.util.concurrent.CountDownLatch;

public class Main {

    public static void main(String[] args) throws Exception {

        CountDownLatch latch = new CountDownLatch(3);

        Runnable worker = () -> {

            System.out.println(Thread.currentThread().getName() + " working");

            try {
                Thread.sleep(2000);
            } catch (Exception e) {}

            System.out.println(Thread.currentThread().getName() + " finished");

            latch.countDown(); // reduce count
        };

        new Thread(worker).start();
        new Thread(worker).start();
        new Thread(worker).start();

        System.out.println("Manager waiting...");

        latch.await(); // wait until count = 0

        System.out.println("All workers finished");
    }
}
```

- Real use case : 
- DB connected, cache loaded, services ready
- Parallel processing, Run tasks in parallel and wait for all results

## CyclicBarrier
- Used when Multiple threads wait for each other.
- All threads must reach the barrier point `barrier.await()`, when everyone arrives, all continues together.

```java
CyclicBarrier barrier = new CyclicBarrier(3);

Runnable worker = () -> {

    System.out.println(Thread.currentThread().getName() + " reached barrier");

    try {
        barrier.await(); // wait for all
    } catch (Exception e) {
        e.printStackTrace();
    }

    System.out.println(Thread.currentThread().getName() + " started work");
};

new Thread(worker).start();
new Thread(worker).start(); 
new Thread(worker).start();
```

- Thread-0 waits, Thread-1 waits, Thread-2 arrives, Now all 3 reached, barrier opens, all continue togethers.
- Why Cyclic : Because it can be reused again and again

- `BrokenBarrierException` exception occurs when one thread fails/interrupted while all threads are waiting at the barrier.
- `BrokenBarrierException` means : the group synchronization failed.
- Example : Suppose barrier needs 3 threads, Thread-1 waits, Thread-2 waits, Thread-1 gets interrupted and got `InterruptedException`, barrier breaks, now Thread-2 get `BrokenBarrierException`

```java
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {

        CyclicBarrier barrier = new CyclicBarrier(3);

        Thread t1 = new Thread(() -> {
            try {
                System.out.println("T1 waiting");
                barrier.await();
            } catch (Exception e) {
                System.out.println("T1 failed");
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                System.out.println("T2 waiting");
                barrier.await();
            } catch (Exception e) {
                System.out.println("T2 got BrokenBarrierException");
            }
        });

        t1.start();
        t2.start();

        try {
            Thread.sleep(2000);
        } catch (Exception e) {}

        t1.interrupt(); // break barrier
    }
}
```

## Phaser
- Skipped.

## Exchanger
- Skipped.

## Atomic Classes

- Thread-safe operations WITHOUT using synchronized/locks.
- They use CAS & CPU Level atomic operation.
- Atomic classes :

```java
AtomicInteger	        // Atomic int
AtomicLong	            // Atomic long
AtomicBoolean	        // Atomic boolean
AtomicReference	        // Atomic object reference
AtomicIntegerArray	    // Atomic array
AtomicStampedReference	// Solves ABA problem
LongAdder	            // High-performance counter
```
- **CAS** : If current value == expected value then update, else retry
- ex : count.compareAndSet(5, 6)
- Good for single variable operation, counter or flags.
- ABA Problem = Thread-1 reads A, Thread-2 changes A -> B -> A, Now Thread-1 checks and it still A, CAS thinks nothing changed, but actually value changed twice.
- Use AtomicStampedReference to solve ABA problem. A v1 -> B v2 -> A v3, now CAS check value + version.
- **LongAdder** = Used for very high concurrent counters.
- **AtomicInteger** uses one single variable, All threads fight on same counter. LongAdder creates multiple internal counters, then combine result.
- **Volatile** = When want to see latest updated value. use at boolean flags, status variables, stop/start signals, can not use with counters(because it provides volatility not atomicity so not provide a thread safe operations) for it can use AtomicInteger.
- **AtomicReference** = CAS works If whole object ref was changes, it's not protect internal fields changes.
- AtomicReference use with Immutable objects.

## Concurrent Collections

```java
ConcurrentHashMap
CopyOnWriteArrayList
```

## Executor Framework

- `java.util.concurrent` package
- Thread pool : Collection of reusable worker threads.
- Instead of creating threads manually, Java introduced a Executor Framework.

### Executor 
- Interface with `void execute(Runnable task)`

```java
Executor executor = new ThreadPoolExecutor(...);
//or
Executor executor = Executors.newSingleThreadExecutor();

executor.execute(() -> System.out.println("Hello"));
```

### ExecutorService
- Interface extends `Executor`
- Adds advance features like submit task, return result, manage a thread pool shutdown the thread pool.

```java
ExecutorService service = Executors.newFixedThreadPool(5);  // Creates 5 reusable threads
```

| execute()     | submit()            |
| ------------- | ------------------- |
| Runnable only | Runnable + Callable |
| No return     | Returns Future      |
| Less flexible | More flexible       |


### ScheduledExecutorService
- Interface extends `ExecutorService`
- Used for delayed task, periodic task

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

// Delayed task
service.schedule(() -> {
    System.out.println("Hello");    // Runs after 5 seconds
}, 5, TimeUnit.SECONDS);

// Scheduled task
service.scheduleAtFixedRate(() -> {
    System.out.println("Hello");},  // Runs after 1 second at every 2 second of an iteration
    1,
    2,
    TimeUnit.SECONDS);
```

### Executors
- Utility class (final class with static factory method)
- Internally it creates `ThreadPoolExecutor`, `ScheduledThreadPoolExecutor`

```java
ExecutorService ex = Executors.newFixedThreadPool(5);
```

```java
Executor (interface)
    ↑
ExecutorService (interface)
    ↑
ScheduledExecutorService (interface)

Executors (utility class) -> creates implementations of above interfaces
```

### Cached Thread Pool
- Creates threads as needed (Dynamic Thread)
- Task arrives, If idle thread exists -> task handed directly to idle thread, else new thread created immediately

```java
ExecutorService service = Executors.newCachedThreadPool();  // Creates threads dynamically

public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                    60L, TimeUnit.SECONDS,
                                    new SynchronousQueue<Runnable>());
}
```


## Callable vs Runnable

### Runnable
- No return value

### Callable
- Returns value
- Can throw checked exception
- If call() throw an exception, result.get() throws `ExecutionException`
- Can avoid waiting forever by result.get(2, TimeUnit.SECONDS), If task not completed in 2 sec `TimeoutException` comes.
- Callable works with a ExecutorService, because Thread class understands Runnable only, There is NO `new Thread(callable)`. Thread class does not support return value, Future, checked exception handling.

```java
Callable<Integer> task = () -> {
    // Perform some operation
    Thread.sleep(5000);

    return 10;
};

Future<Integer> result = service.submit(task);

// task completed or not
result.isDone();

// cancel task
result.cancel(true);

// wait for 5 seconds and print 10
// result.get() is a blocking call
System.out.println(result.get());
```

### shutDown()
- Stops accepting new tasks.
- Already submitted tasks continue and finish normally.

### shutDownNow()
- Stops accepting new tasks.
- `shutdownNow()` interrupts running task, but it stops only if the task handles interruption properly. It does NOT forcibly kill thread, It only sends an interrupt signal.
- It is not guaranteed to stop a task instantly. If the task ignores interrupts, it may continue running.

### awaitTermination()
- Wait for executor to be shutdown.
```java
boolean finished = executor.awaitTermination(5, TimeUnit.SECONDS);
// true = All programs completed successfully
// false = Some tasks are still not finished
```

## ForkJoinPool
- Pending...

## CompletableFuture

- Used for async tasks, API calls, microservices.
- Can chain tasks, combine tasks, run non-blocking code

```java
// runAsync : Used when No return value
CompletableFuture.runAsync(() -> {
    System.out.println("Task Running");
});

// supplyAsync : Used when needs a return value
CompletableFuture<String> future =
    CompletableFuture.supplyAsync(() -> {
        return "Java";
    });

// Get result
// Could throw InterruptedException or ExecutionException and need to handle
future.get();

// No checked exception handling required, Throws runtime exception (CompletionException) internally
future.join(); 

// thenApply : Transforms result
// After submit, If you want to get the result, we need to do result.get() which is blocking operation.
// But by using thenApply, it becomes asynchronously
CompletableFuture<String> future =
    CompletableFuture
        .supplyAsync(() -> "java")
        .thenApply(s -> s.toUpperCase());   // JAVA

// thenApplyAsync : different thread from thread pool
CompletableFuture
    .supplyAsync(() -> "Java")
    .thenApplyAsync(s -> s.toUpperCase());  // Run in diff thread

// thenAccept : Consume result
// no return.
future.thenAccept(System.out::println);

// thenRun : run another task after completion
// No input, No output.
future.thenRun(() -> {
    System.out.println("Done");
})

// thenCompose : One async task completes and another starts.
CompletableFuture<String> user =
    CompletableFuture.supplyAsync(() -> "Vrajesh")
        // Use name into second CompletableFuture and it automatically flattens Future<Future<T>> to Future<T>
        .thenCompose(name ->
            CompletableFuture.supplyAsync(() ->
                name + " Orders"
            )
        )

// thenCombine : Combine 2 independent futures.
CompletableFuture<String> f1 =
    CompletableFuture.supplyAsync(() -> "Java");

CompletableFuture<String> f2 =
    CompletableFuture.supplyAsync(() -> "Spring");

CompletableFuture<String> result =
    f1.thenCombine(f2, (a,b) -> a + " " + b);

// allOf : Wait for all tasks to be complete
CompletableFuture.allOf(f1, f2).join();

// anyOf : Wait for first completed task
CompletableFuture.anyOf(f1, f2);

// exceptionally : Handles exception
CompletableFuture.supplyAsync(() -> {
    int x = 10 / 0;
    return x;
}).exceptionally(ex -> {
    return -1;
});

// handle: handle both Success and Failure
CompletableFuture<Integer> future =
    CompletableFuture.supplyAsync(() -> 10/0)
    .handle((result, ex) -> {
        // If SUCCESS : result has value and ex is null
        // If FAILURE : result is null and ex is value
        if(ex != null) {
            return -1;
        }

        return result;
    });

// whenComplete : Used only for observing or logging, does not change the result
CompletableFuture.supplyAsync(() -> "Java")
    .whenComplete((res, ex) -> {

        System.out.println(res);

    });

// NOTE : handle can modify result, whenComplete not

// Default Thread Pool : When you do 
CompletableFuture.supplyAsync(...)
//Without giving executor, Java automatically uses
ForkJoinPool.commonPool()
// This is shared internal thread pool managed by JVM

// Custom Executor : You can provide your own thread pool
ExecutorService executor = Executors.newFixedThreadPool(5);

CompletableFuture.supplyAsync(task, executor);

// Supplier : Functional Interface, represents give me some value
Supplier<String> s = () -> "Java";
```

## Virtual Threads

- Lightweight threads managed by JVM instead of OS.
- Main Advantage : If virtual thread is waiting, it says lets someone else use the real thread.

```java
virtualThread.sleep(5 sec)
```

```java
// Before sleep
Virtual Thread A
      |
Platform Thread 1

// During sleep
Virtual Thread A -> parked
Platform Thread 1 -> free // => Platform Thread 1 starts running another Virtual Thread, that's why it is scalable

// After sleep
Virtual Thread A resumes, may be on different platform thread.
```

- Example :

```java
// Single Virtual Thread
Thread.startVirtualThread(() -> {
    System.out.println("Virtual Thread");
});
```

```java
// Virtual Thread Executor
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    // Each task gets its own Virtual Thread
    executor.submit(() -> {
        System.out.println("Task 1");
    });

    executor.submit(() -> {
        System.out.println("Task 2");
    });
}
```

```java
Thread vt = Thread.startVirtualThread(() -> {
    System.out.println("Work");
});

vt.join();  // Main thread waits until Virtual Thread finishes
```

```java
// Each async task runs in a Virtual Thread
CompletableFuture.runAsync(
    task,
    Executors.newVirtualThreadPerTaskExecutor()
);
```

- Carrier Thread : Real OS thread used to run Virtual Threads (Carrier Thread = Platform Thread).
- Mount : Virtual Thread attached to Carrier Thread (VT -> PT).
- Unmount : Virtual Thread detached (VT parked, PT free).
- Pinning : Virtual Thread cannot detach while inside some blocking operations under synchronized, Carrier Thread gets stuck, it's called pinning.

```java
synchronized(lock) {
    Thread.sleep(5000);
}
```

- Avoid Pinning : Use ReentrantLock, because ReentrantLock is implemented in Java using AQS and is Virtual-Thread-friendly.

## ThreadLocal

- Use when Each thread should have its own private copy of a variable.

```java
ThreadLocal<String> user = new ThreadLocal<>();

user.set("Vrajesh");    // In Thread - 1
user.get() -> Vrajesh   // Thread - 1
user.get() -> null      // Thread - 2
```

- How it internally works :

```java
Thread
 └── ThreadLocalMap
       ├── key -> ThreadLocal
       └── value -> Data

// If called,
user.set("Vrajesh");

Current Thread
      ↓
ThreadLocalMap
      ↓
user -> "Vrajesh"

// If called, Java looks in the current thread's map
user.get();
```

- Should not use ThreadLocal with vrtual Thread, because millions of Virtual Threads is possible and it results high memory usage.


## Structured Concurrency
