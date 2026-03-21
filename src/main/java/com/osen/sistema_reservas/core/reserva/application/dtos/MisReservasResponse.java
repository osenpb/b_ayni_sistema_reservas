package com.osen.sistema_reservas.core.reserva.application.dtos;

import java.util.List;

/**
 * DTO para respuesta de búsqueda de reservas por DNI
 */
public record MisReservasResponse(
        String mensaje,
        List<ReservaListResponse> reservas
) {
    public static MisReservasResponse empty(String dni) {
        return new MisReservasResponse(
                "No se encontraron reservas para el DNI: " + dni,
                List.of()
        );
    }

    public static MisReservasResponse of(List<ReservaListResponse> reservas) {
        return new MisReservasResponse(null, reservas);
    }
}
