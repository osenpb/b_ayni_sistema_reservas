package com.osen.sistema_reservas.core.reserva.application.dtos;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record ReservaAdminUpdateDTO(
        @NotNull(message = "La fecha de inicio es requerida")
        @FutureOrPresent(message = "La fecha de inicio debe ser hoy o en el futuro")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin es requerida")
        @Future(message = "La fecha de fin debe ser en el futuro")
        LocalDate fechaFin,

        @NotBlank(message = "El estado es requerido")
        @Pattern(regexp = "^(PENDIENTE|CONFIRMADA|CANCELADA|COMPLETADA)$", message = "Estado no válido")
        String estado,

        @NotNull(message = "El hotel es requerido")
        @Positive(message = "El ID del hotel debe ser positivo")
        Long hotelId,

        @NotEmpty(message = "Debe seleccionar al menos una habitación")
        List<Long> habitaciones
) {
}
