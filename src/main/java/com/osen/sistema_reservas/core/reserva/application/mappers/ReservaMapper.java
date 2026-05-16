package com.osen.sistema_reservas.core.reserva.application.mappers;

import com.osen.sistema_reservas.auth.domain.model.User;
import com.osen.sistema_reservas.core.departamento.application.mappers.DepartamentoMapper;
import com.osen.sistema_reservas.core.detalle_reserva.application.dtos.DetalleReservaResponse;
import com.osen.sistema_reservas.core.hotel.application.dtos.HotelResponse;
import com.osen.sistema_reservas.core.reserva.application.dtos.ReservaListResponse;
import com.osen.sistema_reservas.core.reserva.application.dtos.ReservaResponse;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;

import java.util.List;
import java.util.Optional;

public class ReservaMapper {

    private ReservaMapper() {}

    public static ReservaResponse toDTO(Reserva reserva) {
        List<DetalleReservaResponse> detalles = reserva.getDetalles().stream()
                .map(det -> new DetalleReservaResponse(
                        det.getId(),
                        det.getHabitacion().getId(),
                        det.getPrecioNoche()
                ))
                .toList();

        var hotel = reserva.getHotel();
        HotelResponse hotelResponse = new HotelResponse(
                hotel.getId(),
                hotel.getNombre(),
                hotel.getDireccion(),
                DepartamentoMapper.toDTO(hotel.getDepartamento()),
                List.of(),
                hotel.getImagenUrl()
        );

        User user = reserva.getUser();
        return new ReservaResponse(
                reserva.getId(),
                reserva.getFechaReserva(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getTotal(),
                reserva.getEstado(),
                hotelResponse,
                user != null ? user.getId() : null,
                Optional.ofNullable(user).map(User::getNombre).orElse(""),
                Optional.ofNullable(user).map(User::getApellido).orElse(""),
                Optional.ofNullable(user).map(User::getEmail).orElse(""),
                Optional.ofNullable(user).map(User::getDni).orElse(""),
                detalles
        );
    }

    public static ReservaListResponse toListResponse(Reserva reserva) {
        ReservaListResponse.HotelSimple hotelSimple = null;
        if (reserva.getHotel() != null) {
            var h = reserva.getHotel();
            ReservaListResponse.DepartamentoSimple depSimple = h.getDepartamento() != null
                    ? new ReservaListResponse.DepartamentoSimple(h.getDepartamento().getId(), h.getDepartamento().getNombre())
                    : null;
            hotelSimple = new ReservaListResponse.HotelSimple(
                    h.getId(), h.getNombre(),
                    h.getDireccion() != null ? h.getDireccion() : "",
                    depSimple
            );
        }

        ReservaListResponse.UsuarioSimple usuarioSimple = null;
        if (reserva.getUser() != null) {
            User u = reserva.getUser();
            usuarioSimple = new ReservaListResponse.UsuarioSimple(
                    u.getId(),
                    Optional.ofNullable(u.getNombre()).orElse(""),
                    Optional.ofNullable(u.getApellido()).orElse(""),
                    Optional.ofNullable(u.getEmail()).orElse(""),
                    Optional.ofNullable(u.getTelefono()).orElse(""),
                    Optional.ofNullable(u.getDni()).orElse("")
            );
        }

        List<ReservaListResponse.DetalleSimple> detallesSimples = reserva.getDetalles() != null
                ? reserva.getDetalles().stream()
                        .map(det -> new ReservaListResponse.DetalleSimple(
                                det.getId(),
                                det.getHabitacion() != null ? det.getHabitacion().getId() : null,
                                det.getPrecioNoche()
                        ))
                        .toList()
                : List.of();

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

    public static List<ReservaListResponse> toListResponseList(List<Reserva> reservas) {
        return reservas.stream().map(ReservaMapper::toListResponse).toList();
    }
}
