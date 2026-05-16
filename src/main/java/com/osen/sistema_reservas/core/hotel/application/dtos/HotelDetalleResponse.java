package com.osen.sistema_reservas.core.hotel.application.dtos;

import com.osen.sistema_reservas.core.departamento.application.dtos.DepartamentoResponse;
import com.osen.sistema_reservas.core.habitacion.application.dtos.HabitacionResponse;

import java.math.BigDecimal;
import java.util.List;

public record HotelDetalleResponse(
        HotelInfo hotel,
        DepartamentoResponse departamento,
        List<HabitacionResponse> habitaciones
) {
    public record HotelInfo(
            Long id,
            String nombre,
            String direccion,
            BigDecimal precioMinimo,
            int cantidadHabitaciones
    ) {}
}
