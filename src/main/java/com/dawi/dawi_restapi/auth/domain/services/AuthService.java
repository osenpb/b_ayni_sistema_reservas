package com.dawi.dawi_restapi.auth.domain.services;

import com.dawi.dawi_restapi.auth.domain.models.User;
import com.dawi.dawi_restapi.auth.infraestructure.dtos.LoginRequestDTO;
import com.dawi.dawi_restapi.auth.infraestructure.dtos.RegisterRequestDTO;

import java.util.Map;

public interface AuthService {

    Map<String, String> login(LoginRequestDTO loginRequestDTO);
    boolean validateToken(String token);
    String getUserFromToken(String token);
    void createUser(RegisterRequestDTO createUserDto);
    User getUser(Long id);
}
