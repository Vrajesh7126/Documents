# Parent-Child Communication

## Parent Component ➜ Child Component
- Use property binding because the parent component passes the value of its childName property to the child component's @Input() property.
- Example:

```ts
// Parent Component
parentName = "Vrajesh";

// Parent Template
// Left: Child's @Input
// Right : Parent's variable
<app-employee-details
    [childName] = "parentName">
</app-employee-details>
```

```ts
// Child Component
exmport class EmployeeDetailsComponent {
    @Input()
    childName!: string;
}

// Child Template
<h2> {{ childName }} </h2>
```

## Child Component → Parent Component
- Allows Child Component to send an event to the Parent.
- Example:

```ts
// Child Component
@Output()
name = new EventEmitter<string>();

selectEmployee() {
    this.name.emit("Vrajesh");
}

// Child Template
<button (click) = "selectEmployee"></button>
```

```ts
// Parent Template
<app-employee-list
    (name) = "showEmployee($event)">
</app-employee-list>

// Parent Component
showEmployee(empName: string){
    console.log(empName);
}
```