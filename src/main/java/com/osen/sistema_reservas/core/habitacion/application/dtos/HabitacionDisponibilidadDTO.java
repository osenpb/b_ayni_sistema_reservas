package com.osen.sistema_reservas.core.habitacion.application.dtos;

public record HabitacionDisponibilidadDTO(
        boolean disponible,
        int cantidad
) {
}
