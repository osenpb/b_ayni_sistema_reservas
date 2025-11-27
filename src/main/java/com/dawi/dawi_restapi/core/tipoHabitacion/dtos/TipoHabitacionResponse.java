package com.dawi.dawi_restapi.core.tipoHabitacion.dtos;

public record TipoHabitacionResponse(
        Long id,
        String nombre,
        String descripcion,
        int capacidad
) {
}
