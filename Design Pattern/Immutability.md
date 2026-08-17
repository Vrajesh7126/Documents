# Immutability

- An object is immutable if it's data doesn't change once created.
- `String` is the immutable object.

```java
String name = "Vrajesh";
name.concat(" Vaghasiya");  // name is still "Vrajesh"

String newName = name.concat(" Vaghasiya"); // Now newName is "Vrajesh Vaghasiya"
```

- Biggest Advantge : Thread safety

## How to create a immutable class :

- Make the class final
- Make all te fields private final
- Assign all the class fields at the constructor
- Create only getter, not setter

```java
public final class Employee{
    private final int id;
    private final string name;

    public Employee(){
        this.id = id;
        this.name = name;
    }

    public int getId(){return this.id;};
    public int getName(){return this.name;};
}
```