Spring rollback for a RunTime Exception, not for Checked Exception.

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