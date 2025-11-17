package com.dawi.dawi_restapi.core.reserva.services;

import com.dawi.dawi_restapi.core.reserva.models.DetalleReserva;
import com.dawi.dawi_restapi.core.reserva.repositories.DetalleReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DetalleReservaService {

    private final DetalleReservaRepository detalleReservaRepository;

    public DetalleReserva guardar(DetalleReserva detalleReserva) {
        return detalleReservaRepository.save(detalleReserva);
    }

}
