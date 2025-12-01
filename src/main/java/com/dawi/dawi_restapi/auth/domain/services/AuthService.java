package com.dawi.dawi_restapi.auth.domain.services;

import com.dawi.dawi_restapi.auth.domain.models.User;
import com.dawi.dawi_restapi.auth.infraestructure.dtos.LoginRequest;
import com.dawi.dawi_restapi.auth.infraestructure.dtos.RegisterRequest;

import java.util.Map;

public interface AuthService {

    Map<String, String> login(LoginRequest loginRequestDTO);
    boolean validateToken(String token);
    String getUserFromToken(String token);
    void createUser(RegisterRequest createUserDto);
    User getUser(Long id);
}
