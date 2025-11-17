package com.dawi.dawi_restapi.core.dtos;

import java.time.LocalDate;
import java.util.List;

public record ReservaResponseDTO(
     Long id,
     LocalDate fechaInicio,
     LocalDate fechaFin,
     double total,
     String estado,
     List<String> habitaciones
) {
}
