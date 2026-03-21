package com.osen.sistema_reservas.shared.helpers.mappers;

import com.osen.sistema_reservas.core.cliente.domain.model.Cliente;
import com.osen.sistema_reservas.core.detalle_reserva.application.dtos.DetalleReservaResponse;
import com.osen.sistema_reservas.core.reserva.application.dtos.ReservaListResponse;
import com.osen.sistema_reservas.core.reserva.application.dtos.ReservaResponse;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;

import java.util.List;

public class ReservaMapper {

    public static ReservaResponse toDTO(Reserva reserva) {
        List<DetalleReservaResponse> detalles = reserva.getDetalles().stream()
                .map(det -> new DetalleReservaResponse(
                        det.getId(),
                        det.getHabitacion().getId(),
                        det.getPrecioNoche()
                ))
                .toList();

        Cliente cliente = reserva.getCliente();

        return new ReservaResponse(
                reserva.getId(),
                reserva.getFechaReserva(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getTotal(),
                reserva.getEstado(),
                HotelMapper.toDTO(reserva.getHotel()),
                ClienteMapper.toResponse(cliente),
                detalles
        );
    }


    public static ReservaListResponse toListResponse(Reserva reserva) {
        // Hotel simplificado
        ReservaListResponse.HotelSimple hotelSimple = null;
        if (reserva.getHotel() != null) {
            ReservaListResponse.DepartamentoSimple depSimple = null;
            if (reserva.getHotel().getDepartamento() != null) {
                depSimple = new ReservaListResponse.DepartamentoSimple(
                        reserva.getHotel().getDepartamento().getId(),
                        reserva.getHotel().getDepartamento().getNombre()
                );
            }
            hotelSimple = new ReservaListResponse.HotelSimple(
                    reserva.getHotel().getId(),
                    reserva.getHotel().getNombre(),
                    reserva.getHotel().getDireccion() != null ? reserva.getHotel().getDireccion() : "",
                    depSimple
            );
        }

        // Cliente simplificado
        ReservaListResponse.ClienteSimple clienteSimple = null;
        if (reserva.getCliente() != null) {
            Cliente c = reserva.getCliente();
            clienteSimple = new ReservaListResponse.ClienteSimple(
                    c.getId(),
                    c.getNombre() != null ? c.getNombre() : "",
                    c.getApellido() != null ? c.getApellido() : "",
                    c.getEmail() != null ? c.getEmail() : "",
                    c.getTelefono() != null ? c.getTelefono() : "",
                    c.getDni() != null ? c.getDni() : ""
            );
        }


        List<ReservaListResponse.DetalleSimple> detallesSimples = List.of();
        if (reserva.getDetalles() != null) {
            detallesSimples = reserva.getDetalles().stream()
                    .map(det -> new ReservaListResponse.DetalleSimple(
                            det.getId(),
                            det.getHabitacion() != null ? det.getHabitacion().getId() : null,
                            det.getPrecioNoche()
                    ))
                    .toList();
        }

        return new ReservaListResponse(
                reserva.getId(),
                reserva.getFechaReserva(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getTotal(),
                reserva.getEstado(),
                hotelSimple,
                clienteSimple,
                detallesSimples
        );
    }

    /**
     * Convierte una lista de Reservas a lista de ReservaListResponse
     */
    public static List<ReservaListResponse> toListResponseList(List<Reserva> reservas) {
        return reservas.stream()
                .map(ReservaMapper::toListResponse)
                .toList();
    }
}
