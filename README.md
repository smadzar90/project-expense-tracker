# Project Expense Tracker

## Description

The Project Expense Tracker is a web service designed to manage company projects and expenses. 
Built with pure Java, it leverages RESTful APIs to efficiently handle HTTP requests and responses. The application follows a layered architecture, ensuring clear separation between Controllers, Services, and Repositories, while applying object-oriented principles for enhanced reusability and maintainability. 
The Gson library is used for JSON serialization/deserialization, JUnit for testing, and H2 as an embedded database, making this solution fully self-contained and easy to run locally.

## Main Technologies

- Java
- Java HTTP Server
- JUnit
- Embedded H2
- GSON

## API Endpoints

### Category Endpoints

  - `GET /api/category/{id}`
  - `GET /api/category/all`

### Payment Method Endpoints

  - `GET /api/payment/method/{id}`
  - `GET /api/payment/method/all`






