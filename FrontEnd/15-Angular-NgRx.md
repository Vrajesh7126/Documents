**Problem**: Multiples of Components, Multiples of services, Thusands of state updates every day. Question will be arise who changed the cart?, when was it changed?, why was it changed?, can we undo it?, can we replay all chages?

Shared Service + Signal does not provide answer to these questions.

Solution: NgRx

NgRx is not about storing the state, Signal already do that. NgRx is about controlling how state changes.

## NgRx Architecture

```text
Component

↓

Dispatch Action

↓

Reducer

↓

Store Updated

↓

Component Automatically Updates
```

## NgRx Building blocks

```text
Action

↓

Reducer

↓

Store

↓

Selector

↓

Effect
```

Why so many steps?

    Because every state changes is Predictable, Traceable, Debuggable, Reproducible

## Store

Holds shared application's state.

Every component reads this store.

In Shared service, any component can update the value of the state, but In NgRx a component can not update the value state directly, it only can request to change, but it can read it.

## Action

An Action is a plain object that describes an event in the application. It **requests a state change** but does not change the Store itself.

Nothing will be changed until an Action is dispatched.

Action describe an event.

```ts
// cart.actions.ts

import { createAction } from '@ngrx/store';

export const addToCart = createAction(
    '[Cart] Add Item',
    props<{ productId: number }>()
);
```

```ts
// Dispatch it from component

this.store.dispatch(
    addToCart({productId: 101});
)
```

```ts
// NgRx conceptually creates
{
    type: '[Cart] Add Item',
    productId: 101
}
```

## Reducers

Actually updates the state.

It is a function that Receives the current state, Receives an Action and Return a new state, they never modify the existing state.

Flow

```text
Current State

+

Action

↓

Reducer

↓

New State
```

Reducer code

```ts
export const cartReducer = createReducer(
    initialState,

    on(addToCart, (state, { productId }) => ({
        ...state,
        count: state.count + 1
    }))
);
```

## Selector

Reading Data from the Store.

A Selector is a function that extracts a specific piece of data from the Store.

Selector encapsulates the Store structure. Instead os `store.count`, use `selectCart` directly.

```ts
export const selectCart = createSelector(
    selectCartState,
    state => state.count
)
```

```ts
// Read data through Compoennt using State
cartCount$ = this.store.select(selectCart);
```

`cartCount$` automatically emits the new value.

## Effects

List for an Action & Handling API calls & Side Effects and dispatch another Action.

Asynchronous call.

An Effect listen for an Action, performs a side effect (like an API call), and then dispatches another action.

Complete Flow

```text
Component
    │
dispatch(loadEmployees)
    ▼
Effect
    │
GET /employees
    ▼
Spring Boot
    ▼
loadEmployeesSuccess(data)
    ▼
Reducer
    ▼
Store
    ▼
Selector
    ▼
Component
```

**Why Dispatch another Action ? :** Because only Reducer can update the store. Effect does not update the store, and Reducer can only interact through an Action, so need to Dispatch an another action.

```ts
loadEmployee$ = createEffect(() => 
    this.action$.pipe(
        ofType(loadEmployees),

        switchMap(() => 
            this.employeeService.getEmployees()
        )
    )
);
```