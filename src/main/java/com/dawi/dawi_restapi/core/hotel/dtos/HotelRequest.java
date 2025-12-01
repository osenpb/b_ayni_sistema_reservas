package com.dawi.dawi_restapi.core.hotel.dtos;

import com.dawi.dawi_restapi.core.habitacion.dtos.HabitacionRequest;

import java.util.List;

public record HotelRequest(
        // este es para casos de update, x eso no ocupa id
        String nombre,
        String direccion,
        Long departamentoId,
        List<HabitacionRequest> habitaciones,
        String imagenUrl
        ) {

}
