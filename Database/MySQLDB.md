# Constraints
- Rules applied to the table column to ensure that only valid data should is stored in database.
- If Query violates a constraints, Oracle rejects the operation, roll back the statement, and return the error to the calling application.

## NOT NULL
- Value should not be null.

## UNIQUE
- No duplicate values
- It does not treat NULL as a value (Allows multiple NULL values)

## PRIMARY KEY
- UNIQUE + NOT NULL
- Use to uniquely identify the row into the table.
- One PK per table.

## FOREIGN KEY
- Refer to the PK of the other table for the reference.
- Create a relationship between 2 tables.

## CHECK
- Allows a value that satisfied the condition.

## DEFAULT
- If not value has been provided, store the default value.

```java
// MySQL
create Table Employee{
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) UNIQUE,
    age INT CHECK (age >= 18),
    status VARCHAR(10) DEFAULT 'ACTIVE',
    dept_int INT,
    FOREIGN Key(dept_id) REFERENCES Department(dept_id)
}
```

```java
// ORACLE
CREATE TABLE Employee (
    id NUMBER PRIMARY KEY,
    name VARCHAR2(50) NOT NULL,
    email VARCHAR2(50) UNIQUE,
    age NUMBER CHECK (age >= 18),
    status VARCHAR2(10) DEFAULT 'ACTIVE',
    dept_int NUMBER,
    CONSTRAINT fk_emp_dept
        FOREIGN KEY (dept_id)
        REFERENCES Department(dept_id)
)
```

- If we did not mentioned CONSTRAINT name, then Oracle itself will generate the name like SYS_C0012456, and gives an error **ORA-02291: integrity constraint (HR.SYS_C0012456) violated**

- Use name (fk_emp_dept) because If oracle will throw an error regarding the constiant violation it will give like **ORA-02291: integrity constraint (HR.fk_emp_dept) violated**, You can know the problem is with the Employee → Department relationship.

- If we want to drop the FK,
```java
// Without a name
ALTER TABLE Employee
DROP CONSTRAINT SYS_C0012456;

// With a name
ALTER TABLE Employee
DROP CONSTRAINT fk_emp_dept;
```

```java
@Entity
class Employee{
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    private String status = "ACTIVE";

    @ManyToOne
    @JoinColumn(name = "dept_id")
    private Department department;
}
```

NOTE : Instead of relying only on the DB constraints, we should validate at Controller also.

```java
@PostMapping
public Employee save(@Valid @RequestBody Employee employee){
    return repository.save(employee);
}
```

# JOIN
- Join 2 tables based on common columns (PK & FK)

### Employee Table
| id | name  | dept_id |
| -- | ----- | ------- |
| 1  | John  | 101     |
| 2  | Alice | 102     |
| 3  | Bob   | 103     |

```java
@Entity
class Employee{
    @Id
    private int id;

    private String name;

    @ManyToOne              // Many employees belong to the Department
    @JoinColumn("dept_id")  // ON e.id = d.dept_id
    private Department department;
}
```

### Department Table
| dept_id | dept_name |
| ------- | --------- |
| 101     | HR        |
| 102     | IT        |
| 104     | Finance   |

```java
@Entity
class Department{
    @Id
    private int dept_id;

    private String dept_name;
}
```

## INNER JOIN
- Returns only matching rows from both tables.

