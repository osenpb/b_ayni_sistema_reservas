package com.osen.sistema_reservas.core.habitacion.application.dtos;

import com.osen.sistema_reservas.core.habitacion.domain.model.EstadoHabitacion;
import com.osen.sistema_reservas.core.tipoHabitacion.domain.model.TipoHabitacion;

import java.math.BigDecimal;

public record HabitacionResponse(
        Long id,
        String numero,
        EstadoHabitacion estado,
        BigDecimal precio,
        TipoHabitacion tipoHabitacion,
        Long hotelId
) {}
