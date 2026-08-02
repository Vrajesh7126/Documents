# Parent to Child

```java
// Parent Component
export class ParentComponent{
    parentName = "Vrajesh";
}
```

```java
// Parent HTML
<child-component>
    [childName] = "parentName"
</child-component>
```

```java
// Child component
export class childComponent{
    @Input()
    childName! : string;
}
```