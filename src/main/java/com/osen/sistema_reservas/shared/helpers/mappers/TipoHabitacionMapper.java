package com.osen.sistema_reservas.shared.helpers.mappers;

import com.osen.sistema_reservas.core.tipoHabitacion.application.dtos.TipoHabitacionResponse;
import com.osen.sistema_reservas.core.tipoHabitacion.domain.model.TipoHabitacion;

import java.util.List;

public class TipoHabitacionMapper {

    public static TipoHabitacionResponse toDTO(TipoHabitacion tipo) {
        if (tipo == null) return null;

        return new TipoHabitacionResponse(
                tipo.getId(),
                tipo.getNombre(),
                tipo.getDescripcion(),
                tipo.getCapacidad()
        );
    }

    public static List<TipoHabitacionResponse> toDTOList(List<TipoHabitacion> tipos) {
        return tipos.stream()
                .map(TipoHabitacionMapper::toDTO)
                .toList();
    }
}
