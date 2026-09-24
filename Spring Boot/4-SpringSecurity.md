Filter → Security Filter Chain → DispatcherServlet → Interceptor → Controller

`SecurityFilterChain` is a sequence of Spring Security filters.

We can create multiples of the `SecurityFilterChain`, and `FilterChainProxy` will delegate requests to the appropriate chain.

Authentication and Authorization are part of a Security Filter Chain in a form of filters.

```text
HTTP Request
     ↓
FilterChainProxy
     ↓
Does request match /api/** ?
     ├── YES → API SecurityFilterChain
     │           ↓
     │        Filter 1 → Filter 2 → Filter 3
     │
     └── NO → Does it match /admin/** ?
                 ↓
              Admin SecurityFilterChain
                 ↓
              Filter 1 → Filter 2 → Filter 3
```

```java
@Bean
@Order(1)
SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/**")
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());

    return http.build();
}

@Bean
@Order(2)
SecurityFilterChain adminChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/admin/**")
        .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("ADMIN"));

    return http.build();
}
```