package com.dawi.dawi_restapi.auth.infraestructure.dtos;

public record LoginRequest(
        String email,

        String password
) {
}
