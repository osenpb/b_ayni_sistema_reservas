package com.dawi.dawi_restapi.api.publico;

import com.dawi.dawi_restapi.core.cliente.model.Cliente;
import com.dawi.dawi_restapi.core.cliente.service.ClienteService;
import com.dawi.dawi_restapi.core.habitacion.dtos.HabitacionResponse;
import com.dawi.dawi_restapi.core.hotel.dtos.HotelResponse;
import com.dawi.dawi_restapi.core.reserva.dtos.ReservaRequest;
import com.dawi.dawi_restapi.core.reserva.dtos.ReservaResponse;
import com.dawi.dawi_restapi.core.habitacion.models.Habitacion;
import com.dawi.dawi_restapi.core.habitacion.repository.HabitacionRepository;
import com.dawi.dawi_restapi.core.habitacion.service.HabitacionService;
import com.dawi.dawi_restapi.core.departamento.model.Departamento;
import com.dawi.dawi_restapi.core.hotel.model.Hotel;
import com.dawi.dawi_restapi.core.departamento.service.DepartamentoService;
import com.dawi.dawi_restapi.core.hotel.services.HotelService;
import com.dawi.dawi_restapi.core.detalle_reserva.model.DetalleReserva;
import com.dawi.dawi_restapi.core.reserva.models.Reserva;
import com.dawi.dawi_restapi.core.reserva.services.ReservaService;
import com.dawi.dawi_restapi.helpers.mappers.HotelMapper;
import com.dawi.dawi_restapi.helpers.mappers.ReservaMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/reserva")
@RequiredArgsConstructor
public class ReservaController {

    private final DepartamentoService departamentoService;
    private final HotelService hotelService;
    private final ReservaService reservaService;
    private final HabitacionService habitacionService;
    private final HabitacionRepository habitacionRepository;
    private final ClienteService clienteService;

    @GetMapping("/departamentos")
    public ResponseEntity<?> listarDepartamentos(@RequestParam(required = false) String nombre) {
        if (nombre != null && !nombre.isBlank()) {
            return departamentoService.buscarPorNombre(nombre)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.ok(List.of()));
        }
        return ResponseEntity.ok(departamentoService.listar());
    }

    @GetMapping("/hoteles")
    public ResponseEntity<?> verHoteles(@RequestParam Long depId) {
        Optional<Departamento> departamento = departamentoService.buscarPorId(depId);
        List<Hotel> hoteles = hotelService.listarPorDepartamentoId(depId);

        return ResponseEntity.ok(Map.of(
                "departamento", departamento.orElse(null),
                "hoteles", hoteles
        ));
    }

    /**
     * Obtener información de un hotel específico
     */
    @GetMapping("/hoteles/{id}")
    public ResponseEntity<?> infoHotel(@PathVariable Long id) {
        Hotel hotel = hotelService.buscarPorId(id);

        return ResponseEntity.ok(HotelMapper.toDTO(hotel));
    }

    /**
     * Obtener habitaciones disponibles de un hotel para un rango de fechas
     */
    @GetMapping("/hoteles/{id}/habitaciones-disponibles")
    public ResponseEntity<?> habitacionesDisponibles(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        List<HabitacionResponse> habitacionesDisponibles = hotelService.obtenerHabitacionesDisponibles(id,fechaInicio, fechaFin);

            return ResponseEntity.ok(habitacionesDisponibles);
    }

    /**
     * Crear una reserva
     */
    @PostMapping("/hoteles/{id}/reservar")
    public ResponseEntity<?> reservar(
            @PathVariable Long id,
            @RequestBody @Valid ReservaRequest dto) {

        reservaService.reservarHabitaciones(id, dto);

        // x mientras
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensaje", "Reserva creada con éxito"
        ));
    }

    /**
     * Obtener detalles de una reserva (para página de confirmación)
     */
    @GetMapping("/reserva/{id}")
    public ResponseEntity<?> obtenerReserva(@PathVariable Long id) {
        Reserva reserva = reservaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        return ResponseEntity.ok(ReservaMapper.toDTO(reserva));
    }

    /**
     * Buscar reservas por DNI del cliente
     */
    @GetMapping("/mis-reservas")
    public ResponseEntity<?> buscarMisReservas(@RequestParam String dni) {
        List<Reserva> reservas = reservaService.buscarReservasPorDniCliente(dni);

        if (reservas.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "mensaje", "No se encontraron reservas para el DNI: " + dni,
                    "reservas", List.of()
            ));
        }

        List<ReservaResponse> reservasResponse = reservas.stream().map(ReservaMapper::toDTO).toList();

        return ResponseEntity.ok(reservasResponse);
    }
}