package com.osen.sistema_reservas.core.departamento.application.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record DepartamentoRequest(
        @NotBlank(message = "El nombre es requerido")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,

        @Size(max = 500, message = "El detalle no puede exceder 500 caracteres")
        String detalle
) {}
