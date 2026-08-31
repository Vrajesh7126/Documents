# String
- == compares references
- .equals() compares values

## String Pool
- Java stored String literals in the String Pool.
- So both s1 and s2 point to the same object.

```java
String s1 = "Java";
String s2 = "Java";
s1 == s2 // true

String s3 = new String("Java"); // New object will be created into the heap
s1 == s3 // false

String s4 = s3.intern();    // intern return same string from the pool
s1 = s4 // true
```

# Integer caching
- Java catches Integer objects in the range of `-128 to 127`.
- So,

```java
Integer a = 100;
Integer b = 100;
a == b; // true
```

```java
Integer a = 200;
Integer b = 200;
a == b;         // false
a.equals(b);    // true
```

```java
// Caching works with Integer not with int
int a = 200;
int b = 200;
a == b; // true
```

```java
Integer a = 200;    // Integer unbox to int using a.intValue()
int b = 200;
a == b; // true
```

```java
Integer a = null;    // Integer unbox to int using a.intValue()
int b = 200;
a == b; // NullPointerException
```

# `byte`, `short`, and `char`

## Arithmetic Promotion

Arithmetic operations on `byte`, `short`, and `char` are automatically promoted to `int`.

```java
byte a = 5;
byte b = 7;

byte c = a + b; // ❌ a + b becomes int
```

```text
byte  + byte  → int
short + short → int
char  + char  → int
```

So this works:

```java
int c = a + b; // ✅
```

Or use an explicit cast:

```java
byte c = (byte) (a + b); // ✅
```

---

## Compile-Time Constant Exception ⭐

A constant expression can be assigned directly to `byte`, `short`, or `char` if the result fits in the target type.

```java
byte b = 10 + 20; // ✅
```

The compiler evaluates:

```text
10 + 20
  ↓
30
```

Since `30` fits in the `byte` range (`-128` to `127`), the assignment is allowed without a cast.

### `final` Constants

```java
final byte a = 10;
final byte b = 20;

byte c = a + b; // ✅
```

Because `a` and `b` are `final` and initialized with constant values, the compiler treats `a + b` as a compile-time constant (`30`).

Compare:

```java
byte a = 10;
byte b = 20;

byte c = a + b; // ❌ a + b is promoted to int
```

---

## Interview Rule ⭐

```text
byte / short / char
        ↓
Arithmetic operation
        ↓
      int
```

**Exception:** A compile-time constant expression is allowed if its result fits in the target type.

```java
byte a = 10 + 20; // ✅

final byte x = 10;
final byte y = 20;

byte b = x + y;   // ✅
```

**Core rule:** Arithmetic → `int`; compile-time constant + fits → direct assignment allowed.

# switch-case

`switch` on `null` throws `NullPointerException`

```java
String value = null;

switch (value) {
    case "Java":
        System.out.println("Java");
        break;
    default:
        System.out.println("Other");
}

// Output :
// Exception in thread "main"
// java.lang.NullPointerException
```

But in Java 21, switch adds explicit support for case null

```java
switch (value) {
    case null -> System.out.println("Value is null");
    case "Java" -> System.out.println("Java");
    default -> System.out.println("Other");
}
```

# instanceOf

instanceOf checks does this object exist, and is it an instance of this class.

```java
String str = null;

System.out.println(str instanceof String);  // false
System.out.println(str.getClass()); // NullPointerException
```

Java 16+

```java
if (obj instance of String s) {
    System.out.println(s.toUpperCase());
}

// If obj is null, condition will be false and variable s is not created, and no exception is throws.
```

