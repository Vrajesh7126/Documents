**Resolver :** Router mechanism that fetches/prepares required route data BEFORE the component is activated.

## Without Resolver

```
URL /employees/101
       ↓
Angular creates EmployeeComponent  ← Component is ACTIVE
       ↓
Component calls API
       ↓
Data arrives
       ↓
UI displays employee
```

## With Resolver

```
URL /employees/101
       ↓
Angular asks Resolver:
"Give me employee 101"
       ↓
Resolver calls API
       ↓
WAIT ⏳
       ↓
Employee data received
       ↓
NOW Angular creates/activates EmployeeComponent
       ↓
Component already has employee data
```

## Example:

Configure the Route

```js
{
  path: 'employees/:id',
  component: EmployeeComponent,
  resolve: {
    employee: EmployeeResolver
  }
}
```

Create a Resolver

```ts
export const EmployeeResolver: ResolveFn<Employee> = (route) => {

    const service = inject(EmployeeService);

    // Before opening EmployeeComponent, get the employee
    const id = Number(route.paramMap.get('id'));

    return service.getEmployee(id);
};
```

```ts
@Injectable({ providedIn: 'root' })
export class EmployeeService {

    constructor(private http: HttpClient) {}

    // The service calls the API
    getEmployee(id: number) {
    return this.http.get<Employee>(`/api/employees/${id}`);
    }
}
```


The key `employee: EmployeeResolver` means Before activating the `EmployeeComponent`, Angular will use the `EmployeeResolver`.