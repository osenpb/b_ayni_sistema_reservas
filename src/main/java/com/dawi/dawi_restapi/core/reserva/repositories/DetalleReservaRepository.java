package com.dawi.dawi_restapi.core.reserva.repositories;

import com.dawi.dawi_restapi.core.reserva.models.DetalleReserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleReservaRepository extends JpaRepository<DetalleReserva, Long> {
}