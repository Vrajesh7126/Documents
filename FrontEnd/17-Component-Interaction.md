# @ViewChild

We can Transfer a data using `@Input` and `@Output` decorator between Parent & Child, but can not call a function using it from Parent to Child.

For that we can use `@ViewChild` decorator to get a reference to the child component and call its methods directly from the parent component.

**1. Access Child Method from Parent**

Parent HTML

```html
<app-child></app-child>

<button (click)="reset()">Reset</button>
```

Parent Component (TypeScript)

```ts
@ViewChild(ChildComponent) 
child!: ChildComponent;

reset() {
  this.child.someMethod();
}
```

Child Component

```ts
export class ChildComponent {
  someMethod() {
    console.log('Method called from parent component');
  }
}
```

**2. Access Child HTML Element from Parent**

Parent Component

```ts
// Angular returns an ElementRef pointing to the DOM element
@ViewChild('myDiv') 
input!: ElementRef;

ngAfterViewInit() {
  this.input.nativeElement.focus();
}
```

Child HTML

```html
<!-- This is a Template Reference Variable -->
<input #myDiv>
```

# @ViewChildren

It gives all matching children.

Parent HTML

```html
<app-child></app-child>
<app-child></app-child>
```

Parent Component (TypeScript)

```ts
@ViewChildren(ChildComponent) 
children!: QueryList<ChildComponent>;

ngAfterViewInit() {
  this.children.forEach(child => child.someMethod());
}
```

`QueryList` contains all the child components that match the selector passes to `@ViewChildren`.

# @ContentChild

Accesses content projected from the parent using `<ng-content>`.

Parent HTML

```html
<app-card>
  <h2 #title>Employee</h2>
</app-card>
```

Child Component (HTML)

```html
<div class="card">
  <ng-content></ng-content>
</div>
```

Parent Component (TypeScript)

```ts
@ContentChild('title') 
title!: ElementRef;
```

Angular finds:

```ts
<h2 #title>Employee</h2>
```

even through it was written by the parent, because it was projected into `<ng-content>`.

**Difference:** If my component create this element? Use `@ViewChild`. If the element is projected from the parent, use `@ContentChild`.

# TemplateRef

A `TemplateRef` is Angular's representation of an `<ng-template>`. 

`<ng-template>` does not render immediately.It stores a reusable block of HTML that can be rendered later or multiple times.

Component HTML

```html
<ng-template #myTemplate>
  <p>This is a reusable block of HTML.</p>
</ng-template>
```

Component TypeScript

```ts
@ViewChild('myTemplate') 
template!: TemplateRef<any>;
```

Now `template` refer to that stored HTML.

Angular internally use it at `*ngIf`, `*ngFor` & `*ngSwitch`.

## ng-container (Grouping Elements Without Adding Extra DOM Nodes)

`<ng-container>` is used to group elements without creating an actual HTML element.

```html
<div *ngIf="isLoggedIn">
  <p>Welcome</p>
  <button>Logout</button>
</div>
```

```html
<div *ngIf="isLoggedIn">
  <p>Welcome</p>
  <button>Logout</button>
</div>
```

This works, but Angular create an extra `<div>` element in the DOM.

It can affect layout.

```html
<ng-container *ngIf="isLoggedIn">
  <p>Welcome</p>
  <button>Logout</button>
</ng-container>
```

Result in the browser

```html
<p>Welcome</p>
<button>Logout</button>
```

Using `<ng-container>` instead of `<div>` will avoid adding this extra element.

There is no `<ng-container>` element in the final HTML, so no effect on the layout.

**Structural Directives Limitation :**

Angular allows only one structural directive per element.

```html
<li *ngFor="let employee of employees"
    *ngIf="employee.active">

    {{ employee.name }}

</li>
```

**Solution:** Use a wrapper `<ng-container>` to combine multiple structural directives.

```html
<ng-container *ngIf="employee.active">
  <li *ngFor="let employee of employees">
    {{ employee.name }}
  </li>
</ng-container>
```

Use `<div>` when you need
  - CSS classes
  - Styling
  - Borders
  - Margin
  - Padding
  - Flexbox/Grid container

`<div>` is a part of the UI.

Use `<ng-container>` when you need
  - Grouping only
  - Structural directives
  - No extra DOM node

# ngTemplateOutlet (Rendering a TemplateRef)

We can display a stored `<ng-template>` to a `TemplateRef` using the `ngTemplateOutlet` directive.

```html
<ng-template #employeeTemplate>

    <h2>Employee Details</h2>

</ng-template>
```

```html
<ng-container *ngTemplateOutlet="employeeTemplate"></ng-container>
```

Can display same template in multiple places, it reduce code duplication.

## Differences

| Feature            | Purpose                                              |
| ------------------ | ---------------------------------------------------- |
| `<ng-content>`     | Display HTML passed by the parent.                   |
| `<ng-template>`    | Store HTML for later use.                            |
| `ngTemplateOutlet` | Render a stored `TemplateRef`.                       |
| `<ng-container>`   | Group/render content without creating a DOM element. |

# ViewContainerRef (Creating Components Dynamically)

It is a placeholder where Angular can insert components or templates dynamically.

HTML

```html
<ng-container #container></ng-container>
```

Component

```ts
@ViewChild('container', { read: ViewContainerRef })
container!: ViewContainerRef;
```

Now `container` represents that empty location.

**Create a Component dynamically :**

```ts
this.container.createComponent(EmployeeComponent);
```

Real World use case: Based on the used selection, we can create a component dynamically.

