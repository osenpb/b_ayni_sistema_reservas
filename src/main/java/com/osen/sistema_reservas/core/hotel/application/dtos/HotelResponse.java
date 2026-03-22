package com.osen.sistema_reservas.core.hotel.application.dtos;

import com.osen.sistema_reservas.core.habitacion.application.dtos.HabitacionResponse;
import com.osen.sistema_reservas.core.departamento.application.dtos.DepartamentoResponse;

import java.util.List;

public record HotelResponse(
        Long id,
        String nombre,
        String direccion,
        DepartamentoResponse departamento,
        List<HabitacionResponse> habitaciones,
        String imagenUrl
) {
}
