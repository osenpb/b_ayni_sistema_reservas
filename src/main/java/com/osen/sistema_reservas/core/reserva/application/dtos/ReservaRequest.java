package com.osen.sistema_reservas.core.reserva.application.dtos;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record ReservaRequest(
        @NotNull(message = "La fecha de inicio es requerida")
        @FutureOrPresent(message = "La fecha de inicio debe ser hoy o en el futuro")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin es requerida")
        @Future(message = "La fecha de fin debe ser en el futuro")
        LocalDate fechaFin,

        @NotEmpty(message = "Debe seleccionar al menos una habitación")
        List<Long> habitacionesIds
) {
}
