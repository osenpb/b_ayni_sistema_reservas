package com.osen.sistema_reservas.api.publico;

import com.osen.sistema_reservas.core.habitacion.application.dtos.HabitacionDisponibilidadDTO;
import com.osen.sistema_reservas.core.habitacion.application.service.HabitacionService;
import com.osen.sistema_reservas.core.tipoHabitacion.application.dtos.TipoHabitacionResponse;
import com.osen.sistema_reservas.core.tipoHabitacion.domain.model.TipoHabitacion;
import com.osen.sistema_reservas.core.tipoHabitacion.application.service.TipoHabitacionService;
import com.osen.sistema_reservas.shared.helpers.mappers.TipoHabitacionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller público para consultas de habitaciones.
 */
@RestController
@RequestMapping("/public/habitaciones")
public class HabitacionConsultaController {

    private final HabitacionService habitacionService;
    private final TipoHabitacionService tipoHabitacionService;

    public HabitacionConsultaController(HabitacionService habitacionService, TipoHabitacionService tipoHabitacionService) {
        this.habitacionService = habitacionService;
        this.tipoHabitacionService = tipoHabitacionService;
    }

    /**
     * Verifica disponibilidad de habitaciones en un hotel para un rango de fechas
     */
    @GetMapping("/disponibles")
    public ResponseEntity<HabitacionDisponibilidadDTO> verificarDisponibilidad(
            @RequestParam Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        int cantidadDisponible = habitacionService.obtenerCantidadDisponible(hotelId, fechaInicio, fechaFin);
        HabitacionDisponibilidadDTO response = new HabitacionDisponibilidadDTO(
                cantidadDisponible > 0,
                cantidadDisponible
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos los tipos de habitación
     */
    @GetMapping("/tipos")
    public ResponseEntity<List<TipoHabitacionResponse>> listadoTipoHabitaciones() {
        List<TipoHabitacion> tipos = tipoHabitacionService.listar();
        List<TipoHabitacionResponse> response = TipoHabitacionMapper.toDTOList(tipos);
        return ResponseEntity.ok(response);
    }
}
