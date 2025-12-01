package com.dawi.dawi_restapi.auth.infraestructure.dtos;

import com.dawi.dawi_restapi.auth.domain.models.Role;

public record UserResponse(
        Long id,
        String username,
        String email,
        Role role
) {

}
