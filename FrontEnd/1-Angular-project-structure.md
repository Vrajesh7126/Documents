# Node.js
- Allows to runs the Javascript outside the browser.

# npm
- Use npm to **install Angular** and **other packages** your project needs.
- Angular projects define commands in `package.json`, then you can run them using `npm start`.
- `npm start` : Start Angular app locally.
- `npm run build` : Build angular app locally.
- `npm test` : Runs the project's tests.

# Angular CLI
- Install Angular CLI globally, and then you can use a commands of an angular

```bash
npm install -g @angular/cli
```

- `ng new <project-name>` : Create the new project and creates a folder structure.
- `ng serve` : Build, run and watch changes.
- `ng build` : Compiles the project and output will be in `/dist`
- `ng test` : Runs the project's unit test cases
- `ng generate component <component-name>` : Create a new component

# Folder Sructure
- After `ng new my-app`, it will creates a folder structure as below :

```
my-app/

src/
node_modules/
package.json
package-lock.json
angular.json
tsconfig.json
```

## package.json
- Contains **project name**, **project version**, **dependencies**, **scripts**.
- It tells npm, this packages are required to run the project.
- Instead of typing `ng server`, you can execute `npm start`

Example :

```json
{
  "name": "my-app",
  "version": "1.0.0",

  "scripts": {
    "start": "ng serve",
    "build": "ng build"
  },

  "dependencies": {
    "@angular/core": "^20.0.0",
    "rxjs": "^7.8.0"
  }
}
```

## package-lock.json
- Stores exact package version.
- contains the dependency tree.
- Instead of Angular version 20.x, it stores 20.0.4, so every developer installs the exact same version, without it, Develop A may install 20.0.1, and Developer B may install 20.1.0, and behaves different.

| `package.json`                      | `package-lock.json`                                      |
| ----------------------------------- | -------------------------------------------------------- |
| Lists direct dependencies           | Locks every installed dependency (direct + indirect)   |
| Can allow version ranges (`^`, `~`) | Records **exact versions**                                   |
| Edited by developers                | Generated and maintained by npm                          |
| Describes what the project needs    | Ensures everyone installs the exact same **dependency tree** |

## node_modules
- Contains every installed package.
- Created by `npm install`
- While executing `npm install`, npm reads `package.json` and and `package-lock.json` for an exact version and download the required dependencies and store it into the `node_modules`.

## angular.json
- Main configuration file for the Angular project.
- Indicates Angular CLI **how to build**, **serve the application**.
- When you run `ng build` or `ng serve`, Angular CLI reads this file to know what to do.
- It contains assets, style, optimization.

```json
{
  "projects": {
    "my-app": {
      "architect": {
        "build": {

        },
        "serve": {

        }
      }
    }
  }
}
```

## tsconfig.json
- Base TypeScript configuration file.
- Contains the common settings for how TypeScript code should be compiled into JavaScript.
- Other TypeScript configuration files can inherit these settings.

## tsconfig.app.json
- TypeScript configuration specifically for the Angular application.
- Angular CLI uses this file specified by `angular.json` when running `ng build` or `ng serve`.
- It can inherit settings from `tsconfig.json` and define which application files should be included/excluded.

## tsconfig.spec.json
- Settings used for unit tests.

## main.ts
- `main.ts` is the starting point of the Angular application. It starts Angular by loading `AppComponent`.

## index.html
- Webpage that browser opens.
- It contains :

```html
<body>
  <app-root></app-root>
</body>
```

- Angular looks for `<app-root>` in `index.html` and displays `App` component here.

## Simple flow

```
index.html (When user opens a page in web browser)
    │
    ▼
<app-root>
    ▲
    │
AppComponent (selector: "app-root")
    ▲
    │
main.ts starts the application
```

## app/
- Contains **components**, **services**, **routing**, **pipes**.

## How dependencies work
- When you run `npm install bootstrap`, npm updates `package.json`, download the package and store it into the `node_modules`.
- And on the other side if someone clone the project and run `npm install`, npm reads `package.json`, it downloads everything and store it into the `node_modules`.

## What happens during build ?
- Command `ng build`

```
Read angular.json

↓

Read tsconfig.json

↓

Compile TypeScript

↓

Compile HTML templates

↓

Compile SCSS/CSS

↓

Bundle JavaScript

↓

Optimize

↓

Minify

↓

Generate dist/
```

- Output at `/dist`

## What Happens During Compilation?
- Typescript convert into the javascript, because browser understand the javascript not the typescript.

## How Deployment works ?
- After `ng build`, get `/dist` and upload it into the AWS or Azure and it serves the generated HTML, CSS and Javascript files to users.

# Complete Lifecycle

```
Install Node.js
        │
        ▼
npm (comes with Node.js)
        │
        ▼
Install Angular CLI
        │
        ▼
ng new my-app
        │
        ▼
Angular CLI creates project files
(package.json, angular.json, tsconfig.json, src/, etc.)
        │
        ▼
npm install
        │
        ▼
npm reads package.json
        │
        ▼
Downloads dependencies into node_modules/
        │
        ▼
ng serve
        │
        ▼
Angular CLI reads angular.json + tsconfig.json
        │
        ▼
Compiles TypeScript → JavaScript
        │
        ▼
Bundles the application
        │
        ▼
Starts development server (localhost:4200)
        │
        ▼
You write code
        │
        ▼
Save file → Automatic rebuild → Browser refresh
        │
        ▼
ng build --configuration production
        │
        ▼
Creates optimized files in dist/
        │
        ▼
Deploy dist/ to a web server
        │
        ▼
Users open the application in their browser
```