Spring rollback for a `RuntimeException` and `Error`, not for `Checked Exception`.

To add rollback for a checked exception, you can use `@Transactional(rollbackFor = IOException.class)`.

Now for below,

```java
@Transactional(rollbackFor = IOException.class)
void doSomething() throws IOException {
    // do something
    throw new IOException("Checked Exception");
}
```

It is getting rolled back.

Spring skips the AOP when a function with an annotation `@Transactional` is called from within the same class.

# Transaction Propagation

What happens when one transactional method calls another transactional method?

| Propagation       | Existing TX (Outer has `@Transactional`) | No Existing TX (Outer has no `@Transactional`) |
| ----------------- | ---------------------------------------- | ---------------------------------------------- |
| **REQUIRED**      | Join existing TX                         | Create new TX                                  |
| **REQUIRES_NEW**  | Suspend existing TX, create new TX       | Create new TX                                  |
| **SUPPORTS**      | Join existing TX                         | Run without TX                                 |
| **NOT_SUPPORTED** | Suspend existing TX, run without TX      | Run without TX                                 |
| **MANDATORY**     | Join existing TX                         | ❌ Exception                                    |
| **NEVER**         | ❌ Exception                              | Run normally                                   |
| **NESTED**        | Create savepoint inside existing TX      | Create new TX                                  |

## REQUIRED

If inner method throws an exception, the outer method will also roll back.

## **REQUIRED_NEW**

```text
Transaction A starts
      ↓
outerMethod()
      ↓
A is paused
      ↓
Transaction B starts
      ↓
innerMethod()
      ↓
B commits
      ↓
A resumes
      ↓
A rolls back
```

Even If Transaction A rolls back, Transaction B is remains commited.

If Transaction B rolls back, Transaction A is not affected.

## @Transactional(readOnly = true)

With `@Transactional(readOnly = true)`, Spring will set the transaction to read-only mode, which can help with performance optimizations in some databases.

If we will modify the data, the behaviour is depends on the JPA/database setup
- Hibernate may skip dirty checking, so changes may not be persisted.
- Some DB may not execute the updte query and throw an exception.

## @Transaction + @Async

```java
@Transactional
public void placeOrder() {
    // do some work
    sendEmail(); // @Async
}

@Async
public void sendEmail() {
    // send email
}
```

The **transaction is a thread bound**, so when `sendEmail()` is called, it runs in a different thread and does not have access to the transaction started in `placeOrder()`.

So if `sendEmail()` fails, it does not roll back the transaction in `placeOrder()`, because it is running in a different thread.

## @Transactional + @EventListener vs @TransactionalEventListener

```java
@Transactional
public void placeOrder() {
    // do some work
    eventPublisher.publishEvent(new OrderPlacedEvent());
}

@EventListener
public void sendEmail(OrderPlacedEvent event) {
    // send email
}
```

```text
Start Transaction (placeOrder())
       ↓
Publish Event (publshEvent())
       ↓
Listeners are called (sendEmail())
       ↓
Transaction is committed/rolled back
```

If the transaction later rolled back, the email is already sent, which is not what we want.

```java
@TransactionalEventListener
public void sendEmail(OrderPlacedEvent event) {
    // send email
}
```

```text
Start Transaction (placeOrder())
       ↓
Publish Event (publshEvent())
       ↓
Transaction is committed/rolled back
       ↓
Listeners are called (sendEmail())
```

If the transaction later rolled back, the listener never called and the email is not sent, which is what we want.

# Spring JPA

```java
@Transactional
public void createEmployee() {
    repository.save(employee);
}
```

When I use it, Spring starts the transaction.

JPA defines the persistence API, Hibernate implements it.

Hibernate's `EntityManager` operates within that transaction.

`EntityManager` maintains a **first-level cache** called **Persistence Context** which tracks changes to managed entities (dirty checking).

```text
find(Employee, 1)
      ↓
DB query

find(Employee, 1) again
      ↓
Persistence Context
      ↓
No DB query
```

```java
@Transactional
public void updateEmployee() {
    
    Employee emp = repository.find(Employee.class, 1);

    emp.setSalary(5000);
}
```

```text
@Transactional
       ↓
BEGIN TRANSACTION
       ↓
find Employee
       ↓
change salary
       ↓
flush
       ↓
UPDATE query sent to DB
       ↓
COMMIT
       ↓
Changes become permanent
```

We did not need to manually calls `flush()`, when transaction is about to commit, Hibernate automatically calls `flush()` and commit the transaction.

Between flush and commit, Any other transaction is not able to see the changes made within the current transaction, after commit, the changes become permanent and visible to other transactions.

# JpaRepository

`JpaRepository` provides repository methods without you implementing them manually.