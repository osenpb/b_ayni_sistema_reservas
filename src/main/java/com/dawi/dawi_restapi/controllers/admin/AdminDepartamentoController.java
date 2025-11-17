package com.dawi.dawi_restapi.controllers.admin;

import com.dawi.dawi_restapi.core.hotel.models.Departamento;
import com.dawi.dawi_restapi.core.hotel.services.DepartamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/departamentos")
@RequiredArgsConstructor
public class AdminDepartamentoController {

    private final DepartamentoService departamentoService;

    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(departamentoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return departamentoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Departamento dep) {
        return ResponseEntity.ok(departamentoService.guardar(dep));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Departamento dep) {
        dep.setId(id);
        return ResponseEntity.ok(departamentoService.guardar(dep));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        departamentoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
