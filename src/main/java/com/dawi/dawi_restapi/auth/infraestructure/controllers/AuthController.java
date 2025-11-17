package com.dawi.dawi_restapi.auth.infraestructure.controllers;


import com.dawi.dawi_restapi.auth.domain.services.AuthService;
import com.dawi.dawi_restapi.auth.domain.services.TokenService;
import com.dawi.dawi_restapi.auth.infraestructure.dtos.LoginRequestDTO;
import com.dawi.dawi_restapi.auth.infraestructure.dtos.RegisterRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequestDTO createUserDto) {
        authService.createUser(createUserDto);


        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado :)");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {

        try{
            final Map<String, String> tokens = authService.login(loginRequestDTO);
//            ResponseCookie cookie = ResponseCookie.from("access-token", tokens.get("access-token"))
//                    .httpOnly(true)
//                    .secure(true)
//                    .sameSite("Strict")
//                    .path("/")
//                    .maxAge(60*60)
//                    .build();


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
