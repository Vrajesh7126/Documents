# Angular Complete Roadmap

## Index

### Phase 1: Foundations

* [What is Angular?](#what-is-angular)
* [SPA (Single Page Application)](#spa-single-page-application)
* [Angular Architecture](#angular-architecture)
* [Installing Angular & Project Creation](#installing-angular--project-creation)
* [Angular Project Structure](#angular-project-structure)
* [Bootstrapping Process (main.ts → App Component)](#bootstrapping-process-maints--app-component)

### Phase 2: Components

* [Components](#components)
* [Templates](#templates)
* [Data Binding](#data-binding)
* [Interpolation](#interpolation)
* [Property Binding](#property-binding)
* [Event Binding](#event-binding)
* [Two-Way Binding](#two-way-binding)
* [Component Communication](#component-communication)
* [@Input](#input)
* [@Output](#output)

### Phase 3: Directives

* [Directives](#directives)
* [ngIf](#ngif)
* [ngFor](#ngfor)
* [ngSwitch](#ngswitch)
* [Custom Directives](#custom-directives)

### Phase 4: Services & Dependency Injection

* [Services](#services)
* [Dependency Injection](#dependency-injection)
* [Singleton Services](#singleton-services)
* [Injection Hierarchy](#injection-hierarchy)

### Phase 5: Forms

* [Template Driven Forms](#template-driven-forms)
* [Reactive Forms](#reactive-forms)
* [Form Validation](#form-validation)
* [Dynamic Forms](#dynamic-forms)

### Phase 6: HTTP & Backend Integration

* [HttpClient](#httpclient)
* [GET API](#get-api)
* [POST API](#post-api)
* [PUT API](#put-api)
* [DELETE API](#delete-api)
* [Error Handling](#error-handling)
* [Interceptors](#interceptors)

### Phase 7: Routing

* [Routing](#routing)
* [Route Parameters](#route-parameters)
* [Child Routes](#child-routes)
* [Lazy Loading](#lazy-loading)
* [Route Guards](#route-guards)

### Phase 8: RxJS

* [Observable](#observable)
* [Observer](#observer)
* [Subject](#subject)
* [BehaviorSubject](#behaviorsubject)
* [Operators](#operators)

### Phase 9: Angular Lifecycle

* [Lifecycle Hooks](#lifecycle-hooks)
* [ngOnInit](#ngoninit)
* [ngOnChanges](#ngonchanges)
* [ngOnDestroy](#ngondestroy)

### Phase 10: Advanced Angular

* [Pipes](#pipes)
* [Custom Pipes](#custom-pipes)
* [Change Detection](#change-detection)
* [Standalone Components](#standalone-components)
* [Signals](#signals)
* [Content Projection](#content-projection)
* [Dynamic Components](#dynamic-components)

### Phase 11: State Management

* [State Management Basics](#state-management-basics)
* [RxJS State](#rxjs-state)
* [NgRx (Overview)](#ngrx-overview)

### Phase 12: Testing

* [Unit Testing](#unit-testing)
* [Component Testing](#component-testing)
* [Service Testing](#service-testing)

### Phase 13: Production & Enterprise

* [Environment Configuration](#environment-configuration)
* [Build Optimization](#build-optimization)
* [Authentication & JWT](#authentication--jwt)
* [Authorization](#authorization)
* [Angular Material](#angular-material)
* [Performance Optimization](#performance-optimization)
* [Deployment](#deployment)

---

# What is Angular?
- frontend framework developed by Google

---

# SPA (Single Page Application)
- In an SPA, the browser loads one HTML page initially, and after that Angular updates only the required part of the screen without reloading the entire page.

Suppose you have routes:

```
/login
/employees
/profile
```

When you navigate:
```
/login->/employee
```

Angular does not ask the server for a new HTML page.

Instead:

1. Angular changes the URL.
2. Loads the required component.
3. Updates the screen.

---

# Angular Architecture

- Building blocks :

```
Component
Template
Service
Module (or Standalone Component)
Router
```

- Angular separates responsibilities into different pieces.
- This makes app :
    - Easy to maintain
    - Easy to scale
    - Easy to test

## Component
- UI block into the scree

## Template
- HTML file inside the Component

## Service
- Actual business logic for Angular App.
- Calls the Backend Spring Boot API.

## Router
- has a URL mapping with the Component.

### Working Procedure

```
Router
   ↓
Employee Component
   ↓
Employee Service
   ↓
Spring Boot API
   ↓
JSON Response
   ↓
Component
   ↓
Template
   ↓
Screen Updated
```

Think:
```
Angular Component
      ≈
Controller + View

Angular Service
      ≈
Spring Service

Angular Router
      ≈
DispatcherServlet + URL Mapping

Angular Template
      ≈
JSP / Thymeleaf

Angular Application
      ≈
Frontend Layer
```

---

# Installing Angular & Project Creation

Before writing Angular code, you need:

```
Node.js → Runs Angular tools
npm → Downloads Angular packages
Angular CLI → Creates and manages Angular projects
```

| Java/Spring Boot    | Angular  |
| ------------------- | -------- |
| JDK                 | Node.js  |
| Maven/Gradle        | npm      |
| Spring Initializr   | ng new   |
| mvn spring-boot:run | ng serve |
| target/             | dist/    |

---

# Angular Project Structure

- main.ts : This is the application's starting point.
- package.json : Contains project information and dependencies.
- angular.json : Angular project configuration.

Flow :

```
main.ts
   ↓
AppComponent
   ↓
app.component.html
   ↓
Browser
```

---

# Bootstrapping Process (main.ts → App Component)

- AppComponent becomes the parent of the entire application.
- Almost everything eventually starts from AppComponent.

---

# Components

- A Component is a reusable piece of UI (screen or part of a screen).
- Angular applications are basically a tree of components.

- Typescript file : Contains Data, Methods, Business/UI logic
- HTML
- CSS


---

# Templates

---

# Data Binding

---

# Interpolation

---

# Property Binding

---

# Event Binding

---

# Two-Way Binding

---

# Component Communication

---

# @Input

---

# @Output

---

# Directives

---

# ngIf

---

# ngFor

---

# ngSwitch

---

# Custom Directives

---

# Services

---

# Dependency Injection

---

# Singleton Services

---

# Injection Hierarchy

---

# Template Driven Forms

---

# Reactive Forms

---

# Form Validation

---

# Dynamic Forms

---

# HttpClient

---

# GET API

---

# POST API

---

# PUT API

---

# DELETE API

---

# Error Handling

---

# Interceptors

---

# Routing

---

# Route Parameters

---

# Child Routes

---

# Lazy Loading

---

# Route Guards

---

# Observable

---

# Observer

---

# Subject

---

# BehaviorSubject

---

# Operators

---

# Lifecycle Hooks

---

# ngOnInit

---

# ngOnChanges

---

# ngOnDestroy

---

# Pipes

---

# Custom Pipes

---

# Change Detection

---

# Standalone Components

---

# Signals

---

# Content Projection

---

# Dynamic Components

---

# State Management Basics

---

# RxJS State

---

# NgRx (Overview)

---

# Unit Testing

---

# Component Testing

---

# Service Testing

---

# Environment Configuration

---

# Build Optimization

---

# Authentication & JWT

---

# Authorization

---

# Angular Material

---

# Performance Optimization

---

# Deployment
