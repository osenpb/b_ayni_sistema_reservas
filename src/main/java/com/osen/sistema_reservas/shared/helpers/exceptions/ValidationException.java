package com.osen.sistema_reservas.shared.helpers.exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Excepción lanzada cuando hay errores de validación en los datos de entrada.
 * Puede contener múltiples errores de campo.
 */
public class ValidationException extends RuntimeException {

    private final Map<String, String> fieldErrors;

    public ValidationException(String message) {
        super(message);
        this.fieldErrors = new HashMap<>();
    }

    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors != null ? fieldErrors : new HashMap<>();
    }

    public ValidationException(String field, String errorMessage) {
        super(String.format("Error de validación en campo '%s': %s", field, errorMessage));
        this.fieldErrors = new HashMap<>();
        this.fieldErrors.put(field, errorMessage);
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void addFieldError(String field, String errorMessage) {
        this.fieldErrors.put(field, errorMessage);
    }

    public boolean hasFieldErrors() {
        return !fieldErrors.isEmpty();
    }
}
