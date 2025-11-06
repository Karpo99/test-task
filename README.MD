# User Service

The **User Service** is a RESTful API built with Java and Spring Boot, designed to manage user-related operations such
as registration, and authentication.
I am taking a more advanced and structured approach to building this project, focusing on **scalability,
maintainability, and modern best practices** to create a **production-ready application**. 🚀

## Features

- **User Registration**: Allows users to create an account.
- **User Authentication**: Provides access tokens and refresh tokens for secure authentication.
- **Token Management**: Allows token refresh and invalidation.
- **Security**: Uses JWT tokens for secure authentication.

## Technologies Used

- **Java 17** - Programming language.
- **Spring Boot 3** - Framework for building REST APIs.
- **Spring Security** - Security framework for authentication and authorization.
- **Spring Data JPA** - ORM for database management.
- **MySQL** - Relational database for storing user data.
- **Docker** - Containerization platform for deployment.
- **Jib** - Containerization tool for optimized image building.
- **Swagger (Springdoc OpenAPI)** - API documentation and testing.
- **Postman** - API testing tool for validating responses.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6.0 or higher
- Docker

### Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Karpo99/User-service.git
   cd User-service
   ```

2. **Set Up Environment Variables**:
    - Create a `.env` file in the root directory with the following content:
      ```env
      DATABASE_USERNAME=your_db_username
      DATABASE_PASSWORD=your_db_password
      ```

3. **Build the Application**:
   ```bash
   ./mvnw clean package
   ```

4. **Run with Docker Compose**:
   ```bash
   docker-compose up --build
   ```

   This command will start both the MySQL database and the User Service application.

## API Endpoints

The User Service exposes the following RESTful endpoints:

### 1. Register User

- **Endpoint**: `POST /users/register`
- **Description**: Registers a new user.
- **Request Body**:
  ```json
  {
    "email": "johndoe@example.com",
    "password": "securepassword",
    "firstName": "John",
    "lastName": "Doe"
  }
  ```
- **Response**:
    - **201 Created**: Registration successful.
    - **400 Bad Request**: Email already exists or invalid input.

### 2. User Login

- **Endpoint**: `POST /users/login`
- **Description**: Authenticates a user and returns JWT & refresh tokens.
- **Request Body**:
  ```json
  {
    "email": "johndoe@example.com",
    "password": "securepassword"
  }
  ```
- **Response**:
    - **202 Accepted**:
      ```json
      {
        "accessToken": "jwt-access-token",
        "accessTokenExpiresAt": 3600,
        "refreshToken": "jwt-refresh-token"
      }
      ```
    - **401 Unauthorized**: Invalid credentials.

### 3. Refresh Token

- **Endpoint**: `POST /users/refresh`
- **Description**: Refreshes an access token using a refresh token.
- **Request Body**:
  ```json
  {
    "refreshToken": "jwt-refresh-token"
  }
  ```
- **Response**:
    - **202 Accepted**:
      ```json
      {
        "accessToken": "new-jwt-access-token",
        "accessTokenExpiresAt": 3600,
        "refreshToken": "new-jwt-refresh-token"
      }
      ```
    - **401 Unauthorized**: Invalid or expired refresh token.

### 4. Logout

- **Endpoint**: `POST /users/logout`
- **Description**: Invalidates tokens to log out a user.
- **Request Body**:
  ```json
  {
    "accessToken": "jwt-access-token",
    "refreshToken": "jwt-refresh-token"
  }
  ```
- **Response**:
    - **200 OK**: Logout successful.
    - **401 Unauthorized**: Token already invalid.

### 5. Authenticate User

- **Endpoint**: `GET /users/authenticate`
- **Description**: Returns authentication details from a JWT token.
- **Query Parameter**:
    - `token=jwt-access-token`
- **Response**:
    - **200 OK**:
      ```json
      {
        "principal": "johndoe@example.com",
        "authorities": ["ROLE_USER"]
      }
      ```
    - **403 Forbidden**: Invalid token.

### 6. Ping API

- **Endpoint**: `GET /users/ping`
- **Description**: Returns `pong!!!!` to check API status.
- **Response**:
    - **202 Accepted**: `pong!!!!`

## API Flow

Below is a typical flow for a user interacting with the User Service API:

1. **Registration**: The user sends a `POST` request to `/users/register` with their details.
2. **Authentication**: Upon successful registration, the user authenticates by sending a `POST` request to
   `/users/login` to receive JWT & refresh tokens.
3. **Access Protected Routes**: The user includes the JWT token in the `Authorization` header to access protected
   routes.
4. **Token Refresh**: When the access token expires, the user can send a `POST` request to `/users/refresh` with their
   refresh token.
5. **Logout**: The user can log out by sending a `POST` request to `/users/logout` to invalidate their tokens.

## Swagger Documentation

The User Service integrates Swagger for API documentation.

- **Access Swagger UI**: After running the application, navigate to `http://localhost:8080/swagger-ui/index.html` to
  view the API documentation.
