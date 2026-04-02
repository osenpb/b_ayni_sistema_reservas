package com.osen.sistema_reservas.core.hotel.infrastructure.web;

import com.osen.sistema_reservas.core.hotel.application.dtos.HotelRequest;
import com.osen.sistema_reservas.core.hotel.application.dtos.HotelResponse;
import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import com.osen.sistema_reservas.core.hotel.application.service.HotelService;
import com.osen.sistema_reservas.shared.helpers.dtos.MessageResponse;
import com.osen.sistema_reservas.shared.helpers.mappers.HotelMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hoteles")
public class HotelAdminController {

    private final HotelService hotelService;

    public HotelAdminController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public ResponseEntity<List<HotelResponse>> listar(
            @RequestParam(required = false) Long departamentoId) {

        if (departamentoId != null) {
            return ResponseEntity.ok(hotelService.listarPorDepartamentoId(departamentoId));
        }

        return ResponseEntity.ok(hotelService.listarHoteles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.buscarPorIdResponse(id));
    }

    @PostMapping
    public ResponseEntity<HotelResponse> crear(@RequestBody @Valid HotelRequest hotelRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.guardarResponse(hotelRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelResponse> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid HotelRequest hotelRequest) {

        return ResponseEntity.ok(hotelService.actualizarResponse(id, hotelRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Long id) {
        hotelService.eliminar(id);
        return ResponseEntity.ok(MessageResponse.of("Hotel eliminado correctamente"));
    }
}
