# View
- Virtual table.
- View is saved SQL query that behaves like the table.
- **Use case** : Instead of giving the access of the entire table, create the view.

```sql
CREATE VIEW emp_view AS
SELECT emp_id, emp_name
FROM Employee;
```

- Now you can query like,

```sql
SELECT *
FROM emp_view;
```

## **Why to use view**

- **Security** : Hide sensitive column.
- **Simplicity** : Instead of writing a complex JOIN every time, Create a view once and use it.
- **Reusability** : Write a query once, and use it by multiples of the application.

## **Type of Views** :
- **Simple View** :
    - Built from one table
    - Updatable

- **Complex View** :
    - Built from multiple tables, JOINs, GROUP BY, Aggregate Functions.
    - Read Only


# INDEX
- Helps to find the data faster.
- Oracle Automatically create INDEX for the column with the constraint **Primary Key & UNIQUE**.
- Without index, it checks every row (O(n) time complexity) called Full Table Scan.
- Use B-Tree to search the data using an index (O(log n)).

Syntax :

```sql
CREATE INDEX <index_name>
ON <table_name>(<column_name>)
```

```sql
-- Create an index for employee name
CREATE INDEX edx_emp_name    
ON Employee(emp_name)
```

- Create an index on column that frequently used in **WHERE, JOIN, ORDER BY, GROUP BY**.

- **When I use INDEX for any column :**

| SQL Clause | Why index helps                                           |
| ---------- | --------------------------------------------------------- |
| `WHERE`    | Finds matching rows quickly.                              |
| `JOIN`     | Finds matching rows in another table quickly.             |
| `ORDER BY` | May avoid an expensive sort because the index is ordered. |
| `GROUP BY` | May reduce or eliminate sorting before grouping.          |

- **Where should we not use INDEX :**
    - Small tables
    - Column that change very frequently (`INSERT`, `UPDATE`, `DELETE`), because maintaining during the modification cost high to maintain it's indexing.
    - Column with very few unique values (like gender, status)

- **Types of Indexes:**
    - B-Tree Index
    - Unique Index
    - Composte Index (Combination on multiple columns)


# SEQUENCE
- Database object to generate the unique number automatically.
- **Use case :** Instead of manually inserting the Id into the table that can cause the duplicate IDs, oracle generates them automatically using sequence.

```sql
-- Create the sequence
CREATE SEQUENCE <sequence_name>
START WITH 1
INCREMENT BY 1
CACHE 20 | NOCACHE  -- Optional
MINVALUE 1          -- Optional
MAXVALUE 10         -- Optional
NOMAXVALUE          -- Default
CYCLE | NOCYCLE     -- Optional
;

-- Return the next value
SELECT <sequence_name>.NEXTVAL
FROM dual;

-- Returns the curr number
SELECT <sequence_name>.CURRVAL
FROM dual;
```

- Min value = 1, Max value = 10^28 - 1
- After reach to the max value, start with the Min value = 1
- `CYCLE` : After reaching to max, start with the min
- `NOCYCLE` : Stop when maximum reached (Used for Primary Keys)
- `CACHE` : Preallocate a group of sequence numbers in memory, so it doesn't have to access the sequence metadata on disk every time `NEXTVAL` is called.
- `NOCACHE` : Generate every value from the disk.
- Sequence can't rollback.

```sql
INSERT INTO employee
VALUES(emp_seq.NEXTVAL, 'John');

ROLLBACK;   -- Row is removed, but sequence can't be rollback
```

# Transaction
- Group of SQL statement treated as unit of work.
- All succeed or fail together.
- **Commit** : Permenently saves all changes made in the current transaction.
- **Rollback** : Undo changes in curent transaction.
- **Checkpoint** : 
    - Checkpoint inside the transaction.
    - Able to rollback upto that point. 

```sql
BEGIN
    -- Database Operation

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN    -- OTHERS handle any type of exception
    ROLLBACK;
END;
/
```

```sql
-- SavePoint (Commit Operation-1, Rollback Operation-2)
BEGIN
    -- Database Operation-1

    SAVEPOINT sp1;

    -- Database Operation-2

    ROLLBACK TO sp1;

    COMMIT;
END;
```

# ACID
- Every Oracle Transaction follows ACID.

- **Atomicity** : All or nothing.
- **Consistency** : A Transaction always keeps the DB in a valid state.
- **Isolation** : Multiple users can work at a same time without interfering with each other.
- **Durability** : After `COMMIT`, the data is permenently saved.

