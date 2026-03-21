package com.osen.sistema_reservas.api.publico;

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
import com.osen.sistema_reservas.shared.helpers.mappers.DepartamentoMapper;
import com.osen.sistema_reservas.shared.helpers.mappers.HotelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/public/hoteles")
public class HotelController {

    private final HotelService hotelService;
    private final DepartamentoService departamentoService;

    public HotelController(HotelService hotelService, DepartamentoService departamentoService) {
        this.hotelService = hotelService;
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) Long departamentoId) {

        if (departamentoId != null) {
            Departamento departamento = departamentoService.buscarPorId(departamentoId);
            List<HotelResponse> hoteles = hotelService.listarPorDepartamentoId(departamentoId);

            HotelesConDepartamentoResponse response = new HotelesConDepartamentoResponse(
                    DepartamentoMapper.toDTO(departamento),
                    hoteles
            );
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(hotelService.listarHoteles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDetalleResponse> obtener(@PathVariable Long id) {
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

    @GetMapping("/{id}/habitaciones-disponibles")
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
}
