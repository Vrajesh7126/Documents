# Angular Pipes
- Transform data before displaying it in the UI.
- It doesn't change the original data

- Syntax :

```html
{{ value | pipeName }}
```

| Pipe        | Purpose                             |
| ----------- | ----------------------------------- |
| `uppercase` | Converts to uppercase               |
| `lowercase` | Converts to lowercase               |
| `titlecase` | First letter of each word uppercase |
| `date`      | Formats dates                       |
| `currency`  | Formats currency                    |
| `percent`   | Formats percentages                 |
| `json`      | Displays an object as JSON          |
| `slice`     | Extracts part of a string or array  |


| Angular Pipe              | RxJS `pipe()`               |
| ------------------------- | --------------------------- |
| Used in HTML              | Used in TypeScript          |
| Formats displayed data    | Processes Observable data   |
| `{{ name \| uppercase }}` | `observable.pipe(map(...))` |


## Cutsom Pipe

- Syntax to create a pipe

```html
ng generate pipe <pipeName>
ng g p <pipeName>
```

```ts
import { Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'gradePipe';  // Use this as a pipe name
})
export class GradePipe implements PipeTransform{    // custom pipe implements PipeTransform
    tansform(value: string) : string {  // Override transform()
        // custom logic

        switch(value){
            case 'A':
                return 'Excellent';
            case 'B':
                return 'Good';
            case 'C':
                return 'Average';
        }
    }
}
```

```ts
grade = 'A';
```

```java
// Use pipe in HTML file
{{ grade | gradePipe }}
```

- Format Data or custom display of data : Angular Pipe
- Calculation of salary, business logic : Service class