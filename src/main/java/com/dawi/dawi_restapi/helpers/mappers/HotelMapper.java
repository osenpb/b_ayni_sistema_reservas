package com.dawi.dawi_restapi.helpers.mappers;

import com.dawi.dawi_restapi.core.hotel.dtos.HotelResponse;
import com.dawi.dawi_restapi.core.hotel.model.Hotel;

public class HotelMapper {

    public static HotelResponse toDTO(Hotel hotel) {
        return new HotelResponse(
                hotel.getId(),
                hotel.getNombre(),
                hotel.getDireccion(),
                hotel.getDepartamento(),
                hotel.getHabitaciones().stream().map(HabitacionMapper::toDTO).toList(),
                hotel.getImagenUrl()
        );
    }

}
