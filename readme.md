# Java Scheduler Library

## Overview
The **Java Scheduler Library** is a lightweight, in-memory job scheduling framework designed to execute Java code dynamically. The library supports one-time and recurring job execution, allowing jobs to be triggered based on predefined schedules.

## Features
- Schedule and execute Java classes implementing `Supplier`
- Supports one-time and recurring job execution
- In-memory storage for jobs and execution results
- Provides an API for job management
- Configurable execution intervals
- Inspiration for execution zip format: [AWS Lambda](https://docs.aws.amazon.com/lambda/latest/dg/java-package.html)


### Building
```sh
./gradlew build
```
- Build the project and run tests
- Builds example lambda, library and wrapper service which exposes CRUD API packaged in docker

### Running example service
```sh
docker run -d -p 8080:8080 example_service:1.0.0
```
- Resources feature a sample Postman collection for testing

### Future improvements
- Add support for different storage configurations (e.g. Redis, MySQL)
- Implement a job execution queue
- Add support for job execution retries
- Add execution timeout configuration
- Consider remote code execution security
- Implement logging of execution
- Consider remote triggers like event queues or webhooks