- [Exception](#exception)
    - [Checked Exception](#checked-exception)
    - [Unchecked Exception](#unchecked-exception)

- [Exception Propagation](#exception-propagation)
    - [Checked Exception Propagation](#checked-exception-propagation)
    - [Unchecked Exception Propagation](#unchecked-exception-propagation)

- [Stack Unwinding](#stack-unwinding)

- [Custom Exception](#custom-exception)
    - [Checked Custom Exception](#checked-custom-exception)
    - [Unchecked Custom Exception](#unchecked-custom-exception)

- [try-with-resources](#try-with-resources)
    - [Custom Resource](#custom-resource)
    - [Exception Scenarios](#exception-scenarios)

# Exception
- Unexpceted problem during program execution.
- When exception occurs, Java automatically transfers control to exception handling code.

## Checked Exception
- These are exceptions that Java forces you to handle.
- Handle it using try-catch OR throws.

## Unchecked Exception
- These are Runtime Exceptions.
- These are usually programming mistakes, so Java does not force you to handle, instead of it Java expects developer to fix it rather than recover from it, that's why Runtime exception is unchecked.

# Exception Propagation
- When a method doesn't handle an exception, Java automatically passes it to the caller method.

```java
public class Test {

    static void method3() {
        int x = 10 / 0;
    }

    static void method2() {
        method3();
    }

    static void method1() {
        method2();
    }

    public static void main(String[] args) {
        method1();
    }
}
```

```java
main()
  ↓
method1()
  ↓
method2()
  ↓
method3()
  ↓
ArithmeticException
```

- No method handled it, so exception keeps moving upward.
- Why Propagation : Imagine `readFile()` fails deep inside 10 method calls. Instead of every method writing, `try-catch`, Java lets exception travel upward until someone can handle it properly, this keep code cleaner.

## Checked Exception Propagation :

```java
static void method3() throws IOException {
}

static void method2() throws IOException {
    method3();
}

static void method1() throws IOException {
    method2();
}

public static void main(String[] args)
        throws IOException {
    method1();
}
```
- Here throws passes responsibility to caller.

## Unchecked Exception Propagation :
- No throws required, Runtime exceptions automatically propagate.

# Stack Unwinding
- When exception occurs, Java starts removing method calls from stack, Until it finds a matching catch block.
- If nobody handles it, JVM handles it and terminates program.

and give an output :
```java
Exception in thread "main"
java.lang.ArithmeticException: / by zero
```

# Custom Exception
- Your own exception class representing a business-specific error.

## Checked Custom Exception
- Extend Exception

```java
class InvalidAgeException extends Exception {
}
```

## Unchecked Custom Exception
- Extend RuntimeException

```java
class InvalidAgeException extends RuntimeException {
}
```

# try-with-resources
- When using resources like files, sockets, database connections, etc., it's important to close them manually.
- To avoid this, Java 7 introduced try-with-resources statement, try-with-resources automatically closes resources after use, even when exceptions occur.

```java
try (FileInputStream fis = new FileInputStream("file.txt")) {
    // use the resource
} catch (IOException e) {
    // handle the exception
}
```

Closing order :
```java
try (Resource1 r1 = new Resource1(); Resource2 r2 = new Resource2()) {
    // use resources
} catch (Exception e) {
    // handle exception
}

// Open in order : Resource1, Resource2
// Close in reverse order : Resource2, Resource1
// LIFO (Last In First Out)
```

- Objects implementing AutoCloseable or Closeable interface can be used in try-with-resources statement.

Example :

```java
FileReader
BufferedReader
Scanner
Connection
Socket
InputStream
OutputStream
```

## Custom Resource

```java
class MyResource implements AutoCloseable {
    @Override
    public void close() {
        System.out.println("Resource closed");
    }
}
```

```java
// Usage

try(MyResource r = new MyResource()) {

}
```

## Exception Scenarios
- If exception occurs in try block, then catch block will be executed and then close() method will be called.

```java
try(FileReader fr = new FileReader("abc.txt")) {
    int x = 10 / 0;
}
```

```java
ArithmeticException
close()
exception propagates to catch block
```

# Suppressed Exceptions
- If Exception occurs in try block and another exception occurs while closing resource, then the second exception is suppressed and the first exception is propagated to catch block.

```java
class MyResource implements AutoCloseable {

    @Override
    public void close() throws Exception {
        throw new Exception("Close Exception"); // Exception at close
    }

}

public class ExceptionEample {
    public static void main(String[] args) {
        try (MyResource rc = new MyResource()) {
            throw new Exception("Try Exception");   // Exception at try
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Output: Try Exception

            for (Throwable t : e.getSuppressed()) {
                System.out.println(t.getMessage());    // Output: Close Exception
            }
        }
    }
};

```
- Primary Exception : The exception from try block
- Suppressed Exception : The exception from close() method

- Why Java prefer primary exception over suppressed exception : Because the actual work failed at try block, close() failed later during cleanup, so the original failure is usually more important.

# Exception Rule :

```java
try throws exception
        ↓
catch handles it

If catch throws new exception
        ↓
new exception propagates

If finally throws exception
        ↓
finally exception propagates
```