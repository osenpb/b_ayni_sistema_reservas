package com.dawi.dawi_restapi.core.cliente.dtos;

public record ClienteResponse(
        Long id,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String documento
) {}