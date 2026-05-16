# Guía de Arquitectura — Ayni Sistema de Reservas

## Estructura aplicada: Feature-based layered con ports de salida

Cada módulo de negocio sigue esta organización:

```
core/<modulo>/
├── domain/
│   ├── model/          → Entidades JPA (lógica de negocio pura)
│   └── port/out/       → Interfaces de repositorio (abstracción sobre JPA)
├── application/
│   ├── service/        → Casos de uso, lógica de negocio, transacciones
│   └── dtos/           → Objetos de transferencia de entrada y salida
└── infrastructure/
    └── web/            → Controladores REST
```

El módulo `auth` es el único con `port/in` (interfaces de caso de uso). Los módulos `core` no las necesitan por ahora — un servicio con una sola implementación no gana nada con una interfaz extra.

---

## Reglas por capa

### Domain (`domain/model/`)
- Las entidades no importan clases de `application` ni de `infrastructure`.
- La lógica de negocio pura (cálculos, validaciones de estado) va en la entidad, no en el servicio. Ver `Hotel.getPrecioMinimo()` como ejemplo correcto.
- Usar `@Index` en columnas usadas en filtros frecuentes (`estado`, `fechaInicio`, `hotel_id`). `Reserva.java` ya lo hace bien.
- Evitar `@Data` de Lombok en entidades JPA — puede romper `equals/hashCode` con proxies Hibernate. Usar `@Getter @Setter` o escribir los accessors manualmente.
- Los campos de estado con valores fijos (`PENDIENTE`, `CONFIRMADA`, `CANCELADA`) deben ser enums anotados con `@Enumerated(EnumType.STRING)`, no Strings.

### Port/Out (`domain/port/out/`)
- Extienden `JpaRepository<T, ID>` y agregan solo los métodos de consulta personalizados.
- Las queries JPQL con `JOIN FETCH` se definen aquí para evitar el problema N+1. Ver `ReservaRepository` como referencia.
- No colocar lógica de negocio en las queries; solo proyecciones y filtros.

### Application (`application/service/`)
- Coordina la transacción (`@Transactional`) y delega a los repositorios (`port/out`).
- Inyectar repositorios (`port/out`) directamente, no otros servicios de aplicación. `DashboardService` y `ReservaService` inyectan otros servicios — esto crea acoplamiento entre módulos y complica los tests.
- Los métodos de solo lectura llevan `@Transactional(readOnly = true)`.
- Si un servicio supera las 150 líneas, evaluar si tiene más de una responsabilidad.

### Infrastructure (`infrastructure/web/`)
- Los controladores validan input HTTP (`@Valid`), delegan al servicio y devuelven la respuesta. Sin lógica de negocio.
- Separar endpoints públicos de admin en controladores distintos (`PublicController` / `AdminController`).

---

## DTOs

- Los DTOs de **salida** (response) deben ser `record` — son inmutables y no necesitan boilerplate. La mayoría ya lo son correctamente.
- Los DTOs de **entrada** (request) también pueden ser `record` con las anotaciones de validación en el constructor canónico. `ReservaRequest` ya es un buen ejemplo.
- No reutilizar el mismo DTO para crear y actualizar — tienen validaciones distintas.
- Para valores monetarios (`total`, `precio`, `precioNoche`) usar `BigDecimal` en lugar de `double`.

## Mappers

- Cada módulo debería tener su propio mapper en `application/mappers/`, no en `shared/helpers/mappers/`. Los mappers actuales en `shared/` funcionan, pero un cambio en el DTO de `reserva` o `hotel` impacta en `shared`.
- Preferir métodos estáticos si el mapper no necesita inyección de dependencias.

## Excepciones

- Usar siempre las excepciones personalizadas de `shared/helpers/exceptions/`.
- No agregar `try/catch` en servicios para errores esperados — lanzar la excepción correcta y dejar que `GlobalExceptionHandler` la maneje.
- No lanzar `RuntimeException` directamente.

## Transacciones

- `@Transactional` solo en la capa `application/service/`.
- No anotar los controladores con `@Transactional`.

## Caché

- Usar `@Cacheable` en métodos de lectura frecuente y baja variabilidad (listas de departamentos, tipos de habitación).
- Invalidar con `@CacheEvict` en las operaciones de escritura del mismo recurso.