- **Locks** : When you update a row, oracle apply the lock at that row, so another user can't update the same row until `COMMIT` or `ROLLBACK`, this prevents data corruption.

- **READ COMMITED** : You can only read data that has been commited by other transaction.

```sql
-- USER A
UPDATE employee
SET salary = 60000
WHERE emp_id = 101;
-- No Commit yet

-- USER B
SELECT salary
FROM employee
WHERE emp_id = 101;
-- It will not see 60000

-- Now User A perform COMMIT
-- User B will see 60000
```

# DDL (Data defination language)
- DDL is used to create or change the structure of DB objects.
- CREATE, ALTER, DROP, TRUNCATE, RENAME.

| Command      | Deletes Rows           | Deletes Table Structure  | `WHERE` Allowed | Can Rollback?            |
| ------------ | ---------------------- | ------------------------ | --------------- | ------------------------ |
| **DELETE**   | ✅ Selected or all rows | ❌ No                     | ✅ Yes           | ✅ Yes (before `COMMIT`)  |
| **TRUNCATE** | ✅ All rows             | ❌ No                     | ❌ No            | ❌ No (implicit `COMMIT`) |
| **DROP**     | ✅ All rows             | ✅ Yes (table is removed) | ❌ No            | ❌ No                     |


- DDL statements perform an implicit COMMIT before and after they execute.

```sql
UPDATE employee
SET salary = 60000;

CREATE TABLE test(
   id NUMBER
);
```

# DML
- DML is used to modify the data in database tables (INSERT, UPDATE, DELETE).

```sql
BEGIN
    INSERT INTO employee
    VALUES (1, "Vrajesh");

    -- Like that we can perform UPDATE & DELETE also
END;
/
```

```sql
DECLARE
    _salary NUMBER;
BEGIN
    UPDATE employee
    SET salary := _salary   -- Can use variable with the query
    WHEN emp_id = 101;
END;
/
```

```sql
UPDATE
↓
COMMIT
↓
CREATE TABLE
```

# PL/SQL
- Oracle's programming language that extends SQL.
- SQL works with data, PL/SQL allows to write programming logic arong SQL.

# PL/SQL Block Structure
- Every PL/SQL program is written inside the block.
- BEGIN block must contains at least 1 executable statement, otherwise oracle gives an error.

- Syntax :

```sql
DECLARE
    -- Variable (Optional)
    -- Used to declare variable, constants, cursors, etc.
BEGIN
    -- Actual Program (Mandatory)
EXCEPTION
    -- Error handling (Optional)
END;    -- Mandatory
/
```

## Types of PL/SQL Block

### 1. Anonymous Block
- No name
- Written and executed once
- Can not be called again

- Syntax :

```sql
BEGIN
    DBMS_OUTPUT.PUT_LINE("Hello");
END;
/
```

### 2. Named Block
- Stored in DB and can be reused
- Example : `Procedure`, `Function`, `Trigger`, `Package`

# Variables & Constants
- Used to store the values in memory while PL/SQL block is executing.
- **Variable** : Can be changed
```sql
variable_name datatype;
```

- **Constants** : Assigned once, Can not be changed
```sql
constant_name CONSTANT datatype := value;
```

