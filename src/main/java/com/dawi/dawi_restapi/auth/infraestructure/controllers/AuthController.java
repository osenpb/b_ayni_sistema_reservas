package com.dawi.dawi_restapi.auth.infraestructure.controllers;


import com.dawi.dawi_restapi.auth.domain.services.AuthService;
import com.dawi.dawi_restapi.auth.infraestructure.dtos.LoginRequestDTO;
import com.dawi.dawi_restapi.auth.infraestructure.dtos.RegisterRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequestDTO createUserDto) {
        authService.createUser(createUserDto);


        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado :)");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody LoginRequestDTO loginRequestDTO) {

        log.info("Email recibido: {}", loginRequestDTO.email());

        try{
            final Map<String, String> tokens = authService.login(loginRequestDTO);
            log.info("Tokens generados correctamente");
            return ResponseEntity.ok(tokens);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("No existe el usuario {}", e);
        }

    }

    @GetMapping("/saludo")
    public ResponseEntity<?> saludo() {
        return ResponseEntity.ok("hola");
    }

}
