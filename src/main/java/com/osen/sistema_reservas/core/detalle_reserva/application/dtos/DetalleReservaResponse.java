package com.osen.sistema_reservas.core.detalle_reserva.application.dtos;

public record DetalleReservaResponse(
        Long id,
        Long habitacionId,
        double precioNoche
) {}