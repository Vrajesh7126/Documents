- [Single Page Application](#single-page-application)
- [Angular Architecture](#angular-architecture)

# Single Page Application
- In SPA, browser loads a sinle page initially, and then only required part will be changed without reloading a entire page.
- How it was implemented ?
    - Suppose you have routes : `/login`, `/employees`, `profile`
    - When you navigate : `/login` → `/employees`, ANgular does not ask the server for new HTML page.
    - Instead, Angular change the URL, Loads te required component at `<router-outlet>` and update the screen.
    - If Normal routing, Component's javascript is already inside the browser(download when app first loaded), so Angular display it.
    - If Lazy loading, Angular request for the javascript bundle from the server, then load it.

# Angular Architecture
- Angular Architecture how different parts like are connected and work together.

## Building blocks
- **Component** : piece of UI (Typescript + Template + Styles)
- **Template** : Define what the user sees. It display data provided by the component (HTML). Contains angular features like Interpolation, property binding, event binding, directives, pipes
- **Service** : Contains Business logic or API call
- **Dependency Injection(DI)** : Injector is an angular object is used to create an object, store the object and provide the object wherever needed. When you write `constructor(private userService: UserService) {}`, it will see UserService exist at Injetor or not, If it does not then it will create and provide it to the component. Angular does it self.
- **Routing** : Routing decides which component to be display when URL will be changed.
- **Directives** : Change behaviour or appearance of HTML element.
- **Pipes** : Format data before showing it.
- **Lifecycle hookes** : It let Angular notify a component when important events happen (Component created, Data canged, Component Destroyed).
- **Signal** : Make angular automatically updates the UI when data changed.
- **Interface** : Describe the shape of data, only improve the type safety but do not exist after compilation.
- **Model** : Represent real world object in your app.

- **Note**:  `Interface` is a Typescript feature and `Model` is a design concept. Model may contains `Interface` or `Class`. Use `Interface` when you only describe data. Use a class Model when the data also needs a behaviour(methods or business logic).

