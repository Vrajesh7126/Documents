# Routing (Navigation Between Pages)
- Mapping between URL and Component.
- It will navigate to the component based on the URL.
- Uses `<router-outlet>` as the placeholder.
- Use `routerLink` for navigation.
- Define Routes :

```ts
export const routes = [
    {
        path: '',
        component: HomeComponent
    },
    {
        path: 'employee',
        component: EmployeeComponent
    }
]
```

- Navigation Example :

```html
// We can navigate like this (HTML)
<a routerLink="/employee">
    Employees
</a>
```

# Router
- Controls Application navigation.
- Router class provided by `DI`.
- Instead of `routerLink`, we can navigate uing `Router` class's `navigate` function. 
- Sometimes user don't want to navigate by clicking a button or something else, when operation like login success or delete data completed, user want to navigate to the other page/component, so for it, this is useful.

```ts
constructor(private router: Router){}

// Used to change the component programetically (Typescript)
goToProfile(){
    this.router.navigate(['/profile']);
}

viewEmployee(id: Number){
    // We can also pass a param and it will form like
    // employee/101
    this.route.navigate(['/employee', id]);
}
```

# Route Parameter
- Dynamic values in the URL.

- Define a Route parameter :
```ts
{
    path: 'employee/:id',   // :id is a dynamic value
    component: EmployeeDetailsComponent
}
```

- How to read a param :
- Angular provides `ActivatedRoute` by `DI`.
```ts
import {ActivatedRoute} from '@angular/router';

constructor(private route: ActivatedRoute) {}

ngOnInit(){
    // Fetch the parameter once
    const id = this.route.snapshot.paramMap.get('id'); // return string

    // Can use the id
    console.log(id);

    // Subscribe to it
    this.route.params.subscribe(params => {
        console.log(params['id']);  // While id will be change, get the call here
    })    
}
```

# Route Guard
- Decide weather user is allowed to open a route or not.
- Command to create a guard : `ng generate guard <guard-name>` or `ng g guard <guard-name>`
- If guard return `true`, **component** will be **created** and angular **navigate to that component**, If guard return `false`, component is not created and angular blocks navigation.
- If it return `false`, Constructor & ngOnInit() is not called.

```ts
// Functional guard
export const authGuard: canActive = () => {
    return true;
}
```

```ts
// routes
{
    path: 'dashboard',
    component: DashboardComponent,
    canActive: [authGuard]
}
```