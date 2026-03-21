package com.osen.sistema_reservas.auth.domain.port.in;

import org.springframework.security.core.Authentication;

public interface TokenService {

    String generateToken(Authentication authentication);
    String getUserFromToken(String token);
    boolean validateToken(String token);

}
