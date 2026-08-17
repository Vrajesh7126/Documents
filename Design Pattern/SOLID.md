- [Single Responsibility Principle](#s---single-responsibility-principle)
- [Open/Closed Principle](#o---openclosed-principle)
- [Liskov Substitution Principle](#l---liskov-substitution-principle)
- [Interface Segregation Principle](#i---interface-segregation-principle)
- [Dependency Inversion Principle](#d---dependency-inversion-principle)

- 5 Design pattern to write the clean, maintainable and flexible code.

# S - Single Responsibility Principle
- A class should have only one responsibility.

```java
// BAD
class Employee{
    void calculateSalary(){}
    void sendEmail(){}
}
```

```java
// GOOD
class SalaryService(){
    void calculateSalary(){}
}

class EmailService(){
    void sendEmail(){}
}
```

# O - Open/Closed Principle
- Open for Extension, closed for modification.
- Instead of changing existing code, add new one.

```java
// BAD : Every new payment method requires modifying the code
if(paymentType.equals("UPI")){

}
else if(paymentType.equals("CARD")){

}
```

```java
// GOOD : Add the new class when new payment method needed
interface Payment{
    void pay();
}

class UPIPayment implements Payment{
    public void pay(){}
}

class CardPayment implements Payment{
    public void pay(){}
}
```

# L - Liskov Substitution Principle
- A subclass should be replacable by it's parent class without breaking the program.

```java
class Bird{
    void fly(){}
}

// GOOD : A sparrow can fly
class Sparrow extends Bird{
    public void fly(){}
}

// BAD : Ostratich can't fly so it's not replacable by it's parent
class Ostrich extends Bird{
    public void fly(){
        throw new UnsupportedOperationExcepton();
    }
}
```

# I -Interface Segregation
- Maintain an interface a specific.
- Don't force a class to implement methods it doesn't need.

```java
// BAD
interface Workable{
    void work();
    void eat();
}

class Robot implements Workable{
    public void work(){}
    public void eat(){} // Not valid
}
```

```java
// GOOD
interface Workable{
    void work();
}

interface Eatable{
    void eat();
}

class Human implements Workable, Eatable{
    public void work(){}
    public void eat(){}
}

class Robot implements Workable{
    public void work(){}
}
```

# D - Dependency Inversion
- Depends on abstractions (interface), not concrete class.

```java
// BAD
class Car{
    Engine engine = new DieselEngine();
}
```

```java
// GOOD : Just pass DieselEngine in the constructor of the car
interface Engine{
    void start();
}

class DieselEngine implements Engine{
    public void start(){}
}

class Car{
    private Engine engine;

    Car(Engine engine){
        this.engine = engine;
    }
}
```
- Can pass:
    - DieselEngine
    - PetrolEngine
    - ElectricEngine
- without changing the `Car`