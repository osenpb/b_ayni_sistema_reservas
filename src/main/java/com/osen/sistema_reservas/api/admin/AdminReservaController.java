package com.osen.sistema_reservas.api.admin;

import com.osen.sistema_reservas.core.reserva.application.dtos.ReservaAdminUpdateDTO;
import com.osen.sistema_reservas.core.reserva.application.dtos.ReservaListResponse;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import com.osen.sistema_reservas.core.reserva.application.service.ReservaService;
import com.osen.sistema_reservas.shared.helpers.dtos.MessageResponse;
import com.osen.sistema_reservas.shared.helpers.mappers.ReservaMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para administración de reservas.
 * Solo maneja requests/responses, toda la lógica está en ReservaService.
 */
@RestController
@RequestMapping("/admin/reservas")
public class AdminReservaController {

    private final ReservaService reservaService;

    public AdminReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /**
     * Lista todas las reservas
     */
    @GetMapping
    public ResponseEntity<List<ReservaListResponse>> listar() {
        List<ReservaListResponse> response = reservaService.listarResponse();
        return ResponseEntity.ok(response);
    }

    /**
     * Busca reservas por DNI del cliente
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ReservaListResponse>> buscarPorDni(@RequestParam String dni) {
        List<ReservaListResponse> response = reservaService.buscarReservasPorDniClienteResponse(dni);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene una reserva por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservaListResponse> obtener(@PathVariable Long id) {
        Reserva reserva = reservaService.buscarPorId(id);
        return ResponseEntity.ok(ReservaMapper.toListResponse(reserva));
    }

    /**
     * Actualiza una reserva existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReservaListResponse> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid ReservaAdminUpdateDTO dto) {

        Reserva reservaActualizada = reservaService.actualizarReservaAdmin(id, dto);
        return ResponseEntity.ok(ReservaMapper.toListResponse(reservaActualizada));
    }

    /**
     * Elimina una reserva
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
        return ResponseEntity.ok(MessageResponse.of("Reserva eliminada correctamente"));
    }
}
