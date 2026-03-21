package com.osen.sistema_reservas.core.tipoHabitacion.domain.port.out;

import com.osen.sistema_reservas.core.tipoHabitacion.domain.model.TipoHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoHabitacionRepository extends JpaRepository<TipoHabitacion, Long> {
}