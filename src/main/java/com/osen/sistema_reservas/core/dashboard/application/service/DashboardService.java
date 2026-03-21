package com.osen.sistema_reservas.core.dashboard.application.service;

import com.osen.sistema_reservas.core.departamento.domain.model.Departamento;
import com.osen.sistema_reservas.core.departamento.application.service.DepartamentoService;
import com.osen.sistema_reservas.core.habitacion.application.service.HabitacionService;
import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import com.osen.sistema_reservas.core.hotel.application.service.HotelService;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import com.osen.sistema_reservas.core.reserva.application.service.ReservaService;
import com.osen.sistema_reservas.shared.helpers.dtos.DashboardStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    private Map<String, Long> calcularReservasPorEstado(List<Reserva> reservas) {
        Map<String, Long> resultado = new HashMap<>();
        resultado.put("CONFIRMADA", reservas.stream()
                .filter(r -> "CONFIRMADA".equals(r.getEstado()))
                .count());
        resultado.put("CANCELADA", reservas.stream()
                .filter(r -> "CANCELADA".equals(r.getEstado()))
                .count());
        return resultado;
    }

    private double calcularIngresosTotales(List<Reserva> reservas) {
        return reservas.stream()
                .filter(r -> "CONFIRMADA".equals(r.getEstado()))
                .mapToDouble(Reserva::getTotal)
                .sum();
    }

    private Map<String, Long> calcularHotelesPorDepartamento(
            List<Departamento> departamentos, List<Hotel> hoteles) {

        Map<String, Long> resultado = new LinkedHashMap<>();
        for (Departamento dep : departamentos) {
            long count = hoteles.stream()
                    .filter(h -> h.getDepartamento() != null &&
                            h.getDepartamento().getId().equals(dep.getId()))
                    .count();
            resultado.put(dep.getNombre(), count);
        }
        return resultado;
    }

    private Map<String, Long> calcularReservasPorMes(List<Reserva> reservas) {
        Map<String, Long> resultado = new LinkedHashMap<>();
        LocalDate ahora = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth mes = YearMonth.from(ahora.minusMonths(i));
            String nombreMes = mes.getMonth().toString().substring(0, 3) + " " + mes.getYear();

            long count = reservas.stream()
                    .filter(r -> r.getFechaReserva() != null)
                    .filter(r -> YearMonth.from(r.getFechaReserva()).equals(mes))
                    .count();

            resultado.put(nombreMes, count);
        }
        return resultado;
    }

    private Map<String, Double> calcularIngresosPorMes(List<Reserva> reservas) {
        Map<String, Double> resultado = new LinkedHashMap<>();
        LocalDate ahora = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth mes = YearMonth.from(ahora.minusMonths(i));
            String nombreMes = mes.getMonth().toString().substring(0, 3) + " " + mes.getYear();

            double total = reservas.stream()
                    .filter(r -> "CONFIRMADA".equals(r.getEstado()))
                    .filter(r -> r.getFechaReserva() != null)
                    .filter(r -> YearMonth.from(r.getFechaReserva()).equals(mes))
                    .mapToDouble(Reserva::getTotal)
                    .sum();

            resultado.put(nombreMes, total);
        }
        return resultado;
    }

    private List<DashboardStatsResponse.TopHotel> calcularTopHoteles(List<Reserva> reservas) {
        Map<String, Long> reservasPorHotel = reservas.stream()
                .filter(r -> r.getHotel() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getHotel().getNombre(),
                        Collectors.counting()
                ));

        return reservasPorHotel.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new DashboardStatsResponse.TopHotel(e.getKey(), e.getValue()))
                .toList();
    }

    private List<DashboardStatsResponse.ReservaReciente> obtenerReservasRecientes(List<Reserva> reservas) {
        return reservas.stream()
                .sorted(Comparator.comparing(
                        Reserva::getFechaReserva,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .limit(5)
                .map(r -> new DashboardStatsResponse.ReservaReciente(
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