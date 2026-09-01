## Content Projection

Angular provides `<ng-content>`.

It means insert whatever parent component passes here.

Example

Parent Component

```html
<app-card>
    <p>This content is projected from the parent component.</p>
</app-card>
```

Card Component

```html
<div class="card">
  <ng-content></ng-content>
</div>
```

Result

```html
<div class="card">
  <p>This content is projected from the parent component.</p>
</div>
```

Flow

```text
Parent Component

↓

<app-card>

↓

<ng-content>

↓

Card Component
```

**Real World use case :**

Instead of creating `EmployeeCard`, `ProductCard`, `OrderCard`, etc., you can create a generic `Card` component and use content projection to insert the specific content for each use case.

## Multiple Content Projection (select)

With one `<ng-content>`, Everything from the parent was inserted at that one location.

What if your component has multiple sections ?

**Solution:** Use `<ng-content select="...">` to project content into specific sections of your component.

Parent Component

```html
<app-card>
  <h2 header>Card Header</h2>
  <p body>This is the body content of the card.</p>
  <p footer>Card Footer</p>
</app-card>
```

Child Component

```html
<div class="card">
  <ng-content select="[header]"></ng-content>
  <ng-content select="[body]"></ng-content>
  <ng-content select="[footer]"></ng-content>
</div>
```

Result

```html
<div class="card">
  <h2 header>Card Header</h2>
  <p body>This is the body content of the card.</p>
  <p footer>Card Footer</p>
</div>
```

**Using Select you can match**

**1. Attribute**

```html
<!-- Parent -->
<ng-content select="[header]"></ng-content>
```
Matches
```html
<!-- Child -->
<h2 header>Card Header</h2>
```

**2. CSS Class**

```html
<!-- Child -->
<ng-content select=".header"></ng-content>
```
Matches
```html
<!-- Parent -->
<h2 class="header">Card Header</h2>
```

**3. HTML Tag**

```html
<!-- Child -->
<ng-content select="header"></ng-content>
```
Matches
```html
<!-- Parent -->
<header>Card Header</header>
```
