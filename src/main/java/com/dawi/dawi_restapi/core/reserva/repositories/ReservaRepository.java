package com.dawi.dawi_restapi.core.reserva.repositories;

import com.dawi.dawi_restapi.core.reserva.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByClienteDni(String dni);

    @Query("SELECT r FROM Reserva r WHERE r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio")
    List<Reserva> findReservasEnRango(@Param("fechaInicio") LocalDate fechaInicio,
                                      @Param("fechaFin") LocalDate fechaFin);

    List<Reserva> findByFechaReservaBetween(LocalDate inicio, LocalDate fin);
}