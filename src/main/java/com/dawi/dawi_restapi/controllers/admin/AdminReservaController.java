package com.dawi.dawi_restapi.controllers.admin;


import com.dawi.dawi_restapi.core.cliente.model.Cliente;
import com.dawi.dawi_restapi.core.cliente.service.ClienteService;
import com.dawi.dawi_restapi.core.dtos.ReservaAdminUpdateDTO;
import com.dawi.dawi_restapi.core.habitacion.models.Habitacion;
import com.dawi.dawi_restapi.core.habitacion.services.HabitacionService;
import com.dawi.dawi_restapi.core.reserva.models.DetalleReserva;
import com.dawi.dawi_restapi.core.reserva.models.Reserva;
import com.dawi.dawi_restapi.core.reserva.services.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reservas")
@RequiredArgsConstructor
public class AdminReservaController {

    private final ReservaService reservaService;
    private final ClienteService clienteService;
    private final HabitacionService habitacionService;


    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorDni(@RequestParam String dni) {
        return ResponseEntity.ok(reservaService.buscarReservasPorDniCliente(dni));
    }


    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(reservaService.listar());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
        return ResponseEntity.ok("Reserva eliminada correctamente");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return reservaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody ReservaAdminUpdateDTO dto) {

        Reserva reserva = reservaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        reserva.setFechaInicio(dto.fechaInicio());
        reserva.setFechaFin(dto.fechaFin());
        reserva.setEstado(dto.estado());

        Cliente cliente = reserva.getCliente();
        cliente.setNombre(dto.cliente().nombre());
        cliente.setApellido(dto.cliente().apellido());
        cliente.setEmail(dto.cliente().correo());
        cliente.setDni(dto.cliente().dni());
        clienteService.guardar(cliente);

        reserva.getDetalles().clear();
        double total = 0;

        for (Long idHab : dto.habitaciones()) {
            Habitacion h = habitacionService.buscarPorId(idHab)
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

            DetalleReserva det = new DetalleReserva();
            det.setReserva(reserva);
            det.setHabitacion(h);
            det.setPrecioNoche(h.getPrecio());
            reserva.getDetalles().add(det);
            total += h.getPrecio();
        }

        reserva.setTotal(total);
        return ResponseEntity.ok(reservaService.guardar(reserva));
    }

}