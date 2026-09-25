# Memory leaks

In a Java program, some objects no longer needed by the application, but it still references them & GC can not erase it, because it is still reachable. This situation is known as a memory leak.

## Common causes of memory leaks

- Static fields holding object references

```java
private static List<User> myList = new ArrayList<>();

public void addUser(User user) {
    myList.add(user);
}
```

User keeps adding, and the list keeps references for that added users forever.

- `ThreadLocal` leak

Ideally, we should remove a `ThreadLocal` value once it is no longer needed.

```java
private static ThreadLocal<User> threadLocalUser = new ThreadLocal<>();

public void process(User user) {
    try{
        threadLocalUser.set(user);

        // work
    } finally {
        threadLocalUser.remove();
    }
}
```

If we do not remove the `ThreadLocal` value, it can lead to a memory leak because the value will remain associated with the thread even after it is no longer needed.

- Event listeners / callbacks

When an object registers itself as a listener or callback, it must unregister itself when it is no longer needed. Failing to do so can prevent the object from being garbage collected, leading to a memory leak.

```java
interface MyEventListener {
    void handleEvent(String message);
}

class MyService {
    private MyEventListener listener;

    public void addListener(MyEventListener listener) {
        this.listener = listener;
    }

    public void removeListener() {
        this.listener = null;
    }

    public void triggerEvent(String message) {
        if (listener != null) {
            listener.handleEvent(message);
        }
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        MyService service = new MyService();

        MyEventListener listener = new MyEventListener() {
            @Override
            public void handleEvent(String message) {
                System.out.println("Event received: " + message);
            }
        };

        service.addListener(listener);

        // If we did not remove the listener after it was used, it could lead to a memory leak.
        service.removeListener();
    }
}
```

- Anonymous inner classes holding references

When an anonymous inner class holds a reference to an outer class instance, it can prevent the outer class from being garbage collected if the inner class instance outlives the outer class instance. To avoid this, consider using static nested classes or explicitly nullifying references when they are no longer needed.

Problematic example:

```java
class Outer {
    private String data = "Important data";

    public void start() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                System.out.println(data);
            }
        };
        new Thread(task).start();
    }
}
```

In this example, the anonymous inner class `Runnable` holds a reference to the outer class `Outer`. If the thread outlives the `Outer` instance, it can prevent the `Outer` instance from being garbage collected, potentially causing a memory leak.

One way to avoid this issue is to use a static nested class instead of an anonymous inner class:

```java
class Outer {
    private String data = "Important data";

    public void start() {
        Runnable task = new StaticRunnable(data);
        new Thread(task).start();
    }

    private static class StaticRunnable implements Runnable {
        private final String data;

        public StaticRunnable(String data) {
            this.data = data;
        }

        @Override
        public void run() {
            System.out.println(data);
        }
    }
}
```

- Unbounded queue

```java
BlockingQueue<Task> queue = new LinkedBlockingQueue<>();

// Continuously adding elements to the queue without bounds
while (true) {
    queue.add(new Task());
}
```

- Resource not closed

```java
FileInputStream fis = new FileInputStream("file.txt");
// Do something with the file input stream
// Forgot to close the resource
```

Solution:

```java
try (FileInputStream fis = new FileInputStream("file.txt")) {
    // Do something with the file input stream
} catch (IOException e) {
    e.printStackTrace();
}
```

# When Application is slow

It could be due to CPU, Memory, GC, Threads, I/O.

## Solution

1. Check CPU Usage is high ot not.
2. Then check which of the thread is consuming high CPU.