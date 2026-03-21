# Recomendaciones de Arquitectura - Sistema de Reservas

## 📋 Resumen Ejecutivo

El proyecto ha sido reorganizado bajo una arquitectura hexagonal con validaciones robustas en DTOs. A continuación se detalla el progreso realizado y las recomendaciones pendientes.

---

## 🔍 Análisis de la Arquitectura Actual

### ✅ Mejoras Implementadas
- [x] Estandarización de estructura de paquetes bajo **Arquitectura Hexagonal**
- [x] Validaciones robustas en todos los DTOs de Request
- [x] Validaciones movidas de Models a DTOs (separación de responsabilidades)
- [x] Controllers corregidos (bugs, duplicación, mapeo en services)
- [x] Excepción `ForbiddenException` creada con handler global
- [x] `@Valid` agregado en AuthController para Login y Register
- [x] Endpoints REST corregidos (1 recurso por controller, plural, sustantivos, query params)
- [x] SecurityConfig corregido (paths alineados con controllers)
- [x] Perfiles de configuración (dev, test con Testcontainers, prod)
- [x] Dependencia Testcontainers agregada (MySQL + JUnit Jupiter)

### ⚠️ Áreas de Mejora Pendientes
- [ ] Implementación completa del flujo de refresh token
- [ ] Pruebas unitarias e integración
- [ ] Logging estructurado
- [ ] Health checks y métricas
- [ ] Optimización de consultas JPA

---

## 🏗️ Estructura Actual del Proyecto

### Arquitectura Hexagonal Implementada

```
com.osen.sistema_reservas/
├── api/
│   ├── admin/                    # Controllers de administración
│   └── publico/                  # Controllers públicos
├── auth/
│   ├── domain/
│   │   ├── model/                # Entidades (User, Role)
│   │   └── port/
│   │       ├── in/               # Interfaces de casos de uso
│   │       └── out/              # Repositorios
│   ├── application/
│   │   ├── services/             # Implementaciones
│   │   └── mappers/
│   └── infraestructure/
│       ├── config/               # Configuraciones
│       ├── controllers/          # AuthController
│       ├── dtos/                 # DTOs de auth
│       └── filters/              # JWT Filter
├── core/
│   ├── cliente/
│   │   ├── domain/model/
│   │   ├── domain/port/out/
│   │   └── application/{service,dtos}/
│   ├── hotel/
│   │   ├── domain/model/
│   │   ├── domain/port/out/
│   │   └── application/{service,dtos}/
│   ├── habitacion/
│   │   ├── domain/model/
│   │   ├── domain/port/out/
│   │   └── application/{service,dtos}/
│   ├── reserva/
│   │   ├── domain/model/
│   │   ├── domain/port/out/
│   │   └── application/{service,dtos}/
│   ├── departamento/
│   │   ├── domain/model/
│   │   ├── domain/port/out/
│   │   └── application/{service,dtos}/
│   ├── tipoHabitacion/
│   │   ├── domain/model/
│   │   ├── domain/port/out/
│   │   └── application/{service,dtos}/
│   ├── detalle_reserva/
│   │   ├── domain/model/
│   │   ├── domain/port/out/
│   │   └── application/{service,dtos}/
│   ├── dashboard/
│   │   ├── domain/port/out/
│   │   └── application/{service,dtos}/
│   └── payment/
│       ├── application/service/
│       └── infrastructure/web/
├── shared/
│   └── helpers/
│       ├── dtos/                 # DTOs compartidos
│       ├── exceptions/           # Excepciones globales
│       └── mappers/              # Mappers compartidos
└── (SistemaReservasApplication.java)
```

---

## 📊 Progreso de Implementación

### ✅ Fase 1: Controllers y Bugs (COMPLETADO)

| Tarea | Estado | Detalle |
|-------|--------|---------|
| Bug ContactoController | ✅ | `@RestController` corregido, endpoint `/public/contacto` |
| Falta `@Valid` AdminReservaController | ✅ | Agregado `@Valid` al `@RequestBody` |
| Lógica permisos repetida | ✅ | Método `validarPropietario()` extraído |
| `ResponseEntity<?>` genérico | ✅ | Cambiado a tipos específicos |
| Mapeo en controllers | ✅ | Movido a services (`listarResponse()`, etc.) |
| Import duplicado | ✅ | Eliminado |

### ✅ Fase 2: Validaciones de DTOs (COMPLETADO)

