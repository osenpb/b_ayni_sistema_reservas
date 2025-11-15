package com.dawi.dawi_restapi.auth.infraestructure.dtos;

public record LoginRequestDTO(
        String email,

        String password
) {
}
