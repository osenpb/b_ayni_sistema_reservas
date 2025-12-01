package com.dawi.dawi_restapi.auth.infraestructure.dtos;

public record AuthResponse(
        UserResponse userResponseDTO,
        String token
) {
}
