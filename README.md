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
- Gson

## API Endpoints

### Category Endpoints

  - `GET /api/category/{id}`
  - `GET /api/category/all`

### Payment Method Endpoints

  - `GET /api/payment/method/{id}`
  - `GET /api/payment/method/all`

### Project Endpoints

  - `POST /api/project/save`
  - `POST /api/project/save/all`
  - `GET /api/project/find/{id}`
  - `GET /api/project/find/all`
  - `GET /api/project/find?attribute=name&value={name}`
  - `GET /api/project/find?attribute=start_date&value={start_date}`
  - `GET /api/project/find?attribute=budget&greater_than={budget}`
  - `GET /api/project/find?attribute=budget&less_than={budget}`
  - `PUT /api/project/update`
  - `PUT /api/project/update/all`
  - `DELETE /api/project/delete/{id}`
  - `DELETE /api/project/delete/all`

### Expense Endpoints

  - `POST /api/expense/save`
  - `POST /api/expense/save/all`
  - `GET /api/expense/find/{id}`
  - `GET /api/expense/find/all`
  - `GET /api/expense/find?attribute=project_id&value={project_id}`
  - `GET /api/expense/find?attribute=category_id&value={category_id}`
  - `GET /api/expense/find?attribute=payment_id&value={payment_id}`
  - `GET /api/expense/find?attribute=transation_date&value={transation_date}`
  - `GET /api/expense/find?attribute=amount&greater_than={amount}`
  - `GET /api/expense/find?attribute=amount&less_than={amount}`
  - `PUT /api/expense/update`
  - `PUT /api/expense/update/all`
  - `DELETE /api/expense/delete/{id}`
  - `DELETE /api/expense/delete/all`

## Usage

To start the service, clone the repository to your local machine, navigate to the project directory, and run the main method inside the `PETracker` class, which initializes the HTTP server to handle requests. Once the service is running, you can test and access the RESTful API endpoints using a web browser or Postman at `http://localhost:8000`.





