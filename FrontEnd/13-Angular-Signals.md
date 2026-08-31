Before Signal, when someone changed the data, Angular checked all components, even with `OnPush`, Angular still need to check that Components.

Signal make this much easier.

Instead of Angular asks, did anything changed, Signal tells Angular, I changed, update only places that using me.

As soon as signal varable updates the value, it changes the UI.

```ts
import { signal } from `@angular/core`;

name = signal("John");
```

```ts
// Reading a signal
name();

// Set a value: Set completly new value
name.set("David");

// Update the value : Modify from an existing one
name.update(name + " Vaghasiya");

// Use a signal in HTML
<h1>{{ name() }}</h1>
```

Use update() when the new value depends on the current value, such as incrementing a counter or updating part of an object.

## computed()

A computed is a signal whose value is automatically calculated from Other signals.

```ts
// signals
price = signal(1000);
quantity = signal(2);

total = computed(() => price() * quantity());
```

When price or quantity was changed, total automatically update to it's latest computed value.

## effect()

Performs an action when signal changes.

```ts
name = signal("Vrajesh");

constructor() {
    effect(() => {  // When name will be set, effect runs
        console.log(name());
    });
}
```

If a component contains the multiple of the signals, then how can component know which signal has been changed and which effect to be run ?

> Option 1: Create a seperate effect

```ts
// When name was changed
effect(() => {
    conole.log("Name changed: " + name());
})

// When age was changed
effect(() => {
    conole.log("Age changed: " + age());
})
```

## Signal + Observable

```ts
employees = signal<Employee[]>([]);

this.employeeService
    .getEmployees()
    .subscribe(data => {
        this.employees.set(data);
    })
```

> Observable fetches

> Signal stores

> Angular Updates the UI

Signal can not replace RxJS, RxJS brings data into your app, Signals holds data and keep the UI synchronized.