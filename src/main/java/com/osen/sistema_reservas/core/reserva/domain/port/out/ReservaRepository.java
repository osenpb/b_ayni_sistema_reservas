package com.osen.sistema_reservas.core.reserva.domain.port.out;

import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("SELECT DISTINCT r FROM Reserva r JOIN FETCH r.user JOIN FETCH r.hotel h JOIN FETCH h.departamento LEFT JOIN FETCH r.detalles d LEFT JOIN FETCH d.habitacion ORDER BY r.fechaReserva DESC")
    List<Reserva> findAllWithRelations();

    @Query("SELECT DISTINCT r FROM Reserva r JOIN FETCH r.user u JOIN FETCH r.hotel h JOIN FETCH h.departamento LEFT JOIN FETCH r.detalles d LEFT JOIN FETCH d.habitacion WHERE u.dni = :dni ORDER BY r.fechaReserva DESC")
    List<Reserva> findByUsuarioDniWithRelations(@Param("dni") String dni);

    @Query("SELECT DISTINCT r FROM Reserva r JOIN FETCH r.user JOIN FETCH r.hotel h JOIN FETCH h.departamento LEFT JOIN FETCH r.detalles d LEFT JOIN FETCH d.habitacion WHERE r.user.id = :userId ORDER BY r.fechaReserva DESC")
    List<Reserva> findByUsuarioIdWithRelations(@Param("userId") Long userId);

    @Query("SELECT DISTINCT r FROM Reserva r JOIN FETCH r.user JOIN FETCH r.hotel h JOIN FETCH h.departamento LEFT JOIN FETCH r.detalles d LEFT JOIN FETCH d.habitacion WHERE r.user.id = :userId AND r.fechaInicio >= COALESCE(:fechaInicio, r.fechaInicio) AND r.fechaFin <= COALESCE(:fechaFin, r.fechaFin) ORDER BY r.fechaReserva DESC")
    List<Reserva> findByUsuarioIdAndFechas(@Param("userId") Long userId,
                                           @Param("fechaInicio") LocalDate fechaInicio,
                                           @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT DISTINCT r FROM Reserva r JOIN FETCH r.user JOIN FETCH r.hotel h JOIN FETCH h.departamento LEFT JOIN FETCH r.detalles d LEFT JOIN FETCH d.habitacion WHERE r.id = :id")
    Optional<Reserva> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT r FROM Reserva r WHERE r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio")
    List<Reserva> findReservasEnRango(@Param("fechaInicio") LocalDate fechaInicio,
                                      @Param("fechaFin") LocalDate fechaFin);

    List<Reserva> findByFechaReservaBetween(LocalDate inicio, LocalDate fin);
}
