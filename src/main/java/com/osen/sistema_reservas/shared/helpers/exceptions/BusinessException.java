package com.osen.sistema_reservas.shared.helpers.exceptions;

/**
 * Excepción lanzada cuando ocurre un error de lógica de negocio.
 * Ejemplo: Fechas inválidas, habitación no disponible, etc.
 */
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
