package com.osen.sistema_reservas.core.reserva.application.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DTO para solicitud de actualización de fechas de reserva
 */
public record ReservaUpdateRequest(
        @NotNull(message = "La fecha de inicio es requerida")
        @FutureOrPresent(message = "La fecha de inicio debe ser hoy o en el futuro")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin es requerida")
        @Future(message = "La fecha de fin debe ser en el futuro")
        LocalDate fechaFin
) {}
