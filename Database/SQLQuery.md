# ORDER BY
- Sort the result set.
- Types of order : ASC (Default), DESC
- Last Clause after GROUP BY & HAVING

```sql
-- Ascending sorting
SELECT *
FROM Employee
ORDER BY salary;

-- Descending sorting
ORDER BY salary;

-- 2 column sorting
-- sort by salary, If salaries are equal, sort by name
ORDER BY salary DESC, emp_name ASC;

-- ORDER BY + JOIN
SELECT e.emp_name, d.dept_name, e.salary
FROM Employee e
JOIN Department d
ON e.dept_id = d.dept_id
ORDER BY d.dept_name ASC, e.salary DESC;
```

# DISTINCT
- Remove duplicate rows from the result.
- **GROUP BY** : Group rows, usually to perform the aggregate calculations.



```sql
-- DISTINCT with 1 column
SELECT DISTINCT dept_id
FROM Employee;

-- Above query and this GROUP BY query gives the same result
SELECT dept_id
FROM Employee
GROUP BY dept_id;

-- DISTINCT with multiple column
-- It removes duplicate combination of dept_id & salary
SELECT DISTINCT dept_id, salary
FROM EMployee;

-- DISTINCT with aggregate function
SELECT COUNT(DISTINCT dept_id)
FROM Employee;
```

# AGGREGARE FUNCTIONS
- COUNT : Count number of rows
    - COUNT(*) = Count all rows
    - COUNT(column) = Count non-NULL values

- SUM : Count Sum
- AVG : Find Average
- MIN : Find minimum
- MAX : Find maximum

- Mainly use with the `GROUP BY`

# GROUP BY
- Groups rows that have the same value in one or more columns.

```sql
-- Average salary of each department
SELECT dept_id, AVG(salary)
FROM Employee
GROUP BY dept_id;

-- Find departments with it's employee count (GROUP BY + JOIN)
SELECT d.dept_name, COUNT(e.emp_id)
FROM Employee e
JOIN Department d
WHERE e.dept_id = d.dept_id
GROUP BY d.dept_name;

-- Groupin by the dept with it's salary
SELECT dept_id, salary, COUNT(*)
FROM Employee
GROUP BY dept_id, salary
```

- Rules behind the use case of the GROUP BY.
- SUppose we have done

```sql
-- Not valid, because SQL don't know which employee to be display as emp_name from the multiple emp_name into the one dept_id
SELECT dept_id, emp_name
FROM Employee
GROUP BY dept_id;

-- Solution : Every SELECTED column should be wrapped inside the GROUP BY, If not wrapped inside the GROUP BY then column inside the SELECT should be wrapped with the aggregate function.

SELECT dept_id, COUNT(emp_name)
FROM Employee
GROUP BY dept_id;
```

# Execution Order

```sql
FROM
↓
JOIN
↓
WHERE
↓
GROUP BY
↓
Aggregate Functions
↓
HAVING
↓
SELECT
↓
ORDER BY
```

# HAVING
- HAVING filter groups, while WHERE filter rows

```sql
-- Show only department having employee more than 2
SELECT dept_id, COUNT(*)
FROM Employee
GROUP BY dept_id
HAVING COUNT(*) > 2;

-- Departments Whose Total Salary is Greater Than 80000
SELECT dept_id, AVG(salary)
FROM Employee
GROUP BY dept_id
HAVING AVG(salary) > 80000;

-- Find departments whose average salary is more than ₹30,000 (HAVING + JOIN)
SELECT d.dept_name, AVG(e.salary)
FROM Employee e
JOIN Department d
ON e.dept_id = d.dept_id
GROUP BY d.dept_name
HAVING AVG(e.salary) > 30000;

-- Show departments where employees earn more than ₹20,000, and whose total salary is greater than ₹70,000(WHERE + GROUP BY + HAVING)
SELECT d.dept_name, SUM(e.salary)
FROM Employee e
JOIN Department d
ON e.dept_id = d.dept_id
WHERE e.salary > 20000
GROUP BY d.dept_name
HAVING SUM(e.salary) > 70000
```

# SUMMARY
- **WHERE** : Filter Rows
- **GROUP BY** : Create Groups
- **Aggregate Functions** : Calculate values for each group
- **HAVING** : Filter the groups.

# CASE Expression
- Skipped

# NULL Handling Functions
- NULL means No value or Unknown value.

## IS NULL
- Return TRUE if column is NULL.

```sql
-- Find employee with a not commision
SELECT *
FROM Employee
WHERE commision IS NULL;
```

