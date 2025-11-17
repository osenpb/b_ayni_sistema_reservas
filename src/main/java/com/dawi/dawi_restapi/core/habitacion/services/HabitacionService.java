package com.dawi.dawi_restapi.core.habitacion.services;

import com.dawi.dawi_restapi.core.habitacion.models.Habitacion;
import com.dawi.dawi_restapi.core.habitacion.repositories.HabitacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;

    public Optional<Habitacion> buscarPorId(Long id) {
        return habitacionRepository.findById(id);
    }

    public void eliminarPorId(Long id) {
        habitacionRepository.deleteById(id);
    }

    public int obtenerCantidadDisponible(Long hotelId, LocalDate inicio, LocalDate fin) {

        return habitacionRepository.contarDisponibles(hotelId, inicio, fin);
    }

    public boolean estaDisponible(Habitacion habitacion, LocalDate inicio, LocalDate fin) {
        Long idHabitacion = habitacion.getId();

        int conflictos = habitacionRepository
                .contarReservasPorHabitacionYFechas(idHabitacion, inicio, fin);

        return conflictos == 0;
    }
}
