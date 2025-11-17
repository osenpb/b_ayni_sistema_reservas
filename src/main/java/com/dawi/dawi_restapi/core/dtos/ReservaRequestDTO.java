package com.dawi.dawi_restapi.core.dtos;

import java.time.LocalDate;
import java.util.List;

public record ReservaRequestDTO(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        List<Long> habitacionesIds,
        ClienteDTO cliente
) {
}
