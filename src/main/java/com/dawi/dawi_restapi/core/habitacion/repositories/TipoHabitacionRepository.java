package com.dawi.dawi_restapi.core.habitacion.repositories;

import com.dawi.dawi_restapi.core.habitacion.models.TipoHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoHabitacionRepository extends JpaRepository<TipoHabitacion, Long> {
}