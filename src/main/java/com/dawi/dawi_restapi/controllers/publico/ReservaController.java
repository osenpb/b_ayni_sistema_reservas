package com.dawi.dawi_restapi.controllers.publico;


import com.dawi.dawi_restapi.core.cliente.model.Cliente;
import com.dawi.dawi_restapi.core.cliente.service.ClienteService;
import com.dawi.dawi_restapi.core.dtos.ReservaRequestDTO;
import com.dawi.dawi_restapi.core.dtos.ReservaResponseDTO;
import com.dawi.dawi_restapi.core.habitacion.models.Habitacion;
import com.dawi.dawi_restapi.core.habitacion.services.HabitacionService;
import com.dawi.dawi_restapi.core.hotel.models.Hotel;
import com.dawi.dawi_restapi.core.hotel.services.DepartamentoService;
import com.dawi.dawi_restapi.core.hotel.services.HotelService;
import com.dawi.dawi_restapi.core.reserva.models.DetalleReserva;
import com.dawi.dawi_restapi.core.reserva.models.Reserva;
import com.dawi.dawi_restapi.core.reserva.services.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public/reserva")
@RequiredArgsConstructor
public class ReservaController {

    private final DepartamentoService departamentoService;
    private final HotelService hotelService;
    private final ReservaService reservaService;
    private final HabitacionService habitacionService;
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

        return ResponseEntity.ok(Map.of(
                "departamento", departamentoService.buscarPorId(depId).orElse(null),
                "hoteles", hotelService.listarPorDepartamentoId(depId)
        ));
    }

    @GetMapping("/hoteles/{id}")
    public ResponseEntity<?> infoHotel(@PathVariable Long id) {

        Hotel hotel = hotelService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));

        return ResponseEntity.ok(Map.of(
                "hotel", hotel,
                "departamento", hotel.getDepartamento(),
                "habitaciones", hotel.getHabitaciones()
        ));
    }

    @PostMapping("/hoteles/{id}/reservar")
    public ResponseEntity<?> reservar(
            @PathVariable Long id,
            @RequestBody @Valid ReservaRequestDTO dto) {

        Hotel hotel = hotelService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));

        // FECHAS
        LocalDate hoy = LocalDate.now();
        if (dto.fechaInicio().isBefore(hoy) || dto.fechaFin().isBefore(hoy)) {
            throw new RuntimeException("Las fechas no pueden ser anteriores a hoy.");
        }
        if (dto.fechaInicio().isAfter(dto.fechaFin())) {
            throw new RuntimeException("La fecha inicio no puede ser mayor a la fecha fin.");
        }

        // DISPONIBILIDAD
        List<String> noDisponibles = new ArrayList<>();

        List<Habitacion> habitaciones = dto.habitacionesIds().stream()
                .map(idHab -> habitacionService.buscarPorId(idHab)
                        .orElseThrow(() -> new RuntimeException("Habitación no encontrada: " + idHab)))
                .toList();

        for (Habitacion h : habitaciones) {
            if (!habitacionService.estaDisponible(h, dto.fechaInicio(), dto.fechaFin())) {
                noDisponibles.add(h.getTipoHabitacion().getNombre());
            }
        }

        if (!noDisponibles.isEmpty()) {
            throw new RuntimeException("Habitaciones no disponibles: " + String.join(", ", noDisponibles));
        }


        Cliente cli = new Cliente();
        cli.setNombre(dto.cliente().nombre());
        cli.setApellido(dto.cliente().apellido());
        cli.setEmail(dto.cliente().correo());
        cli.setDni(dto.cliente().dni());
        clienteService.guardar(cli);


        Reserva reserva = new Reserva();
        reserva.setCliente(cli);
        reserva.setHotel(hotel);
        reserva.setFechaInicio(dto.fechaInicio());
        reserva.setFechaFin(dto.fechaFin());
        reserva.setFechaReserva(LocalDate.now());
        reserva.setEstado("CONFIRMADA");

        List<DetalleReserva> detalles = new ArrayList<>();
        double total = 0;

        for (Habitacion h : habitaciones) {
            DetalleReserva det = new DetalleReserva();
            det.setHabitacion(h);
            det.setReserva(reserva);
            det.setPrecioNoche(h.getPrecio());
            detalles.add(det);
            total += h.getPrecio();
        }

        reserva.setTotal(total);
        reserva.setDetalles(detalles);
        reservaService.guardar(reserva);

        ReservaResponseDTO response = new ReservaResponseDTO(
                reserva.getId(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getTotal(),
                reserva.getEstado(),
                habitaciones.stream().map(h -> h.getTipoHabitacion().getNombre()).toList()
        );

        return ResponseEntity.ok(response);
    }
}