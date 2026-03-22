package com.osen.sistema_reservas.core.reserva;

import com.osen.sistema_reservas.auth.domain.model.Role;
import com.osen.sistema_reservas.auth.domain.model.User;
import com.osen.sistema_reservas.core.departamento.domain.model.Departamento;
import com.osen.sistema_reservas.core.detalle_reserva.domain.model.DetalleReserva;
import com.osen.sistema_reservas.core.habitacion.domain.model.Habitacion;
import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import com.osen.sistema_reservas.core.tipoHabitacion.domain.model.TipoHabitacion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestFixtures {

    public static Role createRole() {
        Role role = new Role();
        role.setRoleId(1L);
        role.setRolename("CLIENTE");
        return role;
    }

    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encoded_password");
        user.setNombre("Juan");
        user.setApellido("Perez");
        user.setDni("12345678");
        user.setRole(createRole());
        user.setActivo(true);
        return user;
    }

    public static Departamento createDepartamento() {
        Departamento dep = new Departamento();
        dep.setId(1L);
        dep.setNombre("Lima");
        dep.setDetalle("Capital del Peru");
        return dep;
    }

    public static TipoHabitacion createTipoHabitacion() {
        TipoHabitacion tipo = new TipoHabitacion();
        tipo.setId(1L);
        tipo.setNombre("Individual");
        tipo.setDescripcion("Habitacion individual");
        tipo.setCapacidad(1);
        return tipo;
    }

    public static Hotel createHotel() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setNombre("Hotel Test");
        hotel.setDireccion("Av. Test 123");
        hotel.setDepartamento(createDepartamento());
        hotel.setHabitaciones(new ArrayList<>());
        return hotel;
    }

    public static Habitacion createHabitacion(Hotel hotel) {
        Habitacion hab = new Habitacion();
        hab.setId(1L);
        hab.setNumero("101");
        hab.setEstado("DISPONIBLE");
        hab.setPrecio(100.0);
        hab.setTipoHabitacion(createTipoHabitacion());
        hab.setHotel(hotel);
        return hab;
    }

    public static Habitacion createHabitacionOcupada(Hotel hotel) {
        Habitacion hab = createHabitacion(hotel);
        hab.setId(2L);
        hab.setNumero("102");
        hab.setEstado("OCUPADA");
        return hab;
    }

    public static Reserva createReserva(Hotel hotel, User user) {
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setFechaReserva(LocalDate.now());
        reserva.setFechaInicio(LocalDate.now().plusDays(1));
        reserva.setFechaFin(LocalDate.now().plusDays(3));
        reserva.setTotal(200.0);
        reserva.setEstado("CONFIRMADA");
        reserva.setHotel(hotel);
        reserva.setUser(user);
        reserva.setDetalles(new ArrayList<>());
        return reserva;
    }

    public static Reserva createReservaPendiente(Hotel hotel, User user) {
        Reserva reserva = createReserva(hotel, user);
        reserva.setId(2L);
        reserva.setEstado("PENDIENTE");
        reserva.setTotal(150.0);
        return reserva;
    }

    public static DetalleReserva createDetalleReserva(Reserva reserva, Habitacion habitacion) {
        DetalleReserva detalle = new DetalleReserva();
        detalle.setId(1L);
        detalle.setReserva(reserva);
        detalle.setHabitacion(habitacion);
        detalle.setPrecioNoche(habitacion.getPrecio());
        return detalle;
    }

    public static List<Reserva> createReservasCollection(int count, Hotel hotel, User user) {
        List<Reserva> reservas = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Reserva r = createReserva(hotel, user);
            r.setId((long) i);
            reservas.add(r);
        }
        return reservas;
    }
}