```sql
SELECT e.name, d.name
FROM Employee e
INNER JOIN Department d
ON e.dept_id = d.dept_id
```

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long>{

    @Query("""
        SELECT e
        FROM Employee e
        LEFT JOIN e.department d
        """)
    List<Employee> getEmployees();
// No "ON" Needed, because ManyToOne will taken care of it
}
```

## LEFT JOIN
- All rows from the left table + matching rows from the right table.
- If no match → `NULL`

## RIGHT JOIN
- All rows from right table + matching rows from the left table
- If no match → `NULL`

## FULL OUTER JOIN
- All rows from both the tables, if they match merge them else make an individual entry with NULL.
- MySQL did not support it directly, you need to UNION the `LEFT OUTER JOIN` & `RIGHT OUTER JOIN`
- Oracle supports it

```sql
// ORACLE
SELECT e.name, d.name
FROM employee e
FULL OUTER JOIN Department d
ON e.dept_id = d.dept_id
```

## CROSS JOIN
- Return every row of first table combined every row of the second table.

```sql
SELECT e.name, d.dept_name
FROM Employee e
CROSS JOIN Department d
```

## JOIN FETCH
- Solve N+1 Query problem.

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long>{
    /*Fetch employees only If Department is LAZY (N + 1 Problem)*/
    public List<Employee> findAll();

    /*Fetch Employee + Departmnt*/
    @Query("""
        SELECT e
        FROM Employee e
        JOIN FETCH e.department
        """)
    List<Employee> findAllEmployees();
}
```

# Relationship

## @ManyToOne
- Many `Employee` belong to one `Department`.
- Use when you only need `Employee` → `Department`
- Default `FetchType.EAGER` for `department`.

```java
public class Employee{

    @ManyToOne
    private Department department;
}
```

## @OneToMany
- One `Department` contains many `Employee`.
- Default `FetchType.LAZY` for `employees`.

```java
public class Department{

    @OneToMany
    private List<Employee> employees;
}
```

## JoinColumn
- Tells JPA which FK column connects `Department` table.

```java
public class Employee{

    @JoinColumn(name = "dept_id")
    private Department departent;
}
```

## mappedBy
- Tells JPA relationship were handled by `department` field of the `Employee` entity.

```java
public class Department{

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;
}
```

#### Note : Use Both `@OneToMany` & `@ManyToOne` when want bidirectional access `Employee → Department` & `Department → Employee`.

# Cascading

## Who is Parent
- Rule of thumb : Who can be independent is always parent, and who is dependent of the parent is child.

## Ownership of the relationship
- Decide by who contains the FK (Employee)

## Ownership of the lifecycle
- If Delete Parent(Department) also deletes the child(Employee), then `Parent(Department)` owns the child(Employee)'s lifecycle.

- If Delete Parent(Department) does not affect child(Employee), then `Child(Employee)` has it's own lifecycle.

## Cascading
- When you perform an operation on parent, JPA automatically performs the same operation for it's child.
- Rule of thumb : Cascade from the entity who is the `owner of the lifecycle` not from the `owner of the relationship`.

```java
class Department{

    @OneToMany(
        mappedBy = "department",
        cascade = CascadeType.ALL
    )
    private List<Employee> employees;
}
```

| Cascade Type | What it does                      |
| ------------ | --------------------------------- |
| `PERSIST`    | Save parent → Save children       |
| `MERGE`      | Update parent → Update children   |
| `REMOVE`     | Delete parent → Delete children   |
| `REFRESH`    | Refresh parent → Refresh children |
| `DETACH`     | Detach parent → Detach children   |
| `ALL`        | Applies all of the above          |



# Persistence Context
- Keeps track of the persistent entities.
- Temporary memory (**First level cache**) where Hibernate stores and manages entity objects.

```java
Application
      |
      v
Persistence Context (Memory)
      |
      v
Database
```

```java
Employee emp = employeeRepository.findById(1L).get();
```

    - Hibernate checks the Persistence Context.
    - If object is not there, query to the DB.
    - It stores the object in the Persistence Context.
    - It returns the object to you.
    - Second call return an object from the Persistence Context.

# Dirty Checking

```java
emp.setName("Vrajesh");
```

    - We never call employeeRepository.save(emp);
    - When hibernate ends the transactions, it notice new Name != oldName
    - It automatically executes

```java
UPDATE Employee
SET name = 'Vrajesh'
WHERE id = 1;
```

    - This automatically change detection is called dirty checking.

