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

- [Important Notes](#important-notes)

# Exception
- Unexpceted problem during program execution.
- When exception occurs, Java automatically transfers control to exception handling code.

## Checked Exception
- These are exceptions that Java forces you to handle.
- Checked exceptions represent situations that are often **outside your program's control** (e.g., file not found, database unavailable, network issues), so Java forces you to think about handling them.
- Handle it using try-catch OR throws.

## Unchecked Exception
- These are Runtime Exceptions.
- These are usually programming mistakes, so **Java does not force you to handle**, instead of it Java expects **developer to fix** it rather than recover from it, that's why Runtime exception is unchecked.

# Exception Propagation
- When a method doesn't handle an exception, Java automatically passes it to the caller method.
- If the exception not handles and it reaches upto main() methos, the **JVM handle it and terminate the program** and print the exception stack trace.

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

public static void main(String[] args) throws IOException {
    method1();
}
```
- Here **throws passes responsibility** to caller.

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
- It could be both Checked and Unchecked.

## Checked Custom Exception
- Extend `Exception`
- Use when the caller can reasonably recover from the problem.
- Example, External operation failed and caller can retry/fallback.

```java
class InvalidAgeException extends Exception {
}
```

## Unchecked Custom Exception
- Extend `RuntimeException`
- Use when you don't want to force every caller to handle it.
- Example, Invalid Input, Entity not found (User not found).
- Spring Boot typically use it and send HTTP Status code accordingly through out the ControllerAdvice class.

```java
class InvalidAgeException extends RuntimeException {
}
```

# try-with-resources
- When using resources like files, sockets, database connections, etc., it's important to close them manually.
- To avoid this, **Java 7** introduced try-with-resources statement, try-with-resources automatically closes resources after use, even when exceptions occur.

```java
try (FileInputStream fis = new FileInputStream("file.txt")) {
    // use the resource
} catch (IOException e) {
    // handle the exception
}
```

Closing order :
```java
try (Resource1 r1 = new Resource1(); 
     Resource2 r2 = new Resource2()) {
    // use resources
} catch (Exception e) {
    // handle exception
}

// Open in order : Resource1, Resource2
// Close in reverse order : Resource2, Resource1
// LIFO (Last In First Out)
```

- Objects implementing `AutoCloseable` or `Closeable` interface can be used in try-with-resources statement.

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
- If exception occurs in `try` block, then `close()` method will be called and `catch` block will be executed.
- In normal `try-finally`, If finally throw an exception, then caller will get finallly exception instead of try and it hides main `try` exception, `try-with-resource` solves this problem by returning try exception instead of close()'s exception.

```java
Exception occurs
       ↓
close()      ✅ Release the resource immediately
       ↓
catch()      ✅ Now handle the exception
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
            System.out.println(e.getMessage());     // Output: Try Exception

            for (Throwable t : e.getSuppressed()) {
                System.out.println(t.getMessage());    // Output: Close Exception
            }
        }
    }
};

```

- Primary Exception : The exception from try block
- Suppressed Exception : The exception from close() method
- Why Java prefer primary exception over suppressed exception : Because the actual work failed at try block, close() failed later during cleanup, so the **original failure is usually more important**.

# Exception Rule

```java
try throws exception
        ↓
catch handles it
```

```java
If catch throws new exception
        ↓
new exception propagates
```

```java
If finally throws exception
        ↓
finally exception propagates
```

# Exception Hirerchy

```java
Throwable
├── Error
│   ├── OutOfMemoryError
│   └── StackOverflowError
│
└── Exception
    ├── RuntimeException
    │   ├── NullPointerException
    │   ├── ArithmeticException
    │   ├── ArrayIndexOutOfBoundsException
    │   └── IllegalArgumentException
    │
    └── IOException
    │   └── FileNotFoundException
    └── SQLException
```

# Important Notes
- `try-catch`, `try-finally`, `try-catch-finally` all are possible.
- `System.exit(0)` terminates the JVM process and finally may not execute.
- **Return in finally overrides everything**, so avoid to return from the finally.
- Specific type of an exception should come first and then their same level or broder exception should come later.
- Avaoid `catch(Throwable t)`, because you could accidentlly catch serious Errors.
- Avoid `Catch(Exception e)`, it can swallowing an in detail exception.

- **finally** modify returned Reference types, not return primitive type.

```java
public static int test(){
    int x = 10;

    try {
        return x;
    } finally {
        x = 20;
    }
}
// Output 10, it copies value 10 into a hidden return variable.
```

```java
class Employee {
    String name;
}

public static Employee test(){
    Employee e = new Employee();
    e.name = "Java";

    try {
        return e;
    } finally {
        e.name = "Spring";
    }
}
// Output Spring, it copies reference of the object, not the copy of the object, so modification through reference reflects return object.
```

