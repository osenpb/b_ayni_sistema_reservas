package com.dawi.dawi_restapi.auth.infraestructure.dtos;

public record RegisterRequestDTO(

        String username,

        String email,

        String password

) {
}
