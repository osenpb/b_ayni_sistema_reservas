package com.osen.sistema_reservas.core.hotel.domain.port.out;

import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByDepartamentoId(Long departamentoId);
}
