package com.osen.sistema_reservas.core.hotel.application.service;

import com.osen.sistema_reservas.core.departamento.domain.model.Departamento;
import com.osen.sistema_reservas.core.departamento.application.service.DepartamentoService;
import com.osen.sistema_reservas.core.habitacion.application.dtos.HabitacionResponse;
import com.osen.sistema_reservas.core.habitacion.domain.model.Habitacion;
import com.osen.sistema_reservas.core.habitacion.application.service.HabitacionService;
import com.osen.sistema_reservas.core.hotel.application.dtos.HotelRequest;
import com.osen.sistema_reservas.core.hotel.application.dtos.HotelResponse;
import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import com.osen.sistema_reservas.core.hotel.domain.port.out.HotelRepository;
import com.osen.sistema_reservas.core.tipoHabitacion.domain.model.TipoHabitacion;
import com.osen.sistema_reservas.core.tipoHabitacion.application.service.TipoHabitacionService;
import com.osen.sistema_reservas.shared.helpers.exceptions.EntityNotFoundException;
import com.osen.sistema_reservas.shared.helpers.exceptions.ValidationException;
import com.osen.sistema_reservas.shared.helpers.mappers.HotelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final DepartamentoService departamentoService;
    private final TipoHabitacionService tipoHabitacionService;
    private final HabitacionService habitacionService;

    public HotelService(HotelRepository hotelRepository, DepartamentoService departamentoService, TipoHabitacionService tipoHabitacionService, HabitacionService habitacionService) {
        this.hotelRepository = hotelRepository;
        this.departamentoService = departamentoService;
        this.tipoHabitacionService = tipoHabitacionService;
        this.habitacionService = habitacionService;
    }

    // ==================== CONSULTAS ====================

    public List<HotelResponse> listarHoteles() {
        List<Hotel> hoteles = hotelRepository.findAll();
        return hoteles.stream().map(HotelMapper::toDTO).toList();
    }

    public List<Hotel> listarTodos() {
        return hotelRepository.findAll();
    }

    public List<HotelResponse> listarPorDepartamentoId(Long departamentoId) {
        List<Hotel> hoteles = hotelRepository.findByDepartamentoId(departamentoId);
        return hoteles.stream().map(HotelMapper::toDTO).toList();
    }

    public Hotel buscarPorId(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hotel con ID " + id));
    }

    public HotelResponse buscarPorIdResponse(Long id) {
        Hotel hotel = buscarPorId(id);
        return HotelMapper.toDTO(hotel);
    }

    // ==================== OPERACIONES CRUD ====================

    @Transactional
    public Hotel guardar(HotelRequest hotelRequest) {
        validarHotelRequest(hotelRequest);

        Departamento departamento = departamentoService.buscarPorId(hotelRequest.departamentoId());

        Hotel hotel = new Hotel();
        hotel.setNombre(hotelRequest.nombre());
        hotel.setDireccion(hotelRequest.direccion());
        hotel.setDepartamento(departamento);
        hotel.setImagenUrl(hotelRequest.imagenUrl());

        return hotelRepository.save(hotel);
    }

    @Transactional
    public HotelResponse guardarResponse(HotelRequest hotelRequest) {
        Hotel hotel = guardar(hotelRequest);
        return HotelMapper.toDTO(hotel);
    }

    @Transactional
    public Hotel actualizar(Long id, HotelRequest hotelRequest) {
        validarHotelRequest(hotelRequest);

        Hotel hotel = buscarPorId(id);
        Departamento departamento = departamentoService.buscarPorId(hotelRequest.departamentoId());

        hotel.setNombre(hotelRequest.nombre());
        hotel.setDireccion(hotelRequest.direccion());
        hotel.setDepartamento(departamento);

        if (hotelRequest.imagenUrl() != null) {
            hotel.setImagenUrl(hotelRequest.imagenUrl());
        }

        // Actualizar habitaciones si vienen en el request
        if (hotelRequest.habitaciones() != null && !hotelRequest.habitaciones().isEmpty()) {
            actualizarHabitaciones(hotel, hotelRequest);
        }

        return hotelRepository.save(hotel);
    }

    @Transactional
    public HotelResponse actualizarResponse(Long id, HotelRequest hotelRequest) {
        Hotel hotel = actualizar(id, hotelRequest);
        return HotelMapper.toDTO(hotel);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new EntityNotFoundException("Hotel con ID " + id);
        }
        hotelRepository.deleteById(id);
    }

    // ==================== HABITACIONES DISPONIBLES ====================

    public List<HabitacionResponse> obtenerHabitacionesDisponibles(
            Long hotelId, LocalDate fechaInicio, LocalDate fechaFin) {

        Hotel hotel = buscarPorId(hotelId);
        HotelResponse hotelResponse = HotelMapper.toDTO(hotel);

        return hotelResponse.habitaciones().stream()
                .filter(h -> "DISPONIBLE".equals(h.estado()))
                .filter(h -> habitacionService.estaDisponible(h.id(), fechaInicio, fechaFin))
                .toList();
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private void validarHotelRequest(HotelRequest request) {
        if (request.nombre() == null || request.nombre().isBlank()) {
            throw new ValidationException("nombre", "El nombre del hotel es requerido");
        }
        if (request.departamentoId() == null) {
            throw new ValidationException("departamentoId", "El departamento es requerido");
        }
    }

    private void actualizarHabitaciones(Hotel hotel, HotelRequest hotelRequest) {
        hotel.getHabitaciones().clear();

        List<Habitacion> nuevasHabitaciones = hotelRequest.habitaciones().stream().map(habReq -> {
            Habitacion hab = new Habitacion();
            hab.setHotel(hotel);
            hab.setNumero(habReq.numero());
            hab.setEstado(habReq.estado());
            hab.setPrecio(habReq.precio());

            TipoHabitacion tipo = tipoHabitacionService.buscarPorId(habReq.tipoHabitacionId());
            hab.setTipoHabitacion(tipo);

            return hab;
        }).toList();

        hotel.getHabitaciones().addAll(nuevasHabitaciones);
    }
}
