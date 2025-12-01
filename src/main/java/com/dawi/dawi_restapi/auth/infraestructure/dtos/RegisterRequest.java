package com.dawi.dawi_restapi.auth.infraestructure.dtos;

public record RegisterRequest(

        String username,

        String email,

        String password,

        String telefono

) {
}
