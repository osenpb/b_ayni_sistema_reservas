package com.osen.sistema_reservas.api.admin;

import com.osen.sistema_reservas.core.reserva.dtos.ReservaAdminUpdateDTO;
import com.osen.sistema_reservas.core.reserva.dtos.ReservaListResponse;
import com.osen.sistema_reservas.core.reserva.models.Reserva;
import com.osen.sistema_reservas.core.reserva.services.ReservaService;
import com.osen.sistema_reservas.helpers.dtos.MessageResponse;
import com.osen.sistema_reservas.helpers.mappers.ReservaMapper;
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
        List<Reserva> reservas = reservaService.listar();
        List<ReservaListResponse> response = ReservaMapper.toListResponseList(reservas);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca reservas por DNI del cliente
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ReservaListResponse>> buscarPorDni(@RequestParam String dni) {
        List<Reserva> reservas = reservaService.buscarReservasPorDniCliente(dni);
        List<ReservaListResponse> response = ReservaMapper.toListResponseList(reservas);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene una reserva por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservaListResponse> obtener(@PathVariable Long id) {
        Reserva reserva = reservaService.buscarPorId(id);
        ReservaListResponse response = ReservaMapper.toListResponse(reserva);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza una reserva existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReservaListResponse> actualizar(
            @PathVariable Long id,
            @RequestBody ReservaAdminUpdateDTO dto) {

        Reserva reservaActualizada = reservaService.actualizarReservaAdmin(id, dto);
        ReservaListResponse response = ReservaMapper.toListResponse(reservaActualizada);
        return ResponseEntity.ok(response);
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
