package com.osen.sistema_reservas.core.cliente.application.dtos;

public record ClienteResponse(
        Long id,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String documento
) {}