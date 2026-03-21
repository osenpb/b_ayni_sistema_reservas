package com.osen.sistema_reservas.core.hotel.application.dtos;

import com.osen.sistema_reservas.core.departamento.application.dtos.DepartamentoResponse;

import java.util.List;

/**
 * DTO para respuesta de hoteles con su departamento
 */
public record HotelesConDepartamentoResponse(
        DepartamentoResponse departamento,
        List<HotelResponse> hoteles
) {}
