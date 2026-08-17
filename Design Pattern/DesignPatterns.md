- [Factory](#factory)
- [Abstract](#abstract)
- [Strategy](#strategy)
- [Builder](#builder)
- [Observer](#observer)
- [Singleton](#singleton)
- [Prototype](#prototype)
- [Abstract](#abstract)
- [Adapter](#adapter)
- [Decorator](#decorator)
- [Facade](#facade)
- [Proxy](#proxy)
- [Composite](#composite)
- [Command](#command)
- [State](#state)
- [Template](#template)
- [Chain of Responsibility](#chain-of-responsibility)
- [Iterator](#iterator)
- [Mediator](#mediator)

# Factory
- Suppose you want to create objects, but you don't want the client code to know which exact class is being created.
- Without Factory:

```java
Car car = new BMW();
```

- Here the client is tightly coupled to BMW.
- If tomorrow BMW changes to Audi, you must modify the client code.
- Factory Idea : "Give the responsibility of object creation to a separate class."

Example :

```java
// Product
interface Car {
    void drive();
}

// Implementations
class BMW implements Car {
    public void drive() {
        System.out.println("Driving BMW");
    }
}

class Audi implements Car {
    public void drive() {
        System.out.println("Driving Audi");
    }
}

// Factory
class CarFactory {

    public static Car getCar(String type) {

        if(type.equals("BMW"))
            return new BMW();

        if(type.equals("AUDI"))
            return new Audi();

        return null;
    }
}
```

```java
// Client
Car car = CarFactory.getCar("BMW");
car.drive();
```

Benifits :
- Loose Coupling
- Centralized Object Creation
- Easy Maintenance
- Scalable

# Abstract
- Abstract Factory Pattern provides an interface to create a family of related objects without specifying their concrete classes.

Example :

```java
// Products

// Button
interface Button {
    void click();
}

// Checkbox
interface Checkbox {
    void check();
}

// Windows Products
class WindowsButton implements Button {
    public void click() {
        System.out.println("Windows Button");
    }
}
class WindowsCheckbox implements Checkbox {
    public void check() {
        System.out.println("Windows Checkbox");
    }
}

// Mac Products
class MacButton implements Button {
    public void click() {
        System.out.println("Mac Button");
    }
}
class MacCheckbox implements Checkbox {
    public void check() {
        System.out.println("Mac Checkbox");
    }
}
```

```java
// Abstract Factory
interface UIFactory {

    Button createButton();

    Checkbox createCheckbox();
}

// Concrete Factories

// Windows Factory
class WindowsFactory implements UIFactory {

    public Button createButton() {
        return new WindowsButton();
    }

    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }
}

// Mac Factory
class MacFactory implements UIFactory {

    public Button createButton() {
        return new MacButton();
    }

    public Checkbox createCheckbox() {
        return new MacCheckbox();
    }
}
```

```java
// Client
UIFactory factory = new WindowsFactory();

Button button = factory.createButton();

Checkbox checkbox = factory.createCheckbox();
```


# Strategy
- When you have multiple ways to perform the same task, and you want to choose the behavior at runtime.

Instead of:

```java
if(paymentType.equals("UPI")) {
   ...
}
else if(paymentType.equals("CARD")) {
   ...
}
else if(paymentType.equals("NETBANKING")) {
   ...
}
```

- Put each behavior in a separate class.
- Encapsulate different algorithms/behaviors into separate classes and switch them dynamically.

Example: Payment System

```java
// Strategy Interface
interface PaymentStrategy {
    void pay(int amount);
}

// Different Strategies
class UpiPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid using UPI");
    }
}

class CardPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid using Card");
    }
}

// Context Class
class PaymentService {

    private PaymentStrategy strategy;

    public PaymentService(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void makePayment(int amount) {
        strategy.pay(amount);
    }
}
```

```java
// Client
PaymentStrategy strategy = new UpiPayment();

PaymentService service = new PaymentService(strategy);

service.makePayment(1000);
```

Benefits :
- Removes Large if-else/switch
- Easy to Add New Behavior
- Runtime Behavior Change

# Builder
- When an object has many fields, creating it using constructors becomes messy.
- Main idea : Build an object step-by-step and create it only when everything is ready.

Example :

```java
// User Class
class User {

    private String name;
    private int age;
    private String city;

    private User(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.city = builder.city;
    }

    public static class Builder {

        private String name;
        private int age;
        private String city;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
```

```java
// Usage
User user = new User.Builder()
        .name("Vrajesh")
        .age(25)
        .city("Ahmedabad")
        .build();
```

Benefits :
- Readable Code
- No Constructor Explosion
- Optional Fields Become Easy (Only set what you need)
- Helps Create Immutable Objects (After build(), object can be made read-only)

# Observer
- When one object changes, and multiple other objects need to be notified automatically.
- Instead of manually calling everyone:

```java
emailService.send();
smsService.send();
mobileApp.notify();
```
we let them subscribe and get notified automatically.

- Main Idea : One object (Subject) maintains a list of interested objects (Observers) and notifies them whenever something changes.

Component :
- Subject : The object being watched
- Observer : Objects interested in updates.
- Notify : When data changes, all observers are informed.

Example :

```java
// Observer Interface
interface Observer {
    void update(int temperature);
}

// Observers
class MobileDisplay implements Observer {

    public void update(int temperature) {
        System.out.println("Mobile: " + temperature);
    }
}

class TVDisplay implements Observer {

    public void update(int temperature) {
        System.out.println("TV: " + temperature);
    }
}

// Subject
class WeatherStation {

    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void setTemperature(int temperature) {
        notifyObservers(temperature);
    }

    private void notifyObservers(int temperature) {

        for (Observer observer : observers) {
            observer.update(temperature);
        }
    }
}
```

```java
// Client
WeatherStation station = new WeatherStation();

station.addObserver(new MobileDisplay());
station.addObserver(new TVDisplay());

station.setTemperature(35);
```

Flow :
```java
Weather Changed
        ↓
WeatherStation
        ↓
Notify All Observers
        ↓
MobileDisplay Updated
TVDisplay Updated
Website Updated
```

Benefits :
- Loose Coupling (Subject doesn't know internal details of observers).
- Easy to Add New Observers (Scalable)
- One-to-Many Relationship

# Singleton
- When you want only one object of a class in the entire application.
- Main Idea : Allow only one instance of a class and provide a global way to access it

Example :

```java
class Singleton {

    private static Singleton instance;

    private Singleton() {   // Private Constructor
    }

    public static Singleton getInstance() {

        if (instance == null) {
            instance = new Singleton();
        }

        return instance;
    }
}
```

```java
// Usage

Singleton s1 = Singleton.getInstance();
Singleton s2 = Singleton.getInstance();

System.out.println(s1 == s2);   // true
```

Problem with Above Implementation :

In multithreading:

Thread-1:
```java
if(instance == null)
```

Thread-2:
```java
if(instance == null)
```

Both may create objects simultaneously.

Result: 2 Singleton Objects, Pattern breaks.

Solution :

```java
public static synchronized Singleton getInstance()  // Only one thread enters at a time

// or

public static Singleton getInstance() {

    if(instance == null) {

        // Synchronization only during first creation, later it will directly return instance
        synchronized (Singleton.class) {

            if(instance == null) {
                instance = new Singleton();
            }
        }
    }

    return instance;
}
```

Use case :
- Logger
- Configuration Manager
- Spring Bean

# Prototype
- Instead of creating a new object from scratch, we copy an existing object.
- Main Idea : Create new objects by cloning an existing object.

- Cloneable is a marker interface, Its only purpose is to tell Java: "This class allows cloning."
- without it, calling clone() will throw **CloneNotSupportedException**
- default clone() = Shallow Copy.

Example :

```java
class Person implements Cloneable {
    private String name;

    public Person(String name) {
        this.name = name;
    }

    // Copy Constructor (Alternative to clone method)
    public Person(Person other) {
        this.name = other.name;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
```

```java
// Usage
Person p1 = new Person("Original");
Person p2 = (Person) p1.clone();
System.out.println(p1 == p2);       // false (different objects)
System.out.println(p1.equals(p2));  // true (same content)
```

- But in modern Java, many developers prefer.

```java
Person p2 = new Person(p1);  // Copy Constructor
```

- **@Scope("prototype")** : Spring creates a new copy/object whenever requested.

Benifits :
- Faster Object Creation (Cloning is faster than new)
- Avoid Complex Initialization (No need to set up everything from scratch)
- Dynamic Object Creation (Create objects at runtime based on existing ones)

Drawback :
- Deep cloning can become complicated when objects contain many nested objects.

# Adapter
- When two classes/interfaces are incompatible, but you want them to work together without modifying their existing code.

Example :
- Suppose your application expects:

```java
interface PaymentProcessor {
    void pay(int amount);
}
```

- But you have a third-party library that provides:

```java
class ThirdPartyPayment {
    public void makePayment(int amount) {
        System.out.println("Payment of " + amount + " made using ThirdPartyPayment");
    }
}
```

```java
class RazorpayAdapter implements PaymentProcessor {
    private ThirdPartyPayment thirdPartyPayment;

    public RazorpayAdapter(ThirdPartyPayment thirdPartyPayment) {
        this.thirdPartyPayment = thirdPartyPayment;
    }

    @Override
    public void pay(int amount) {
        thirdPartyPayment.makePayment(amount);
    }
}
```

```java
// Usage
PaymentProcessor processor = new RazorpayAdapter(new ThirdPartyPayment());
processor.pay(1000);
```

Benifits :
- Reuse Existing Code (Use third-party libraries without modification)
- Loose Coupling (Client code is decoupled from third-party implementation)
- Easy Integration

# Decorator
- When you want to add new functionality to an object without modifying its existing class.

Example :
```java
// Component
interface Coffee {
    String getDescription();
    int getCost();
}

// Concrete Component
class SimpleCoffee implements Coffee {
    public String getDescription() {
        return "Coffee";
    }

    public int getCost() {
        return 50;
    }
}

```

```java
// Decorator
abstract class CoffeeDecorator implements Coffee {
    protected Coffee decoratedCoffee;

    public CoffeeDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }
}

// Concrete Decorator
class MilkDecorator extends CoffeeDecorator {

    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Milk";
    }

    public int getCost() {
        return decoratedCoffee.getCost() + 20;
    }
}

class SugarDecorator extends CoffeeDecorator {

    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Sugar";
    }

    public int getCost() {
        return decoratedCoffee.getCost() + 10;
    }
}
```

```java
// Client
Coffee coffee = new SimpleCoffee();

CoffeeDecorator milkCoffee = new MilkDecorator(coffee);

CoffeeDecorator sugarMilkCoffee = new SugarDecorator(milkCoffee);

System.out.println(sugarMilkCoffee.getDescription());  // Coffee, Milk, Sugar
System.out.println(sugarMilkCoffee.getCost());         // 80
```

Benifits :
- Add Functionality Dynamically
- Avoid Class Explosion
- Follows Open/Closed Principle

Where Used in Java :
- **FileInputStream** wrapped by **BufferedInputStream** wrapped by **DataInputStream**.

```java
InputStream in = new BufferedInputStream(new FileInputStream("data.txt"));
```

# Facade
- When a system has many complex classes, and the client doesn't want to interact with all of them directly.
- Main Idea : Provide a simplified interface to a complex subsystem.

Example :

```java
// Subsystem Classes
class TV {
    void on() {
        System.out.println("TV ON");
    }
}
class SoundSystem {
    void on() {
        System.out.println("Sound ON");
    }
}
class Projector {
    void on() {
        System.out.println("Projector ON");
    }
}
```

```java
class HomeTheaterFacade {

    private TV tv;
    private SoundSystem sound;
    private Projector projector;

    public HomeTheaterFacade() {
        tv = new TV();
        sound = new SoundSystem();
        projector = new Projector();
    }

    public void watchMovie() {

        tv.on();
        sound.on();
        projector.on();

        System.out.println("Movie Started");
    }
}
```

```java
// Client
HomeTheaterFacade homeTheater = new HomeTheaterFacade();
homeTheater.watchMovie();
```

Where Used in Spring :

```java
userRepository.save(user);
// Internally Spring handles 
// - Connection
// - Transaction
// - Commit/Rollback
// - SQL
// - Exception Handling
```

Benifits :
- Hides Complexity
- Reduces Coupling (Client doesn't depend on many subsystem classes.)


# Proxy
- When you want to control access to an object without changing the original object.
- The Proxy sits in front of the real object and decides:
    - Allow?
    - Block?
    - Cache?
    - Log?
    - Security Check?
    - Lazy Load?
- Before forwarding the request to the real object.

- **Main Idea** : Proxy acts as a placeholder or middleman for the real object.

Example :

```java
// Subject Internet
interface Internet{
    void connect();
}

// Real Object
class RealInternet implements Internet{
    public void connect(){
        System.out.println("Connected");
    }
}
```

```java
// Proxy
class ProxyInternet implements Internet{
    private RealInternet internet = new RealInternet();

    public void connect(){
        System.out.println("Checking Access...");

        internet.connect();
    }
}
```

```java
Internet internet = new ProxyInternet();
internet.connect();
```

Common Types of Proxy :
- Security Proxy : Checks Permissions.
- Logging Proxy : Logs calls.
- Caching Proxy : Caches results.
- Virtual Proxy : Creates expensive objects only when needed (Lazy Loading).

Where Used in Java :
- *Spring AOP* : @Transactional

# Composite
- When you have a tree-like structure and want to treat:
    - A single object
    - A group of objects
- The same way.

- Main Idea : Treat individual objects(leaf) and groups of objects(composite) uniformly.

- Think of a File System:

```java
Folder
    ├── File1
    ├── File2
    └── SubFolder
        ├── File3
        └── File4
```

- And want to perform `showDetails()` on both File and Folder in the same way.

Example :

```java
// Component
interface FileSystem {
    void showDetails();
}

// Leaf : object that cannot contain other objects.
class File implements FileSystem {
    private String name;

    public File(String name) {
        this.name = name;
    }

    public void showDetails() {
        System.out.println("File: " + name);
    }
}

// Composite : Can contain other objects (Files or Folders)
class Folder implements FileSystem {
    private String name;
    private List<FileSystem> children = new ArrayList<>();

    public Folder(String name) {
        this.name = name;
    }

    public void add(FileSystem fs) {
        children.add(fs);
    }

    public void showDetails() {
        System.out.println("Folder: " + name);
        for (FileSystem fs : children) {
            fs.showDetails();
        }
    }
}
```

```java
// Client
Folder subFolder = new Folder("SubFolder");
subFolder.add(new File("File3.txt"));
subFolder.add(new File("File4.txt"));

Folder rootFolder = new Folder("RootFolder");
rootFolder.add(new File("File1.txt"));
rootFolder.add(new File("File2.txt"));
rootFolder.add(subFolder);

// Show Details of Entire Structure
rootFolder.showDetails();

// Output:
// Folder: RootFolder
// File: File1.txt
// File: File2.txt
// Folder: SubFolder
// File: File3.txt
// File: File4.txt
```

# Command
- When you want to convert a request/action into an object.
- Main Idea : Encapsulate a request as an object.

```java
// Receiver : Actual Object
class TV {
    public void turnON() {
        System.out.println("Turn ON TV");
    }
}

// Command Interface
interface Command {
    void execute();
}

// Concrete Command
class TurnONCommand implements Command {
    private TV tv;

    public TurnONCommand(TV tv){
        this.tv = tv;
    }

    public void execute(){
        tv.turnON();
    }
}

// Invoker
class RemoteControl {
    private Command command;

    public RemoteControl(Command command) {
        this.command = command;
    }

    public void pressButton() {
        command.execute();
    }
}
```

```java
TV tv = new TV();

Command command = new TurnONCommand(tv);

RemoteControl remote = new RemoteControl(command);
remote.pressButton();
```

# State
- Used when object's behavior changes based on its current state.

Without State Pattern:

```java
class Document {
    private String state; // "Draft", "Moderation", "Published"

    public void publish() {
        if(state.equals("Draft")) {
            state = "Moderation";
        } else if(state.equals("Moderation")) {
            state = "Published";
        }
    }
}
```

- Problem : As states increase, the code becomes messy with many if-else conditions.

Main Idea : Move state-specific behavior into separate classes.

Example :

```java
// State Interface
interface State {
    void pressButton();
}

// Concrete States
class NoCoinState implements State {
    public void pressButton() {
        System.out.println("Insert Coin First");
    }
}

class CoinInsertedState implements State {
    public void pressButton() {
        System.out.println("Dispensing Item");
    }
}
```

```java
// Context
class VendingMachine {
    private State state;

    public void setState(State state) {
        this.state = state;
    }

    public void pressButton() {
        state.pressButton();
    }
}
```

```java
VendingMachine machine = new VendingMachine();

machine.setState(new NoCoinState());
machine.pressButton();  // Output: Insert Coin First

machine.setState(new CoinInsertedState());
machine.pressButton();  // Output: Dispensing Item
```

- State Pattern allows an object to change its behavior when its internal state changes by moving state-specific behavior into separate state classes.

# Template
- Main Idea : Define the algorithm skeleton in a parent class and let subclasses implement specific steps.

- Instead of duplicating the whole algorithm, define the common flow once and allow subclasses to customize specific steps.

Example :

```java
// Abstract Class (Template)
abstract class Beverage {

    // Template Method
    public final void prepare() {
        boilWater();
        addIngredients();
        pourIntoCup();
    }

    private void boilWater() {
        System.out.println("Boiling Water");
    }

    private void pourIntoCup() {
        System.out.println("Pouring into Cup");
    }

    protected abstract void addIngredients();  // Step to be implemented by subclasses
}

// Tea
class Tea extends Beverage {
    protected void addIngredients() {
        System.out.println("Adding Tea Leaves");
    }
}

class Coffee extends Beverage {
    protected void addIngredients() {
        System.out.println("Adding Coffee Powder");
    }
}
```

```java
// Client
Beverage tea = new Tea();
tea.prepare();
```

- Why `final` on Template Method?
    - Prevent subclasses from changing the algorithm flow

# Chain of Responsibility
- Main Idea : Pass a request through a chain of handlers until one of them handles it.

Example :

```java
// Handler
abstract class Handler {
    protected Handler next;

    public void setNext(Handler next) {
        this.next = next;
    }

    abstract void handleRequest(String request);
}

// Concrete Handlers
class Manager extends Handler {
    void handleRequest(int amount) {
        if(amount <= 1000) {
            System.out.println("Manager approved: " + amount);
        } else if(next != null) {
            next.handleRequest(amount);
        }
    }
}

class Director extends Handler {
    void handleRequest(int amount) {
        if(amount <= 5000) {
            System.out.println("Director approved: " + amount);
        } else if(next != null) {
            next.handleRequest(amount);
        }
    }
}

class CEO extends Handler {
    void handleRequest(int amount) {
        System.out.println("CEO approved: " + amount);
    }
}
```

```java
// Client
Manager manager = new Manager();
Director director = new Director();
CEO ceo = new CEO();

manager.setNext(director);
director.setNext(ceo);

manager.handleRequest(3000);  // Output: Director approved: 3000
manager.handleRequest(7000);  // Output: CEO approved: 7000
```

# Iterator
- When you want to traverse (iterate through) a collection without exposing its internal structure.
- Main Idea : Provide a standard way to access elements one by one without exposing how they are stored.

Example :

```java
// Aggregate
interface Collection {
    void add(String item);
    Iterator iterator();
}

// Concrete Aggregate
class MyCollection implements Collection {
    private List<String> items = new ArrayList<>();
    public void add(String item) {
        items.add(item);
    }

    public Iterator iterator() {
        return new MyIterator(items);
    }
}

// Iterator
interface Iterator {
    boolean hasNext();
    String next();
}

// Concrete Iterator
class MyIterator implements Iterator {
    private List<String> items;
    private int index = 0;

    public MyIterator(List<String> items) {
        this.items = items;
    }

    public boolean hasNext() {
        return index < items.size();
    }

    public String next() {
        if(hasNext()) {
            return items.get(index++);
        }
        return null;
    }
}

// Client
Collection collection = new MyCollection();
collection.add("Item 1");
collection.add("Item 2");

Iterator iterator = collection.iterator();
while(iterator.hasNext()) {
    System.out.println(iterator.next());
}
```


# Mediator
- When many objects need to communicate with each other, creating a lot of dependencies.
- Main Idea : Instead of objects talking directly to each other, they communicate through a Mediator.

Example :

```java
// Mediator Interface
interface ChatMediator {
    void sendMessage(String message, User user);
}

// Concrete Mediator
class ChatRoom implements ChatMediator {
    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
    }

    public void sendMessage(String message, User user) {
        for(User u : users) {
            if (u != user) {
                u.receive(message);
            }
        }
    }
}
```

```java
// User
class User {
    protected ChatMediator mediator;

    public User(ChatMediator mediator) {
        this.mediator = mediator;
    }

    abstract void send(String message);
    abstract void receive(String message);
}

// Concrete User
class ChatUser extends User {
    public ChatUser(ChatMediator mediator) {
        super(mediator);
    }

    void send(String message) {
        mediator.sendMessage(message, this);
    }

    void receive(String message) {
        System.out.println("Received message: " + message);
    }
}
```

```java
// Client
ChatMediator room = new ChatRoom();

User user1 = new ChatUser(room);
User user2 = new ChatUser(room);

room.addUser(user1);
room.addUser(user2);

user1.send("Hello Users");
```

- User never talks directly.
- Without mediator : Every user must know about every other user, leading to tight coupling.
- With mediator : Only Mediator knows everyone.