package com.dawi.dawi_restapi.core.hotel.dtos;

import com.dawi.dawi_restapi.core.habitacion.dtos.HabitacionResponse;
import com.dawi.dawi_restapi.core.departamento.model.Departamento;

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
