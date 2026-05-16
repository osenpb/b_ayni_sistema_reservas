package com.osen.sistema_reservas.core.dashboard.application.dtos;

import java.math.BigDecimal;
import java.util.List;

public record DashboardStatsResponse(
        int totalDepartamentos,
        int totalHoteles,
        long totalHabitaciones,
        int totalReservas,

        List<ReservaEstadoDTO> reservasPorEstado,
        BigDecimal ingresosTotales,

        List<HotelDepartamentoDTO> hotelesPorDepartamento,
        List<ReservaMensualDTO> reservasPorMes,
        List<IngresoMensualDTO> ingresosPorMes,

        List<TopHotelDTO> topHoteles,
        List<ReservaRecienteDTO> reservasRecientes
) {}
