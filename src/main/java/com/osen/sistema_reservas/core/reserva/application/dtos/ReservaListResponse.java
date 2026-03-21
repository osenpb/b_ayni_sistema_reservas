package com.osen.sistema_reservas.core.reserva.application.dtos;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO simplificado para listado de reservas en admin.
 * Evita referencias circulares y expone solo datos necesarios.
 */
public record ReservaListResponse(
        Long id,
        LocalDate fechaReserva,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        double total,
        String estado,
        HotelSimple hotel,
        ClienteSimple cliente,
        List<DetalleSimple> detalles
) {
    /**
     * Hotel simplificado para evitar referencias circulares
     */
    public record HotelSimple(
            Long id,
            String nombre,
            String direccion,
            DepartamentoSimple departamento
    ) {}


    public record DepartamentoSimple(
            Long id,
            String nombre
    ) {}


    public record ClienteSimple(
            Long id,
            String nombre,
            String apellido,
            String email,
            String telefono,
            String documento
    ) {}


    public record DetalleSimple(
            Long id,
            Long habitacionId,
            double precioNoche
    ) {}
}
