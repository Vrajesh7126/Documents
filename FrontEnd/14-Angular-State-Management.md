**Angular state management** = It's a process of storing and sharing application data so that components stay synchronized without unnecessary API calls or duplicates state.

**State** = Data your application is currently holding.

## 2 Types of state:
    
**Local State:** Used by only one component.

**Global State:** Used by multiple component.

## How can we manage state ?

### 1. Signals (Simple Application)

For small Application.

Use when data belongs to one component only.

### 2. Shared Service + Signals

Medium Application.

Use when multiple components need the same data.

When signal updates stored in service class using `signal.set("Value")`, all component that was used this signal updates automatically.

```text
CartService

↓

cartCount Signal

↓

Header

Product

Checkout
```

```ts
@Injectable({
    providedIn: 'root'
})
export class UserService {
    user = signal<User | null>(null);
}
```

Whenever user will be changed, all the components that uses this signal variable will be reflect automatically.

This is called Single source of Truth.

**@Injectable({
    providedIn: 'root'
})**
creates a singleton UserService class for the whole application.

### 3. NgRx

Used for Large Enterprise.

Hundreds of component may read and update shared state.