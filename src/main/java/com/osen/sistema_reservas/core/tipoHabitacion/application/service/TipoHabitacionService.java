package com.osen.sistema_reservas.core.tipoHabitacion.application.service;

import com.osen.sistema_reservas.core.tipoHabitacion.domain.model.TipoHabitacion;
import com.osen.sistema_reservas.core.tipoHabitacion.domain.port.out.TipoHabitacionRepository;
import com.osen.sistema_reservas.shared.helpers.exceptions.EntityNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TipoHabitacionService {

    private final TipoHabitacionRepository tipoHabitacionRepository;

    public TipoHabitacionService(TipoHabitacionRepository tipoHabitacionRepository) {
        this.tipoHabitacionRepository = tipoHabitacionRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "tiposHabitacion")
    public List<TipoHabitacion> listar() {
        return tipoHabitacionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TipoHabitacion buscarPorId(Long id) {
        return tipoHabitacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TipoHabitacion con ID: " + id));
    }
}
