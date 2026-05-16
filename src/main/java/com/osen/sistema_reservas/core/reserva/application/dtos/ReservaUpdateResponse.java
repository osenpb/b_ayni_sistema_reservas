package com.osen.sistema_reservas.core.reserva.application.dtos;

import java.math.BigDecimal;

public record ReservaUpdateResponse(String message, BigDecimal nuevoTotal) {
    public static ReservaUpdateResponse of(BigDecimal nuevoTotal) {
        return new ReservaUpdateResponse("Reserva actualizada exitosamente", nuevoTotal);
    }
}
