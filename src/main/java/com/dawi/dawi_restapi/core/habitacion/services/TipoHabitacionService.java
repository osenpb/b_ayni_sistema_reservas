package com.dawi.dawi_restapi.core.habitacion.services;

import com.dawi.dawi_restapi.core.habitacion.models.TipoHabitacion;
import com.dawi.dawi_restapi.core.habitacion.repositories.TipoHabitacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoHabitacionService {

    private final TipoHabitacionRepository tipoHabitacionRepository;

    public List<TipoHabitacion> listar() {
        return tipoHabitacionRepository.findAll();
    }
}
