# Lazy Loading
- Instead of loading all the component initially, load when needed and visited by the user.
- Once it loaded lazily then it stores into the browser caches and keeps the loaded code, and If user request after it then it is loaded from this cache, instead of doing the netwrok call again.

- instead of :
```ts
{
    path: 'employees',
    component: EmployeeComponent
}
```

```ts
// Old Approach
{
    path: 'employees',
    loadChildren: () => 
        import('./employee/employee.module')
            .then(m => m.EmployeeModule)
}

// New Approach
{
    path: 'employees',
    loadComponent: () =>
        import('./employee/employee.component')
            .then(m => m.EmployeeComponent)
}
```

# Route Level Lazy Loading
- Angular loads the components when user visits its route.
- Instead of putting all these routes inside `app.routes.ts`, Angular lets you move them into their own file, where `app.routes.ts` is the main route file.

```ts
// employee.routes.ts
export const employeeRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./employee-list.component')
        .then(c => c.EmployeeListComponent)
  },
  {
    path: 'add',
    loadComponent: () =>
      import('./employee-add.component')
        .then(c => c.EmployeeAddComponent)
  },
  {
    path: 'edit/:id',
    loadComponent: () =>
      import('./employee-edit.component')
        .then(c => c.EmployeeEditComponent)
  },
  {
    path: 'details/:id',
    loadComponent: () =>
      import('./employee-details.component')
        .then(c => c.EmployeeDetailsComponent)
  }
];
```

```ts
// app.routes.ts
{
    path: 'employees',
    loadChildren: () => 
        import('./employee/employee.routes')
            .then(r => r.employeeRoutes)
}
```

- Each feature manage it's own routes,
```
app.routes.ts
│
├── employee.routes.ts
├── product.routes.ts
├── order.routes.ts
├── admin.routes.ts
└── report.routes.ts
```

- **loadComponent** : Means don't load it now, load when user navigates to `/employees`.

# OG-Angular Module
- Imagine a feature, Employee Feature

```
employee
│
├── employee.module.ts
├── employee-routing.module.ts
├── employee.component.ts
├── employee.component.html
├── employee.component.css
└── employee.service.ts
```

## employee.component.ts
- This is the main file of your component.

```ts
@component({
    selector: 'app-employee',
    templateUrl: './employee.component.html',
    styleUrls: ['./employee.component.css']
})
export class EmployeeComponent {}
```

## employee.module.ts
- This file indicates, This Employee feature contains these components and needs these modules.

```ts
@NgModule({
    declarations: [         // declaration means this are the Components, directives and Pipes that belongs to this module
        EmployeeComponent
    ],
    imports: [              // imports import the feature
        CommonModule,
        EmployeeRoutingModule
    ]
})
```

## employee-routing.module.ts
- Suppose user opens `/employees`, which component should I display, this file answers that.

```ts
const routes = [
    {
        path: '',
        component: EmployeeComponent
    }
];
```

## employee.service.ts
- Calls the Backend APIs.

### **Note**
- Since **Angular 17+**, new projects use Standlone Components by default.
- In that there is no `employee.module.ts`.
- Each component declare it's own component.
- Lazy loading directly loads the component instead of `EmployeeModule`.