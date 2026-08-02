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
// To implement OnDestroy is not compulsury
export class myComponent implments OnDestroy {

    ngOnDestroy(): void {
        // logic here
    }
}
```
