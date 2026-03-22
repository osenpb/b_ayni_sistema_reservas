package com.osen.sistema_reservas.core.dashboard;

import com.osen.sistema_reservas.auth.domain.model.Role;
import com.osen.sistema_reservas.auth.domain.model.User;
import com.osen.sistema_reservas.core.departamento.domain.model.Departamento;
import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DashboardTestFixtures {

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

    public static Hotel createHotel() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setNombre("Hotel Test");
        hotel.setDireccion("Av. Test 123");
        hotel.setDepartamento(createDepartamento());
        hotel.setHabitaciones(new ArrayList<>());
        return hotel;
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

    public static List<Reserva> createReservasCollection(int count, Hotel hotel, User user) {
        List<Reserva> reservas = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Reserva r = createReserva(hotel, user);
            r.setId((long) i);
            reservas.add(r);
        }
        return reservas;
    }

    public static List<Reserva> createReservasPorEstados(Hotel hotel, User user) {
        List<Reserva> reservas = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Reserva r = createReserva(hotel, user);
            r.setId((long) i);
            r.setEstado("CONFIRMADA");
            reservas.add(r);
        }
        for (int i = 5; i < 10; i++) {
            Reserva r = createReserva(hotel, user);
            r.setId((long) i);
            r.setEstado("PENDIENTE");
            reservas.add(r);
        }
        return reservas;
    }

    public static List<Reserva> createReservasConTotales(Hotel hotel, User user) {
        List<Reserva> reservas = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Reserva r = createReserva(hotel, user);
            r.setId((long) i);
            r.setTotal(100.0 + i * 50);
            r.setEstado("CONFIRMADA");
            reservas.add(r);
        }
        Reserva cancelada = createReserva(hotel, user);
        cancelada.setId(100L);
        cancelada.setTotal(500.0);
        cancelada.setEstado("CANCELADA");
        reservas.add(cancelada);

        Reserva pendiente = createReserva(hotel, user);
        pendiente.setId(101L);
        pendiente.setTotal(300.0);
        pendiente.setEstado("PENDIENTE");
        reservas.add(pendiente);

        return reservas;
    }
}
