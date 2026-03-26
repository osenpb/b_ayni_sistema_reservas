# Ayni – Reservation System

Ayni is a lodging reservation application for hotels affiliated across Peruvian territory. The backend was developed with **Java 21** and **Spring Boot**, focusing on good practices in architecture, security, and design.

The project implements **JWT-based authentication**, DTO management, custom exceptions, Spring Security filters, and pagination. It combines **Clean Architecture** for the authentication module with a **feature-based architecture** for the rest of the domain.  
It also follows **SOLID principles**.

The frontend repository that consumes this project can be found at:  
https://github.com/osenpb/f_ayni_sistema_reservas

---

## Main Technologies

- Java 21  
- Spring Boot  
- Spring Security + JWT + Nimbus JOSE  
- Spring Data JPA  
- Hibernate  
- REST API  
- Maven  
- Database: MySQL  
- Docker  

---

## Security and Authentication

The authentication module follows **Clean Architecture principles**, clearly separating:

- **Domain**: business rules and core models  
- **Application**: use cases (login, registration, validation)  
- **Infrastructure**: JWT implementation, repositories, and security configuration  
- **Entrypoints**: REST controllers  

---

## Features

- Authentication and authorization using **JWT**
- Custom **Spring Security filters**
- Clear **separation of responsibilities** between layers
- Centralized handling of **security-related errors**

---

## Project Architecture

**Hybrid approach**

- **Auth module → Clean Architecture**
- **Rest of the domain → Feature-based architecture**

This approach provides:

- Maximum clarity in a critical module such as authentication  
- Scalability and organization by **functional context** across the rest of the system  

The project uses **DTOs** to:

- Avoid exposing entities directly  
- Control the API contract  
- Facilitate validation and model evolution  

Includes:

- **Request and response DTOs**
- Explicit conversion between **entities and DTOs**

---

### Exception Handling

- Custom **domain exceptions**
- **@ControllerAdvice** for global exception handling
- Clear and consistent **error responses**

---

## Project Approach

Ayni is designed as:

- A **demonstration project** for a hotel reservation web application  
- A **solid foundation for a real e-commerce system**  
- An example of **modern Spring Boot best practices**

Special emphasis on:

- Clean architecture  
- Security  
- Scalability  
- Code readability  

---

## Upcoming Improvements

- **OAuth2 integration**
- **OpenAPI / Swagger documentation**
