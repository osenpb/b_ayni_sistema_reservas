package com.osen.sistema_reservas.core.dashboard.application.dtos;

import com.osen.sistema_reservas.core.reserva.domain.model.EstadoReserva;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservaRecienteDTO(
        Long id,
        String cliente,
        String hotel,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        BigDecimal total,
        EstadoReserva estado
) {}
