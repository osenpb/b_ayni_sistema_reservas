# Ayni – Reservation System (Backend)

Hotel reservation system for affiliates in Peruvian territory. Built with **Java 21** and **Spring Boot**, following architecture, security, and design best practices.

## Main Technologies

- Java 21
- Spring Boot 3.x
- Spring Security + JWT (Nimbus JOSE)
- Spring Data JPA + Hibernate
- REST API
- Maven
- MySQL
- Docker

## Architecture

**Package structure:** `com.osen.sistema_reservas`

**Hybrid approach:**
- `auth/` module → Clean Architecture (Domain-Driven Design)
- `core/` modules → Feature-based architecture
- `api/` module → REST controllers (public/admin)
- `shared/` module → Reusable components (exceptions, mappers, DTOs)

### Auth Module Layers (Clean Architecture)

```
auth/
├── domain/
│   ├── model/          # Entities (User, Role)
│   └── port/           # Interfaces (in/out)
├── application/        # Use cases (services, mappers)
└── infrastructure/    # Implementations (controllers, configs, filters)
```

### Core Modules (Feature-Based)

- **reserva** - Reservation management
- **hotel** - Hotel administration
- **habitacion** - Room management
- **tipoHabitacion** - Room type catalog
- **departamento** - Locations/geography
- **detalle_reserva** - Reservation details
- **payment** - Mercado Pago integration
- **dashboard** - Statistics and reports

## Features

- JWT authentication and authorization
- Custom Spring Security filters
- Role-based access control (ADMIN, AFILIADO, CLIENTE)
- Centralized exception handling
- DTOs for API contract control
- Data validation with Jakarta Bean Validation
- Response caching with Caffeine
- Migration from Cliente to User: fields nombre, apellido, dni now in User entity

## Run the Project

```bash
# Compile
mvn clean package

# Run
mvn spring-boot:run
```

## Frontend Repository

https://github.com/osenpb/f_ayni_sistema_reservas
