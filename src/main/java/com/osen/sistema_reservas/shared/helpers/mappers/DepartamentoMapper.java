package com.osen.sistema_reservas.shared.helpers.mappers;

import com.osen.sistema_reservas.core.departamento.application.dtos.DepartamentoResponse;
import com.osen.sistema_reservas.core.departamento.domain.model.Departamento;

import java.util.List;

public class DepartamentoMapper {

    public static DepartamentoResponse toDTO(Departamento departamento) {
        if (departamento == null) return null;

        return new DepartamentoResponse(
                departamento.getId(),
                departamento.getNombre(),
                departamento.getDetalle() != null ? departamento.getDetalle() : ""
        );
    }

    public static List<DepartamentoResponse> toDTOList(List<Departamento> departamentos) {
        return departamentos.stream()
                .map(DepartamentoMapper::toDTO)
                .toList();
    }
}
