package com.dawi.dawi_restapi.core.dtos;

import java.time.LocalDate;
import java.util.List;

public record ReservaAdminUpdateDTO(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String estado,
        Long hotelId,
        ClienteDTO cliente,
        List<Long> habitaciones
) {
}
