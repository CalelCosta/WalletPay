# Wallet Service 🚀

The **Wallet Service** is a microservice developed to manage financial operations such as wallet creation, balance inquiry, deposits, withdrawals, and transfers. Built using **Clean Architecture** and **Hexagonal Architecture** principles for modularity, scalability, and maintainability.

---

## 📌 Project Structure
The project follows a modular and organized structure, divided into clear layers to facilitate maintenance and scalability:
````
 /wallet-service
 ├── src/main/java/com/example/wallet
 │   ├── application  # Use cases (business logic)
 │   ├── domain       # Entities and business rules
 │   ├── infrastructure
 │   │   ├── persistence  # JPA repositories (Spring Data)
 │   │   ├── security     # Security configuration (JWT)
 │   │   ├── tracing      # Observability configuration
 │   ├── adapters
 │   │   ├── controller   # REST controllers
 │   │   ├── gateway      # Integration with external services (if needed)
 ├── docker-compose.yml  # PostgreSQL and microservice configuration in Docker
 ├── README.md           # Project documentation
 ````
## 🛠️ Features

| Method | Endpoint                                | Description                          |
|--------|-----------------------------------------|--------------------------------------|
| POST   | `/wallets`                              | Create new wallet                    |
| GET    | `/wallets/{id}/balance`                 | Get current balance                  |
| GET    | `/wallets/{id}/balance/history`         | Historical balance (add `?date=`)    |
| POST   | `/wallets/{id}/deposit`                 | Deposit funds                        |
| POST   | `/wallets/{id}/withdraw`                | Withdraw funds                       |
| POST   | `/wallets/{fromId}/transfer/{toId}`     | Transfer between wallets             |

---

## 📋 Business Rules

- ✅ **One Wallet Per User**: Strict 1:1 user-wallet relationship
- 💰 **Positive Deposits Only**: Rejects negative/zero deposit amounts
- 🔐 **Balance Checks**: System-wide balance validation for withdrawals/transfers
- 📜 **Transaction Auditing**: Full audit trail using Outbox pattern
- ⚡ **Async Processing**: Kafka-powered transaction queue

---

## ✅ Benefits of the Architecture
The project structure brings several benefits, including:

Ease of maintenance: Business logic is centralized in the UseCase layer, making the code more organized and easier to understand.

Testability: Each use case can be tested in isolation, ensuring code quality.

Scalability: The modularity of Clean Architecture allows the system to grow sustainably.

Asynchronous processing: Operations such as deposits, withdrawals, and transfers are processed asynchronously, ensuring scalability and resilience.

## 🚀 Getting Started
Prerequisites
* Java 17
* Docker and Docker Compose
* Maven
1. Clone the repository:
   `git clone https://github.com/recargapay-dev/CalelCosta.git`
2. Start the containers with Docker Compose:
The project uses Docker Compose to configure PostgreSQL and the microservice.
`docker-compose up -d`
3. Run the project:
If you prefer to run locally without Docker, use Maven:
`mvn spring-boot:run`
4. Access the API:
The API will be available at http://localhost:8080. Use tools like Postman or Insomnia to test the endpoints.

## 🧪 Tests
The project includes unit and integration tests to ensure code quality. To run the tests, use the command:
`mvn test`

🛠️ Technologies Used
* Java 17
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Docker
* JUnit 5 and Mockito (for testing)
* JWT (for authentication and security)
* Kafka (for asynchronous transaction processing)

## 🔧 Future Improvements
The project was developed in 3 days, and while it was possible to deliver a functional and robust solution, some improvements could be implemented with more time:
1. Increase unit test coverage:
- Add more test scenarios to cover edge cases and ensure greater reliability.
2. Add JWT user verification and adjust route access:
- Implement more robust JWT token validation to ensure only authenticated users can access specific resources.
3. Enhance observability with Grafana or Spring Actuator:
- Integrate monitoring tools like Grafana or Spring Actuator to improve system visibility and facilitate issue identification.
4. Add more error handling for specific cases:
- Implement more detailed error handling for scenarios such as database connection failures, timeouts in external calls, etc.
5. Adjust

## 🧪 How to Test
Here are example payloads to test the API endpoints:

### Authentication (/authenticate):
```
{
  "username": "admin",
  "password": "admin123"
}
```
### Create Wallet (/wallets):
User 1:
```
{
    "username": "Teste1",
    "password": "te123",
    "email": "teste1.teste@teste.com",
    "currency": "BRL",
    "cpf": "941.035.598-29"
}
```
User 2:
```
{
    "username": "Teste2",
    "password": "teste123",
    "email": "teste.teste@teste.com",
    "currency": "BRL",
    "cpf": "945.272.908-27"
}
```
### Deposit (/deposit):
```
{
    "cpf": "941.035.598-29",
    "amount": 1000
}
```
### Withdraw (/withdraw):
```
{
    "cpf": "941.035.598-29",
    "amount": 50
}
```
### Transfer (/transfer):
````
{
    "fromCpf": "941.035.598-29",
    "toCpf": "945.272.908-27",
    "amount": 50
}
````
## 📚 API Documentation
After starting the project, you can access the API documentation using Swagger:

### Swagger UI
* URL: http://localhost:8080/swagger-ui.html

* Description: Interactive API documentation where you can test endpoints directly from your browser.

### OpenAPI Specification
* URL: http://localhost:8080/v3/api-docs

* Description: Raw OpenAPI specification in JSON format.