| DTO | Estado | Validaciones |
|-----|--------|--------------|
| `LoginRequest` | ✅ | `@NotBlank`, `@Email` |
| `RegisterRequest` | ✅ | `@NotBlank`, `@Size`, `@Email`, `@Pattern` |
| `HotelRequest` | ✅ | `@NotBlank`, `@Size`, `@NotNull`, `@Positive`, `@NotEmpty`, `@Valid` |
| `HabitacionRequest` | ✅ | `@NotBlank`, `@Size`, `@Pattern`, `@NotNull`, `@Positive` |
| `ReservaRequest` | ✅ | `@NotNull`, `@FutureOrPresent`, `@Future`, `@Valid`, `@NotEmpty` |
| `ReservaAdminUpdateDTO` | ✅ | `@NotNull`, `@FutureOrPresent`, `@Future`, `@Pattern`, `@Valid`, `@NotEmpty` |
| `ClienteRequest` | ✅ | `@NotBlank`, `@Pattern`, `@Email` (movidas del model) |
| `DepartamentoRequest` | ✅ | `@Size` agregado |
| `ReservaUpdateRequest` | ✅ | `@FutureOrPresent`, `@Future` |

**Validaciones movidas de Models a DTOs:**
- `Cliente.java`: Removidas `@NotBlank`, `@Pattern`, `@Email`
- `User.java`: Removida `@Size`

### ✅ Fase 3: Arquitectura Hexagonal (COMPLETADO)

| Módulo | Estado | Estructura |
|--------|--------|------------|
| `hotel` | ✅ | `domain/{model,port/out}`, `application/{service,dtos}` |
| `cliente` | ✅ | `domain/{model,port/out}`, `application/{service,dtos}` |
| `departamento` | ✅ | `domain/{model,port/out}`, `application/{service,dtos}` |
| `habitacion` | ✅ | `domain/{model,port/out}`, `application/{service,dtos}` |
| `reserva` | ✅ | `domain/{model,port/out}`, `application/{service,dtos}` |
| `tipoHabitacion` | ✅ | `domain/{model,port/out}`, `application/{service,dtos}` |
| `detalle_reserva` | ✅ | `domain/{model,port/out}`, `application/{service,dtos}` |
| `dashboard` | ✅ | `domain/port/out`, `application/{service,dtos}` |
| `payment` | ✅ | `application/service`, `infrastructure/web` |
| `auth` | ✅ | `domain/{model,port/{in,out}}`, `application`, `infraestructure` |
| `helpers` | ✅ | Movidos a `shared/helpers/` |

---

## 🏗️ Recomendaciones Pendientes

### 1. **Implementación de Perfiles de Configuración** (Alta Prioridad)

#### Problema Actual
Configuración única (`application.yml`) con `ddl-auto: update` y `show-sql: true`.

#### Solución Propuesta

```yaml
# application-dev.yml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

# application-prod.yml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  datasource:
    url: ${DATABASE_URL}

# application-test.yml
spring:
  jpa:
    database: H2
    hibernate:
      ddl-auto: create-drop
```

---

### 2. **Implementación Completa de Refresh Token** (Alta Prioridad)

#### Problema Actual
Tokens de refresh configurados en `application.yml` pero no implementados.

#### Solución Propuesta

```java
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String token;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private Instant expiryDate;
    private boolean revoked;
}

// Endpoints
POST /api/auth/refresh   → { "refreshToken": "xxx" }
POST /api/auth/logout     → { "refreshToken": "xxx" }
```

---

### 3. **Implementación de Testing** (Alta Prioridad)

#### Estructura Propuesta

```
src/test/java/com/osen/sistema_reservas/
├── unit/
│   ├── auth/service/
│   ├── core/{modulo}/application/service/
├── integration/
│   ├── api/
│   └── domain/port/out/
└── e2e/
```

#### Dependencias

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

---

### 4. **Optimización de Consultas JPA** (Media Prioridad)

#### Solución Propuesta

```java
@Entity
@Table(name = "reservas", indexes = {
    @Index(name = "idx_reserva_fecha", columnList = "fecha_inicio, fecha_fin"),
    @Index(name = "idx_reserva_estado", columnList = "estado"),
    @Index(name = "idx_reserva_hotel", columnList = "hotel_id")
})
public class Reserva { ... }
```

---

### 5. **Logging Estructurado** (Baja Prioridad)

#### Dependencias

```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
</dependency>
```

---

### 6. **Health Checks y Métricas** (Baja Prioridad)

#### Dependencias

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

---

## 🔴 Problemas en Endpoints REST (PENDIENTE)

### Controllers con problemas

#### ReservaController (`/public/reserva`)

