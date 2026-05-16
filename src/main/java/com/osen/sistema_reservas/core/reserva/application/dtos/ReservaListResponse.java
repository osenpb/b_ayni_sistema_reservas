package com.osen.sistema_reservas.core.reserva.application.dtos;

import com.osen.sistema_reservas.core.reserva.domain.model.EstadoReserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReservaListResponse(
        Long id,
        LocalDate fechaReserva,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        BigDecimal total,
        EstadoReserva estado,
        HotelSimple hotel,
        UsuarioSimple usuario,
        List<DetalleSimple> detalles
) {
    public record HotelSimple(Long id, String nombre, String direccion, DepartamentoSimple departamento) {}

    public record DepartamentoSimple(Long id, String nombre) {}

    public record UsuarioSimple(Long id, String nombre, String apellido, String email, String telefono, String dni) {}

    public record DetalleSimple(Long id, Long habitacionId, BigDecimal precioNoche) {}
}