# IS NOT NULL
- - Return TRUE if column is NOT NULL.

```sql
-- Find employee who have commision
SELECT *
FROM Employee
WHERE commision IS NOT NULL;
```

## NVL()
- Replace NULL with a value.

```sql
-- Select Employee against it's commision, IF commision is not there then display 0
SELECT emp_name, NVL(commision, 0) AS commision
FROM Employee;

-- Select Employee with it's department, If department is not available, display 'No Department'
SELECT emp_name, NVL(dept_name, 'No Department')
FROM Employee;
```

## NVL2()
- Return one value if NULL exist and another if it doesn't.

```sql
NVL2(column, value_if_not_null, value_if_null)
```

```sql
-- Yes If commision, else No against employee
SELECT emp_name, NVL2(commision, "Yes", "No") As commision_status
FROM Employee;
```

## COALESCE()
- Returns first non-NULL value.

```sql
SELECT emp_name, COALESCE(phone, email, 'No COntact')
FROM Employee;
```

## NULLIF()
- Return NULL if two values are equal.

```sql
-- Returns NULL
SELECT NULLIF(10, 10)
FROM dual;
```

# String Functions
- UPPER() : Upper case
- LOWER() : Lower case
- INITCAP() : Capitalize the first letter of eveyr word
- LENGTH() : Get length
- SUBSTR(column, start_pos, length) : Get the substring
- INSTR() : Find the position of a character or substring
- TRIM() : Removes spaces from the beginning and end
- REPLACE() : Replaces one string with another
- CONCAT()/|| : Join two string
- LPAD(column, length, character) : Pads characters on the left
- RPAD(column, length, character) : Pads characters on the right
- LIKE : 
    - Match the pattern (Use with CHAR, VARCHAR2)
    - column_name LIKE 'pattern'

# Subqueries
- Query inside the another query.
- Inner query executed first, then outer query is executed.
- Run once for every row of outer query

## Single-Row Subquery

```sql
-- Employee with highest salary
SELECT *
FROM Employee
WHERE salary = (
    SELECT MAX(salary)
    FROM Employee
);

-- Employees in the IT Department(Suppose you don't know the department ID)
SELECT *
FROM Employee
WHERE dept_id = (
    SELECT dept_id
    FROM Department
    WHERE dept_name = 'IT'
);
```

## Multiple-Row Subquery
- Inner query returns more than one row.
- Use with `IN`, `ANY`, `ALL`, `EXIST`
- Run once for every row of outer query

```sql
SELECT *
FROM Employee
WHERE dept_id IN (
    SELECT dept_id
    FROM Department
    WHERE dept_name IN ('IT', 'HR')
);
```

## Correlated Subquery
- Correlated subquery depends on the current row of the outer query.

```sql
-- Find employees earning more than the average salary of their own department.

SELECT e1.*
FROM Employee e1
WHERE salary > (
    SELECT AVG(salary)
    FROM Employee e2
    WHERE e1.dept_id = e2.dept_id;
)
```

# EXISTS
- `EXIST` checks whether a subquery returns at least 1 row.
- Returns `TRUE` or `FALSE`, it doesn't return the data
- Use `SELECT 1`, because existence of rows matters instead of values.
- Stop after first search into the correlated subqueries, so it's faster then `IN`.
- `IN` returns all rows in subquery.

```sql
-- Departments that have employees
SELECT *
FROM Department d
WHERE EXIST (
    SELECT 1
    FROM Employee
    WHERE d.dept_id = e.dept_id
);

-- Above is equivalnt to this
SELECT *
FROM Department
WHERE dept_id IN (
    SELECT DISTINCT dept_id
    FROM Employee
)

-- Find employees whose department exist
SELECT *
FROM Employee e
WHERE EXISTS (
    SELECT 1
    FROM Department d
    WHERE d.dept_id = e.dept_id
);

SELECT *
FROM Employee
WHERE dept_id IS NOT NULL;
```

# NOT EXISTS
- Retuns rows for which the subquery returns no rows.

```sql
-- Departments Without Employees
SELECT *
FROM Department d
WHERE NOT EXIST (
    SELECT 1
    FROM Employee
    WHERE d.dept_id = e.dept_id
);
```

# Difference Between EXISTS and IN
| EXISTS                                  | IN                                      |
| --------------------------------------- | --------------------------------------- |
| Checks whether rows exist               | Checks whether a value exists in a list |
| Usually used with correlated subqueries | Usually used with simple subqueries     |
| Stops searching after the first match   | May evaluate the full result set        |
| Often better for large datasets         | Often fine for small datasets           |

