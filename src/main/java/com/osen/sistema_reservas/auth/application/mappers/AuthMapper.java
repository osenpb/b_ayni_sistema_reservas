package com.osen.sistema_reservas.auth.application.mappers;

import com.osen.sistema_reservas.auth.domain.model.Role;
import com.osen.sistema_reservas.auth.domain.model.User;
import com.osen.sistema_reservas.auth.infraestructure.dtos.LoginRequest;
import com.osen.sistema_reservas.auth.infraestructure.dtos.RegisterRequest;
import com.osen.sistema_reservas.auth.infraestructure.dtos.UserResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


public class AuthMapper {

    private AuthMapper() {
        throw new UnsupportedOperationException("This class should never be instantiated");
    }

    public static User fromDto(final RegisterRequest createUserDto) {
        return User.builder()
                .email(createUserDto.email())
                .username(createUserDto.username())
                .build();
    }

    public static Authentication fromDto(final LoginRequest loginRequestDTO) {
        return new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password());
        // Por contrato se habia creado un username, pero no es obligatorio
        // en este caso como username estoy usando el email

    }

    public static UserResponse toDto(final User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }

}
