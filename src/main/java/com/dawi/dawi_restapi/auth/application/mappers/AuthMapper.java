package com.dawi.dawi_restapi.auth.application.mappers;

import com.dawi.dawi_restapi.auth.domain.models.User;
import com.dawi.dawi_restapi.auth.infraestructure.dtos.LoginRequestDTO;
import com.dawi.dawi_restapi.auth.infraestructure.dtos.RegisterRequestDTO;
import com.dawi.dawi_restapi.auth.infraestructure.dtos.UserResponseDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


public class AuthMapper {

    private AuthMapper() {
        throw new UnsupportedOperationException("This class should never be instantiated");
    }

    public static User fromDto(final RegisterRequestDTO createUserDto) {
        return User.builder()
                .email(createUserDto.email())
                .username(createUserDto.username())
                .build();
    }

    public static Authentication fromDto(final LoginRequestDTO loginRequestDTO) {
        return new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password());
        // Por contrato se habia creado un username, pero no es obligatorio
        // en este caso como username estoy usando el email

    }

    public static UserResponseDTO toDto(final User user) {
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }

}
