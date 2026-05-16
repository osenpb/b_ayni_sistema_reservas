package com.osen.sistema_reservas.core.reserva.application.dtos;

import com.osen.sistema_reservas.core.detalle_reserva.application.dtos.DetalleReservaResponse;
import com.osen.sistema_reservas.core.hotel.application.dtos.HotelResponse;
import com.osen.sistema_reservas.core.reserva.domain.model.EstadoReserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReservaResponse(
        Long id,
        LocalDate fechaReserva,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        BigDecimal total,
        EstadoReserva estado,
        HotelResponse hotel,
        Long usuarioId,
        String usuarioNombre,
        String usuarioApellido,
        String usuarioEmail,
        String usuarioDni,
        List<DetalleReservaResponse> detalles
) {}
