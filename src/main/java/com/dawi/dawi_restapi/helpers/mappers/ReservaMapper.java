package com.dawi.dawi_restapi.helpers.mappers;

import com.dawi.dawi_restapi.core.cliente.model.Cliente;
import com.dawi.dawi_restapi.core.detalle_reserva.dtos.DetalleReservaResponse;

import com.dawi.dawi_restapi.core.reserva.dtos.ReservaResponse;
import com.dawi.dawi_restapi.core.reserva.models.Reserva;

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

}
