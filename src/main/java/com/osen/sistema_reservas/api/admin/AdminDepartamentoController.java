package com.osen.sistema_reservas.api.admin;

import com.osen.sistema_reservas.core.departamento.application.dtos.DepartamentoRequest;
import com.osen.sistema_reservas.core.departamento.application.dtos.DepartamentoResponse;
import com.osen.sistema_reservas.core.departamento.domain.model.Departamento;
import com.osen.sistema_reservas.core.departamento.application.service.DepartamentoService;
import com.osen.sistema_reservas.shared.helpers.dtos.MessageResponse;
import com.osen.sistema_reservas.shared.helpers.mappers.DepartamentoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/departamentos")
public class AdminDepartamentoController {

    private final DepartamentoService departamentoService;

    public AdminDepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    /**
     * Lista todos los departamentos
     */
    @GetMapping
    public ResponseEntity<List<DepartamentoResponse>> listar() {
        List<DepartamentoResponse> response = departamentoService.listarResponse();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un departamento por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoResponse> obtener(@PathVariable Long id) {
        Departamento departamento = departamentoService.buscarPorId(id);
        return ResponseEntity.ok(DepartamentoMapper.toDTO(departamento));
    }

    /**
     * Crea un nuevo departamento
     */
    @PostMapping
    public ResponseEntity<DepartamentoResponse> crear(@RequestBody @Valid DepartamentoRequest request) {
        Departamento departamento = departamentoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(DepartamentoMapper.toDTO(departamento));
    }

    /**
     * Actualiza un departamento existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoResponse> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid DepartamentoRequest request) {

        Departamento departamento = departamentoService.actualizar(id, request);
        return ResponseEntity.ok(DepartamentoMapper.toDTO(departamento));
    }

    /**
     * Elimina un departamento
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Long id) {
        departamentoService.eliminar(id);
        return ResponseEntity.ok(MessageResponse.of("Departamento eliminado correctamente"));
    }
}
