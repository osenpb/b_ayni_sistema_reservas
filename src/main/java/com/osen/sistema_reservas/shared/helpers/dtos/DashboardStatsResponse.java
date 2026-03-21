package com.osen.sistema_reservas.shared.helpers.dtos;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO para respuesta de estadísticas del dashboard
 */
public record DashboardStatsResponse(
        // Contadores generales
        int totalDepartamentos,
        int totalHoteles,
        long totalHabitaciones,
        int totalReservas,

        Map<String, Long> reservasPorEstado,

        double ingresosTotales,

        Map<String, Long> hotelesPorDepartamento,
        Map<String, Long> reservasPorMes,
        Map<String, Double> ingresosPorMes,

        List<TopHotel> topHoteles,
        List<ReservaReciente> reservasRecientes
) {
    public record TopHotel(
            String nombre,
            long reservas
    ) {}

    public record ReservaReciente(
            Long id,
            String cliente,
            String hotel,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            double total,
            String estado
    ) {}
}
