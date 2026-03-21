package com.osen.sistema_reservas.shared.helpers.exceptions;

public class TokenExpiredException extends RuntimeException{

    public TokenExpiredException(String message) {
        super(message);
    }

}
