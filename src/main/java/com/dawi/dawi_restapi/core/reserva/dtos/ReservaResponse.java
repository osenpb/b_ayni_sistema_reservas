package com.dawi.dawi_restapi.core.reserva.dtos;

import com.dawi.dawi_restapi.core.cliente.dtos.ClienteResponse;
import com.dawi.dawi_restapi.core.detalle_reserva.dtos.DetalleReservaResponse;
import com.dawi.dawi_restapi.core.hotel.dtos.HotelResponse;

import java.time.LocalDate;
import java.util.List;

public record ReservaResponse(
        Long id,
        LocalDate fechaReserva,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        double total,
        String estado,
        HotelResponse hotel,
        ClienteResponse cliente,
        List<DetalleReservaResponse> detalles
) {}
