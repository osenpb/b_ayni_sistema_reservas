package com.osen.sistema_reservas.shared.helpers.exceptions;

/**
 * Excepción lanzada cuando hay un conflicto de datos.
 */
public class ConflictException extends RuntimeException {

    private final String conflictType;

    public ConflictException(String message) {
        super(message);
        this.conflictType = "CONFLICT";
    }

    public ConflictException(String message, String conflictType) {
        super(message);
        this.conflictType = conflictType;
    }

    public String getConflictType() {
        return conflictType;
    }
}
