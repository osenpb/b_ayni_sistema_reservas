# Recomendaciones de Refactorización — Ayni Sistema de Reservas

Observaciones basadas en el código actual. Cada ítem incluye el archivo afectado, el problema y la corrección sugerida.

---

## 1. Reemplazar `String estado` por enums de dominio

**Archivos:** `Reserva.java`, `Habitacion.java`

`estado` se guarda y compara como String literal, lo que permite valores inválidos en tiempo de compilación y genera comparaciones frágiles.

```java
// Problema — en ReservaService.java:171
if (!"PENDIENTE".equals(reserva.getEstado())) { ... }
reserva.setEstado("CONFIRMADA");
```

**Corrección:** Crear enums en el paquete `domain/model/` de cada módulo:

```java
// core/reserva/domain/model/EstadoReserva.java
public enum EstadoReserva { PENDIENTE, CONFIRMADA, CANCELADA }

// core/habitacion/domain/model/EstadoHabitacion.java
public enum EstadoHabitacion { DISPONIBLE, OCUPADA, MANTENIMIENTO }
```

Cambiar el tipo en las entidades y anotar con `@Enumerated(EnumType.STRING)`:

```java
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private EstadoReserva estado;
```

El servicio queda limpio y con seguridad de tipos:
```java
if (reserva.getEstado() != EstadoReserva.PENDIENTE) { ... }
reserva.setEstado(EstadoReserva.CONFIRMADA);
```

---

## 2. Usar `BigDecimal` para valores monetarios

**Archivos:** `Reserva.java` (campo `total`), `Habitacion.java` (campo `precio`), `DetalleReserva.java` (campo `precioNoche`)

`double` acumula errores de punto flotante en sumas de precios. Para importes monetarios el estándar es `BigDecimal`.

```java
// Problema — error de precisión acumulado en ReservaService.java:248-250
double total = habitaciones.stream().mapToDouble(h -> h.getPrecio() * nochesFinales).sum();
```

**Corrección:**
```java
// Entidad
private BigDecimal precio;

// Cálculo
BigDecimal total = habitaciones.stream()
    .map(h -> h.getPrecio().multiply(BigDecimal.valueOf(noches)))
    .reduce(BigDecimal.ZERO, BigDecimal::add);
```

Los DTOs de respuesta también deben actualizar el tipo de `total` y `precioNoche`.

---

## 3. Eliminar validación manual redundante en `ReservaService`

**Archivo:** `ReservaService.java:183-193`

El método `validarReservaRequest` replica exactamente las anotaciones `@NotNull`, `@NotEmpty` que ya están en el record `ReservaRequest`. Si el controlador usa `@Valid`, estas comprobaciones nunca se alcanzan con datos inválidos.

```java
// Redundante — el record ya declara @NotNull y @NotEmpty
private void validarReservaRequest(ReservaRequest dto) {
    if (dto.fechaInicio() == null) { ... }
    if (dto.fechaFin() == null) { ... }
    if (dto.habitacionesIds() == null || dto.habitacionesIds().isEmpty()) { ... }
}
```

**Corrección:** Eliminar `validarReservaRequest` y verificar que el controlador tenga `@Valid` en el parámetro del body.

---

## 4. `ReservaService` no debe inyectar otros servicios de aplicación

**Archivo:** `ReservaService.java:34-38`

```java
// Problema — viola la separación de capas de aplicación
private final HotelService hotelService;
private final HabitacionService habitacionService;
```

Un servicio de aplicación que inyecta otro servicio de aplicación crea acoplamiento entre módulos y dificulta las pruebas. La dependencia debe ser hacia los puertos de salida (`port/out`), no hacia implementaciones de otros módulos.

**Corrección:** Inyectar directamente los repositorios:
```java
private final ReservaRepository reservaRepository;
private final HotelRepository hotelRepository;       // port/out de hotel
private final HabitacionRepository habitacionRepository; // port/out de habitacion
```

Esto también elimina la necesidad de crear interfaces `port/in` solo para satisfacer dependencias internas.

