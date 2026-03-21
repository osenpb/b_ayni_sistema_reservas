package com.osen.sistema_reservas.core.reserva.application.dtos;

/**
 * DTO para la respuesta cuando se crea una reserva exitosamente.
 */
public record ReservaCreatedResponse(
        String mensaje,
        Long id
) {
    public static ReservaCreatedResponse of(Long id) {
        return new ReservaCreatedResponse("Reserva creada con éxito", id);
    }
}