```sql
-- Find employees earning more than the average salary of their department
SELECT *
FROM Employee e1
WHERE EXISTS (
    SELECT 1
    FROM Employee e2
    WHERE e1.dept_id = e2.dept_id
    GROUP BY dept_id
    HAVING e1.salary > AVG(e2.salary)
);

-- Above query is same as this below one
SELECT e1.*
FROM Employee e1
WHERE SALARY > (
    SELECT AVG(e2.salary)
    FROM Employee e2
    WHERE e1.dept_id = e2.dept_id
);
```

# IN, ANY, ALL
- `IN` : In checks whether value exists in a list of values.
- `ANY` : Compares with every value until one match found (OR)
- `ALL` : The condition must be true for every value (AND)

```SQL
-- Find employees earning more than at least one employee in Department 1
SELECT *
FROM Employee
WHERE salary > ANY (
    SELECT salary
    FROM Employee
    WHERE dept_id = 1
)

-- Find employees earning more than every employee in Department 1
SELECT *
FROM Employee
WHERE salary > ALL (
    SELECT salary
    FROM Employee
    WHERE dept_id = 1
)
```

## Relation with MIN() & MAX()

- MIN() = ANY
- MAX() = ALL

```sql
salary > ANY (...)
-- is equal to
salary > MIN(...)
-- Because being greater than at least 1 value is equal to being greater than the minimum.

salary < ANY(...)
-- is equal to
salary < MAX(...)
```

```sql
salary > ALL (...)
-- is equal to
salary > MAX(...)
-- Because being greater than from all the values is equal to being greater than the maximum.

salary < ALL (...)
-- is equal to 
salary < MIN(...)
```

```sql
SELECT *
FROM Employee
WHERE salary > ANY (
    SELECT salary
    FROM Employee
    WHERE dept_id = 1
);

-- is equal to

SELECT *
FROM Employee
WHERE salary > (
    SELECT MAX(salary)
    FROM Employee
    WHERE dept_id = 1
);
```

# UNION, UNION ALL, INTERSECT, MINUS
- To apply it, both queries must be with
    - Same number of columns
    - Same order of columns
    - COmpitable data types

- **UNION** : Combines both results and removes duplicates.
- **UNION ALL** : Combines results without removing duplicates.
- UNION is slower than UNION ALL, because UNION removed the duplicate, so it takes time to process.
- **INTERSECT** : Returns only common rows (Compare entire rows from two result sets)
- **MINUS** : Returns rows from the first query that are not in the second query.

# DDL (Data defination language)
- Used to create/modify the database structure:
    - Tables
    - Views
    - Indexes
    - Sequences
    - Synonyms
    - Materialized Views
    - Procedures
    - Functions
    - Packages
    - Triggers

| Command    | Use Case                        | Example                                        |
| ---------- | ------------------------------- | ---------------------------------------------- |
| `CREATE`   | Create a new object             | `CREATE TABLE Employee (...);`                 |
| `ALTER`    | Modify an existing object       | `ALTER TABLE Employee ADD phone VARCHAR2(15);` |
| `DROP`     | Delete an object permanently    | `DROP TABLE Employee;`                         |
| `TRUNCATE` | Delete all rows, keep the table | `TRUNCATE TABLE Employee;`                     |
| `RENAME`   | Rename an object                | `RENAME Employee TO Employees;`                |

## Examples:
```sql
-- Create the table
CREATE TABLE Employee(
    emp_id NUMBER PRIMARY KEY,
    emp_name VARCHAR(50),
    salary NUMBER
) 

-- Adds, Modify or Remove columns
--- Adds
ALTER TABLE Employee
ADD email VARCHAR2(100);

--- Modify
ALTER TABLE Employee
MODIFY salary NUMBER(10, 2);

--- Remove
ALTER TABLE Employee
DROP COLUMN email;

-- Delete the table
DROP TABLE Employee;

-- Delete all rows, but keeps the table schema
TRUNCATE TABLE Employee;

-- Rename table name
RENAME TABLE Employee TO Employees;
```

# DML ( Data Manipulation Language)
- Used to work with data inside the tables.

| Command  | Use Case                          | Example                                |
| -------- | --------------------------------- | -------------------------------------- |
| `INSERT` | Add new rows                      | `INSERT INTO Employee VALUES (...);`   |
| `UPDATE` | Modify existing rows              | `UPDATE Employee SET salary=50000;`    |
| `DELETE` | Delete selected rows              | `DELETE FROM Employee WHERE emp_id=1;` |
| `MERGE`  | Insert or update in one statement | Synchronize data                       |