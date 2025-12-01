package com.dawi.dawi_restapi.core.cliente.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(
        @NotBlank
        String dni,

        @NotBlank
        String nombre,

        @NotBlank
        String apellido,

        @Email
        String email,

        String telefono

) {
}
