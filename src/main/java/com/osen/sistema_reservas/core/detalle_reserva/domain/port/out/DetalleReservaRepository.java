package com.osen.sistema_reservas.core.detalle_reserva.domain.port.out;

import com.osen.sistema_reservas.core.detalle_reserva.domain.model.DetalleReserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleReservaRepository extends JpaRepository<DetalleReserva, Long> {
}