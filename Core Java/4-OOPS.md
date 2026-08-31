# Class Initialization Order

```java
class Parent {

    static {
        System.out.println("Parent Static Block");
    }

    {
        System.out.println("Parent Instance Block");
    }

    Parent() {
        System.out.println("Parent Constructor");
    }
}

class Child extends Parent {

    static {
        System.out.println("Child Static Block");
    }

    {
        System.out.println("Child Instance Block");
    }

    Child() {
        System.out.println("Child Constructor");
    }
}

public class Test {

    public static void main(String[] args) {
        new Child();
    }
}
```

```text
new Child()

        │
        ▼
-------------------------
Load Parent Class
-------------------------
Static Variables
Static Block
        │
        ▼
-------------------------
Load Child Class
-------------------------
Static Variables
Static Block
        │
        ▼
-------------------------
Create Parent Object
-------------------------
Instance Variables
Instance Block
Constructor
        │
        ▼
-------------------------
Create Child Object
-------------------------
Instance Variables
Instance Block
Constructor
```

Java executes static members in the order they appear, same for Instance variables & Methods.

# Inheritance

If the parent doesn't have a no-argument constructor:

```java
class Parent {

    Parent(int id) {}
}

class Child extends Parent {

    Child() {}
}
```

Compilation error : Implicit super constructor Parent() is undefined.
Must explicitly invoke another constructor.

Fix:

```java
class Child extends Parent {

    Child() {
        super(10);
    }
}
```

## Object Initialization Order

When we create an object:

```java
Child c = new Child();
```

Java performs initialization in the following order:

```text
new Child()
    ↓
1. Allocate memory for complete Child object
    ↓
2. Initialize all fields with default values
   (int = 0, boolean = false, Object = null)
    ↓
3. Parent field initializers execute
    ↓
4. Parent constructor executes
    ↓
5. Child field initializers execute
    ↓
6. Child constructor executes
    ↓
7. Reference `c` points to the created object
```

## Example

```java
class Parent {

    int a = 10;

    Parent() {
        System.out.println("Parent Constructor");
    }
}

class Child extends Parent {

    int b = 20;

    Child() {
        System.out.println("Child Constructor");
    }
}
```

```java
Child c = new Child();
```

## What Happens Internally?

### Step 1: Memory Allocation

`new Child()` allocates memory for **one complete Child object** on the Heap.

Initially, fields contain default values:

```text
Heap

Child Object
┌──────────────────┐
│ Parent part      │
│   a = 0          │
│                  │
│ Child part       │
│   b = 0          │
└──────────────────┘
```

> Java does NOT create separate Parent and Child objects.
> It creates one Child object containing Parent's inherited state + Child's state.

### Step 2: Parent Initialization

Parent field initializer executes:

```java
int a = 10;
```

Then Parent constructor executes:

```java
Parent() {
    System.out.println("Parent Constructor");
}
```

Object state:

```text
a = 10
b = 0
```

### Step 3: Child Initialization

Child field initializer executes:

```java
int b = 20;
```

Then Child constructor executes:

```java
Child() {
    System.out.println("Child Constructor");
}
```

Final object:

```text
Heap

Child Object
┌──────────────────┐
│ a = 10           │
│ b = 20           │
└──────────────────┘
        ↑
        c
```

## Key Points

- `new Child()` creates **one complete Child object**.
- Memory is allocated before constructors execute.
- All instance fields first receive their **default values**.
- Parent initialization happens before Child initialization.
- Parent constructor executes before Child constructor.
- A constructor **initializes an already allocated object**; it does not allocate the object's memory.

# Method Overloading
- Same method name, different parameters.
- Method overloading decides at compile time.
- Method Overloading, java choose most specific method, for ex.

```java
class MyClass{
    void print(String name){
        System.out.println("String method");
    }

    void print(Object name){
        System.out.println("Object method");
    }
}

MyClass obj = new MyClass();
obj.eat("Vrajesh"); // String method
obj.eat(null);      // String method -> Java choose most specific method
```

```java
class MyClass{
    void print(Integer i){
        System.out.println("Integer method");
    }

    void print(Object o){
        System.out.println("Object method");
    }
}

MyClass obj = new MyClass();
obj.eat(null);    // Gives ambiguous error
// null checks both Integer and String. Neither is more specific.
```

```java
class MyClass{
    void print(int i){
        System.out.println("int method");
    }

    void print(long l){
        System.out.println("long method");
    }
}

MyClass obj = new MyClass();
obj.print(18);  // int method

byte b = 18;
obj.print(b);   // int method
```

```java
static void print(int x) {
    System.out.println("int");
}

static void print(Integer x) {
    System.out.println("Integer");
}

public static void main(String[] args) {
    print(10);
}

// int, because exact match
```

```java
static void print(long x) {
    System.out.println("long");
}

static void print(Integer x) {
    System.out.println("Integer");
}

print(10);

// long, because Java prefers Widening > Autoboxing
```

```java
static void print(Integer x) {
    System.out.println("Integer");
}

static void print(int... x) {
    System.out.println("Varargs");
}

print(10);

// Integer, Java prefers Autoboxing > Varargs
```

```java
static void print(long x) {
    System.out.println("long");
}

static void print(int... x) {
    System.out.println("Varargs");
}

print(10);

// long, Java prefers Widening > Varargs
```

Priority Order

```text
1. Exact Match
        ↓
2. Primitive Widening
        ↓
3. Autoboxing / Unboxing
        ↓
4. Varargs
```

- Static method are hidden not overridden, it means in `Parent p = new Child()` and while we done `p.method()` then it will call Parent's method not Child's method, because it hides Child's method.
- Always call `Parent.method()` or `Child.method()`, avoid `p.method()`.

# Method Overriding
- Same method signature in both parent and child.
- Method overriding decides at Runtime. (Dynamic method dispatch).

- Calling an overridden method from the constructor :

```java
class Parent {
    Parent(){
        show(); // Because show() is overridden, Java uses runtime polymorphism and call Child.show()
    }

    void show(){
        System.out.println("Parent");
    }
}

class Child extends Parent {
    private String name = "Vrajesh";

    @Override
    void show(){
        System.out.println(name);
    }
}

public class Main {
    public static void main(String[] args){
        new Child();    // Output = null
    }
}
```

- **Rule to be remember** : Never call overridable method from a constructor.

## Diamond Problem

Occurs when interface default method was conflict.

```java
interface A {
    default void show() {
        System.out.println("A");
    }
}

interface B {
    default void show() {
        System.out.println("B");
    }
}

class Test implements A, B {

}
```

It will not compile, because `new Test().show()` can not get what to call A's show() or b's show().

Solution

```java
class Test implements A, B {
    @Override
    public void show() {
        System.out.println("Resolved");
    }
}
```

If two interfaces provide the same default method, you must override it.

What is one is a class and other is an interface ?

```java
class Parent {
    public void show() {
        System.out.println("Parent");
    }
}

interface A {
    public void show() {
        System.out.println("A");
    }
}

class Child extends Parent implements A {

}
```

`new Child().show()` calls `Parent`.

Method from the class has higher priority than a default method from an interface.

Priority Order

```text
1. Child Class
        ↓
2. Parent Class
        ↓
3. Interface Default Method
```