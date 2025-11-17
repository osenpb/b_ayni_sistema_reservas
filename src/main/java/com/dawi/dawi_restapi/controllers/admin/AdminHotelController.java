package com.dawi.dawi_restapi.controllers.admin;

import com.dawi.dawi_restapi.core.habitacion.services.HabitacionService;
import com.dawi.dawi_restapi.core.hotel.models.Hotel;
import com.dawi.dawi_restapi.core.hotel.services.DepartamentoService;
import com.dawi.dawi_restapi.core.hotel.services.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/hoteles")
@RequiredArgsConstructor
public class AdminHotelController {

    private final HotelService hotelService;
    private final DepartamentoService departamentoService;
    private final HabitacionService habitacionService;

    @GetMapping
    public ResponseEntity<?> listarPorDepartamento(@RequestParam Long depId) {
        return ResponseEntity.ok(hotelService.listarPorDepartamentoId(depId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return hotelService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Hotel hotel) {
        return ResponseEntity.ok(hotelService.guardar(hotel));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Hotel hotel) {
        hotel.setId(id);
        return ResponseEntity.ok(hotelService.guardar(hotel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        hotelService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/por-departamento/{id}")
    public ResponseEntity<?> hotelesPorDepartamento(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.listarPorDepartamentoId(id));
    }
}
