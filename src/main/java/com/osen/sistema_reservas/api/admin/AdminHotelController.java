package com.osen.sistema_reservas.api.admin;

import com.osen.sistema_reservas.core.hotel.dtos.HotelRequest;
import com.osen.sistema_reservas.core.hotel.dtos.HotelResponse;
import com.osen.sistema_reservas.core.hotel.model.Hotel;
import com.osen.sistema_reservas.core.hotel.services.HotelService;
import com.osen.sistema_reservas.helpers.dtos.MessageResponse;
import com.osen.sistema_reservas.helpers.mappers.HotelMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para administración de hoteles.
 * Solo maneja requests/responses, toda la lógica está en HotelService.
 */
@RestController
@RequestMapping("/admin/hoteles")
public class AdminHotelController {

    private final HotelService hotelService;

    public AdminHotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    /**
     * Lista todos los hoteles
     */
    @GetMapping
    public ResponseEntity<List<HotelResponse>> obtenerTodosLosHoteles() {
        List<HotelResponse> hoteles = hotelService.listarHoteles();
        return ResponseEntity.ok(hoteles);
    }

    /**
     * Lista hoteles por departamento
     */
    @GetMapping("/departamento/{id}")
    public ResponseEntity<List<HotelResponse>> listarPorDepartamento(@PathVariable("id") Long depId) {
        List<HotelResponse> hoteles = hotelService.listarPorDepartamentoId(depId);
        return ResponseEntity.ok(hoteles);
    }

    /**
     * Obtiene un hotel por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<HotelResponse> buscarPorId(@PathVariable Long id) {
        HotelResponse hotelResponse = hotelService.buscarPorIdResponse(id);
        return ResponseEntity.ok(hotelResponse);
    }

    /**
     * Crea un nuevo hotel
     */
    @PostMapping
    public ResponseEntity<HotelResponse> crear(@RequestBody @Valid HotelRequest hotelRequest) {
        Hotel hotel = hotelService.guardar(hotelRequest);
        HotelResponse response = HotelMapper.toDTO(hotel);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza un hotel existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<HotelResponse> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid HotelRequest hotelRequest) {

        Hotel hotel = hotelService.actualizar(id, hotelRequest);
        HotelResponse response = HotelMapper.toDTO(hotel);
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina un hotel
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Long id) {
        hotelService.eliminar(id);
        return ResponseEntity.ok(MessageResponse.of("Hotel eliminado correctamente"));
    }
}
