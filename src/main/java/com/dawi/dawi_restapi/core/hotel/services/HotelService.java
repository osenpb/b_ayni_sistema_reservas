package com.dawi.dawi_restapi.core.hotel.services;


import com.dawi.dawi_restapi.core.hotel.models.Hotel;
import com.dawi.dawi_restapi.core.hotel.repositories.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    public List<Hotel> listarPorDepartamentoId(Long departamentoId) {
        return hotelRepository.findByDepartamentoId(departamentoId);
    }

    public Hotel guardar(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public Optional<Hotel> buscarPorId(Long id) {
        return hotelRepository.findById(id);
    }

    public void eliminar(Long id) {
        hotelRepository.deleteById(id);
    }

}
