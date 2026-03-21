package com.osen.sistema_reservas.api.publico;

import com.osen.sistema_reservas.auth.domain.models.User;
import com.osen.sistema_reservas.core.cliente.service.ClienteService;
import com.osen.sistema_reservas.core.departamento.dtos.DepartamentoResponse;
import com.osen.sistema_reservas.core.departamento.model.Departamento;
import com.osen.sistema_reservas.core.departamento.service.DepartamentoService;
import com.osen.sistema_reservas.core.habitacion.dtos.HabitacionResponse;
import com.osen.sistema_reservas.core.habitacion.dtos.HabitacionesDisponiblesResponse;
import com.osen.sistema_reservas.core.hotel.dtos.HotelDetalleResponse;
import com.osen.sistema_reservas.core.hotel.dtos.HotelResponse;
import com.osen.sistema_reservas.core.hotel.dtos.HotelesConDepartamentoResponse;
import com.osen.sistema_reservas.core.hotel.model.Hotel;
import com.osen.sistema_reservas.core.hotel.services.HotelService;
import com.osen.sistema_reservas.core.reserva.dtos.*;
import com.osen.sistema_reservas.core.reserva.dtos.*;
import com.osen.sistema_reservas.core.reserva.models.Reserva;
import com.osen.sistema_reservas.core.reserva.services.ReservaService;
import com.osen.sistema_reservas.helpers.dtos.MessageResponse;
import com.osen.sistema_reservas.helpers.mappers.DepartamentoMapper;
import com.osen.sistema_reservas.helpers.mappers.HotelMapper;
import com.osen.sistema_reservas.helpers.mappers.ReservaMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller público para reservas de clientes.
 * Solo maneja requests/responses, toda la lógica está en los services.
 */
@RestController
@RequestMapping("/public/reserva")
public class ReservaController {

    private final DepartamentoService departamentoService;
    private final HotelService hotelService;
    private final ReservaService reservaService;
    private final ClienteService clienteService;

    public ReservaController(DepartamentoService departamentoService, HotelService hotelService, ReservaService reservaService, ClienteService clienteService) {
        this.departamentoService = departamentoService;
        this.hotelService = hotelService;
        this.reservaService = reservaService;
        this.clienteService = clienteService;
    }

    // ==================== DEPARTAMENTOS ====================

    /**
     * Lista todos los departamentos o busca por nombre
     */
    @GetMapping("/departamentos")
    public ResponseEntity<List<DepartamentoResponse>> listarDepartamentos(
            @RequestParam(required = false) String nombre) {

        if (nombre != null && !nombre.isBlank()) {
            return departamentoService.buscarPorNombre(nombre)
                    .map(dep -> ResponseEntity.ok(List.of(DepartamentoMapper.toDTO(dep))))
                    .orElseGet(() -> ResponseEntity.ok(List.of()));
        }

        List<Departamento> departamentos = departamentoService.listar();
        List<DepartamentoResponse> response = DepartamentoMapper.toDTOList(departamentos);
        return ResponseEntity.ok(response);
    }

    // ==================== HOTELES ====================

    /**
     * Lista hoteles por departamento
     */
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

    /**
     * Obtiene información detallada de un hotel
     */
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

    /**
     * Obtiene habitaciones disponibles de un hotel para un rango de fechas
     */
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

    /**
     * Crea una nueva reserva vinculada al usuario autenticado
     */
    @PostMapping("/hoteles/{id}/reservar")
    public ResponseEntity<?> reservar(@AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody @Valid ReservaRequest dto) {

        // Obtener usuario autenticado (puede ser null si no está autenticado)

        Reserva reserva = reservaService.reservarHabitaciones(id, dto, user);
        ReservaCreatedResponse response = ReservaCreatedResponse.of(reserva.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtiene detalles de una reserva (para página de confirmación)
     */
    @GetMapping("/reserva/{id}")
    public ResponseEntity<ReservaResponse> obtenerReserva(@PathVariable Long id) {
        Reserva reserva = reservaService.buscarPorId(id);
        ReservaResponse response = ReservaMapper.toDTO(reserva);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene las reservas del usuario autenticado con filtros opcionales por fecha
     */
    @GetMapping("/mis-reservas")
    public ResponseEntity<?> obtenerMisReservas(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(MessageResponse.of("Debe iniciar sesión para ver sus reservas"));
        }

        List<Reserva> reservas = reservaService.buscarReservasPorUsuarioIdYFechas(user.getId(), fechaInicio, fechaFin);

        if (reservas.isEmpty()) {
            return ResponseEntity.ok(MisReservasResponse.empty("usuario"));
        }

        List<ReservaListResponse> reservasDTO = ReservaMapper.toListResponseList(reservas);
        return ResponseEntity.ok(reservasDTO);
    }

    /**
     * Confirma el pago de una reserva (cambia estado de PENDIENTE a CONFIRMADA)
     */
    @PostMapping("/{id}/confirmar-pago")
    public ResponseEntity<?> confirmarPago(@PathVariable Long id, @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(MessageResponse.of("Debe iniciar sesión para confirmar el pago"));
        }

        Reserva reserva = reservaService.buscarPorId(id);
        
        // Verificar que la reserva pertenece al usuario
        if (reserva.getCliente().getUser() == null || 
            !reserva.getCliente().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(MessageResponse.of("No tiene permiso para confirmar esta reserva"));
        }

        Reserva reservaConfirmada = reservaService.confirmarPago(id);
        return ResponseEntity.ok(MessageResponse.of("Pago confirmado exitosamente. Reserva #" + reservaConfirmada.getId()));
    }

    /**
     * Cancela/Elimina una reserva (solo si pertenece al usuario autenticado)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> cancelarReserva(@PathVariable Long id, @AuthenticationPrincipal User user) {

        Reserva reserva = reservaService.buscarPorId(id);
        
        // Verificar que la reserva pertenece al usuario
        if (reserva.getCliente().getUser() == null || 
            !reserva.getCliente().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(MessageResponse.of("No tiene permiso para cancelar esta reserva"));
        }

        reservaService.eliminar(id);
        return ResponseEntity.ok(MessageResponse.of("Reserva cancelada exitosamente"));
    }

    /**
     * Actualiza las fechas de una reserva (solo si pertenece al usuario autenticado)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarReserva(
            @PathVariable Long id,
            @RequestBody @Valid ReservaUpdateRequest request, @AuthenticationPrincipal User user) {

        Reserva reserva = reservaService.buscarPorId(id);
        
        // Verificar que la reserva pertenece al usuario
        if (reserva.getCliente().getUser() == null || 
            !reserva.getCliente().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(MessageResponse.of("No tiene permiso para actualizar esta reserva"));
        }

        Reserva reservaActualizada = reservaService.actualizarFechas(id, request.fechaInicio(), request.fechaFin());
        ReservaUpdateResponse response = ReservaUpdateResponse.of(reservaActualizada.getTotal());

        return ResponseEntity.ok(response);
    }
}
