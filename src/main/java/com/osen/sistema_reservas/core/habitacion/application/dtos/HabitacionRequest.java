package com.osen.sistema_reservas.core.habitacion.application.dtos;

import jakarta.validation.constraints.*;

public record HabitacionRequest(
        @NotBlank(message = "El número de habitación es requerido")
        @Size(max = 10, message = "El número no puede exceder 10 caracteres")
        String numero,

        @NotBlank(message = "El estado es requerido")
        @Pattern(regexp = "^(DISPONIBLE|OCUPADA|MANTENIMIENTO)$", message = "Estado no válido")
        String estado,

        @NotNull(message = "El precio es requerido")
        @Positive(message = "El precio debe ser positivo")
        Double precio,

        @NotNull(message = "El tipo de habitación es requerido")
        @Positive(message = "El ID del tipo de habitación debe ser positivo")
        Long tipoHabitacionId
) {
}
