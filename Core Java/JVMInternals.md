# Index
- [JVM Architecture](#jvm-architecture)
    - [Class Loader](#1-class-loader)
    - [Runtime Data Areas](#2-runtime-data-areas)
    - [Execution Engine](#3-execution-engine)
    - [Garbage Collector](#4-garbage-collector)

- [Class Loader Subsystems](#class-loader-subsystems)
    - [Bootstrap Class Loader](#1-bootstrap-class-loader)
    - [Platform Class Loader](#2-platform-class-loader)
    - [Application Class Loader](#3-application-class-loader)
    - [Parent Delegation Model](#parent-delegation-model)

- [Running Data Areas (JVM Memory Areas)](#running-data-areas-jvm-memory-areas)

- [Execution Engine](#execution-engine)

- [Main Components](#main-components)
    - [Interpreter](#interpreter)
    - [JIT Compiler](#jit-compiler)
    - [Code Cache](#code-cache)

- [Garbage Collector](#garbage-collector)
    - [Types of GC](#types-of-gc)
    - [Stop The World (STW)](#stop-the-world-stw)

- [Java Memory Model (JMM)](#java-memory-model-jmm)

## JVM Architecture
- JVM (Java Virtual Machine) is a software engine that runs Java bytecode.
- Why JVM : Because every operating system understands different machine code.
- Instead of compiling Java directly for Windows, Linux, Mac, can use JVM of that operating system to run the same bytecode.

Flow :

```
.java
  ↓
javac
  ↓
.class (Bytecode)
  ↓
JVM of the operating system
  ↓
Native Machine Code
  ↓
CPU
```

### 1. Class Loader
- Responsible for loading .class files into memory.
- When JVM starts, it does not loads all classes, it loads classes only when needed.

Example :
```java
Employee emp = new Employee();
```
- Before creating object, JVM Loads Employee class into memory.

### 2. Runtime Data Areas

### 3. Execution Engine
- Responsible for executing the bytecode.
- Converts bytecode into machine code and executes it.

### 4. Garbage Collector
- Responsible for Removing unused objects.

Example :
```java
Employee emp = new Employee();
emp = null; // emp object is now eligible for garbage collection
```

## Class Loader Subsystems

Types of class loaders in JVM :

### 1. Bootstrap Class Loader
- Loads core Java classes (e.g., java.lang.*, java.util.*).

Example :
```java
String
Object
Integer
ArrayList
```

### 2. Platform Class Loader
- Loads JDK libraries that are not core (e.g., java.sql.*, java.xml.*).

Example :
```java
java.sql.Connection
java.sql.DriverManager
java.xml.parsers.DocumentBuilder
```

### 3. Application Class Loader
- Loads classes from the application's classpath or external jars (e.g., classes in src/ or lib/ or target/classes/).

```java
Bootstrap Class Loader
    ↑
Platform Class Loader
    ↑
Application Class Loader
```

### Parent Delegation Model
- Suppose JVM needs String, Application does not loads it directly, instead it delegates to Platform Class Loader, if Platform Class Loader does not find it, it delegates to Bootstrap Class Loader. This is called Parent Delegation Model.
- Bootstrap Class Loader says Yes, I have String class, so it loads String class and returns it to Platform Class Loader, which then returns it to Application Class Loader, and finally Application Class Loader gives it to the application.
- So If somebody writes
```java
package java.lang;

public class String {
}
```
- Without delegation, JVM might load this fake String, Parent delegation ensures Real JDK classes get priority.
- After finding a class, JVM Performs :
1. **Loading** : Reads the .class file and creates a class metadata in memory.
2. **Linking** : Verifies the .class (Is bytecode valid?, Any corruption?, Any security issues?), prepares memory for static variables, and resolves references.
3. **Initialization** : Executes static blocks and initializes static variables.

## Running Data Areas (JVM Memory Areas)
- This is the memory managed by JVM.
1. **Method Area** : Stores class-level data (class metadata, static variables, method code).
2. **Heap** : Stores all objects created by the application.
3. **Stack** : Stores method call frames (local variables, operand stack, return address).
4. **Program Counter (PC) Register** : Keeps track of the current instruction being executed
5. **Native Method Stack** : Used for native method calls (methods written in languages like C/C++).

- For 2 Threads :

```java
Thread 1
 ├─ Stack
 ├─ PC Register
 └─ Native Stack

Thread 2
 ├─ Stack
 ├─ PC Register
 └─ Native Stack

// Shared by all threads
Method Area
Heap
```

## Execution Engine
- Class loader loads classes.
- Memory areas store data.
- Execution engine **executes bytecode** using the data in memory areas.

```java
.java
  ↓
javac
  ↓
.class (Bytecode)
  ↓
Execution Engine
  ↓
Machine Code
  ↓
CPU
```

# Main Components

## Interpreter
- Interpreter reads bytecode line by line and executes it.

Example :
```java
for(int i=0; i<10000; i++) {
    add();
}
```
- Interpreter will read the bytecode for the loop and execute it line by line, which is slow.
- Solution : Just-In-Time (JIT) Compiler.

## JIT Compiler
- JVM watch the program, when method is called multiple times, JVM identifies it as a "hot spot" and JIT compiles that bytecode into machine code. Now CPU runs machine code directly, which is much faster.
- **C1 Compiler** : Optimizes for startup time (compiles quickly, less optimization).
- **C2 Compiler** : Optimizes for long-running performance (compiles slower, more optimization).
- **Inlining** : JIT can inline small methods (replace method call with method body) to reduce overhead.
```java
int result = add(a, b); // Method call
public int add(int a, int b) {
    return a + b;
}
// After inlining
int result = a + b; // No method call, directly adds a and b
```
- **Warm up Period** : JIT needs some time to identify hot spots and compile them, so performance may be slower at the beginning of the program.

## Code Cache
- JIT compiled code is stored in a special area of memory called Code Cache.

# Garbage Collector
- JVM automatically manages memory, it identifies and removes objects that are no longer needed (Garbage Collection).

## Types of GC
1. **Minor GC** : Occurs in the Young Object Area (where new objects are created).
2. **Major GC** : Occurs in the Old Object Area (where long-lived objects are stored).
3. **Full GC** : Cleans Entire Heap (both Young and Old areas).

## Stop The World (STW)
- During GC, JVM pauses all application threads to safely perform garbage collection. This is called "Stop The World" (STW) event.

# Java Memory Model (JMM)
- Main responsibility of JMM:

1. **Visibility** : Ensures that changes made by one thread to shared variables are visible to other threads.
2. **Ordering** : CPU and JVM may reorder instructions for perfomance.
3. **Atomicity** : Operation complete in a single step without interruption. achieve by synchronized, Lock or Atomic classes.

# JVM Monitoring & Tuning
1.  **Heap Size** : Heap can be configured using -Xms (initial heap size) and -Xmx (maximum heap size).

2. **OutOfMemoryError** : If application tries to use more memory than the maximum heap size, JVM throws OutOfMemoryError. 
- Reason : Memory leak, very large data structures, or Heap too small.

3. **Thread Dump** : Shows what every thread is doing right now.
- Useful for deadlocks, Application hangs, High CPU usage.
- Command : `jstack <pid>`

4. **Heap Dump** : Snapshot of the heap memory at a given time.
- Command : `jmap -dump <pid>`

5. **Visual VM** : GUI tool for JVM monitoring (Heap Usage, Threads, CPU Usage, GC Activity).

6. **GC Logs** : Shows GC activity (GC Started, Memory Freed, Pause Time).
- Useful when application becomes slow due to GC.