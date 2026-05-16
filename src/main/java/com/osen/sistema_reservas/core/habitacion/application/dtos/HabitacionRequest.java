package com.osen.sistema_reservas.core.habitacion.application.dtos;

import com.osen.sistema_reservas.core.habitacion.domain.model.EstadoHabitacion;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record HabitacionRequest(
        @NotBlank(message = "El número de habitación es requerido")
        @Size(max = 10, message = "El número no puede exceder 10 caracteres")
        String numero,

        @NotNull(message = "El estado es requerido")
        EstadoHabitacion estado,

        @NotNull(message = "El precio es requerido")
        @Positive(message = "El precio debe ser positivo")
        BigDecimal precio,

        @NotNull(message = "El tipo de habitación es requerido")
        @Positive(message = "El ID del tipo de habitación debe ser positivo")
        Long tipoHabitacionId
) {}
