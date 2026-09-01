# Component
- Reusable piece of UI.
- Command : `ng generate component <component-name>` or `ng g c <component-name>`
- Structure :
    - HTML
    - CSS
    - Typescript file

    ```ts
    @Component({
        selector: 'app-employee',   // Use this as a custom HTML tag
        templateUrl: './employee.component.html',   // Template
        styleUrl: `./employee.component.css`
    })
    export class EmployeeComponent {

    }
    ```

# Template
- HTML view
- Types :
    - External Component
    ```ts
    templateUrl: './employee.component.html'
    ```

    - Inline


    ```ts
    templateUrl: '<h1>Employee</h1>'
    ```

- You can create dynamic template using the `Interpolation ({{ }})` which display the data from `Component` to `HTML`.
- Can use variables ({{ name }}), expression ({{ 10 + 20 }}), object properties ({{ user.name }}), method ({{ getName() }}).

# Peoperty Binding
- Used to set HTML element's property from the component.
- Property like `Image souce(src)`, `Hyperlink(href)`, `Input value(value)`, `Button disabled(disabled)`.
- `TS` → `HTML`
- Syntax : `[property] = "expression"`
- Example :
    ```ts
    // Component
    imageUrl = "employee.png"

    // Template
    <img [src]="imageUrl">
    // Internally it sets to
    <img [src]="employee.png">
    ```

# Event Binding
- Call component method when a user performs an action.
- `HTML` → `TS`
- Syntax : `(event) = "method"`
- Example :
    ```html
    <button (click)="save()">
        Save
    </button>

    // or

    <button (dblclick)="save()">
    // or
    <button (keyup)="search()">
    // or
    <button (mouseenter)="search()">
    // or
    <button (mouseleave)="search()">
    ```

# Two way binding
- Keeps `Component` and `HTML Input` synchronized.
- Property Binding + Event Binding.
- Mainly used in forms.
- Import `FormsModule`, this enable `ngModel`.
- Example :
    ```ts
    // Component
    name = "";

    // Template
    <input [(ngModel)]="name">

    <h2>{{ name }}</h2>
    ```

# Directive
- Change Behaviour or Appearance of the HTML elements (Make HTML dynamic).

## Component Directive
- Every Component is a directive, angular is replace is replace it with the component code.

## Structural Directive
- ***ngIf** 
    - Can be used with `else` and `ng-template`.
- ***ngFor** 
    - Modern angular has `@for`.
    - Suppose array has 2 elements and the array was changed, Angular automatically updates the UI by detecting the array changes.
- ***ngSwitch**
    - Modern angular supports `@switch`, `@case` & `@default`.

## Attribute Directive
- **ngClass** : Adds a CSS class dynamically.
- **ngStyle** : Applies CSS style dynamically.

## @Directive

It is an Angular Decorator used to create a custom behaviour for HTML elements.

Example: Change its background when clicked.

```ts
import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
  selector: '[appHighlight]'
})
export class HighlightDirective {

  constructor(private element: ElementRef) {}

  @HostListener('click')
  highlight() {
    this.element.nativeElement.style.backgroundColor = 'yellow';
  }
}
```

```html
<p appHighlight>
  Click me!
</p>
```

