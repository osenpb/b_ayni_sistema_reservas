package com.osen.sistema_reservas.auth.infraestructure.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "El username es requerido")
        @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
        String username,

        @NotBlank(message = "El email es requerido")
        @Email(message = "Debe ingresar un correo válido")
        String email,

        @NotBlank(message = "La contraseña es requerida")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @Pattern(regexp = "9\\d{8}", message = "El número debe tener 9 dígitos y empezar con 9")
        String telefono
) {
}
