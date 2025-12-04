package com.dawi.dawi_restapi.api.admin;

import com.dawi.dawi_restapi.core.departamento.service.DepartamentoService;
import com.dawi.dawi_restapi.core.hotel.services.HotelService;
import com.dawi.dawi_restapi.core.habitacion.service.HabitacionService;
import com.dawi.dawi_restapi.core.reserva.services.ReservaService;
import com.dawi.dawi_restapi.core.reserva.models.Reserva;
import com.dawi.dawi_restapi.core.hotel.model.Hotel;
import com.dawi.dawi_restapi.core.departamento.model.Departamento;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DepartamentoService departamentoService;
    private final HotelService hotelService;
    private final HabitacionService habitacionService;
    private final ReservaService reservaService;

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // Contadores generales
        List<Departamento> departamentos = departamentoService.listar();
        List<Hotel> hoteles = hotelService.listarTodos();
        List<Reserva> reservas = reservaService.listarTodas();

        stats.put("totalDepartamentos", departamentos.size());
        stats.put("totalHoteles", hoteles.size());
        stats.put("totalHabitaciones", habitacionService.contarTodas());
        stats.put("totalReservas", reservas.size());

        // Reservas por estado
        long confirmadas = reservas.stream().filter(r -> "CONFIRMADA".equals(r.getEstado())).count();
        long canceladas = reservas.stream().filter(r -> "CANCELADA".equals(r.getEstado())).count();

        Map<String, Long> reservasPorEstado = new HashMap<>();
        reservasPorEstado.put("CONFIRMADA", confirmadas);
        reservasPorEstado.put("CANCELADA", canceladas);
        stats.put("reservasPorEstado", reservasPorEstado);

        // Ingresos totales (solo reservas confirmadas)
        double ingresosTotales = reservas.stream()
                .filter(r -> "CONFIRMADA".equals(r.getEstado()))
                .mapToDouble(Reserva::getTotal)
                .sum();
        stats.put("ingresosTotales", ingresosTotales);

        // Hoteles por departamento
        Map<String, Long> hotelesPorDepartamento = new LinkedHashMap<>();
        for (Departamento dep : departamentos) {
            long count = hoteles.stream()
                    .filter(h -> h.getDepartamento() != null && h.getDepartamento().getId().equals(dep.getId()))
                    .count();
            hotelesPorDepartamento.put(dep.getNombre(), count);
        }
        stats.put("hotelesPorDepartamento", hotelesPorDepartamento);

        // Reservas por mes (últimos 6 meses)
        Map<String, Long> reservasPorMes = new LinkedHashMap<>();
        LocalDate ahora = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth mes = YearMonth.from(ahora.minusMonths(i));
            String nombreMes = mes.getMonth().toString().substring(0, 3) + " " + mes.getYear();

            long count = reservas.stream()
                    .filter(r -> {
                        if (r.getFechaReserva() == null) return false;
                        YearMonth mesReserva = YearMonth.from(r.getFechaReserva());
                        return mesReserva.equals(mes);
                    })
                    .count();
            reservasPorMes.put(nombreMes, count);
        }
        stats.put("reservasPorMes", reservasPorMes);

        // Ingresos por mes (últimos 6 meses)
        Map<String, Double> ingresosPorMes = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth mes = YearMonth.from(ahora.minusMonths(i));
            String nombreMes = mes.getMonth().toString().substring(0, 3) + " " + mes.getYear();

            double total = reservas.stream()
                    .filter(r -> "CONFIRMADA".equals(r.getEstado()))
                    .filter(r -> {
                        if (r.getFechaReserva() == null) return false;
                        YearMonth mesReserva = YearMonth.from(r.getFechaReserva());
                        return mesReserva.equals(mes);
                    })
                    .mapToDouble(Reserva::getTotal)
                    .sum();
            ingresosPorMes.put(nombreMes, total);
        }
        stats.put("ingresosPorMes", ingresosPorMes);

        // Top 5 hoteles con más reservas
        Map<String, Long> reservasPorHotel = reservas.stream()
                .filter(r -> r.getHotel() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getHotel().getNombre(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> topHoteles = reservasPorHotel.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("nombre", e.getKey());
                    item.put("reservas", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
        stats.put("topHoteles", topHoteles);

        // Reservas recientes (últimas 5)
        List<Map<String, Object>> reservasRecientes = reservas.stream()
                .sorted(Comparator.comparing(Reserva::getFechaReserva, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(r -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", r.getId());
                    item.put("cliente", r.getCliente() != null ?
                            r.getCliente().getNombre() + " " + r.getCliente().getApellido() : "N/A");
                    item.put("hotel", r.getHotel() != null ? r.getHotel().getNombre() : "N/A");
                    item.put("fechaInicio", r.getFechaInicio());
                    item.put("fechaFin", r.getFechaFin());
                    item.put("total", r.getTotal());
                    item.put("estado", r.getEstado());
                    return item;
                })
                .collect(Collectors.toList());
        stats.put("reservasRecientes", reservasRecientes);

        return ResponseEntity.ok(stats);
    }
}