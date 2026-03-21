package com.osen.sistema_reservas.core.habitacion.domain.port.out;

import com.osen.sistema_reservas.core.habitacion.domain.model.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

    @Query("SELECT h FROM Habitacion h JOIN FETCH h.tipoHabitacion JOIN FETCH h.hotel WHERE h.hotel.id = :hotelId")
    List<Habitacion> findByHotelIdWithRelations(@Param("hotelId") Long hotelId);

    @Query("SELECT h FROM Habitacion h JOIN FETCH h.hotel WHERE h.id IN :ids")
    List<Habitacion> findByIdInWithRelations(@Param("ids") List<Long> ids);

    List<Habitacion> findByHotelId(Long hotelId);
    List<Habitacion> findByIdIn(List<Long> ids);

    @Query("SELECT h FROM Habitacion h JOIN FETCH h.tipoHabitacion JOIN FETCH h.hotel WHERE h.hotel.id = :hotelId AND h.estado = 'DISPONIBLE'")
    List<Habitacion> findDisponiblesByHotelId(@Param("hotelId") Long hotelId);

    @Query("SELECT COUNT(h) FROM Habitacion h WHERE h.hotel.id = :hotelId AND h.id NOT IN (" +
            "SELECT dr.habitacion.id FROM DetalleReserva dr WHERE " +
            "(dr.reserva.fechaInicio <= :fin AND dr.reserva.fechaFin >= :inicio))")
    int contarDisponibles(@Param("hotelId") Long hotelId,
                          @Param("inicio") LocalDate inicio,
                          @Param("fin") LocalDate fin);

    @Query("SELECT COUNT(dr) FROM DetalleReserva dr " +
            "WHERE dr.habitacion.id = :habitacionId " +
            "AND dr.reserva.fechaInicio <= :fin " +
            "AND dr.reserva.fechaFin >= :inicio")
    int contarReservasPorHabitacionYFechas(
            @Param("habitacionId") Long habitacionId,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );
}
