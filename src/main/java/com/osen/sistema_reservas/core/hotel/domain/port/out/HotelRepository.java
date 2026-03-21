package com.osen.sistema_reservas.core.hotel.domain.port.out;

import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("SELECT h FROM Hotel h JOIN FETCH h.departamento LEFT JOIN FETCH h.habitaciones hab LEFT JOIN FETCH hab.tipoHabitacion")
    List<Hotel> findAllWithRelations();

    @Query("SELECT h FROM Hotel h JOIN FETCH h.departamento LEFT JOIN FETCH h.habitaciones hab LEFT JOIN FETCH hab.tipoHabitacion WHERE h.departamento.id = :departamentoId")
    List<Hotel> findByDepartamentoIdWithRelations(@Param("departamentoId") Long departamentoId);

    @Query("SELECT h FROM Hotel h JOIN FETCH h.departamento LEFT JOIN FETCH h.habitaciones hab LEFT JOIN FETCH hab.tipoHabitacion WHERE h.id = :id")
    Optional<Hotel> findByIdWithRelations(@Param("id") Long id);

    List<Hotel> findByDepartamentoId(Long departamentoId);
}