| Endpoint Actual | Problema | Corrección |
|-----------------|----------|------------|
| `GET /public/reserva/departamentos` | Departamentos bajo `/reserva` | `GET /public/departamentos` |
| `GET /public/reserva/hoteles` | Hoteles bajo `/reserva` | `GET /public/hoteles?depId=x` |
| `GET /public/reserva/hoteles/{id}` | Hoteles bajo `/reserva` | `GET /public/hoteles/{id}` |
| `POST /public/reserva/hoteles/{id}/reservar` | Verbo en URL + path anidado | `POST /public/reservas` |
| `GET /public/reserva/reserva/{id}` | Redundancia `/reserva/reserva/` | `GET /public/reservas/{id}` |
| `GET /public/reserva/mis-reservas` | Mezcla path/query | `GET /public/reservas/mias` |
| `POST /public/reserva/{id}/confirmar-pago` | Verbo en URL | `PATCH /public/reservas/{id}/pagar` |

**Problema adicional**: Este controller maneja 3 recursos diferentes (departamentos, hoteles, reservas). Debería separarse en 3 controllers.

#### AdminReservaController (`/admin/reservas`)

| Endpoint Actual | Problema | Corrección |
|-----------------|----------|------------|
| `GET /admin/reservas/buscar?dni=x` | Path `/buscar` innecesario | `GET /admin/reservas?dni=x` |

#### AdminHotelController (`/admin/hoteles`)

| Endpoint Actual | Problema | Corrección |
|-----------------|----------|------------|
| `GET /admin/hoteles/departamento/{id}` | Filtro como path | `GET /admin/hoteles?departamentoId={id}` |

### Reglas REST violadas

1. **No usar verbos en URLs**: `/reservar`, `/confirmar-pago`, `/buscar`
2. **Un recurso por controller**: ReservaController maneja 3 recursos
3. **Usar sustantivos pluralizados**: `/reservas` no `/reserva`
4. **Filtros como query params**: `?departamentoId=x` no `/departamento/{id}`
5. **HTTP methods correctos**: `PATCH` para actualizaciones parciales

### Estructura RESTful recomendada

```
# RECURSO: Departamentos (público)
GET    /public/departamentos              # Listar / buscar por nombre
GET    /public/departamentos/{id}         # Obtener por ID

# RECURSO: Hoteles (público)
GET    /public/hoteles?departamentoId=x   # Listar / filtrar
GET    /public/hoteles/{id}               # Obtener detalle

# RECURSO: Habitaciones (público)
GET    /public/habitaciones/disponibles?hotelId=x&fechaInicio=y&fechaFin=z
GET    /public/habitaciones/tipos

# RECURSO: Reservas (público - autenticado)
POST   /public/reservas                   # Crear (body: hotelId, fechas, habitaciones, cliente)
GET    /public/reservas/{id}              # Obtener detalle
GET    /public/reservas/mias              # Listar mis reservas (+ filtros fecha)
PUT    /public/reservas/{id}              # Actualizar fechas
PATCH  /public/reservas/{id}/pagar        # Confirmar pago
DELETE /public/reservas/{id}              # Cancelar

# RECURSO: Departamentos (admin)
GET    /admin/departamentos
GET    /admin/departamentos/{id}
POST   /admin/departamentos
PUT    /admin/departamentos/{id}
DELETE /admin/departamentos/{id}

# RECURSO: Hoteles (admin)
GET    /admin/hoteles?departamentoId=x
GET    /admin/hoteles/{id}
POST   /admin/hoteles
PUT    /admin/hoteles/{id}
DELETE /admin/hoteles/{id}

# RECURSO: Reservas (admin)
GET    /admin/reservas?dni=x              # Sin /buscar
GET    /admin/reservas/{id}
PUT    /admin/reservas/{id}
DELETE /admin/reservas/{id}

# Dashboard (admin)
GET    /admin/dashboard/stats
```

### Tareas de corrección de endpoints

- [ ] Separar ReservaController en 3 controllers: DepartamentoController, HotelController, ReservaController
- [ ] Renombrar endpoints con verbos a sustantivos
- [ ] Cambiar filtros de path params a query params
- [ ] Usar `/reservas` (plural) en lugar de `/reserva`
- [ ] Usar `PATCH` para confirmar pago

---

## 📊 Priorización Restante

| Prioridad | Recomendación | Tiempo Estimado | Impacto |
|-----------|---------------|-----------------|---------|
| ✅ | Corregir endpoints REST | COMPLETADO | API consistente |
| ✅ | Perfiles de configuración | COMPLETADO | Seguridad |
| 🔴 Alta | Refresh Token completo | 8-12 horas | UX/Sesiones |
| 🔴 Alta | Implementación Testing | 20-40 horas | Mantenibilidad |
| 🟡 Media | Optimización JPA | 6-10 horas | Performance |
| 🟢 Baja | Logging estructurado | 4-6 horas | Observabilidad |
| 🟢 Baja | Health checks | 2-4 horas | Monitoreo |

---

## 📝 Checklist Actualizado

