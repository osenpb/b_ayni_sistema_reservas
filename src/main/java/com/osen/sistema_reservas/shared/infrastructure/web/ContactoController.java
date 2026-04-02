package com.osen.sistema_reservas.shared.infrastructure.web;

import com.osen.sistema_reservas.shared.helpers.dtos.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/contacto")
public class ContactoController {

    @GetMapping
    public ResponseEntity<MessageResponse> mostrarContacto() {
        return ResponseEntity.ok(MessageResponse.of("Información de contacto disponible"));
    }
}
