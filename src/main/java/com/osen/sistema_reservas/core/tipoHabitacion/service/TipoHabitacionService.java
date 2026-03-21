package com.osen.sistema_reservas.core.tipoHabitacion.service;

import com.osen.sistema_reservas.core.tipoHabitacion.model.TipoHabitacion;
import com.osen.sistema_reservas.core.tipoHabitacion.repository.TipoHabitacionRepository;
import com.osen.sistema_reservas.helpers.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoHabitacionService {

    private final TipoHabitacionRepository tipoHabitacionRepository;

    public TipoHabitacionService(TipoHabitacionRepository tipoHabitacionRepository) {
        this.tipoHabitacionRepository = tipoHabitacionRepository;
    }

    public List<TipoHabitacion> listar() {
        return tipoHabitacionRepository.findAll();
    }

    public TipoHabitacion buscarPorId(Long id) {
        return tipoHabitacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TipoHabitacion con ID: " + id));
    }
}
