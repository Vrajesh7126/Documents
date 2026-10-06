- [Generic Classes](#generic-classes)
    - [Benifits of Generics](#benefits-of-generics)

- [Generic Methods](#generic-methods)
    - [Generic Return Types](#generic-return-types)

- [Bounded Types (<T extends ...>)](#bounded-types-t-extends-)
    - [Benifits](#benefits)

- [Wildcards (?, ? extends, ? super)](#wildcards---extends--super)
    - [Unbounded Wildcard (?)](#1-unbounded-wildcard--)
    - [Upper Bounded Wildcard (? extends X)](#2-upper-bounded-wildcard--extends-x-)
    - [Lower Bounded Wildcard (? super X)](#3-lower-bounded-wildcard--super-x-)

- [Type Erasure](#type-erasure)

- [Generic Inheritance](#generic-inheritance)

- [Raw Type](#raw-type)

- []

# Generic Classes
- A class that can work with any data type without rewriting code.
- A class whose data type is decided when the object is created, giving type safety and removing explicit casting while getting values.

```java
class Box<T> {
    T value;
}

// Use case
Box<String> box = new Box<>();
box.value = "Hello";

String s = box.value; // No casting
```

## Benefits of Generics
1. **Compile-Time Type Safety**:
```java
List<String> list = new ArrayList<>();

list.add("Java");
list.add(10); // Compile Error
```

2. **No Casting Required**:
```java
// With Generics
String s = list.get(0); // No casting needed

// Without Generics
String s = (String) list.get(0);
```

3. **Reusable Code**

- Primitive types are not allowed, because generic works only with an Objects.

# Generic Methods
- A method that can work with different data types.

```java
<T> returnType methodName(...)
```

Example :

```java
public static <T> void print(T value) {
    System.out.println(value);
}

print("Hello"); // String
print(10);      // Integer
```

## Generic Return Types
- A method that returns a generic type.

```java
public static <T> T getValue(T value) {
    return value;
}

String str = getValue("Hello");
Integer num = getValue(10);
```

# Bounded Types (<T extends ...>)
- Restrict which types can be used in a Generic.

```java
class Box<T extends Number> {
    T value;
}

// Allowed :
Box<Integer>
Box<Double>
```

## Benefits
1. **Compiler knows T is a Number, so Number methods can be used.**

```java
public <T extends Number> void show(T value) {
    System.out.println(value.intValue());
}
```
- Without extends Number, intValue() is not allowed.

2. **Extends Interface** :
- Extends Interface → "I need objects that support this behavior."

```java
<T extends Runnable>
```
Example :
```java
// 1:
public <T extends Runnable> void runTask(T task) {
    task.run();
}

class MyTask implements Runnable {
    public void run() {
        System.out.println("Running MyTask");
    }
}

MyTask myTask = new MyTask();
runTask(myTask);

// 2:
public static <T extends Comparable<T>> T max(T a, T b) {
    return a.compareTo(b) > 0 ? a : b;
}

class Student implements Comparable<Student> {

    int age;

    Student(int age) {
        this.age = age;
    }

    @Override
    public int compareTo(Student other) {
        return this.age - other.age;
    }
}

Student s1 = new Student(20);
Student s2 = new Student(25);

Student older = max(s1, s2);

System.out.println(older.age);
```

3. Multiple Bounds

```java
<T extends Number & Comparable<T>>
```

You need:

- Number → for numeric operations (intValue(), doubleValue())
- Comparable → for comparison (compareTo())

# Wildcards (?, ? extends, ? super)

## 1. **Unbounded Wildcard (?)** :
- A wildcard represents an unknown type.

- T = "I want to remember and use the type."
- ? = "I don't care what the type is."

Example Diff :

```java
class Box<T> {
    void print(List<T> list) {}
}

Box<String> box = new Box<>();  // String is the type param
box.print(new ArrayList<String>()); // So List<String> is allowed
```

```java
class Box<T> {
    void print(List<?> list) {}
}

Box<String> box = new Box<>();  // String is the type param
box.print(new ArrayList<Integer>()); // List<Integer> is allowed because of wildcard
```

## 2. **Upper Bounded Wildcard (? extends X)** :
- Represents an unknown type that is a subtype of X.

Example :
```java
List<? extends Number>
```

Use Case : 
- When you only need to read data.
- If we passed `List<Integer>` to a method to `List<? extends Number>` and If we add `Double` to that list, it would break type safety because `Double` is not an `Integer`, that's why it's read-only.
- If we passed `List<Object> list = Arrays.asList<5, "Java">` to the `List<? extends Number>`, then if we done `Integer i = list.get(1)` might become `Integer i = "Java"`, Java can not gaurantee that every element is an integer.

## 3. **Lower Bounded Wildcard (? super X)** :
- Represents an unknown type that is a supertype of X.

Example :
```java
List<? super Integer>
```

Use Case : 
- When you only need to write data.
- If we passed `List<Integer>` or `List<Number>` or `List<Object>` to a method to `List<? super Integer>` and If we add `list.add(10)` to that list, it would be fine for all Integer, Number and Object, that's why it's write-friendly.

# Type Erasure
- Generics exist only at compile time.
- After compilation, Java removes Generic type information.
- `List<String>` and `List<Integer>` are the same class at runtime, it becomes `List list = new ArrayList<>()`.
- If `<T extends Number>`, Compiler replaces T with its upper bound `Number`.
- If `<T>` only then it replace `T` with `Object`.

# Generic Inheritance

## What is it?

Generic Inheritance means a **child class inherits a generic parent class** by specifying the actual type.

```java
class Repository<T> {
    void save(T obj) { }
}

class UserRepository extends Repository<User> { }

class EmployeeRepository extends Repository<Employee> { }
```

---

## What problem does it solve?

It **eliminates code duplication**.

Without Generic Inheritance:

```java
class UserRepository {
    void save(User user) { }
    void delete(User user) { }
}

class EmployeeRepository {
    void save(Employee employee) { }
    void delete(Employee employee) { }
}
```

The same methods (`save()`, `delete()`, etc.) have to be written again and again for every entity.

With Generic Inheritance:

```java
class Repository<T> {
    void save(T obj) { }
    void delete(T obj) { }
}
```

The common logic is written only once.

---

## How does it work?

The generic parent class contains the common implementation.

```java
class Repository<T> {
    void save(T obj) { }
}
```

Each child specifies what `T` should be.

```java
class UserRepository extends Repository<User> { }
// T = User

class EmployeeRepository extends Repository<Employee> { }
// T = Employee
```

The compiler automatically replaces `T` with the specified type.

For example:

- `Repository<User>` → `save(User obj)`
- `Repository<Employee>` → `save(Employee obj)`

---

## Why do we still create UserRepository and EmployeeRepository?

Because they can contain **entity-specific methods** while reusing all the common methods.

```java
class UserRepository extends Repository<User> {

    User findByEmail(String email) {
        // User-specific logic
    }
}
```

So:

- Common methods → Parent (`Repository<T>`)
- Specific methods → Child (`UserRepository`, `EmployeeRepository`)

---

## Real-world use case

Spring Data JPA uses Generic Inheritance.

```java
interface UserRepository extends JpaRepository<User, Long> {
}
```

Without writing any code, you automatically get:

- save()
- findById()
- findAll()
- delete()

because they are already implemented in the generic parent interface.

---

## Easy way to remember

- **Generic Parent** → Contains common logic.
- **Child Class** → Specifies the actual type (`User`, `Employee`, etc.).
- **Benefit** → Write common code once, reuse it everywhere, and maintain type safety.

### Interview One-Liner

> Generic Inheritance allows a child class to reuse a generic parent's common logic by specifying the actual type, reducing code duplication while providing compile-time type safety.

# Raw Type
- Using a Generic class without specifying a type parameter.

- Before Java 5, Generic did not exist, so old code looks like
```java
List list = new ArrayList();
```
- When Generics were introduced, Java kept supporting old code for backward compatibility.

And after Generic
```java
List<String> list = new ArrayList<>();
```

Problem with Raw Types :

```java
List list = new ArrayList();

list.add("Java");
list.add(10);

String s = (String) list.get(1);    // Runtime Error (ClassCastException), No type safety.
```

Benifit with Generic :
```java
List<String> list = new ArrayList();

list.add("Java");
list.add(10);   // Compiler gives an error here
```

# Generic Restrictions
1. **Cannot Create Object of T**

```java
class Box<T> {
    T obj = new T();
}
```
- At runtime, Java doesn't know what T is.

2. **Cannot Create Generic Array** :

```java
T[] arr = new T[10];
```

- Again, actual type of T is unknown at runtime.

3. **Cannot Use Primitive Types**
```java
List<int>   // Now allowed
```

4. **Cannot Use instanceof with Exact Generic Type**
```java
if(list instanceof List<String>)
```

- Because String information is erased.

5. **Cannot Create Static Field of T**:
```java
class Box<T> {
    static T value;
}
```
- Static members belong to the class, but T changes per object, so Java cannot decide which T to use.

6. **Cannot Catch Generic Type**

```java
catch(T e)
```
- Exception type must be known at compile time.