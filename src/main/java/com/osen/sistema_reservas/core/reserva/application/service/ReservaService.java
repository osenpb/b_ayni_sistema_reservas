package com.osen.sistema_reservas.core.reserva.application.service;

import com.osen.sistema_reservas.auth.domain.model.User;
import com.osen.sistema_reservas.core.cliente.domain.model.Cliente;
import com.osen.sistema_reservas.core.cliente.application.service.ClienteService;
import com.osen.sistema_reservas.core.detalle_reserva.domain.model.DetalleReserva;
import com.osen.sistema_reservas.core.habitacion.domain.model.Habitacion;
import com.osen.sistema_reservas.core.habitacion.application.service.HabitacionService;
import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import com.osen.sistema_reservas.core.hotel.application.service.HotelService;
import com.osen.sistema_reservas.core.reserva.application.dtos.ReservaAdminUpdateDTO;
import com.osen.sistema_reservas.core.reserva.application.dtos.ReservaListResponse;
import com.osen.sistema_reservas.core.reserva.application.dtos.ReservaRequest;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import com.osen.sistema_reservas.core.reserva.domain.port.out.ReservaRepository;
import com.osen.sistema_reservas.shared.helpers.exceptions.BusinessException;
import com.osen.sistema_reservas.shared.helpers.exceptions.ConflictException;
import com.osen.sistema_reservas.shared.helpers.exceptions.EntityNotFoundException;
import com.osen.sistema_reservas.shared.helpers.exceptions.ValidationException;
import com.osen.sistema_reservas.shared.helpers.mappers.ReservaMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteService clienteService;
    private final HotelService hotelService;
    private final HabitacionService habitacionService;

    public ReservaService(ReservaRepository reservaRepository, ClienteService clienteService, HotelService hotelService, HabitacionService habitacionService) {
        this.reservaRepository = reservaRepository;
        this.clienteService = clienteService;
        this.hotelService = hotelService;
        this.habitacionService = habitacionService;
    }

    // ==================== CONSULTAS ====================

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<ReservaListResponse> listarResponse() {
        return reservaRepository.findAllWithRelations().stream()
                .map(ReservaMapper::toListResponse)
                .toList();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<ReservaListResponse> buscarReservasPorDniClienteResponse(String dni) {
        return reservaRepository.findByClienteDniWithRelations(dni).stream()
                .map(ReservaMapper::toListResponse)
                .toList();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<Reserva> listarTodas() {
        return reservaRepository.findAllWithRelations();
    }

    public Reserva buscarPorId(Long id) {
        return reservaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva con ID:" + id));
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<Reserva> buscarReservasPorUsuarioIdYFechas(Long userId, LocalDate fechaInicio, LocalDate fechaFin) {
        return reservaRepository.findByUsuarioIdAndFechas(userId, fechaInicio, fechaFin);
    }

    // ==================== OPERACIONES CRUD ====================

    public Reserva guardar(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new EntityNotFoundException("Reserva con ID:" + id);
        }
        reservaRepository.deleteById(id);
    }

    // ==================== CREAR RESERVA ====================

    @Transactional
    public Reserva reservarHabitaciones(Long hotelId, ReservaRequest dto, User user) {
        validarReservaRequest(dto);

        Hotel hotel = hotelService.buscarPorId(hotelId);
        validarFechas(dto.fechaInicio(), dto.fechaFin());

        List<Habitacion> habitaciones = validarYObtenerHabitaciones(
                hotel, dto.habitacionesIds(), dto.fechaInicio(), dto.fechaFin());

        Cliente cliente = clienteService.crearOActualizar(dto.cliente(), user);

        long noches = ChronoUnit.DAYS.between(dto.fechaInicio(), dto.fechaFin());
        double total = calcularTotal(habitaciones, noches);

        Reserva reserva = new Reserva();
        reserva.setFechaReserva(LocalDate.now());
        reserva.setFechaInicio(dto.fechaInicio());
        reserva.setFechaFin(dto.fechaFin());
        reserva.setCliente(cliente);
        reserva.setHotel(hotel);
        reserva.setTotal(total);
        reserva.setEstado("PENDIENTE");

        for (Habitacion hab : habitaciones) {
            DetalleReserva det = new DetalleReserva();
            det.setHabitacion(hab);
            det.setPrecioNoche(hab.getPrecio());
            reserva.addDetalle(det);
        }

        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva reservarHabitaciones(Long hotelId, ReservaRequest dto) {
        return reservarHabitaciones(hotelId, dto, null);
    }

    // ==================== ACTUALIZAR RESERVA (ADMIN) ====================

    @Transactional
    public Reserva actualizarReservaAdmin(Long id, ReservaAdminUpdateDTO dto) {
        Reserva reserva = buscarPorId(id);
        validarFechas(dto.fechaInicio(), dto.fechaFin());

        reserva.setFechaInicio(dto.fechaInicio());
        reserva.setFechaFin(dto.fechaFin());
        reserva.setEstado(dto.estado());

        if (dto.hotelId() != null && !dto.hotelId().equals(reserva.getHotel().getId())) {
            Hotel nuevoHotel = hotelService.buscarPorId(dto.hotelId());
            reserva.setHotel(nuevoHotel);
        }

        Cliente cliente = reserva.getCliente();
        cliente.setNombre(dto.cliente().nombre());
        cliente.setApellido(dto.cliente().apellido());
        cliente.setEmail(dto.cliente().email());
        cliente.setDni(dto.cliente().dni());
        if (dto.cliente().telefono() != null) {
            cliente.setTelefono(dto.cliente().telefono());
        }
        clienteService.guardar(cliente);

        actualizarHabitacionesReserva(reserva, dto);

        return reservaRepository.save(reserva);
    }

    // ==================== ACTUALIZAR FECHAS (PÚBLICO) ====================

    @Transactional
    public Reserva actualizarFechas(Long id, LocalDate fechaInicio, LocalDate fechaFin) {
        Reserva reserva = buscarPorId(id);
        validarFechas(fechaInicio, fechaFin);

        long noches = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        if (noches <= 0) noches = 1;

        final long nochesFinales = noches;
        double nuevoTotal = reserva.getDetalles().stream()
                .mapToDouble(d -> d.getPrecioNoche() * nochesFinales)
                .sum();

        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);
        reserva.setTotal(nuevoTotal);

        return reservaRepository.save(reserva);
    }

    // ==================== CONFIRMAR PAGO ====================

    @Transactional
    public Reserva confirmarPago(Long id) {
        Reserva reserva = buscarPorId(id);

        if (!"PENDIENTE".equals(reserva.getEstado())) {
            throw new BusinessException(
                    "Solo se pueden confirmar reservas pendientes. Estado actual: " + reserva.getEstado(),
                    "ESTADO_INVALIDO"
            );
        }

        reserva.setEstado("CONFIRMADA");
        return reservaRepository.save(reserva);
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private void validarReservaRequest(ReservaRequest dto) {
        if (dto.fechaInicio() == null) {
            throw new ValidationException("fechaInicio", "La fecha de inicio es requerida");
        }
        if (dto.fechaFin() == null) {
            throw new ValidationException("fechaFin", "La fecha de fin es requerida");
        }
        if (dto.habitacionesIds() == null || dto.habitacionesIds().isEmpty()) {
            throw new ValidationException("habitacionesIds", "Debe seleccionar al menos una habitación");
        }
        if (dto.cliente() == null) {
            throw new ValidationException("cliente", "Los datos del cliente son requeridos");
        }
    }

    private void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDate maniana = LocalDate.now().plusDays(1);

        if (fechaInicio.isBefore(maniana)) {
            throw new BusinessException(
                    "Las reservas deben realizarse con al menos 24 horas de anticipación. La fecha mínima de check-in es: " + maniana,
                    "FECHA_MINIMA_24_HORAS"
            );
        }

        if (fechaFin.isBefore(fechaInicio) || fechaFin.isEqual(fechaInicio)) {
            throw new BusinessException(
                    "La fecha de salida debe ser posterior a la fecha de entrada",
                    "FECHA_FIN_INVALIDA"
            );
        }
    }

    private List<Habitacion> validarYObtenerHabitaciones(
            Hotel hotel, List<Long> habitacionesIds, LocalDate inicio, LocalDate fin) {

        List<Habitacion> habitaciones = habitacionService.buscarPorIds(habitacionesIds);

        if (habitaciones.size() != habitacionesIds.size()) {
            List<Long> encontrados = habitaciones.stream().map(Habitacion::getId).toList();
            List<Long> noEncontrados = habitacionesIds.stream()
                    .filter(id -> !encontrados.contains(id))
                    .toList();
            throw new EntityNotFoundException("Una o más habitaciones no fueron encontradas: " + noEncontrados);
        }

        for (Habitacion h : habitaciones) {
            if (!h.getHotel().getId().equals(hotel.getId())) {
                throw new BusinessException(
                        String.format("La habitación %d no pertenece al hotel '%s'", h.getId(), hotel.getNombre()),
                        "HABITACION_HOTEL_INCORRECTO"
                );
            }
        }

        for (Habitacion h : habitaciones) {
            if (!habitacionService.estaDisponible(h.getId(), inicio, fin)) {
                throw new ConflictException(
                        String.format("La habitación %s no está disponible para las fechas seleccionadas", h.getNumero()),
                        "HABITACION_NO_DISPONIBLE"
                );
            }
        }

        return habitaciones;
    }

    private double calcularTotal(List<Habitacion> habitaciones, long noches) {
        if (noches <= 0) noches = 1;
        final long nochesFinales = noches;
        return habitaciones.stream().mapToDouble(h -> h.getPrecio() * nochesFinales).sum();
    }

    private void actualizarHabitacionesReserva(Reserva reserva, ReservaAdminUpdateDTO dto) {
        reserva.getDetalles().clear();

        long noches = ChronoUnit.DAYS.between(dto.fechaInicio(), dto.fechaFin());
        if (noches <= 0) noches = 1;

        double total = 0;
        for (Long idHab : dto.habitaciones()) {
            Habitacion h = habitacionService.buscarPorId(idHab);

            DetalleReserva det = new DetalleReserva();
            det.setReserva(reserva);
            det.setHabitacion(h);
            det.setPrecioNoche(h.getPrecio());
            reserva.getDetalles().add(det);

            total += h.getPrecio() * noches;
        }

        reserva.setTotal(total);
    }
}
