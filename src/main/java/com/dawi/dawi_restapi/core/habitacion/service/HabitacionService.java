package com.dawi.dawi_restapi.core.habitacion.service;

import com.dawi.dawi_restapi.core.habitacion.dtos.HabitacionResponse;
import com.dawi.dawi_restapi.core.habitacion.models.Habitacion;
import com.dawi.dawi_restapi.core.habitacion.repository.HabitacionRepository;
import com.dawi.dawi_restapi.core.hotel.dtos.HotelResponse;
import com.dawi.dawi_restapi.core.hotel.services.HotelService;
import com.dawi.dawi_restapi.helpers.mappers.HabitacionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;

    public long contarTodas() {
        return habitacionRepository.count();
    }

    public Optional<Habitacion> buscarPorId(Long id) {
        return habitacionRepository.findById(id);
    }

    public void eliminarPorId(Long id) {
        habitacionRepository.deleteById(id);
    }

    public int obtenerCantidadDisponible(Long hotelId, LocalDate inicio, LocalDate fin) {

        return habitacionRepository.contarDisponibles(hotelId, inicio, fin);
    }

    public boolean estaDisponible(Long habitacionId, LocalDate inicio, LocalDate fin) {

        int conflictos = habitacionRepository
                .contarReservasPorHabitacionYFechas(habitacionId, inicio, fin);

        return conflictos == 0;
    }



}