# Data Types
1. **NUMBER**
2. **VARCHAR2** : Store variable length text
3. **CHAR** : Stores fixed length text
4. **DATE**
5. **BOOLEAN** : Stores TRUE, FALSE, NULL (It's available inside the PL/SQL not as a column in Oracle Table)
6. **%TYPE** : Copy data type as same as column type
    ```sql
    DECLARE
        variable_name table_name.column_name%TYPE;
    ```
7. **%ROWTYPE** : Copy entire row structure of the table.
    ```sql
    DECLARE
        variable_name table_name%ROWTYPE;
    ```
    - Can use as variable_name.column_name

# Operators
- **Arithmetic** : +, -, *, /, **
- **Comparision** : =, != or <>, >, <, >=, <=
- **Logical** : AND, OR, NOT
- **Assignment** : :=
- **String Concatenation** : ||

# IF-ELSE
Syntax:
```sql
    IF Condition THEN
        -- If Condition is TRUE
    ELSIF Condition2 THEN
        -- If Consition2 is TRUE
    ELSE
        -- Code for Else
    END IF;
```

# CASE
Syntax:
```sql
CASE variable
    WHEN value1 THEN
        -- Code
    WHEN value2 THEN
        -- Code
    ELSE
        -- Default Code
END CASE;
```

# Loops
## 1. Basic Loop
- It Loop until you explicitly exit using EXIT.
- Use when you don't know in advance how many times the loop is going to run.

Syntax :
```sql
LOOP
    -- Code
    EXIT WHEN Condition;
END LOOP;
```

## 2. While Loop
- Checks the condition before every iteration.
```sql
WHILE Condition LOOP
    -- Code
END LOOP;
```

## 3. For Loop
- Use when initially know the number of iterations.
```sql
FOR variable IN start..end LOOP
    -- Code
END LOOP;

// Reverse Loop
FOR variable IN REVERSE start..end LOOP
    -- Code
END LOOP;
```

# Exit & Continue
1. **EXIT** : Exit the loop (Java's break)
```sql
LOOP
    -- Code
    EXIT WHEN condition;    -- Exit when condition is true
END LOOP;
```

2. **CONTINUE** : Skip the remaining code of the current iteration and start with the next iteration. (Java's Continue)
```sql
LOOP
    -- Code
    CONTINUE WHEN condition
    -- Code (Skip this when condition is true)
END LOOP
```

# SELECT INTO
- Used to fetch data from DB and store it into the variables.

Syntax :
```sql
DECLARE
    variable_name1 data_type;
    variable_name2 data_type;
BEGIN
    SELECT column_name1, column_name2
    INTO variable_name1, variable_name2  -- Used here
    FROM table_name
    WHERE condition;
END;
/
```

- Number of column should match with the number of variables.
- If no row found : Oracle Throws `NO_DATA_FOUND`
- If multiple row found : Oracle Throws `TOO_MANY_ROWS`

## SQL Attributes

| Attribute      | Meaning                                                             |
| -------------- | ------------------------------------------------------------------- |
| `SQL%ROWCOUNT` | Number of affected rows                                             |
| `SQL%FOUND`    | `TRUE` if at least one row was affected                             |
| `SQL%NOTFOUND` | `TRUE` if no rows were affected                                     |
| `SQL%ISOPEN`   | Always `FALSE` becuse Oracle automatically opens and close the implicit cursor.  (used mainly with explicit cursors) |

- DML Changes become permenant If `COMMIT;` & Rollback If `ROLLBACK;`

# CURSORS
- Used to fetch and process multiples rows returned by the SQL query.
- `SELECT INTO` is for one row.
- `CURSOR` is for multiples of rows.

## Types of Cursor

### 1. Implicit Cursor
- Created automatically by Oracle.
- When we execute `INSERT`, `UPDATE`, `DELETE`, `SELECT INTO` implicit cursor will be created.

### 1. Explicit Cursor
- When query returns a multiples of rows, you create your own cursor.

```sql
DECLARE
    CURSOR cursor_name IS
    SELECT column_name1
    FROM table_name;

    variable_name datatype;

BEGIN
    OPEN cursor_name    -- Execute the query and prepare the result

    LOOP
        FETCH cursor_name INTO variable_name;   -- Read one row into variable_name

        EXIT WHEN cursor_name%NOTFOUND; -- Check if cursor contains data or not

        DBS_OUTPUT.PUT_LINE(variable_name);
    END LOOP;

    CLOSE cursor_name;  -- Close the cursor (Free the resource)

END;
/
```

## Cursor Attributes
| Attribute   | Meaning                        |
| ----------- | ------------------------------ |
| `%FOUND`    | True If Row was fetched        |
| `%NOTFOUND` | No more rows                   |
| `%ROWCOUNT` | Number of rows fetched so far  |
| `%ISOPEN`   | Whether the cursor is open     |

## FOR CURSOR Loop
- Oracle Automatically do `OPEN` the cursor, `FETCH` the row, `CLOSE` the cursor.

```sql
BEGIN
    FOR cursor_name IN (
        SELECT column_name(s)
        FROM table_name
    ) LOOP

    -- Can use curson_name like cursor_name.column_name

    END LOOP;
END;
/
```

# RECORD
- Used to store multiple related values together instead of creating a seperate data types for all the columns.

```sql
DECLARE
    record_name table_name%ROWTYPE;

BEGIN
    SELECT *
    INTO record_name
    FROM table_name
    WHERE condition;

    -- Access field using record_name.column_name
    DBMS_OUTPUT.PUT_LINE(record_name.column_name1);
    DBMS_OUTPUT.PUT_LINE(record_name.column_name2);
END;
/
```

## User defined records
- Sometimes you don't need all columns, so we can create our own record.

```sql
DECLARE
    TYPE record_type IS RECORD (    -- Create a custom record
        variable_name1 datatype,
        variable_name2 datatype,
    );

    record_name record_type;
BEGIN
    SELECT column_name1, column_name2
    INTO record_name
    FROM table_name
    WHERE condition;
END;
/
```

## RECORD with CURSOR
- We can use Record with Cursor

```sql
DECLARE
    CURSOR c IS     -- Declare Cursor
        SELECT *
        FROM Employee;

    emp Employee%ROW_TYPE;  -- Declare Record with a matching table's structure
BEGIN
    LOOP
        FETCH c INTO emp    -- Fetch From cursor and store into the record

        EXIT WHEN c%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE(emp.name); -- Use record
    END LOOP;

END;
/
```

# Collections
- Store multiple values in a memory

## Types of Collection

### 1. Associative Array (Index by table)
- Java's HashMap.
- Store key-value pairs.
- Key could be `PLS_INTEGER` or `VARCHAR2`

```sql
DECLARE
    TYPE array_name IS TABLE OF data_type INDEX BY PLS_INTEGER | VARCHAR2;

    myArray array_name;

BEGIN
    myArray(key1) := value1
    myArray(key2) := value2

    DBMS_OUTPUT.PUT_LINE(myArray(key1));   -- Print value1
END;
/
```

### 2. Nested Table
- Java's ArrayList

```sql
DECLARE
    TYPE table_name IS TABLE OF datatype;

    myTable table_name := table_name(value1, value2, value3);
BEGIN
    myTable.EXTEND;         -- Extend the capacity of the nested table
    myTable(4) := value4;   -- And then can add value, otherwise it give an error

    DBMS_OUTPUT.PUT_LINE(myTable(1));    -- Print value1
END;
/
```

### 3. VARRAY (Variable size array)
- Java's fixed size array
```sql
DECLARE
    TYPE varray_name IS VARRAY(5) OF data_type;

    myVarray varray_name := varray_name(value1, value2, value3);
BEGIN
    myVarray.EXTEND;
    myVarray(4) := value4;
END;
/
```

## Collection Methods
1. COUNT
2. FIRST
3. LAST
4. NEXT (index)
4. DELETE (index)
5. EXIST (index)

- Use to process thousands of rows in memory.
- Use with `BULK COLLECT` & `FORALL`

---

# Stored Procedure
- Named PL/SQL block stored inside the Oracle DB and can be used whenever needed.
- Can contains SQL statements, Loops, condition, exception handling, etc.

```sql
CREATE OR REPLACE PROCEDURE procedure_name
AS
BEGIN
    -- PL/SQL Statements
END;
/
```

```sql
-- Call it
BEGIN
    procedure_name
END;
/
```

```sql
-- Procedure with the parameter
CREATE OR REPLACE PROCEDURE pprocedure_name (
    parameter IN table_name.col_name%TYPE;
)
AS
    var_name table_name.col_name%TYPE;  -- Used to store the data from the SELECT
BEGIN
    SELECT column_name(s)
    INTO var_name
    FROM table_name
    WHERE column_name = parameter;   -- Can use param like this

    -- Use var_name here
END;
/
```

- Benifits:
    - Reusability
    - Better Performance : Compiled and stored in the DB.
    - Security : User allows to execute procedure instead directly giving an access of the table
    - Easy maintanance : If business logic change, update the procedure (don't go to update the application code)

# FUNCTION
- Procedure with a return value.
- Technically Function is able to modify the data, but we should not do it, for it we should use PROCEDURE.
- Function's job is to compute and retrive the data.

```sql
CREATE OR REPLACE FUNCTION function_name
RETURN data_type    -- Tells oracle which type of value will be return
AS
BEGIN
    -- Perform an operation
    Return Value;
END;
/
```

- Use with **SQL Queries** and with **Expressions** because it returns the value.
- Example :

```sql
CREATE OR REPLACE FUNCTION getBonus(
    salary IN NUMBER;
)
AS
BEGIN
    RETURN salary * 0.1;
END;
/
```

```sql
-- Use in SELECT
SELECT emp_name, salary, getBonus(salary) AS bonus
FROM Employee;

-- Use in WHERE
SELECT *
FROM Employee
WHERE getBonus(salary) > 5000;

-- Use in an expression
SELECT emp_name, salary + getBonus(salary) AS total_pay
FROM Employee;

-- Use in ORDER BY
SELECT *
FROM Employee
ORDER BY getBonus(salary);
```

# PARAMETERS
- Used to pass the data between Caller & Procedure/Function.

1. **IN** : Caller → Procedure
2. **OUT** : Procedure → Caller
3. **IN OUT** : Both

# Package
- Container that groups related PL/SQL Objects together.
- It contains:
    - Procedure
    - Function
    - Variable
    - Constants
    - Cursors
    - Types

## Package has 2 parts

### 1. Package Specificaion (Header)
- It tells Oracle what is vailable.
- Like Java's Interface.

```sql
CREATE OR REPLACE PACKAGE package_name AS
    PROCEDURE procedure_name;
    
    FUNCTION function_name;
    RETURN NUMBER;
END package_name;
/
```

### 2. Package Body
- It contains the actual implementation.

```sql
CREATE OR REPLACE PACKAGE BODY package_name AS
    PROCEDURE procedure_name
    AS
    BEGIN
        -- Procedure code block
    END;

    FUNCTION function_name 
    RETURN NUMBER
    AS
    BEGIN
        -- Function code block
    END;

END package_name;
```

- **Specification** = The public interface (what outside code can use).
- **Body** = The implementation of the public members, plus any private procedures, functions, variables, cursors, constants, or types used internally.

- Calling package members :

```sql
package_name.object_name;
```

- Benifits of package:
    - Better organization
    - Reusability
    - Easy maintanance
    - Better performance

# Triggers
- PL/SQL block that executes automatically when specific event occurs.
- Procedure with automatic call by oracle.
- Trigger can use Procedure inside it.

```sql
CREATE OR REPLACE TRIGGER trigger_name
BEFORE | AFTER
INSERT | UPDATE | DELETE
OF column_name(s)  -- Optional (Use when want to trigger on modification of a specific column(s))
ON table_name
FOR EACH ROW    -- Optional (If want Row level trigger then add, else for statement level trigger remove it)
BEGIN
    -- PL/SQL code
END;
/
```

- Triggers can access OLD & NEW values of a row (Only use with `FOR EACH ROW`)

```sql
:OLD.column_name    -- returns old value
:NEW.column_name    -- returns new value
```

```sql
IF :NEW.salary > 100000 THEN
    ---
END IF;
```

## Types of Triggers
### 1. Statement-Level Triggers
- Fire once for an entire SQL statement.

### 2. Row-Level Triggers
- Fire once for each affected row.

- Use Case :
    - Logging
    - Auditing

# BULK COLLECT & FORALL
- Normally, PL/SQL process 1 row at a time, which is slower for large amount of data.
- `BULK COLLECT` & `FORALL` allows you to process many rows at once.

## 1. BULK COLLECT
- Fetch multiple rows into a collection in one operation.
- Read Multiple Rows (Use with `SELECT`)

```sql
DECLARE
    TYPE collection_name IS TABLE OF data_type;
    myCollection collection_name;
BEGIN
    SELECT column_name
    BULK COLLECT INTO myCollection
    FROM table_name;
END;
/
```

## 2. FORALL
- FORALL performs INSERT, UPDATE, DELETE for all elements in a collection in one operation (Use collection to perform DML).
- Write Multiple Rows (Use with `INSERT`, `UPDATE`, `DELETE`)

```sql
FORALL i IN 1..collection_name.COUNT
    UPDATE table_name
    SET column_name = collection_name(i).column_name1 + 1000
    WHERE id = collection_name(i).column_name2;
```

- Oracle sends all update efficiently.

# Dynamic Query
- Creating a SQL Query at runtime and then executing it.

```sql
DECLARE
    v_empId := 1;
    v_empName := 'Vrajesh';

    v_id NUMBER := 101;

    v_tableName VARCHAR2(20) = 'Employee';

    v_count NUMBER;
BEGIN
    -- INSERT Query
    EXECUTE IMMEDIATE
        'INSERT INTO Employee Values (:1, :2)'
    USING v_empId, v_empName;

    -- Update Query
    EXECUTE IMMEDIATE
        'UPDATE Employee SET name = :1 WHERE id = :2'
    USING v_empName, v_empId;

    -- Delete Query
    EXECUTE IMMEDIATE
        'DELETE FROM Employee WHERE id = :1';
    USING v_id;

    -- Concatenate Table name
    EXECUTE IMMEDIATE 'SELECT * From ' || v_tableName;

    -- Store the result
    EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM ' || v_tableName
    INTO v_count;
END;
/
```

- `EXECUTE IMMEDIATE` : Dynamic SQL is executed using it.
- `USING` : Bind variables dynamically.
- `String Concatenation` : Can build an SQL query dynamically.
- `INTO` : Capture Query result.

- Static SQL is Faster to execute, Use Dynamic SQL wherever needed.

# User-Defined Exceptions
- Create yourself to handle business rules.

```sql
DECLARE
    exception_name EXCEPTION;
BEGIN
    -- code

    IF condition THEN
        RAISE exception_name;   -- Throw an exception
    ENDIF;

    -- code
EXCEPTION
    WHEN exception_name THEN
        -- handle an exception

END;
/
```

# SAVE EXCEPTIONS
- Used with **FORALL**.
- While performing a Batch oprations, If something goes fail then instead of stopping there and don't proceed the remaining rows, that row should save the exception and continue the further proceed for the remaining rows.

```sql
FORALL i IN 1..ids.COUNT
    SAVE EXCEPTIONS

    UPDATE Employee
    SET salary = salary + 1000
    WHERE id = ids(i);
```

# Handling Errors
- After `SAVE EXCEPTION`, Oracle stores the error in : `SQL%BULK_EXCEPTIONS`.

```sql
EXCEPTION
WHEN OTHERS THEN
    FOR i IN 1..SQL%BULK_EXCEPTIONS.COUNT LOOP

        DBMS_OUTPUT.PUT_LINE(SQL%BULK_EXCEPTIONS(i).ERROR_INDEX);
        DBMS_OUTPUT.PUT_LINE(SQL%BULK_EXCEPTIONS(i).ERROR_CODE);
    
    END LOOP;
```

- **ERROR_INDEX** : Which element from the collection failed.
- **ERROR_CODE** : Oracle error number.

# Synonyms
- Alternate name (alias) for a database object.
- A synonym does not store the data, it only stored the reference to another object (Tables, Views, Sequences, Procedures, Functions, Packages).
- A single database can have multiple users, and each user has it's own schema like tables, views, procedures, sequences, etc.

- Benifits :
    - **Simpler queries**
    - **Hide Schema Names** : Application doesn't need to know whether the object belong to HR, ADMIN, Vrajesh.
    - **Easy Maintenance** : Suppose table moves from HR to ADMIN, need to change only synonym
    - 

- Can one schema use another schema's objects?
    - Yes, if the owner grants the permission.
    - For example, If HR can grant SELECT on EMPLOYEE to vrajesh user.

    ```sql
    GRANT SELECT ON employee TO vrajesh;
    ```

    - Now vrajesh user can do

    ```sql
    SELECT *
    FROM HR.Employee;

    -- or create synonym

    CREATE SYNONYM employee FOR HR.Employee;
    ```

    - Then vrajesh user can do,

    ```sql
    SELECT * From employee;
    ```

## Private Synonym
- Visible only to the user who creates it.

```sql
CREATE SYNONYM employee
FOR HR.Employee;
```

## Public Synonym
- Visible to all database users.
- Only visible to all other user, it doesn't mean they can access without the permission of the HR, for it user need to get the access from the HR.

```sql
CREATE PUBLIC SYNONYM employee
FOR HR.Employee;
```

# Materialized View
- Store the query result physically in the database.
- Copy of the query result.
- **Benifit** : Store the result once and use by all the users instead of run the query again and again which may takes time to be execute.

```sql
CREATE MATERIALIZED VIEW view_name
AS
SELECT column_name(s)
FROM table_name;
```

```sql
-- Read the stored data into materialized view directly.
SELECT *
FROM view_name
```

- If table was changed, view doesnt know automatically, for it, need to perform the Refresh.

## Types of Refresh

### Complete Refresh
- Delete all stored data and rebuilds it.

### Fast Refresh
- Update only changes rows.

### On-Demand Refresh
- Refreshed only when you explicitly request it.

```sql
EXEC DBMS_MVIEW.REFRESH('EMP_MV');
```

### On Commit Refresh
- Whenever you execute `COMMIT;`, Oracle automatically refresh the materialized view.