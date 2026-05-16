package com.osen.sistema_reservas.core.detalle_reserva.application.dtos;

import java.math.BigDecimal;

public record DetalleReservaResponse(Long id, Long habitacionId, BigDecimal precioNoche) {}
