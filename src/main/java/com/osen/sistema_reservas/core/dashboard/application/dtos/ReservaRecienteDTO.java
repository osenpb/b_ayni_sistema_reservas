package com.osen.sistema_reservas.core.dashboard.application.dtos;

import java.time.LocalDate;

public record ReservaRecienteDTO(
        Long id,
        String cliente,
        String hotel,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        double total,
        String estado
) {}
