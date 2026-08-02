- [Form in Angular](#forms-in-angular)
    - [Template Driven Form](#template-driven-form)
    - [Form Validation](#form-validation)
    - [Reactive Forms](#reactive-forms)

# Forms in Angular

## Template Driven Form
- Can take a input from the HTML form using `ngModel` (2 way binding). 

```java
import { FormModule } from '@angular/forms';    // FormModule requires for the ngModel

TS File :
export class employeeComponent {
    employeeName = "";

    save(){
        console.log(employeeName);
    }
}
```

```HTML
<input
    type = "text"
    [(ngModel)] = "employeeName" // Component variable that stores the value
    name = "employeeName" // Identification inside the form control
>

<button (click) = "save()">
```

- User types "Vrajesh", employeeName variable updated to "Vrajesh", and vice-versa (because of 2-way binding)

## Form Validation

### Built in validation

- Use validator within the HTML component.

| Validator   | Purpose                         |
| ----------- | ------------------------------- |
| `required`  | Field cannot be empty           |
| `minlength` | Minimum number of characters    |
| `maxlength` | Maximum number of characters    |
| `pattern`   | Must match a regular expression |
| `email`     | Must be a valid email           |

```java
<input
    type = "text"
    name = "employeeName"
    [(ngModel)] = "employeeName"
    required        // This field is required
    minLength = 3   // Validation of min length of 3
>
```

### Accessing validation state

```java
<input
    name = "employeeName"
    [(ngModel)] = "employeeName"
    required
    #name = "ngModel"   // Angular creates a object called name
>

// Showing an error message using name object
<div *ngIf = "name.invalid && name.touched">
    Name is required
</div>

// Disable submit button if form is invalid
<button [disabled] = "employeeForm.invalid">
    Save
</button>
```

- Angular automatically keeps the track of every form control by creating an angular object.

| Property         | Meaning                         |
| ---------------- | ------------------------------- |
| `name.valid`     | Field is valid                  |
| `name.invalid`   | Field is invalid                |
| `name.touched`   | User clicked and left the field |
| `name.untouched` | User hasn't interacted yet      |
| `name.dirty`     | Value has changed               |
| `name.pristine`  | Value hasn't changed            |

- Initially Input is empty
    - valid = false
    - invalid = true
    - touched = false
    - untouched = true
    - dirty = false
    - pristine = true

- User clicks input and leaves empty
    - valid = false
    - invalid = true
    - touched = true
    - untouched = false
    - dirty  = false
    - pristine = true

- User enters "Vrajesh"
    - valid = true
    - invalid = false
    - touched = true
    - untouched = false
    - dirty = true
    - pristine = false

- Why touched & untouched? : If user opens the page and user hasn't even looked at the form yet.

```html
// Without touched, user will be display an error
<div *ngIf = "name.invalid">
    Name is required
</div>
```

```html
// With touched
<div *ngIf = "name.touched && name.invalid">
    Name is required
</div>
```

## Reactive Forms

### FormControl


### FormGroup


### FormBuilder


### Custom Validator




