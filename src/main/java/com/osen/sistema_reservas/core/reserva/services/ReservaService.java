package com.osen.sistema_reservas.core.reserva.services;

import com.osen.sistema_reservas.auth.domain.models.User;
import com.osen.sistema_reservas.core.cliente.model.Cliente;
import com.osen.sistema_reservas.core.cliente.service.ClienteService;
import com.osen.sistema_reservas.core.detalle_reserva.model.DetalleReserva;
import com.osen.sistema_reservas.core.habitacion.models.Habitacion;
import com.osen.sistema_reservas.core.habitacion.service.HabitacionService;
import com.osen.sistema_reservas.core.hotel.model.Hotel;
import com.osen.sistema_reservas.core.hotel.services.HotelService;
import com.osen.sistema_reservas.core.reserva.dtos.ReservaAdminUpdateDTO;
import com.osen.sistema_reservas.core.reserva.dtos.ReservaRequest;
import com.osen.sistema_reservas.core.reserva.models.Reserva;
import com.osen.sistema_reservas.core.reserva.repositories.ReservaRepository;
import com.osen.sistema_reservas.helpers.exceptions.BusinessException;
import com.osen.sistema_reservas.helpers.exceptions.ConflictException;
import com.osen.sistema_reservas.helpers.exceptions.EntityNotFoundException;
import com.osen.sistema_reservas.helpers.exceptions.ValidationException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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

    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }

    public Reserva buscarPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva con ID:" + id));
    }

    public List<Reserva> buscarReservasPorDniCliente(String dni) {
        return reservaRepository.findByClienteDni(dni);
    }

    public List<Reserva> buscarReservasPorUsuarioId(Long userId) {
        return reservaRepository.findByUsuarioId(userId);
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
        // 1. Validar request
        validarReservaRequest(dto);

        // 2. Obtener hotel
        Hotel hotel = hotelService.buscarPorId(hotelId);

        // 3. Validar fechas
        validarFechas(dto.fechaInicio(), dto.fechaFin());

        // 4. Validar y obtener habitaciones
        List<Habitacion> habitaciones = validarYObtenerHabitaciones(
                hotel,
                dto.habitacionesIds(),
                dto.fechaInicio(),
                dto.fechaFin()
        );

        // 5. Crear o actualizar cliente (vinculando con usuario si existe)
        Cliente cliente = clienteService.crearOActualizar(dto.cliente(), user);

        // 6. Calcular total
        long noches = ChronoUnit.DAYS.between(dto.fechaInicio(), dto.fechaFin());
        double total = calcularTotal(habitaciones, noches);

        // 7. Crear la reserva con estado PENDIENTE (se confirma al pagar)
        Reserva reserva = new Reserva();
        reserva.setFechaReserva(LocalDate.now());
        reserva.setFechaInicio(dto.fechaInicio());
        reserva.setFechaFin(dto.fechaFin());
        reserva.setCliente(cliente);
        reserva.setHotel(hotel);
        reserva.setTotal(total);
        reserva.setEstado("PENDIENTE");

        // 8. Crear detalles
        for (Habitacion hab : habitaciones) {
            DetalleReserva det = new DetalleReserva();
            det.setHabitacion(hab);
            det.setPrecioNoche(hab.getPrecio());
            reserva.addDetalle(det);
        }

        // 9. Guardar
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

        // Validar fechas
        validarFechas(dto.fechaInicio(), dto.fechaFin());

        // Actualizar fechas y estado
        reserva.setFechaInicio(dto.fechaInicio());
        reserva.setFechaFin(dto.fechaFin());
        reserva.setEstado(dto.estado());

        // Actualizar hotel si cambió
        if (dto.hotelId() != null && !dto.hotelId().equals(reserva.getHotel().getId())) {
            Hotel nuevoHotel = hotelService.buscarPorId(dto.hotelId());
            reserva.setHotel(nuevoHotel);
        }

        // Actualizar cliente
        Cliente cliente = reserva.getCliente();
        cliente.setNombre(dto.cliente().nombre());
        cliente.setApellido(dto.cliente().apellido());
        cliente.setEmail(dto.cliente().email());
        cliente.setDni(dto.cliente().dni());
        if (dto.cliente().telefono() != null) {
            cliente.setTelefono(dto.cliente().telefono());
        }
        clienteService.guardar(cliente);

        // Actualizar habitaciones
        actualizarHabitacionesReserva(reserva, dto);

        return reservaRepository.save(reserva);
    }

    // ==================== MÉTODOS PRIVADOS DE VALIDACIÓN ====================

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
        LocalDate hoy = LocalDate.now();
        LocalDate maniana = hoy.plusDays(1);

        // Validar que la fecha de inicio sea al menos mañana (24 horas de anticipación)
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
            Hotel hotel,
            List<Long> habitacionesIds,
            LocalDate inicio,
            LocalDate fin) {

        List<Habitacion> habitaciones = habitacionService.buscarPorIds(habitacionesIds);

        // Verificar que todas las habitaciones existan
        if (habitaciones.size() != habitacionesIds.size()) {
            List<Long> encontrados = habitaciones.stream().map(Habitacion::getId).toList();
            List<Long> noEncontrados = habitacionesIds.stream()
                    .filter(id -> !encontrados.contains(id))
                    .toList();
            throw new EntityNotFoundException(
                    "Una o más habitaciones no fueron encontradas: " + noEncontrados
            );
        }

        // Validar que pertenecen al hotel
        for (Habitacion h : habitaciones) {
            if (!h.getHotel().getId().equals(hotel.getId())) {
                throw new BusinessException(
                        String.format("La habitación %d no pertenece al hotel '%s'",
                                h.getId(), hotel.getNombre()),
                        "HABITACION_HOTEL_INCORRECTO"
                );
            }
        }

        // Validar disponibilidad
        for (Habitacion h : habitaciones) {
            if (!habitacionService.estaDisponible(h.getId(), inicio, fin)) {
                throw new ConflictException(
                        String.format("La habitación %s no está disponible para las fechas seleccionadas",
                                h.getNumero()),
                        "HABITACION_NO_DISPONIBLE"
                );
            }
        }

        return habitaciones;
    }

    // ==================== MÉTODOS PRIVADOS DE CÁLCULO ====================

    private double calcularTotal(List<Habitacion> habitaciones, long noches) {
        if (noches <= 0) noches = 1;
        
        final long nochesFinales = noches;
        return habitaciones.stream()
                .mapToDouble(h -> h.getPrecio() * nochesFinales)
                .sum();
    }

    private void actualizarHabitacionesReserva(Reserva reserva, ReservaAdminUpdateDTO dto) {
        // Limpiar detalles anteriores
        reserva.getDetalles().clear();

        // Calcular noches
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

    // ==================== ACTUALIZAR FECHAS (PÚBLICO) ====================

    @Transactional
    public Reserva actualizarFechas(Long id, LocalDate fechaInicio, LocalDate fechaFin) {
        Reserva reserva = buscarPorId(id);

        // Validar fechas
        validarFechas(fechaInicio, fechaFin);

        // Recalcular total
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

    // ==================== BUSCAR RESERVAS CON FILTROS ====================

    public List<Reserva> buscarReservasPorUsuarioIdYFechas(Long userId, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Reserva> reservas = reservaRepository.findByUsuarioId(userId);
        
        // Filtrar por fechas si se proporcionan
        if (fechaInicio != null && fechaFin != null) {
            reservas = reservas.stream()
                    .filter(r -> !r.getFechaInicio().isBefore(fechaInicio) && !r.getFechaFin().isAfter(fechaFin))
                    .toList();
        } else if (fechaInicio != null) {
            reservas = reservas.stream()
                    .filter(r -> !r.getFechaInicio().isBefore(fechaInicio))
                    .toList();
        } else if (fechaFin != null) {
            reservas = reservas.stream()
                    .filter(r -> !r.getFechaFin().isAfter(fechaFin))
                    .toList();
        }
        
        return reservas;
    }
}
