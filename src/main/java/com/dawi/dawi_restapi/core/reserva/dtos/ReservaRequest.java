package com.dawi.dawi_restapi.core.reserva.dtos;

import com.dawi.dawi_restapi.core.cliente.dtos.ClienteRequest;

import java.time.LocalDate;
import java.util.List;

public record ReservaRequest(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        ClienteRequest cliente,
        List<Long> habitacionesIds
) {
}