### ✅ Fase 1: Controllers y Validaciones (COMPLETADO)
- [x] Corregir bugs en controllers
- [x] Eliminar código duplicado
- [x] Mover mapeo de controllers a services
- [x] Implementar validaciones en todos los DTOs
- [x] Mover validaciones de Models a DTOs

### ✅ Fase 2: Arquitectura Hexagonal (COMPLETADO)
- [x] Reorganizar módulo hotel
- [x] Reorganizar módulo cliente
- [x] Reorganizar módulo departamento
- [x] Reorganizar módulo habitacion
- [x] Reorganizar módulo reserva
- [x] Reorganizar módulo tipoHabitacion
- [x] Reorganizar módulo detalle_reserva
- [x] Reorganizar módulo dashboard
- [x] Reorganizar módulo payment
- [x] Reorganizar módulo auth
- [x] Mover helpers a shared/

### ✅ Fase 3: Endpoints REST (COMPLETADO)
- [x] Separar ReservaController en 3 controllers (PublicoDepartamentoController, PublicoHotelController, ReservaController)
- [x] Renombrar endpoints con verbos a sustantivos
- [x] Cambiar filtros de path params a query params
- [x] Usar `/reservas` (plural) en lugar de `/reserva`
- [x] Usar `PATCH` para confirmar pago
- [x] Eliminar path `/buscar` innecesario en AdminReservaController
- [x] Corregir SecurityConfig (mismatch `/api` prefix que no existía)

### ⏳ Fase 4: Configuración (EN PROGRESO)
- [x] Configurar perfiles de aplicación (dev, test, prod)
- [ ] Implementar flujo completo de refresh token
- [ ] Agregar rate limiting a endpoints de autenticación
- [ ] Configurar CORS de forma más restrictiva en producción

### ⏳ Fase 5: Calidad (PENDIENTE)
- [ ] Implementar pruebas unitarias (cobertura > 70%)
- [ ] Implementar pruebas de integración
- [ ] Configurar CI/CD con pipeline de calidad
- [ ] Implementar SonarQube para análisis de código

### ⏳ Fase 6: Optimización (PENDIENTE)
- [ ] Implementar cache para consultas frecuentes
- [ ] Optimizar consultas JPA (evitar N+1)
- [ ] Implementar paginación en todos los listados
- [ ] Agregar logging estructurado

### ⏳ Fase 7: Observabilidad (PENDIENTE)
- [ ] Configurar Spring Boot Actuator
- [ ] Implementar métricas con Prometheus
- [ ] Configurar health checks personalizados
- [ ] Documentar API con OpenAPI/Swagger

---

## 🎯 Métricas de Éxito

| Métrica | Valor Actual | Objetivo |
|---------|--------------|----------|
| Cobertura de código | ~0% | > 70% |
| Validaciones DTOs | 100% | 100% ✅ |
| Arquitectura hexagonal | 100% | 100% ✅ |
| Tiempo de respuesta API | N/A | < 200ms (P95) |
| Disponibilidad | N/A | > 99.5% |

---

## 📚 Referencias

- [Arquitectura Hexagonal - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Spring Boot Best Practices](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Clean Architecture - Robert C. Martin](https://www.amazon.com/Clean-Architecture-Craftsmans-Software-Structure/dp/0134494164)
- [OWASP Security Guidelines](https://owasp.org/www-project-top-ten/)

---

## 🚀 Instrucciones de Uso

### Perfiles de Configuración

| Perfil | Base de datos | DDL | SQL Init | Uso |
|--------|--------------|-----|----------|-----|
| `dev` (default) | MySQL local (`localhost:3306/dawiapp`) | `update` | `always` | Desarrollo local |
| `prod` | MySQL via `${DATABASE_URL}` | `validate` | `never` | Producción |
| `test` | MySQL via Testcontainers | `create-drop` | `always` | Tests de integración |

### Comandos

```bash
# Desarrollo (perfil por defecto)
mvn spring-boot:run

# Producción
mvn spring-boot:run -Dspring-boot.run.profiles=prod
# Requiere variables de entorno: DATABASE_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET

# Tests (requiere Docker corriendo)
mvn test

# Compilar sin tests
mvn compile -DskipTests
```

### Variables de Entorno (Producción)

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DATABASE_URL` | URL completa de MySQL | `jdbc:mysql://db.host:3306/reservas` |
| `DB_USERNAME` | Usuario de la base de datos | `admin` |
| `DB_PASSWORD` | Contraseña | `secret123` |
| `JWT_SECRET` | Clave secreta JWT (obligatoria en prod) | cadena aleatoria larga |

---

*Documento actualizado el: 21 de Marzo de 2026*
*Versión: 3.1*
*Progreso: Fase 1, 2 y 3 completadas. Fase 4 en progreso (perfiles configurados).*
