package com.dawi.dawi_restapi.core.hotel.services;

import com.dawi.dawi_restapi.core.departamento.service.DepartamentoService;
import com.dawi.dawi_restapi.core.habitacion.dtos.HabitacionResponse;
import com.dawi.dawi_restapi.core.habitacion.models.Habitacion;
import com.dawi.dawi_restapi.core.habitacion.service.HabitacionService;
import com.dawi.dawi_restapi.core.hotel.dtos.HotelResponse;
import com.dawi.dawi_restapi.core.hotel.dtos.HotelRequest;
import com.dawi.dawi_restapi.core.departamento.model.Departamento;
import com.dawi.dawi_restapi.core.hotel.model.Hotel;
import com.dawi.dawi_restapi.core.hotel.repositories.HotelRepository;
import com.dawi.dawi_restapi.core.tipoHabitacion.model.TipoHabitacion;
import com.dawi.dawi_restapi.core.tipoHabitacion.service.TipoHabitacionService;
import com.dawi.dawi_restapi.helpers.mappers.HabitacionMapper;
import com.dawi.dawi_restapi.helpers.mappers.HotelMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final DepartamentoService departamentoService;
    private final TipoHabitacionService tipoHabitacionService;
    private final HabitacionService habitacionService;

    public List<HotelResponse> listarPorDepartamentoId(Long departamentoId) {
        List<Hotel> hoteles = hotelRepository.findByDepartamentoId(departamentoId);
        return hoteles.stream().map(HotelMapper::toDTO).toList();
    }

    public Hotel guardar(HotelRequest hotelRequest) {
        Departamento departamento = departamentoService
                .buscarPorId(hotelRequest.departamentoId())
                .orElseThrow(() -> new RuntimeException("Departamento no existe"));

        Hotel hotel = new Hotel();
        hotel.setId(null);
        hotel.setNombre(hotelRequest.nombre());
        hotel.setDireccion(hotelRequest.direccion());
        hotel.setDepartamento(departamento);
        hotel.setImagenUrl(hotelRequest.imagenUrl());
        return hotelRepository.save(hotel);
    }

    @Transactional
    public boolean actualizar(Long id, HotelRequest hotelRequest) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));

        Departamento departamento = departamentoService
                .buscarPorId(hotelRequest.departamentoId())
                .orElseThrow(() -> new RuntimeException("Departamento no existe"));

        hotel.setNombre(hotelRequest.nombre());
        hotel.setDireccion(hotelRequest.direccion());
        hotel.setDepartamento(departamento);

        hotel.getHabitaciones().clear(); // esto esta x ver

        List<Habitacion> nuevasHabitaciones = hotelRequest.habitaciones().stream().map(habReq -> {
            Habitacion hab = new Habitacion();
            hab.setHotel(hotel);
            hab.setNumero(habReq.numero());
            hab.setEstado(habReq.estado());
            hab.setPrecio(habReq.precio());

            TipoHabitacion tipo = tipoHabitacionService
                    .buscarPorId(habReq.tipoHabitacionId());
            hab.setTipoHabitacion(tipo);

            return hab;
        }).toList();

        hotel.getHabitaciones().addAll(nuevasHabitaciones);

        return true;
    }

    public Hotel buscarPorId(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));
        return hotel;
    }

    public void eliminar(Long id) {
        hotelRepository.deleteById(id);
    }

    public List<HotelResponse> listarHoteles() {
        List<Hotel> hoteles = hotelRepository.findAll();
        return hoteles.stream().map(HotelMapper::toDTO).toList();
    }

    public List<Hotel> listarTodos() {
        return hotelRepository.findAll();
    }


    // esto podria ponerlo en ReservaService, pero x ahora lo dejo aqui
    public List<HabitacionResponse> obtenerHabitacionesDisponibles(
            Long hotelId, LocalDate fechaInicio, LocalDate fechaFin) {

        Hotel hotel = this.buscarPorId(hotelId);

        HotelResponse hotelResponse = HotelMapper.toDTO(hotel);

        return hotelResponse.habitaciones().stream()
                .filter(h -> "DISPONIBLE".equals(h.estado()))
                .filter(h -> habitacionService.estaDisponible(h.id(), fechaInicio, fechaFin))
                .toList();
    }
}