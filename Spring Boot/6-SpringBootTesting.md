# Spring Boot Testing Configurations

## @SpringBootTest

Loads the full Spring ApplicationContext.

Use it for **integration testing**.

```text
@SpringBootTest
      ↓
Loads Spring Boot
      ↓
Creates Controller
Creates Service
Creates Repository
Creates Security
Creates Configurations
      ↓
You can test their behavior
```

in `JUnit`, we can test

    - Expected behavior using `assertEquals`
    - Wrong input using `assertEquals`
    - Exception handling using `assertThrows`
    - Database interaction
    - Full flow (Controller → Service → Repository)

## @WebMvcTest

Controller focused test.

> **`@SpringBootTest` → loads the whole application.**

> **`@WebMvcTest` → loads only the MVC/Web layer.**

Example :

```java
@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }
}
```

Test

```java
@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    // MockMvc is used to perform HTTP requests and assert responses in the test
    @Autowired
    private MockMvc mockMvc;

    // Mock the EmployeeService bean to be used in the test
    @MockBean
    private EmployeeService employeeService;

    @Test
    public void testGetEmployeeById() throws Exception {

        Employee employee = new Employee(1L, "John Doe");

        // Mock the behavior of the EmployeeService to return the employee when requested by ID
        when(employeeService.getEmployeeById(1L)).thenReturn(employee);

        // Perform a GET request to the /employees/1 endpoint and verify the response
        mockMvc.perform(get("/employees/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("John Doe"));
    }
}
```

Flow :

```text
MockMvc
   ↓
/employees/1
   ↓
Controller ✅
   ↓
Mock EmployeeService
   ↓
JSON response
```

### @MockBean

Create a Mockito mock and register it as a Spring bean.

```text
Create a Mockito mock of EmployeeService
             ↓
Put it into ApplicationContext
             ↓
Inject it into EmployeeController
```

## @DataJpaTest

Test Repository layer.


```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByDepartment(String department);
}
```

Test :

```java
@DataJpaTest
class EmployeeRepositoryTest {
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void shouldFindEmployeeBydepartment() {
        Employee employee = new Employee(1L, "John Doe", "IT");

        employeeRepository.save(employee);

        List<Employee> employees = employeeRepository.findByDepartment("IT");

        assertEquals(1, employees.size());
    }
}
```

`@DataJpaTest` tests are transactional by default and the transaction is typically rolled back after each test.

