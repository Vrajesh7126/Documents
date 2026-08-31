- [Change Detection](#change-detection)
    - [Default change detection strategy](#default-change-detection-strategy)
    - [OnPush](#onpush)
    - [Manual Change Detection (ChangeDetectorRef)](#manual-change-detection-changedetectorref)

# Change Detection

## Default change detection strategy
- Change detection is the process in angular checks weather component data has changed and updates the UI accordingly.

```java
// When someone change
name = "Vrajesh"
```

```java
// HTML will updates it automatically
{{ name }}
```

- This has been done by the Change Detection.
- Angular starts from the root component and check component tree even though only a single component changed.

## OnPush
- With OnPush, only component that should be check is checked by the Angular instead of all the component checking.

```ts
@Component({
    selector: 'app-employee',
    templateUrl: './employee.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EmployeeComponent {}
```

- `OnPush`  component is checked when:
    - `@Input` **reference** was changed.
    - Event occurs inside the component (button click).
    - **Observable** emit the value.
    - Manually trigger `ChangeDetectorRef`.

```java
@Component({
    changeDetection: ChangeDetectionStrategy.OnPush
})
```

## Manual Change Detection (ChangeDetectorRef)
- Use mainly while working with `OnPush`

```java
constructor(private cdr: ChangeDetectorRef){}

updateName(){
    // This is not handled by OnPush, so we can use ChangeDetectorRef for manual change detection
    this.name = "Vrajesh";

    this.cdr.detectChanges();   // Updates the component immedietly.
    
    this.cdr.markForCheck();    // Mark this component as a next change detection cycle.
}
```

- `detectChanges()` is prefrerred over a reference change of `@Input` variable, because change the reference everytime is costly.

- If any third party library change the data, then we can use it.

```java
thirdParthLibrary.onData(data => {
    this.myData = data;

    this.cdr.detectChanges();
})
```

- Modern Angular uses async pipe and signal to update the UI when data was changed.