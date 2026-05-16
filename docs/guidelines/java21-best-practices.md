# Mejores Prácticas — Java 21 en Spring Boot 3

## Records para DTOs

Java 21 incluye `record` como tipo estable. Reemplaza clases DTO que usan `@Data` de Lombok.

**Antes (con Lombok):**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartamentoResponse {
    private Long id;
    private String nombre;
}
```

**Después (record):**
```java
public record DepartamentoResponse(Long id, String nombre) {}
```

Los records son ideales para DTOs de salida porque:
- Son inmutables por diseño.
- El compilador genera `equals`, `hashCode`, `toString` y accessors automáticamente.
- Son más legibles y requieren cero boilerplate.
- Se integran perfectamente con Jackson (Spring Boot 3 los serializa/deserializa de forma nativa).

Los DTOs de entrada (request) también pueden ser records si se agrega `@JsonDeserialize` y las anotaciones de validación van en los parámetros del constructor canónico:

```java
public record ReservaRequest(
    @NotNull @FutureOrPresent LocalDate fechaInicio,
    @NotNull @Future LocalDate fechaFin,
    @NotEmpty List<Long> habitacionesIds
) {}
```

---

## Sealed Classes para jerarquías de dominio

Cuando el dominio tiene un conjunto fijo de variantes (ej. estados de pago, resultado de operaciones), usar `sealed interface` + `record`:

```java
public sealed interface ResultadoPago
    permits ResultadoPago.Exitoso, ResultadoPago.Fallido, ResultadoPago.Pendiente {

    record Exitoso(String transaccionId, BigDecimal monto) implements ResultadoPago {}
    record Fallido(String codigo, String mensaje) implements ResultadoPago {}
    record Pendiente(String referencia) implements ResultadoPago {}
}
```

Esto permite usar `switch` exhaustivo sin `default`:
```java
String mensaje = switch (resultado) {
    case ResultadoPago.Exitoso e -> "Pago confirmado: " + e.transaccionId();
    case ResultadoPago.Fallido f -> "Error: " + f.mensaje();
    case ResultadoPago.Pendiente p -> "Pendiente: " + p.referencia();
};
```

---

## Pattern Matching

### `instanceof` con pattern matching
```java
// Antes
if (exception instanceof BusinessException) {
    BusinessException be = (BusinessException) exception;
    return be.getErrorCode();
}

// Java 21
if (exception instanceof BusinessException be) {
    return be.getErrorCode();
}
```

### `switch` con pattern matching (Java 21)
```java
String describir(Object estado) {
    return switch (estado) {
        case Integer i -> "Número: " + i;
        case String s when s.isBlank() -> "Cadena vacía";
        case String s -> "Texto: " + s;
        case null -> "Sin valor";
        default -> "Desconocido";
    };
}
```

---

## Text Blocks para consultas y mensajes

Usar text blocks (`"""`) en lugar de concatenación para JPQL, SQL nativo, JSON de pruebas o mensajes largos:

```java
@Query("""
    SELECT r FROM Reserva r
    JOIN FETCH r.detalles d
    JOIN FETCH d.habitacion h
    WHERE r.user.id = :userId
      AND r.estado = :estado
    ORDER BY r.fechaCreacion DESC
    """)
List<Reserva> findByUserAndEstado(@Param("userId") Long userId,
                                   @Param("estado") EstadoReserva estado);
```

---

## Virtual Threads (Project Loom)

Spring Boot 3.2+ soporta virtual threads. Habilitarlos en `application.yml`:

```yaml
spring:
  threads:
    virtual:
      enabled: true
```

Beneficios para este proyecto:
- Las llamadas bloqueantes a Mercado Pago SDK se ejecutan en virtual threads sin consumir hilos del platform pool.
- Las queries a MySQL que esperan I/O no bloquean hilos reales.
- No requiere cambios en el código de negocio.

---

## Optional correctamente

```java
// Incorrecto — lanza excepción genérica o nula
User user = userRepository.findById(id).get();

// Correcto — excepción tipada del dominio
User user = userRepository.findById(id)
    .orElseThrow(() -> new EntityNotFoundException("Usuario", id));

// Correcto — valor por defecto
String nombre = Optional.ofNullable(request.getNombre()).orElse("Sin nombre");
```

---

## Validación de beans (Jakarta EE 10)

En Spring Boot 3 las anotaciones migran de `javax.validation` a `jakarta.validation`. Asegurarse de importar siempre desde `jakarta.*`:

```java
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
```

Anotaciones útiles para este dominio:
- `@FutureOrPresent` — fechaInicio de reserva
- `@Future` — fechaFin de reserva
- `@Positive` — precios, números de habitación
- `@Email` — campo email en registros
- `@Pattern(regexp = "\\d{8}")` — DNI peruano

---

## Inmutabilidad en entidades

Para campos que no deben cambiar después de la creación (ej. `fechaCreacion`, `userId`), usar el atributo `updatable = false` en `@Column`:

```java
@Column(nullable = false, updatable = false)
private LocalDateTime fechaCreacion;
```

---

## Constantes de dominio en lugar de Strings mágicos

Reemplazar literales string repetidos por constantes o enums:

```java
// Incorrecto
if (reserva.getEstado().equals("CONFIRMADA")) { ... }

// Correcto (ya se usa EstadoReserva enum — mantener este patrón)
if (reserva.getEstado() == EstadoReserva.CONFIRMADA) { ... }
```

---

## @Transactional(readOnly = true)

Marcar explícitamente los métodos de solo lectura:

```java
@Transactional(readOnly = true)
public List<HotelResponse> listarTodos() {
    return hotelRepository.findAll().stream()
        .map(hotelMapper::toResponse)
        .toList(); // Java 16+ — preferir sobre collect(Collectors.toList())
}
```

`Stream.toList()` devuelve una lista inmutable, lo cual es correcto para respuestas de API.

---

## Logging estructurado

Usar `@Slf4j` de Lombok y mensajes con parámetros en lugar de concatenación:

```java
@Slf4j
public class ReservaService {

    // Incorrecto — siempre evalúa el String
    log.debug("Creando reserva para usuario: " + userId);

    // Correcto — el String solo se evalúa si DEBUG está activo
    log.debug("Creando reserva para usuario: {}", userId);
    log.error("Error al procesar pago [reservaId={}]: {}", reservaId, e.getMessage(), e);
}
```

---

## Colecciones inmutables

```java
// Preferir List.of() / Map.of() para colecciones que no se modifican
private static final List<String> ESTADOS_FINALES = List.of("CONFIRMADA", "CANCELADA");

// En retornos de métodos de lectura
return Collections.unmodifiableList(resultado);
```
