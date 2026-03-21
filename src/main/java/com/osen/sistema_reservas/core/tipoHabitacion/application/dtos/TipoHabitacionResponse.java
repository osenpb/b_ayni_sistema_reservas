package com.osen.sistema_reservas.core.tipoHabitacion.application.dtos;

public record TipoHabitacionResponse(
        Long id,
        String nombre,
        String descripcion,
        int capacidad
) {
}
