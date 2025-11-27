package com.dawi.dawi_restapi.core.hotel.services;

import com.dawi.dawi_restapi.core.departamento.service.DepartamentoService;
import com.dawi.dawi_restapi.core.habitacion.models.Habitacion;
import com.dawi.dawi_restapi.core.hotel.dtos.HotelResponse;
import com.dawi.dawi_restapi.core.hotel.dtos.HotelRequest;
import com.dawi.dawi_restapi.core.departamento.model.Departamento;
import com.dawi.dawi_restapi.core.hotel.model.Hotel;
import com.dawi.dawi_restapi.core.hotel.repositories.HotelRepository;
import com.dawi.dawi_restapi.core.tipoHabitacion.model.TipoHabitacion;
import com.dawi.dawi_restapi.core.tipoHabitacion.service.TipoHabitacionService;
import com.dawi.dawi_restapi.helpers.mappers.HotelMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final DepartamentoService departamentoService;
    private final TipoHabitacionService tipoHabitacionService;

    public List<Hotel> listarPorDepartamentoId(Long departamentoId) {
        return hotelRepository.findByDepartamentoId(departamentoId);
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

        // Limpiar habitaciones existentes
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



    /**
     * Buscar hotel por ID - retorna HotelDTO
     */
    public HotelResponse buscarPorId(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));
        return HotelMapper.toDTO(hotel);
    }

    /**
     * Buscar hotel por ID - retorna entidad Hotel (para uso interno)
     */
    public Optional<Hotel> buscarEntidadPorId(Long id) {
        return hotelRepository.findById(id);
    }

    public void eliminar(Long id) {
        hotelRepository.deleteById(id);
    }

    public List<HotelResponse> listarHoteles() {
        List<Hotel> hoteles = hotelRepository.findAll();
        return hoteles.stream().map(HotelMapper::toDTO).toList();
    }
}