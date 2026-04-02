package com.osen.sistema_reservas.core.reserva.infrastructure.web;

import com.osen.sistema_reservas.core.reserva.application.dtos.ReservaAdminUpdateDTO;
import com.osen.sistema_reservas.core.reserva.application.dtos.ReservaListResponse;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import com.osen.sistema_reservas.core.reserva.application.service.ReservaService;
import com.osen.sistema_reservas.shared.helpers.dtos.MessageResponse;
import com.osen.sistema_reservas.shared.helpers.mappers.ReservaMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/reservas")
public class ReservaAdminController {

    private final ReservaService reservaService;

    public ReservaAdminController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaListResponse>> listar(
            @RequestParam(required = false) String dni) {

        if (dni != null && !dni.isBlank()) {
            return ResponseEntity.ok(reservaService.buscarReservasPorDniClienteResponse(dni));
        }

        return ResponseEntity.ok(reservaService.listarResponse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaListResponse> obtener(@PathVariable Long id) {
        Reserva reserva = reservaService.buscarPorId(id);
        return ResponseEntity.ok(ReservaMapper.toListResponse(reserva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaListResponse> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid ReservaAdminUpdateDTO dto) {

        Reserva reservaActualizada = reservaService.actualizarReservaAdmin(id, dto);
        return ResponseEntity.ok(ReservaMapper.toListResponse(reservaActualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
        return ResponseEntity.ok(MessageResponse.of("Reserva eliminada correctamente"));
    }
}