---

## 5. `Hotel.java` — `@Builder` de Lombok sin las anotaciones complementarias

**Archivo:** `Hotel.java`

La clase tiene la anotación `@Builder` de Lombok pero declara manualmente el constructor sin argumentos, el constructor completo y todos los getters/setters. Lombok genera exactamente eso con `@Getter @Setter @NoArgsConstructor @AllArgsConstructor`.

```java
// Estado actual — mezclado: @Builder de Lombok + código manual
@Builder
public class Hotel {
    // constructor vacío manual
    public Hotel() {}
    // constructor completo manual
    public Hotel(Long id, ...) { ... }
    // getters/setters manuales x 6
    public Long getId() { return id; }
    ...
}
```

**Corrección — opción A (Lombok completo):**
```java
@Entity
@Table(name = "hotel", indexes = { ... })
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {
    // Solo campos y métodos de negocio
    public double getPrecioMinimo() { ... }
    public int cantidadHabitaciones() { ... }
}
```

**Corrección — opción B (sin Lombok):** Mantener el código manual y quitar `@Builder`, reemplazándolo con un constructor fluido o factory method si se necesita.

Lo mismo aplica a `Habitacion.java`, que tiene un builder manual de 25 líneas (`HabitacionBuilder`) que puede reemplazarse con `@Builder` de Lombok.

---

## 6. Mover los mappers a sus módulos correspondientes

**Archivos:** `shared/helpers/mappers/ReservaMapper.java`, `HotelMapper.java`, `HabitacionMapper.java`, `DepartamentoMapper.java`, `TipoHabitacionMapper.java`

Los mappers en `shared/` crean dependencias cruzadas: cualquier cambio en un DTO de `reserva` o `hotel` impacta en `shared`. En arquitectura hexagonal, la transformación entidad → DTO pertenece a la capa `application` del propio módulo.

**Corrección:** Mover cada mapper a su módulo:
```
core/reserva/application/mappers/ReservaMapper.java
core/hotel/application/mappers/HotelMapper.java
core/habitacion/application/mappers/HabitacionMapper.java
core/departamento/application/mappers/DepartamentoMapper.java
core/tipoHabitacion/application/mappers/TipoHabitacionMapper.java
```

---

## 7. Lógica de cálculo de noches duplicada

**Archivo:** `ReservaService.java:100, 150, 256`

El cálculo `ChronoUnit.DAYS.between(fechaInicio, fechaFin)` con el guard `if (noches <= 0) noches = 1` aparece tres veces: en `reservarHabitaciones`, `actualizarFechas` y `actualizarHabitacionesReserva`.

**Corrección:** Extraer a un método privado:
```java
private long calcularNoches(LocalDate inicio, LocalDate fin) {
    long noches = ChronoUnit.DAYS.between(inicio, fin);
    return noches > 0 ? noches : 1;
}
```

---

## 8. `fechaReserva` debe asignarse con `@CreationTimestamp`

**Archivo:** `Reserva.java`, `ReservaService.java:104`

```java
// En el servicio — responsabilidad que pertenece a la entidad
reserva.setFechaReserva(LocalDate.now());
```

La fecha de creación de un registro es responsabilidad de la capa de persistencia, no del servicio.

**Corrección:** Anotar el campo en la entidad y quitar la asignación manual del servicio:
```java
@CreationTimestamp
@Column(nullable = false, updatable = false)
private LocalDate fechaReserva;
```

---

## 9. Añadir interfaces `port/in` a todos los módulos `core`

**Afecta a:** todos los módulos en `core/` (reserva, hotel, habitacion, departamento, tipoHabitacion, detalleReserva, dashboard)

Solo `auth` tiene interfaces `port/in`. Sin ellas, los controladores dependen directamente de la clase concreta del servicio, lo que acopla la infraestructura al application y dificulta el reemplazo o las pruebas.

