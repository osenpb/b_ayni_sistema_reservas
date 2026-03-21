package com.osen.sistema_reservas.core.reserva.domain.port.out;

import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByClienteDni(String dni);
    
    List<Reserva> findByClienteUserId(Long userId);

    @Query("SELECT r FROM Reserva r WHERE r.cliente.user.id = :userId ORDER BY r.fechaReserva DESC")
    List<Reserva> findByUsuarioId(@Param("userId") Long userId);
    //List<Reserva> findByClienteUserIdOrderByFechaReservaDesc(Long userId);


    @Query("SELECT r FROM Reserva r WHERE r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio")
    List<Reserva> findReservasEnRango(@Param("fechaInicio") LocalDate fechaInicio,
                                      @Param("fechaFin") LocalDate fechaFin);

    List<Reserva> findByFechaReservaBetween(LocalDate inicio, LocalDate fin);
}