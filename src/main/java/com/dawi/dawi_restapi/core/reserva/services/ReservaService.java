package com.dawi.dawi_restapi.core.reserva.services;

import com.dawi.dawi_restapi.core.cliente.dtos.ClienteRequest;
import com.dawi.dawi_restapi.core.cliente.dtos.ClienteResponse;
import com.dawi.dawi_restapi.core.cliente.model.Cliente;
import com.dawi.dawi_restapi.core.cliente.repository.ClienteRepository;
import com.dawi.dawi_restapi.core.cliente.service.ClienteService;
import com.dawi.dawi_restapi.core.detalle_reserva.model.DetalleReserva;
import com.dawi.dawi_restapi.core.habitacion.models.Habitacion;
import com.dawi.dawi_restapi.core.habitacion.repository.HabitacionRepository;
import com.dawi.dawi_restapi.core.hotel.dtos.HotelResponse;
import com.dawi.dawi_restapi.core.hotel.model.Hotel;
import com.dawi.dawi_restapi.core.hotel.repositories.HotelRepository;
import com.dawi.dawi_restapi.core.hotel.services.HotelService;
import com.dawi.dawi_restapi.core.reserva.dtos.ReservaRequest;
import com.dawi.dawi_restapi.core.reserva.dtos.ReservaResponse;
import com.dawi.dawi_restapi.core.reserva.models.Reserva;
import com.dawi.dawi_restapi.core.reserva.repositories.ReservaRepository;
import com.dawi.dawi_restapi.helpers.mappers.ClienteMapper;
import com.dawi.dawi_restapi.helpers.mappers.ReservaMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;

    private final ClienteService clienteService;

    private final ClienteRepository clienteRepository;

    private final HotelService hotelService;

    private final HotelRepository hotelRepository;

    private final HabitacionRepository habitacionRepository;


    public Reserva guardar(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }

    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }

    public List<Reserva> buscarReservasPorDniCliente(String dni){
        return reservaRepository.findByClienteDni(dni);
    }

    @Transactional
    public Reserva reservarHabitaciones(Long hotelId, ReservaRequest dto) {

        // 1. Obtener hotel como ENTIDAD
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));

        // 2. Validar y obtener habitaciones
        List<Habitacion> habitaciones = validarYObtenerHabitaciones(
                hotel,
                dto.habitacionesIds(),
                dto.fechaInicio(),
                dto.fechaFin()
        );

        // 3. Crear o reutilizar cliente
        Cliente cliente = this.crearOCrearCliente(dto.cliente());

        // 4. Calcular total
        long noches = ChronoUnit.DAYS.between(dto.fechaInicio(), dto.fechaFin());
        double total = calcularTotal(habitaciones, noches);

        // 5. Crear la reserva
        Reserva reserva = new Reserva();
        reserva.setFechaReserva(LocalDate.now());
        reserva.setFechaInicio(dto.fechaInicio());
        reserva.setFechaFin(dto.fechaFin());
        reserva.setCliente(cliente);
        reserva.setHotel(hotel);
        reserva.setTotal(total);
        reserva.setEstado("CONFIRMADA");

        // 6. Crear detalles
        for (Habitacion hab : habitaciones) {
            DetalleReserva det = new DetalleReserva();
            det.setHabitacion(hab);
            det.setPrecioNoche(hab.getPrecio());
            reserva.addDetalle(det);
        }

        // 7. Guardar (cascade salvará los detalles)
        return reservaRepository.save(reserva);
    }


    private Cliente crearOCrearCliente(ClienteRequest dto) {
        // Buscar cliente existente por DNI
        Optional<Cliente> existente = clienteRepository.findByDni(dto.dni());

        if (existente.isPresent()) {
            // Actualizar datos del cliente existente si es necesario
            Cliente cliente = existente.get();
            cliente.setNombre(dto.nombre());
            cliente.setApellido(dto.apellido());
            cliente.setEmail(dto.email());
            if (dto.telefono() != null && !dto.telefono().isBlank()) {
                cliente.setTelefono(dto.telefono());
            }
            return clienteRepository.save(cliente);
        }

        // Crear nuevo cliente
        Cliente nuevo = new Cliente();
        nuevo.setNombre(dto.nombre());
        nuevo.setApellido(dto.apellido());
        nuevo.setDni(dto.dni());
        nuevo.setEmail(dto.email());
        nuevo.setTelefono(dto.telefono() != null && !dto.telefono().isBlank() ? dto.telefono() : "");
        return clienteRepository.save(nuevo);
    }



    // Métodos privados: validarFechas, validarYObtenerHabitaciones, calcularTotal, crearReserva... para mantener el flujo principal limpio
    private double calcularTotal(List<Habitacion> habitaciones, long noches) {
        return habitaciones.stream()
                .mapToDouble(h -> h.getPrecio() * noches)
                .sum();
    }

    private List<Habitacion> validarYObtenerHabitaciones(
            Hotel hotel,
            List<Long> habitacionesIds,
            LocalDate inicio,
            LocalDate fin) {

        if (habitacionesIds == null || habitacionesIds.isEmpty()) {
            throw new RuntimeException("Debe seleccionar al menos una habitación.");
        }

        List<Habitacion> habitaciones = habitacionRepository.findAllById(habitacionesIds);

        if (habitaciones.size() != habitacionesIds.size()) {
            throw new RuntimeException("Una o más habitaciones no existen.");
        }

        // Validar que pertenecen al hotel
        for (Habitacion h : habitaciones) {
            if (!h.getHotel().getId().equals(hotel.getId())) {
                throw new RuntimeException("La habitación " + h.getId() + " no pertenece al hotel " + hotel.getNombre());
            }
        }

//         Validar disponibilidad
//    for (Habitacion h : habitaciones) {
//        boolean ocupada = reservaRepository.existeReservaParaHabitacion(h.getId(), inicio, fin);
//        if (ocupada) {
//            throw new RuntimeException("La habitación " + h.getId() + " no está disponible.");
//        }
//    }

        return habitaciones;
    }
}