# Interceptor
- Command to create an interceptor : `ng generate interceptor <interceptor-name>` or `ng g 
interceptor <interceptor-name>`

- Instead of writing a code at every HTTP request, you write it once, and Angular applies it automatically.

- Angular 17+ uses Functional Interceptor.

```ts
export const authInterceptor: HttpInterceptorFn = (
    request,    // Represent current HTTP request
    next        // It means send this request to the next step
) => {
    return next(request);
};
```

- HtppRequest is `immutable`, so we can not modify the request after creating it, so we can modify/add into HttpRequest by `request.clone()`.

```ts
export const authInterceptor: HttpInterceptorFn = (request, next) => {

    const clonedRequest = request.clone({
        setHeader: {
            Authorization: 'Bearer YOUR_TOKEN'
        }
    });

    return next(clonedRequest);     // Send the cloned request
}
```

- Need to register an interceptor, so angular can applies it

```ts
//app.config.ts
providers: [
    provideHttpClient(
        withInterceptors([
            authInterceptor,
            loggingInterceptor,
            loadingInterceptor
        ])
    )
]
```

- `next(request)` of the `authInterceptor` pass request to `loggingInterceptor`, and it's `next(request)` pass to the `loadingInterceptor`.

## Response

- Responce to the Interceptor from the backend

```ts
export const authInterceptor: HttpInterceptorFn = (request, next) => {
    
    console.log("Request");

    return next(request).pipe(
        tap((response) => {
            console.log("Response: " + response);
        })
    );
};
```

## Handling an Errors
It can handle all HTTP errors at one place.

Every API call does same repetative thing again and again

```ts
.subscribe({
    next: ...,
    error: ...
})
```

Intercetpor does it once

```ts
return next(request).pipe(
    catchError(error => {   // Catch an error
        if(error.status == 401) {
            // Redirect to login
        }

        return throwError(() => error); // Pass an error to the next subscriber
    })
)
```

## HTTP Status Code

| Status | Meaning               | Common Action              |
| ------ | --------------------- | -------------------------- |
| 400    | Bad Request           | Show validation message    |
| 401    | Unauthorized          | Redirect to login          |
| 403    | Forbidden             | Show "Access Denied"       |
| 404    | Not Found             | Show "Resource Not Found"  |
| 500    | Internal Server Error | Show generic error message |
