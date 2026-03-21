package com.osen.sistema_reservas.core.habitacion.application.dtos;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para respuesta de habitaciones disponibles
 */
public record HabitacionesDisponiblesResponse(
        Long hotelId,
        String hotelNombre,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        List<HabitacionResponse> habitacionesDisponibles,
        int cantidad
) {}