**Corrección:** Crear la interfaz en `domain/port/in/` para cada módulo:
```java
// core/reserva/domain/port/in/ReservaUseCase.java
public interface ReservaUseCase {
    List<ReservaListResponse> listarTodas();
    Reserva buscarPorId(Long id);
    Reserva reservarHabitaciones(Long hotelId, ReservaRequest dto, User user);
    Reserva actualizarFechas(Long id, LocalDate inicio, LocalDate fin);
    Reserva confirmarPago(Long id);
    void eliminar(Long id);
}
```

`ReservaService` implementa `ReservaUseCase` y el controlador inyecta la interfaz:
```java
// ReservaPublicController
public ReservaPublicController(ReservaUseCase reservaUseCase) { ... }
```

---

## 10. `ReservaListResponse` — clases anidadas deben ser records

**Archivo:** `ReservaListResponse.java`

Las clases internas (`HotelSimple`, `UsuarioSimple`, `DetalleSimple`, `DepartamentoSimple`) son proyecciones de solo lectura — candidatas ideales para `record`.

```java
// Corrección
public record ReservaListResponse(
    Long id,
    LocalDate fechaReserva,
    ...
) {
    public record HotelSimple(Long id, String nombre, String direccion, DepartamentoSimple departamento) {}
    public record DepartamentoSimple(Long id, String nombre) {}
    public record UsuarioSimple(Long id, String nombre, String apellido, String email, String telefono, String dni) {}
    public record DetalleSimple(Long id, Long habitacionId, double precioNoche) {}
}
```

---

## 11. Null-safety en `ReservaMapper` con `Optional`

**Archivo:** `ReservaMapper.java:36-40`

```java
// Repetitivo y verbose
String usuarioNombre = user != null ? (user.getNombre() != null ? user.getNombre() : "") : "";
```

**Corrección:**
```java
String usuarioNombre = Optional.ofNullable(user).map(User::getNombre).orElse("");
String usuarioEmail  = Optional.ofNullable(user).map(User::getEmail).orElse("");
```

O mejor: si el `user` siempre existe (viene de autenticación), eliminar el guard `user != null` y lanzar `EntityNotFoundException` antes si fuera nulo.

---

## 12. Eliminar el comentario Javadoc en `ReservaMapper.toListResponseList`

**Archivo:** `ReservaMapper.java:117-120`

```java
/**
 * Convierte una lista de Reservas a lista de ReservaListResponse
 */
public static List<ReservaListResponse> toListResponseList(List<Reserva> reservas) { ... }
```

El nombre del método describe exactamente lo que hace. El Javadoc es redundante y no aporta información sobre por qué existe o restricciones no obvias.

---

## Resumen de prioridades

| # | Estado | Impacto | Esfuerzo | Recomendación                                   |
|---|--------|---------|----------|-------------------------------------------------|
| 1 | ✅ Hecho    | Alto    | Bajo     | Enums `EstadoReserva` y `EstadoHabitacion`       |
| 2 | ✅ Hecho    | Alto    | Medio    | `BigDecimal` para valores monetarios            |
| 3 | ✅ Hecho    | Medio   | Bajo     | Eliminar `validarReservaRequest` redundante     |
| 4 | ✅ Hecho    | Medio   | Medio    | `DashboardService` inyecta repositorios directamente |
| 5 | ✅ Hecho    | Medio   | Bajo     | Lombok unificado en `Hotel` y `Habitacion`      |
| 6 | ✅ Hecho    | Medio   | Medio    | Mappers movidos a sus módulos                   |
| 7 | ✅ Hecho    | Bajo    | Bajo     | Extraer método `calcularNoches`                 |
| 8 | ✅ Hecho    | Bajo    | Bajo     | `@CreationTimestamp` en `fechaReserva`          |
| 9 | ➖ Descartado | Alto   | Medio    | ~~Agregar `port/in` a todos los módulos `core`~~ — decisión de arquitectura: no aplica |
|10 | ✅ Hecho    | Bajo    | Bajo     | Records anidados en `ReservaListResponse`       |
|11 | ✅ Hecho    | Bajo    | Bajo     | Null-safety con `Optional` en mapper            |
|12 | ✅ Hecho    | Bajo    | Muy bajo | Eliminar Javadoc redundante                     |

