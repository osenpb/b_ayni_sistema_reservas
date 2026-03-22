package com.osen.sistema_reservas.shared.helpers.mappers;

import com.osen.sistema_reservas.core.hotel.application.dtos.HotelResponse;
import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;

public class HotelMapper {

    public static HotelResponse toDTO(Hotel hotel) {
        return new HotelResponse(
                hotel.getId(),
                hotel.getNombre(),
                hotel.getDireccion(),
                DepartamentoMapper.toDTO(hotel.getDepartamento()),
                hotel.getHabitaciones().stream().map(HabitacionMapper::toDTO).toList(),
                hotel.getImagenUrl()
        );
    }

}
