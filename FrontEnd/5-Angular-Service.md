# Service
- Command to create a Service : `ng generate service <service-name>` or `ng g s <service-name>`
- Can call the backend API or perform a can execute a business logic.
- Example :

```ts
@Injectable({
    providedIn: 'root'
})
export class EmployeeService {
    getEmployee(){
        // You can implement this body something like
        return this.http.get("/employees");
    }
}
```

# Injectable
- `@Injectable` means this class can be participate in DI, it doesn't create an object, it only indicate this class is available in DI system.
- `root` : Create a singleton instance of this class with `providedIn:'root'`
- Angular class which use the `@Injectable({ providedIn:'root' })` :
    - **Service**
    - **Guard:** Used by Router internally (Will see in future, once it will be cover, remove from here)
    - **Resolver:** Used by Router internally (Will see in future, once it will be cover, remove from here)
    - **Interceptor:** Used by HTTP System internally (Will see in future, once it will be cover, remove from here)

# Dependency Injection (DI)
- Instead of creating an object yourself, Angular creates it and gives it to you.
- Example :

```ts
export class EmployeeComponent {
    constructor(
        private employeeService: EmployeeService
    ) {}
}
```

- Use case:
    - **Service**
    - **HttpClient** (Will see in future, once it will be cover, remove from here)
    - **Router** (Will see in future, once it will be cover, remove from here)
    - **ActivatedRoute** (Will see in future, once it will be cover, remove from here)
    - **FormBuilder** (Will see in future, once it will be cover, remove from here)
    - **CustomServices** (Will see in future, once it will be cover, remove from here)

## inject()

```ts
@Component({...})
export class EmployeeComponent{
    private employeeService = inject(EmployeeService);
}
```

It behaves dependency as a class field instead of constructor. It is an alternative of a constructor injection.

Modern approach Angular 14+.

**Real world use case :**

There is no class or constructor at **Funtional Interceptor** or **Functional Guard** or **Functional Resolver** so we can use `inject()`.

```ts
export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);

    // use authService;

    return next(req);
}
```

