package com.osen.sistema_reservas.core.departamento.infrastructure.web;

import com.osen.sistema_reservas.core.departamento.application.dtos.DepartamentoResponse;
import com.osen.sistema_reservas.core.departamento.application.service.DepartamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/departamentos")
public class DepartamentoPublicController {

    private final DepartamentoService departamentoService;

    public DepartamentoPublicController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public ResponseEntity<List<DepartamentoResponse>> listar(
            @RequestParam(required = false) String nombre) {

        if (nombre != null && !nombre.isBlank()) {
            return departamentoService.buscarPorNombreResponse(nombre)
                    .map(dep -> ResponseEntity.ok(List.of(dep)))
                    .orElseGet(() -> ResponseEntity.ok(List.of()));
        }

        return ResponseEntity.ok(departamentoService.listarResponse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(
                com.osen.sistema_reservas.shared.helpers.mappers.DepartamentoMapper.toDTO(
                        departamentoService.buscarPorId(id)));
    }
}
