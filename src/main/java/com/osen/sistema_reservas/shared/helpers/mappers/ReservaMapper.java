package com.osen.sistema_reservas.shared.helpers.mappers;

import com.osen.sistema_reservas.auth.domain.model.User;
import com.osen.sistema_reservas.core.detalle_reserva.application.dtos.DetalleReservaResponse;
import com.osen.sistema_reservas.core.hotel.application.dtos.HotelResponse;
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

        // Hotel simplificado sin habitaciones para evitar LazyInitializationException
        var hotel = reserva.getHotel();
        HotelResponse hotelResponse = new HotelResponse(
                hotel.getId(),
                hotel.getNombre(),
                hotel.getDireccion(),
                DepartamentoMapper.toDTO(hotel.getDepartamento()),
                List.of(),
                hotel.getImagenUrl()
        );

        // Usuario datos (de User directamente)
        User user = reserva.getUser();
        Long usuarioId = user != null ? user.getId() : null;
        String usuarioNombre = user != null ? (user.getNombre() != null ? user.getNombre() : "") : "";
        String usuarioApellido = user != null ? (user.getApellido() != null ? user.getApellido() : "") : "";
        String usuarioEmail = user != null ? (user.getEmail() != null ? user.getEmail() : "") : "";
        String usuarioDni = user != null ? (user.getDni() != null ? user.getDni() : "") : "";

        return new ReservaResponse(
                reserva.getId(),
                reserva.getFechaReserva(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getTotal(),
                reserva.getEstado(),
                hotelResponse,
                usuarioId,
                usuarioNombre,
                usuarioApellido,
                usuarioEmail,
                usuarioDni,
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

        // Usuario simplificado (de User directamente)
        ReservaListResponse.UsuarioSimple usuarioSimple = null;
        if (reserva.getUser() != null) {
            User u = reserva.getUser();
            usuarioSimple = new ReservaListResponse.UsuarioSimple(
                    u.getId(),
                    u.getNombre() != null ? u.getNombre() : "",
                    u.getApellido() != null ? u.getApellido() : "",
                    u.getEmail() != null ? u.getEmail() : "",
                    u.getTelefono() != null ? u.getTelefono() : "",
                    u.getDni() != null ? u.getDni() : ""
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
                usuarioSimple,
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
