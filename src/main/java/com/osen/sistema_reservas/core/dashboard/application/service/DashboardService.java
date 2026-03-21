package com.osen.sistema_reservas.core.dashboard.application.service;

import com.osen.sistema_reservas.core.departamento.domain.model.Departamento;
import com.osen.sistema_reservas.core.departamento.application.service.DepartamentoService;
import com.osen.sistema_reservas.core.habitacion.application.service.HabitacionService;
import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import com.osen.sistema_reservas.core.hotel.application.service.HotelService;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import com.osen.sistema_reservas.core.reserva.application.service.ReservaService;
import com.osen.sistema_reservas.core.dashboard.application.dtos.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final DepartamentoService departamentoService;
    private final HotelService hotelService;
    private final HabitacionService habitacionService;
    private final ReservaService reservaService;

    public DashboardService(DepartamentoService departamentoService, HotelService hotelService, HabitacionService habitacionService, ReservaService reservaService) {
        this.departamentoService = departamentoService;
        this.hotelService = hotelService;
        this.habitacionService = habitacionService;
        this.reservaService = reservaService;
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse obtenerEstadisticas() {
        List<Departamento> departamentos = departamentoService.listar();
        List<Hotel> hoteles = hotelService.listarTodos();
        List<Reserva> reservas = reservaService.listarTodas();

        return new DashboardStatsResponse(
                departamentos.size(),
                hoteles.size(),
                habitacionService.contarTodas(),
                reservas.size(),
                calcularReservasPorEstado(reservas),
                calcularIngresosTotales(reservas),
                calcularHotelesPorDepartamento(departamentos, hoteles),
                calcularReservasPorMes(reservas),
                calcularIngresosPorMes(reservas),
                calcularTopHoteles(reservas),
                obtenerReservasRecientes(reservas)
        );
    }

    private List<ReservaEstadoDTO> calcularReservasPorEstado(List<Reserva> reservas) {
        Map<String, Long> counts = reservas.stream()
                .collect(Collectors.groupingBy(Reserva::getEstado, Collectors.counting()));

        return List.of(
                new ReservaEstadoDTO("CONFIRMADA", counts.getOrDefault("CONFIRMADA", 0L)),
                new ReservaEstadoDTO("PENDIENTE", counts.getOrDefault("PENDIENTE", 0L)),
                new ReservaEstadoDTO("CANCELADA", counts.getOrDefault("CANCELADA", 0L))
        );
    }

    private double calcularIngresosTotales(List<Reserva> reservas) {
        return reservas.stream()
                .filter(r -> "CONFIRMADA".equals(r.getEstado()))
                .mapToDouble(Reserva::getTotal)
                .sum();
    }

    private List<HotelDepartamentoDTO> calcularHotelesPorDepartamento(
            List<Departamento> departamentos, List<Hotel> hoteles) {

        Map<Long, Long> countByDepId = hoteles.stream()
                .filter(h -> h.getDepartamento() != null)
                .collect(Collectors.groupingBy(
                        h -> h.getDepartamento().getId(),
                        Collectors.counting()
                ));

        return departamentos.stream()
                .map(dep -> new HotelDepartamentoDTO(
                        dep.getNombre(),
                        countByDepId.getOrDefault(dep.getId(), 0L)))
                .toList();
    }

    private List<ReservaMensualDTO> calcularReservasPorMes(List<Reserva> reservas) {
        Map<YearMonth, Long> countByMonth = reservas.stream()
                .filter(r -> r.getFechaReserva() != null)
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getFechaReserva()),
                        Collectors.counting()
                ));

        List<ReservaMensualDTO> resultado = new ArrayList<>();
        LocalDate ahora = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth mes = YearMonth.from(ahora.minusMonths(i));
            resultado.add(new ReservaMensualDTO(
                    formatMonth(mes),
                    countByMonth.getOrDefault(mes, 0L)));
        }
        return resultado;
    }

    private List<IngresoMensualDTO> calcularIngresosPorMes(List<Reserva> reservas) {
        Map<YearMonth, Double> sumByMonth = reservas.stream()
                .filter(r -> "CONFIRMADA".equals(r.getEstado()))
                .filter(r -> r.getFechaReserva() != null)
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getFechaReserva()),
                        Collectors.summingDouble(Reserva::getTotal)
                ));

        List<IngresoMensualDTO> resultado = new ArrayList<>();
        LocalDate ahora = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth mes = YearMonth.from(ahora.minusMonths(i));
            resultado.add(new IngresoMensualDTO(
                    formatMonth(mes),
                    sumByMonth.getOrDefault(mes, 0.0)));
        }
        return resultado;
    }

    private String formatMonth(YearMonth mes) {
        return mes.getMonth().toString().substring(0, 3) + " " + mes.getYear();
    }

    private List<TopHotelDTO> calcularTopHoteles(List<Reserva> reservas) {
        return reservas.stream()
                .filter(r -> r.getHotel() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getHotel().getNombre(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new TopHotelDTO(e.getKey(), e.getValue()))
                .toList();
    }

    private List<ReservaRecienteDTO> obtenerReservasRecientes(List<Reserva> reservas) {
        return reservas.stream()
                .sorted(Comparator.comparing(
                        Reserva::getFechaReserva,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .limit(5)
                .map(r -> new ReservaRecienteDTO(
                        r.getId(),
                        r.getCliente() != null ?
                                r.getCliente().getNombre() + " " + r.getCliente().getApellido() : "N/A",
                        r.getHotel() != null ? r.getHotel().getNombre() : "N/A",
                        r.getFechaInicio(),
                        r.getFechaFin(),
                        r.getTotal(),
                        r.getEstado()
                ))
                .toList();
    }
}
