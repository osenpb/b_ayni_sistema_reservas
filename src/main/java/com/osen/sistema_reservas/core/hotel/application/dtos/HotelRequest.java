package com.osen.sistema_reservas.core.hotel.application.dtos;

import com.osen.sistema_reservas.core.habitacion.application.dtos.HabitacionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record HotelRequest(
        @NotBlank(message = "El nombre del hotel es requerido")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,

        @NotBlank(message = "La dirección es requerida")
        @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
        String direccion,

        @NotNull(message = "El departamento es requerido")
        @Positive(message = "El ID del departamento debe ser positivo")
        Long departamentoId,

        @NotEmpty(message = "Debe haber al menos una habitación")
        @Valid
        List<HabitacionRequest> habitaciones,

        @Size(max = 500, message = "La URL de imagen no puede exceder 500 caracteres")
        String imagenUrl
) {
}
