package com.osen.sistema_reservas.core.hotel.application.dtos;

import com.osen.sistema_reservas.core.habitacion.application.dtos.HabitacionResponse;
import com.osen.sistema_reservas.core.departamento.domain.model.Departamento;

import java.util.List;

public record HotelResponse(
        // este podria ser HotelResponse, pero ya seria refactorizar el front tmb
        Long id,
        String nombre,
        String direccion,
        Departamento departamento,
        List<HabitacionResponse> habitaciones,
        String imagenUrl
) {
}
