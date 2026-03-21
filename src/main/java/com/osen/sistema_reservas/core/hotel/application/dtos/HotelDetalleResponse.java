package com.osen.sistema_reservas.core.hotel.application.dtos;

import com.osen.sistema_reservas.core.departamento.application.dtos.DepartamentoResponse;
import com.osen.sistema_reservas.core.habitacion.application.dtos.HabitacionResponse;

import java.util.List;

/**
 * DTO para respuesta con detalle completo de un hotel
 */
public record HotelDetalleResponse(
        HotelInfo hotel,
        DepartamentoResponse departamento,
        List<HabitacionResponse> habitaciones
) {
    /**
     * Información básica del hotel
     */
    public record HotelInfo(
            Long id,
            String nombre,
            String direccion,
            double precioMinimo,
            int cantidadHabitaciones
    ) {}
}
