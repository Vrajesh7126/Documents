# HTTP Client
- Used to send HTTP requests to a backend.
- Implement it using `DI`.

```ts
// Service class
@Injectable({
    providedIn: 'root'
})
export class EmployeeService{

    constructor(private http: HttpClient){}

    getEmployee(){
        return this.http.get('/employee');
    }
}
```

```ts
// Calling the service at Component
export class EmployeeComponent {
    constructor(private service: EmployeeService){}

    ngOnInit(): void {
        this.service.
            getEmployee().
            subscribe(data => {
            // get the data here...
            })
    }
}
```

# RxJS

## Observable
- Observable is an object that produces the data over time.
- In `HttpClient`, `this.http.get(...)` returns `Observable<Employee[]>`, not `Employee[]`.
- To receive the data from `Observable<Employee[]>`, need to `.subscribe()`.

- Use cases:
    - **HttpClient**
    - **ActivatedRoute**
    - **EventEmitter (@Output)** => Internally use Observable
    - **Subject** : By subscribe to it, component will receive the emitted data

## subscribe()
- While `get(...)` it will not call Http request, while `subscribe()` Http request will be called.
- subscribe is not get the data immedietly, subscribe means it start listening to this Observable, once data will be prepared then subscribe will receive it.
- If data receives, call `next` & `complete`.
- If error occurs, call `error`.
- subscribe can handle 3 things:
    - **next**
    - **error**
    - **complete**

```ts
.subscribe({
    next: (data) => {
        console.log(data);
    },
    error: (err) => {
        console.log(err);
    },
    complete: () => {
        console.log("Finished");
    }
})
```

- **next** : Calls when data arrives
- **error** : Calls when something went wrong or error occurs
- **complete** : Calls when data arrival was completed. (For HttpClient it happens immediately after the response arrived.)

## Promise
- A promise represent a one future value while Observable is a stream of a future values.
- Http is respond a one value(Promise is also valid for it), but it still uses Observable to maintain the consistency across the programming model.

## RxJS Operators
- Operators are functions that process data emitted by an Observable.
- Operators are used with `pipe()`.
- `pipe()` is a method that chain multiple RxJS operators together.
- Output of one operator becomes an input of the next.
- Example:
```ts
this.http.get<Employee[]>('/employee')
    .pipe(
        map(...),
        filter(...),
        tap(...)
    )
    .subscribe(...);
```

- **map** : Trasform a value.
- **filter** : Filter a value.
- **tap** : Print a data for a debugging purpose or only see it (Don't allow to modify it).

## switchMap
- When a new value comes, it cancles the previous one and switch to the latest one.

```ts
this.searchControl.valueChanges
    .pipe(
        debounceTime(300),      // Optional
        distinctUntilChanged(), // Optional
        switchMap(value => this.http.get('/employee?name=' + value))
    )
    .subscribe(data => {
        console.log(data);
    });
```

## catchError
- Use to handle an error while occurs in an Observable, such as HTTP request fail.
- We can handle an error within `subscribe(...)`, but `catchError()` catches errors before it reach to `subscribe()`, to reduce the repetation of the error handling.
- It can `return of([])`, so `subscribe()` can receive `[]`  

```ts
.pipe(
    catchError(error => {
        // Error catched here

        return of([]);
    })
)
```

## Subject
- A Subject is also an Observable, so you can subscribe to it and receive the data it emits.
- Subject is both:
    - An Observable (You can subscribe to it and receive its emitted values)
    - An Observer (You can send values to it using next(), error(), or complete())

```ts
// Subject can provide both Observable and Observer
const subject = new Subject<string>();

subject.subscribe(value => {
  console.log(value);
});

subject.next("Hello");

// If we only want to expose a Observable functionality not Observer's functionality
const observable: Observable<string> = subject.asObservable();

observable.subscribe(value => {
  console.log(value);
});

// Not Possible, it hides next() from the receiver
observable.next("Hello");
```

- After emitted a value to the Subject, If some component is subscribe to it, it will not receive the last omitted value.

### BehaviourSubject
- Stores a latest value.
- Even through a someone subscribe later after value was omitted, it still receives the data.

### ReplaySubject
- Stored last N values.
- Even through a someone subscribe later after N values were omitted, it still receives last N values.

### AsyncSubject
- Stored last value and emits only when `subject.complete()` called.