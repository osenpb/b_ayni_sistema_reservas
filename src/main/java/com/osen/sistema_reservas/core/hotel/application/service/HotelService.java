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

    @Transactional(readOnly = true)
    public List<HotelResponse> listarHoteles() {
        return hotelRepository.findAllWithRelations().stream()
                .map(HotelMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Hotel> listarTodos() {
        return hotelRepository.findAllWithRelations();
    }

    @Transactional(readOnly = true)
    public List<HotelResponse> listarPorDepartamentoId(Long departamentoId) {
        return hotelRepository.findByDepartamentoIdWithRelations(departamentoId).stream()
                .map(HotelMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Hotel buscarPorId(Long id) {
        return hotelRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new EntityNotFoundException("Hotel con ID " + id));
    }

    @Transactional(readOnly = true)
    public HotelResponse buscarPorIdResponse(Long id) {
        return HotelMapper.toDTO(buscarPorId(id));
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
        return HotelMapper.toDTO(guardar(hotelRequest));
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

        if (hotelRequest.habitaciones() != null && !hotelRequest.habitaciones().isEmpty()) {
            actualizarHabitaciones(hotel, hotelRequest);
        }

        return hotelRepository.save(hotel);
    }

    @Transactional
    public HotelResponse actualizarResponse(Long id, HotelRequest hotelRequest) {
        return HotelMapper.toDTO(actualizar(id, hotelRequest));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new EntityNotFoundException("Hotel con ID " + id);
        }
        hotelRepository.deleteById(id);
    }

    // ==================== HABITACIONES DISPONIBLES ====================

    @Transactional(readOnly = true)
    public List<HabitacionResponse> obtenerHabitacionesDisponibles(
            Long hotelId, LocalDate fechaInicio, LocalDate fechaFin) {

        List<Habitacion> habitaciones = habitacionService.buscarDisponiblesPorHotelId(hotelId);

        return habitaciones.stream()
                .filter(h -> habitacionService.estaDisponible(h.getId(), fechaInicio, fechaFin))
                .map(h -> new HabitacionResponse(
                        h.getId(), h.getNumero(), h.getEstado(), h.getPrecio(),
                        h.getTipoHabitacion(),
                        h.getHotel() != null ? h.getHotel().getId() : null
                ))
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
