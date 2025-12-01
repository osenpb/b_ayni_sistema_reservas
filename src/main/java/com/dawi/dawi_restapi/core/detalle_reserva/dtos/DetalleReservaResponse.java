package com.dawi.dawi_restapi.core.detalle_reserva.dtos;

public record DetalleReservaResponse(
        Long id,
        Long habitacionId,
        double precioNoche
) {}