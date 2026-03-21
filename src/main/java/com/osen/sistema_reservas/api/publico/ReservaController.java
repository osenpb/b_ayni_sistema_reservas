package com.osen.sistema_reservas.api.publico;

import com.osen.sistema_reservas.auth.domain.model.User;
import com.osen.sistema_reservas.core.reserva.application.dtos.*;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import com.osen.sistema_reservas.core.reserva.application.service.ReservaService;
import com.osen.sistema_reservas.shared.helpers.dtos.MessageResponse;
import com.osen.sistema_reservas.shared.helpers.exceptions.ForbiddenException;
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
@RequestMapping("/public/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaCreatedResponse> crear(
            @AuthenticationPrincipal User user,
            @RequestParam Long hotelId,
            @RequestBody @Valid ReservaRequest dto) {

        Reserva reserva = reservaService.reservarHabitaciones(hotelId, dto, user);
        ReservaCreatedResponse response = ReservaCreatedResponse.of(reserva.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> obtener(@PathVariable Long id) {
        Reserva reserva = reservaService.buscarPorId(id);
        return ResponseEntity.ok(ReservaMapper.toDTO(reserva));
    }

    @GetMapping("/mias")
    public ResponseEntity<List<ReservaListResponse>> listarMias(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        List<Reserva> reservas = reservaService.buscarReservasPorUsuarioIdYFechas(user.getId(), fechaInicio, fechaFin);

        if (reservas.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(ReservaMapper.toListResponseList(reservas));
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<MessageResponse> pagar(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        Reserva reserva = reservaService.buscarPorId(id);
        validarPropietario(reserva, user);

        Reserva reservaConfirmada = reservaService.confirmarPago(id);
        return ResponseEntity.ok(MessageResponse.of("Pago confirmado exitosamente. Reserva #" + reservaConfirmada.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaUpdateResponse> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid ReservaUpdateRequest request,
            @AuthenticationPrincipal User user) {

        Reserva reserva = reservaService.buscarPorId(id);
        validarPropietario(reserva, user);

        Reserva reservaActualizada = reservaService.actualizarFechas(id, request.fechaInicio(), request.fechaFin());
        return ResponseEntity.ok(ReservaUpdateResponse.of(reservaActualizada.getTotal()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        Reserva reserva = reservaService.buscarPorId(id);
        validarPropietario(reserva, user);

        reservaService.eliminar(id);
        return ResponseEntity.ok(MessageResponse.of("Reserva cancelada exitosamente"));
    }

    private void validarPropietario(Reserva reserva, User user) {
        if (reserva.getCliente().getUser() == null ||
                !reserva.getCliente().getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("No tiene permiso sobre esta reserva");
        }
    }
}
