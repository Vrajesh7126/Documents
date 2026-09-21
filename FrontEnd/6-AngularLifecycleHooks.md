# Angular Lifecycle Hooks

## Complete Lifecycle Order

```text
Constructor
      │
      ▼
Angular sets @Input values by Parent component
      │
      ▼
ngOnChanges()
      │
      ▼
ngOnInit()
      │
      ▼
Angular initialize the view (HTML)
      │
      ▼
ngAfterViewInit()
      │
      ▼
ngOnDestroy()
```

## Constructor
- Typescript's part (not an angular's part)
- Performs a dependency injection.
- Initialize an onject.
- Create the component.

## ngOnInit()
- Calls after creating and initializing the component.
- Load initial data.
- Call backend API.

## ngOnChange()
- Call when @Input data was changed by parent to the child.
- Used in child component.
- provides SimpleChanges to see the previous and current values.

```java
export class childComponent{
    @Input
    childName! : string;

    ngOnChanges(changes: SimpleChanges){
        console.log(changes['childName'].previousValue);

        console.log(changes['childName'].currentValue);

        console.log(changes['childName'].firstChange);
    }
}
```

## ngOnDestroy()
- Call just before removing component.
- To unsubscribe the observable.
- In modern, use async pipe (subscribe when component is created and unsubscrie when destroy)

```java
// To implement OnDestroy is not compulsory
export class myComponent implements OnDestroy {

    ngOnDestroy(): void {
        // logic here
    }
}
```

## ngDoCheck()
- Called during every **change detection run**.
- Used to perform custom change detection logic because Angular's default change detection covers only when reference was changed.

## ngAfterContentInit()
- Called once after content projected through `<ng-content>` is initialized.

Parent:
```html
<app-card>
    <p>Hello Vrajesh</p>
</app-card>
```

Card:

```html
<div class="card">
    <ng-content></ng-content>
</div>
```

After Angular puts:
```html
<p>Hello Vrajesh</p>
</div>
```

into `<ng-content>`, `ngAfterContentInit()` will be called.

## ngAfterContentChecked()
- Called after Angular checks the projected content.

## ngAfterViewInit()
- Called once after the component's own view and child views are initialized.

HTML:
```html
<h1>Hello Vrajesh</h1>
<app-child></app-child>
```

After Angular creates this view, `ngAfterViewInit()` will be called.

- Can access child component using `@ViewChild` decorator.
- If you try to access the child using `@ViewChild` before it exists, you can get undefined/runtime problems.

## ngAfterViewChecked()
- Called after Angular checks the component's view and child views.
