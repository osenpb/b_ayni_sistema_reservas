package com.osen.sistema_reservas.auth.infraestructure.dtos;

import com.osen.sistema_reservas.auth.domain.model.Role;

public record UserResponse(
        Long id,
        String username,
        String email,
        Role role
) {

}
