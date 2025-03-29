# Java Scheduler Library

## Overview
The **Java Scheduler Library** is a lightweight, in-memory job scheduling framework designed to execute Java code dynamically. The library supports one-time and recurring job execution, allowing jobs to be triggered based on predefined schedules.

## Features
- Schedule and execute Java classes implementing `Supplier`
- Supports one-time and recurring job execution
- In-memory storage for jobs and execution results
- Provides an API for job management
- Configurable execution intervals


### Building
```sh
./gradlew build
```
- Build the project and run tests
- Builds example lambda, library and wrapper service which exposes CRUD API packaged in docker

#### Running example service
```sh
docker run -d -p 8080:8080 example_service:1.0.0
```