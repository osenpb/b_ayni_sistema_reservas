package com.dawi.dawi_restapi.core.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteDTO(
        @NotBlank
        String dni,

        @NotBlank
        String nombre,

        @NotBlank
        String apellido,

        @Email
        String correo
        // tal vez requiera el ID, pero ya lo agregamos cuando si es necesario
) {
}
