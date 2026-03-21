package com.osen.sistema_reservas.auth.domain.port.in;

import com.osen.sistema_reservas.auth.domain.model.User;
import com.osen.sistema_reservas.auth.infraestructure.dtos.LoginRequest;
import com.osen.sistema_reservas.auth.infraestructure.dtos.RegisterRequest;

import java.util.Map;

public interface AuthService {

    Map<String, String> login(LoginRequest loginRequestDTO);
    boolean validateToken(String token);
    String getUserFromToken(String token);
    void createUser(RegisterRequest createUserDto);
    User getUser(Long id);
}
