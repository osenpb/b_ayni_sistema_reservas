package com.osen.sistema_reservas.core.detalle_reserva.application.service;

import com.osen.sistema_reservas.core.detalle_reserva.domain.model.DetalleReserva;
import com.osen.sistema_reservas.core.detalle_reserva.domain.port.out.DetalleReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class DetalleReservaService {

    private final DetalleReservaRepository detalleReservaRepository;

    public DetalleReservaService(DetalleReservaRepository detalleReservaRepository) {
        this.detalleReservaRepository = detalleReservaRepository;
    }

    public DetalleReserva guardar(DetalleReserva detalleReserva) {
        return detalleReservaRepository.save(detalleReserva);
    }

}
