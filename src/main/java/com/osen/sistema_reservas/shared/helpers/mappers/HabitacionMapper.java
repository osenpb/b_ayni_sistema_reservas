package com.osen.sistema_reservas.shared.helpers.mappers;

import com.osen.sistema_reservas.core.habitacion.application.dtos.HabitacionResponse;
import com.osen.sistema_reservas.core.habitacion.domain.model.Habitacion;



public class HabitacionMapper {

    private HabitacionMapper() {
        throw new UnsupportedOperationException("Esta clase no debe ser instanciada");
    }

    public static HabitacionResponse toDTO(Habitacion habitacion) {
        if (habitacion == null) {
            return null;
        }

        return new HabitacionResponse(
                habitacion.getId(),
                habitacion.getNumero(),
                habitacion.getEstado(),
                habitacion.getPrecio(),
                habitacion.getTipoHabitacion(),
                habitacion.getHotel().getId()
        );
    }

    public static Habitacion toEntity(HabitacionResponse dto) {
        if (dto == null) {
            return null;
        }

        return Habitacion.builder()
                .id(dto.id())
                .numero(dto.numero())
                .estado(dto.estado())
                .precio(dto.precio())
                .tipoHabitacion(dto.tipoHabitacion())
                .build();
    }
}
