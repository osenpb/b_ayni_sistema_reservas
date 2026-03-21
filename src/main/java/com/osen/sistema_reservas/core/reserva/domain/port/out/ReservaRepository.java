package com.osen.sistema_reservas.core.reserva.domain.port.out;

import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("SELECT r FROM Reserva r JOIN FETCH r.cliente JOIN FETCH r.hotel LEFT JOIN FETCH r.detalles d LEFT JOIN FETCH d.habitacion ORDER BY r.fechaReserva DESC")
    List<Reserva> findAllWithRelations();

    @Query("SELECT r FROM Reserva r JOIN FETCH r.cliente c JOIN FETCH r.hotel LEFT JOIN FETCH r.detalles d LEFT JOIN FETCH d.habitacion WHERE c.dni = :dni ORDER BY r.fechaReserva DESC")
    List<Reserva> findByClienteDniWithRelations(@Param("dni") String dni);

    @Query("SELECT r FROM Reserva r JOIN FETCH r.cliente JOIN FETCH r.hotel LEFT JOIN FETCH r.detalles d LEFT JOIN FETCH d.habitacion WHERE r.cliente.user.id = :userId ORDER BY r.fechaReserva DESC")
    List<Reserva> findByUsuarioIdWithRelations(@Param("userId") Long userId);

    @Query("SELECT r FROM Reserva r JOIN FETCH r.cliente JOIN FETCH r.hotel LEFT JOIN FETCH r.detalles d LEFT JOIN FETCH d.habitacion WHERE r.cliente.user.id = :userId AND r.fechaInicio >= COALESCE(:fechaInicio, r.fechaInicio) AND r.fechaFin <= COALESCE(:fechaFin, r.fechaFin) ORDER BY r.fechaReserva DESC")
    List<Reserva> findByUsuarioIdAndFechas(@Param("userId") Long userId,
                                           @Param("fechaInicio") LocalDate fechaInicio,
                                           @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT r FROM Reserva r JOIN FETCH r.cliente JOIN FETCH r.hotel LEFT JOIN FETCH r.detalles d LEFT JOIN FETCH d.habitacion WHERE r.id = :id")
    Optional<Reserva> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT r FROM Reserva r WHERE r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio")
    List<Reserva> findReservasEnRango(@Param("fechaInicio") LocalDate fechaInicio,
                                      @Param("fechaFin") LocalDate fechaFin);

    List<Reserva> findByFechaReservaBetween(LocalDate inicio, LocalDate fin);
}
