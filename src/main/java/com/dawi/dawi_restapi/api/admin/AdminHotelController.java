package com.dawi.dawi_restapi.api.admin;

import com.dawi.dawi_restapi.core.habitacion.service.HabitacionService;
import com.dawi.dawi_restapi.core.hotel.dtos.HotelResponse;
import com.dawi.dawi_restapi.core.hotel.dtos.HotelRequest;
import com.dawi.dawi_restapi.core.departamento.service.DepartamentoService;
import com.dawi.dawi_restapi.core.hotel.services.HotelService;
import com.dawi.dawi_restapi.helpers.mappers.HotelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/hoteles")
@RequiredArgsConstructor
public class AdminHotelController {

    private final HotelService hotelService;
    private final DepartamentoService departamentoService;
    private final HabitacionService habitacionService;

    @GetMapping
    public ResponseEntity<List<HotelResponse>> obtenerTodosLosHoteles() {
        List<HotelResponse> hoteles = hotelService.listarHoteles();
        return ResponseEntity.ok(hoteles);

    }

    @GetMapping("departamento/{id}")
    public ResponseEntity<?> listarPorDepartamento(@PathVariable("id") Long depId) { // antes estaba con RequestParam
        return ResponseEntity.ok(hotelService.listarPorDepartamentoId(depId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelResponse> buscarPorId(@PathVariable Long id) {

        HotelResponse hotelResponse = HotelMapper.toDTO(hotelService.buscarPorId(id));

        return ResponseEntity.ok(hotelResponse);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody HotelRequest hotel) {
        return ResponseEntity.ok(hotelService.guardar(hotel));
    }


    // REVISAR METODO, PROBLEMAS DE LOGICA
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody HotelRequest hotelRequest) {
        return ResponseEntity.ok(hotelService.actualizar(id, hotelRequest));
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
