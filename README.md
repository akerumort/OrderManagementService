
OrderManagementService is a microservice designed to manage orders. It is built using Spring Boot and follows best practices for RESTful services. This service handles CRUD operations for orders and provides a robust API for integrating with other services or front-end applications.

## ⚙ Features

- CRUD operations for managing orders
- Integration with PostgreSQL database
- Liquibase for database versioning
- Swagger/OpenAPI for API documentation
- Exception handling and validation
- Integration with external services using REST
- Docker support for containerized deployment
- Unit testing

## 💻 Used technologies

- **Java 17**
- **Spring Boot 3.3.1**
- **Spring Data JPA**
- **Maven**
- **PostgreSQL**
- **Liquibase**
- **MapStruct 1.5.0.Final**
- **SpringDoc OpenAPI 2.5.0**
- **Log4j 2.23.1**
- **Hibernate Validator 8.0.0.Final**
- **Apache POI 5.2.3**
- **iText7 8.0.4**
- **JUnit 5**
- **Mockito**
- **Docker + Docker Compose**

## 🛠️ Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/akerumort/OrderManagementService.git
   
   cd OrderManagementService

2. Build the project:
   ```bash
   ./mvnw clean install

3. Set up the PostgreSQL database:
   ```bash
   CREATE DATABASE order_management_db;
   CREATE USER postgres WITH ENCRYPTED PASSWORD 'postgresadmin';
   GRANT ALL PRIVILEGES ON DATABASE order_management_db TO postgres;

4. Update the src/main/resources/application.properties file with your database credentials

## 🏃🏼‍♀️ Running the app

1. Run the application using Maven:
    ```bash
   ./mvnw spring-boot:run
    
2. The application will start on http://localhost:8080

## 🐋 Running with Docker Compose

1. Ensure Docker and Docker Compose are installed.

2. Build and start the containers:
   ```bash
   docker-compose up --build

3. The application will start on http://localhost:8080

## ⌨️ Testing

- Run the tests using Maven:
    ```bash
    ./mvnw test

## 📝 API Documentation

- Available on:
   ```bash
   http://localhost:8080/swagger-ui/index.html

## 🛡️ License
This project is licensed under the MIT License. See the `LICENSE` file for more details.

## ✉️ Contact
For any questions or inquiries, please contact `akerumort404@gmail.com`.

