package com.osen.sistema_reservas.shared.helpers.dtos;


public record MessageResponse(
        String message
) {
    public static MessageResponse of(String message) {
        return new MessageResponse(message);
    }
}