---

## Cambios realizados

### ✅ Dashboard: aggregaciones movidas a la base de datos (`feat/refactor`)

**Archivos modificados:**
- `core/reserva/domain/port/out/ReservaRepository.java` — 6 queries nuevas
- `core/hotel/domain/port/out/HotelRepository.java` — 1 query nueva
- `core/dashboard/application/service/DashboardService.java` — reescrito completo

**Qué cambió:** El servicio ya no carga todas las entidades en memoria. Cada cálculo es ahora una query con `GROUP BY`, `COUNT`, `SUM` o `LIMIT` ejecutada directamente en MySQL.

**Queries añadidas en `ReservaRepository`:**
- `countGroupByEstado()` — conteo por estado para el gráfico de pie
- `sumIngresosTotales()` — suma de reservas CONFIRMADAS
- `countGroupByMes(desde)` — conteo mensual de los últimos 6 meses
- `sumIngresosGroupByMes(desde)` — ingresos mensuales de los últimos 6 meses
- `findTop5HotelesByReservas()` — top 5 hoteles con `ORDER BY COUNT DESC LIMIT 5`
- `findTop5RecentWithRelations()` — últimas 5 reservas con `JOIN FETCH` de user y hotel

**Query añadida en `HotelRepository`:**
- `countGroupByDepartamento()` — hoteles agrupados por departamento

**El frontend no requiere ningún cambio** — la estructura del JSON de respuesta es idéntica.

---

### ✅ Enums `EstadoReserva` y `EstadoHabitacion` (`feat/refactor`)

**Archivos nuevos:**
- `core/reserva/domain/model/EstadoReserva.java` — PENDIENTE, CONFIRMADA, CANCELADA
- `core/habitacion/domain/model/EstadoHabitacion.java` — DISPONIBLE, OCUPADA, MANTENIMIENTO

**Archivos modificados:** `Reserva.java`, `Habitacion.java`, `ReservaService.java`, `MercadoPagoController.java`, `ReservaAdminUpdateDTO.java`, `HabitacionRequest.java`, `ReservaResponse.java`, `ReservaListResponse.java`, `ReservaRecienteDTO.java`, `HabitacionResponse.java`

**El JSON de respuesta es idéntico** — Jackson serializa `EstadoReserva.CONFIRMADA` como `"CONFIRMADA"`, igual que antes.

---

### ✅ `BigDecimal` para valores monetarios (`feat/refactor`)

**Campos migrados:** `Reserva.total`, `Habitacion.precio`, `DetalleReserva.precioNoche` — todos de `double`/`Double` a `BigDecimal` con `@Column(precision=10, scale=2)`.

**Archivos modificados:** entidades (`Reserva`, `Habitacion`, `DetalleReserva`, `Hotel.getPrecioMinimo()`), DTOs (`ReservaResponse`, `ReservaListResponse`, `ReservaUpdateResponse`, `DetalleReservaResponse`, `DashboardStatsResponse`, `IngresoMensualDTO`, `ReservaRecienteDTO`, `HotelDetalleResponse`, `HabitacionRequest`, `HabitacionResponse`), servicios (`ReservaService`, `DashboardService`), repositorio (`ReservaRepository.sumIngresosTotales`), `MercadoPagoService`.

> ⚠️ **Aviso de migración de base de datos**
>
> Hibernate con `ddl-auto: update` cambiará automáticamente las columnas `DOUBLE` a `DECIMAL(10,2)` al arrancar la aplicación. Esto es seguro en desarrollo, pero **en producción se debe ejecutar un script de migración explícito** (Flyway o Liquibase) antes de desplegar, para tener control sobre cuándo y cómo ocurre el cambio de tipo en cada columna afectada: `reserva.total`, `habitacion.precio`, `detalle_reserva.precio_noche`.
