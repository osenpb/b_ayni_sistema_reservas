package com.osen.sistema_reservas.core.dashboard.application.service;

import com.osen.sistema_reservas.core.dashboard.application.dtos.*;
import com.osen.sistema_reservas.core.departamento.domain.port.out.DepartamentoRepository;
import com.osen.sistema_reservas.core.habitacion.domain.port.out.HabitacionRepository;
import com.osen.sistema_reservas.core.hotel.domain.port.out.HotelRepository;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import com.osen.sistema_reservas.core.reserva.domain.port.out.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ReservaRepository reservaRepository;
    private final HotelRepository hotelRepository;
    private final HabitacionRepository habitacionRepository;
    private final DepartamentoRepository departamentoRepository;

    public DashboardService(ReservaRepository reservaRepository,
                            HotelRepository hotelRepository,
                            HabitacionRepository habitacionRepository,
                            DepartamentoRepository departamentoRepository) {
        this.reservaRepository = reservaRepository;
        this.hotelRepository = hotelRepository;
        this.habitacionRepository = habitacionRepository;
        this.departamentoRepository = departamentoRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse obtenerEstadisticas() {
        LocalDate seiseMesesAtras = LocalDate.now().minusMonths(5).withDayOfMonth(1);

        return new DashboardStatsResponse(
                (int) departamentoRepository.count(),
                (int) hotelRepository.count(),
                habitacionRepository.count(),
                (int) reservaRepository.count(),
                calcularReservasPorEstado(),
                Optional.ofNullable(reservaRepository.sumIngresosTotales()).orElse(BigDecimal.ZERO),
                calcularHotelesPorDepartamento(),
                calcularReservasPorMes(seiseMesesAtras),
                calcularIngresosPorMes(seiseMesesAtras),
                calcularTopHoteles(),
                obtenerReservasRecientes()
        );
    }

    private List<ReservaEstadoDTO> calcularReservasPorEstado() {
        Map<String, Long> counts = reservaRepository.countGroupByEstado().stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Number) row[1]).longValue()
                ));

        return List.of(
                new ReservaEstadoDTO("CONFIRMADA", counts.getOrDefault("CONFIRMADA", 0L)),
                new ReservaEstadoDTO("PENDIENTE",  counts.getOrDefault("PENDIENTE",  0L)),
                new ReservaEstadoDTO("CANCELADA",  counts.getOrDefault("CANCELADA",  0L))
        );
    }

    private List<HotelDepartamentoDTO> calcularHotelesPorDepartamento() {
        return hotelRepository.countGroupByDepartamento().stream()
                .map(row -> new HotelDepartamentoDTO((String) row[0], ((Number) row[1]).longValue()))
                .toList();
    }

    private List<ReservaMensualDTO> calcularReservasPorMes(LocalDate desde) {
        Map<YearMonth, Long> counts = reservaRepository.countGroupByMes(desde).stream()
                .collect(Collectors.toMap(
                        row -> YearMonth.of(((Number) row[0]).intValue(), ((Number) row[1]).intValue()),
                        row -> ((Number) row[2]).longValue()
                ));

        List<ReservaMensualDTO> resultado = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth mes = YearMonth.from(LocalDate.now().minusMonths(i));
            resultado.add(new ReservaMensualDTO(formatMonth(mes), counts.getOrDefault(mes, 0L)));
        }
        return resultado;
    }

    private List<IngresoMensualDTO> calcularIngresosPorMes(LocalDate desde) {
        Map<YearMonth, BigDecimal> sums = reservaRepository.sumIngresosGroupByMes(desde).stream()
                .collect(Collectors.toMap(
                        row -> YearMonth.of(((Number) row[0]).intValue(), ((Number) row[1]).intValue()),
                        row -> (BigDecimal) row[2]
                ));

        List<IngresoMensualDTO> resultado = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth mes = YearMonth.from(LocalDate.now().minusMonths(i));
            resultado.add(new IngresoMensualDTO(formatMonth(mes), sums.getOrDefault(mes, BigDecimal.ZERO)));
        }
        return resultado;
    }

    private List<TopHotelDTO> calcularTopHoteles() {
        return reservaRepository.findTop5HotelesByReservas().stream()
                .map(row -> new TopHotelDTO((String) row[0], ((Number) row[1]).longValue()))
                .toList();
    }

    private List<ReservaRecienteDTO> obtenerReservasRecientes() {
        return reservaRepository.findTop5RecentWithRelations().stream()
                .map(this::toReservaRecienteDTO)
                .toList();
    }

    private ReservaRecienteDTO toReservaRecienteDTO(Reserva r) {
        String cliente = r.getUser() != null
                ? (Optional.ofNullable(r.getUser().getNombre()).orElse("") + " " +
                   Optional.ofNullable(r.getUser().getApellido()).orElse("")).strip()
                : "N/A";
        String hotel = r.getHotel() != null ? r.getHotel().getNombre() : "N/A";

        return new ReservaRecienteDTO(
                r.getId(), cliente, hotel,
                r.getFechaInicio(), r.getFechaFin(),
                r.getTotal(), r.getEstado()
        );
    }

    private String formatMonth(YearMonth mes) {
        return mes.getMonth().toString().substring(0, 3) + " " + mes.getYear();
    }
}
