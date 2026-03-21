package com.osen.sistema_reservas.api.publico;

import com.osen.sistema_reservas.auth.domain.model.User;
import com.osen.sistema_reservas.core.cliente.application.service.ClienteService;
import com.osen.sistema_reservas.core.departamento.application.dtos.DepartamentoResponse;
import com.osen.sistema_reservas.core.departamento.domain.model.Departamento;
import com.osen.sistema_reservas.core.departamento.application.service.DepartamentoService;
import com.osen.sistema_reservas.core.habitacion.application.dtos.HabitacionResponse;
import com.osen.sistema_reservas.core.habitacion.application.dtos.HabitacionesDisponiblesResponse;
import com.osen.sistema_reservas.core.hotel.application.dtos.HotelDetalleResponse;
import com.osen.sistema_reservas.core.hotel.application.dtos.HotelResponse;
import com.osen.sistema_reservas.core.hotel.application.dtos.HotelesConDepartamentoResponse;
import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import com.osen.sistema_reservas.core.hotel.application.service.HotelService;
import com.osen.sistema_reservas.core.reserva.application.dtos.*;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import com.osen.sistema_reservas.core.reserva.application.service.ReservaService;
import com.osen.sistema_reservas.shared.helpers.dtos.MessageResponse;
import com.osen.sistema_reservas.shared.helpers.exceptions.ForbiddenException;
import com.osen.sistema_reservas.shared.helpers.mappers.DepartamentoMapper;
import com.osen.sistema_reservas.shared.helpers.mappers.HotelMapper;
import com.osen.sistema_reservas.shared.helpers.mappers.ReservaMapper;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/public/reserva")
public class ReservaController {

    private final DepartamentoService departamentoService;
    private final HotelService hotelService;
    private final ReservaService reservaService;
    private final ClienteService clienteService;

    public ReservaController(DepartamentoService departamentoService, HotelService hotelService,
                            ReservaService reservaService, ClienteService clienteService) {
        this.departamentoService = departamentoService;
        this.hotelService = hotelService;
        this.reservaService = reservaService;
        this.clienteService = clienteService;
    }

    // ==================== DEPARTAMENTOS ====================

    @GetMapping("/departamentos")
    public ResponseEntity<List<DepartamentoResponse>> listarDepartamentos(
            @RequestParam(required = false) String nombre) {

        if (nombre != null && !nombre.isBlank()) {
            return departamentoService.buscarPorNombreResponse(nombre)
                    .map(dep -> ResponseEntity.ok(List.of(dep)))
                    .orElseGet(() -> ResponseEntity.ok(List.of()));
        }

        List<DepartamentoResponse> response = departamentoService.listarResponse();
        return ResponseEntity.ok(response);
    }

    // ==================== HOTELES ====================

    @GetMapping("/hoteles")
    public ResponseEntity<HotelesConDepartamentoResponse> verHoteles(@RequestParam Long depId) {
        Departamento departamento = departamentoService.buscarPorId(depId);
        List<HotelResponse> hoteles = hotelService.listarPorDepartamentoId(depId);

        HotelesConDepartamentoResponse response = new HotelesConDepartamentoResponse(
                DepartamentoMapper.toDTO(departamento),
                hoteles
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/hoteles/{id}")
    public ResponseEntity<HotelDetalleResponse> infoHotel(@PathVariable Long id) {
        Hotel hotel = hotelService.buscarPorId(id);
        HotelResponse hotelDTO = HotelMapper.toDTO(hotel);

        HotelDetalleResponse.HotelInfo hotelInfo = new HotelDetalleResponse.HotelInfo(
                hotel.getId(),
                hotel.getNombre(),
                hotel.getDireccion() != null ? hotel.getDireccion() : "",
                hotel.getPrecioMinimo(),
                hotel.cantidadHabitaciones()
        );

        DepartamentoResponse departamentoResponse = DepartamentoMapper.toDTO(hotel.getDepartamento());

        HotelDetalleResponse response = new HotelDetalleResponse(
                hotelInfo,
                departamentoResponse,
                hotelDTO.habitaciones() != null ? hotelDTO.habitaciones() : List.of()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/hoteles/{id}/habitaciones-disponibles")
    public ResponseEntity<HabitacionesDisponiblesResponse> habitacionesDisponibles(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        Hotel hotel = hotelService.buscarPorId(id);
        List<HabitacionResponse> habitaciones = hotelService.obtenerHabitacionesDisponibles(id, fechaInicio, fechaFin);

        HabitacionesDisponiblesResponse response = new HabitacionesDisponiblesResponse(
                id,
                hotel.getNombre(),
                fechaInicio,
                fechaFin,
                habitaciones,
                habitaciones.size()
        );

        return ResponseEntity.ok(response);
    }

    // ==================== RESERVAS ====================

    @PostMapping("/hoteles/{id}/reservar")
    public ResponseEntity<ReservaCreatedResponse> reservar(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody @Valid ReservaRequest dto) {

        Reserva reserva = reservaService.reservarHabitaciones(id, dto, user);
        ReservaCreatedResponse response = ReservaCreatedResponse.of(reserva.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/reserva/{id}")
    public ResponseEntity<ReservaResponse> obtenerReserva(@PathVariable Long id) {
        Reserva reserva = reservaService.buscarPorId(id);
        ReservaResponse response = ReservaMapper.toDTO(reserva);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaListResponse>> obtenerMisReservas(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        List<Reserva> reservas = reservaService.buscarReservasPorUsuarioIdYFechas(user.getId(), fechaInicio, fechaFin);

        if (reservas.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<ReservaListResponse> reservasDTO = ReservaMapper.toListResponseList(reservas);
        return ResponseEntity.ok(reservasDTO);
    }

    @PostMapping("/{id}/confirmar-pago")
    public ResponseEntity<MessageResponse> confirmarPago(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        Reserva reserva = reservaService.buscarPorId(id);
        validarPropietario(reserva, user);

        Reserva reservaConfirmada = reservaService.confirmarPago(id);
        return ResponseEntity.ok(MessageResponse.of("Pago confirmado exitosamente. Reserva #" + reservaConfirmada.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> cancelarReserva(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        Reserva reserva = reservaService.buscarPorId(id);
        validarPropietario(reserva, user);

        reservaService.eliminar(id);
        return ResponseEntity.ok(MessageResponse.of("Reserva cancelada exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaUpdateResponse> actualizarReserva(
            @PathVariable Long id,
            @RequestBody @Valid ReservaUpdateRequest request,
            @AuthenticationPrincipal User user) {

        Reserva reserva = reservaService.buscarPorId(id);
        validarPropietario(reserva, user);

        Reserva reservaActualizada = reservaService.actualizarFechas(id, request.fechaInicio(), request.fechaFin());
        ReservaUpdateResponse response = ReservaUpdateResponse.of(reservaActualizada.getTotal());

        return ResponseEntity.ok(response);
    }

    // ==================== HELPERS ====================

    private void validarPropietario(Reserva reserva, User user) {
        if (reserva.getCliente().getUser() == null ||
                !reserva.getCliente().getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("No tiene permiso sobre esta reserva");
        }
    }
}
