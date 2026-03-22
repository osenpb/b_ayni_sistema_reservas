package com.osen.sistema_reservas.core.habitacion;

import com.osen.sistema_reservas.core.habitacion.application.service.HabitacionService;
import com.osen.sistema_reservas.core.habitacion.domain.model.Habitacion;
import com.osen.sistema_reservas.core.habitacion.domain.port.out.HabitacionRepository;
import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import com.osen.sistema_reservas.core.tipoHabitacion.domain.model.TipoHabitacion;
import com.osen.sistema_reservas.shared.helpers.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HabitacionService - Tests de rendimiento y disponibilidad")
class HabitacionServiceTest {

    @Mock
    private HabitacionRepository habitacionRepository;

    @InjectMocks
    private HabitacionService habitacionService;

    private Hotel hotel;
    private Habitacion habitacion;
    private TipoHabitacion tipoHabitacion;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setNombre("Hotel Test");

        tipoHabitacion = new TipoHabitacion();
        tipoHabitacion.setId(1L);
        tipoHabitacion.setNombre("Individual");

        habitacion = new Habitacion();
        habitacion.setId(1L);
        habitacion.setNumero("101");
        habitacion.setEstado("DISPONIBLE");
        habitacion.setPrecio(100.0);
        habitacion.setHotel(hotel);
        habitacion.setTipoHabitacion(tipoHabitacion);
    }

    @Nested
    @DisplayName("Disponibilidad de habitaciones")
    class DisponibilidadTests {

        @Test
        @DisplayName("estaDisponible retorna true cuando no hay conflictos de fechas")
        void shouldReturnTrueWhenNoConflicts() {
            LocalDate inicio = LocalDate.now().plusDays(1);
            LocalDate fin = LocalDate.now().plusDays(3);

            when(habitacionRepository.contarReservasPorHabitacionYFechas(1L, inicio, fin)).thenReturn(0);

            boolean disponible = habitacionService.estaDisponible(1L, inicio, fin);

            assertTrue(disponible);
            verify(habitacionRepository).contarReservasPorHabitacionYFechas(1L, inicio, fin);
        }

        @Test
        @DisplayName("estaDisponible retorna false cuando hay conflictos de fechas")
        void shouldReturnFalseWhenHasConflicts() {
            LocalDate inicio = LocalDate.now().plusDays(1);
            LocalDate fin = LocalDate.now().plusDays(3);

            when(habitacionRepository.contarReservasPorHabitacionYFechas(1L, inicio, fin)).thenReturn(1);

            boolean disponible = habitacionService.estaDisponible(1L, inicio, fin);

            assertFalse(disponible);
        }

        @Test
        @DisplayName("obtenerCantidadDisponible retorna el conteo de habitaciones libres")
        void shouldReturnCantidadDisponible() {
            LocalDate inicio = LocalDate.now().plusDays(1);
            LocalDate fin = LocalDate.now().plusDays(3);

            when(habitacionRepository.contarDisponibles(1L, inicio, fin)).thenReturn(5);

            int disponible = habitacionService.obtenerCantidadDisponible(1L, inicio, fin);

            assertEquals(5, disponible);
        }
    }

    @Nested
    @DisplayName("Metodos de consulta")
    class QueryTests {

        @Test
        @DisplayName("buscarPorId lanza EntityNotFoundException si no existe")
        void shouldThrowWhenNotFound() {
            when(habitacionRepository.findById(999L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> habitacionService.buscarPorId(999L));

            assertTrue(ex.getMessage().contains("999"));
        }

        @Test
        @DisplayName("buscarPorIdOptional retorna Optional vacio si no existe")
        void shouldReturnEmptyOptional() {
            when(habitacionRepository.findById(999L)).thenReturn(Optional.empty());

            Optional<Habitacion> result = habitacionService.buscarPorIdOptional(999L);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("buscarPorHotelId delega al repositorio")
        void shouldDelegateToRepository() {
            List<Habitacion> habitaciones = List.of(habitacion);
            when(habitacionRepository.findByHotelId(1L)).thenReturn(habitaciones);

            List<Habitacion> result = habitacionService.buscarPorHotelId(1L);

            assertEquals(1, result.size());
            verify(habitacionRepository).findByHotelId(1L);
        }

        @Test
        @DisplayName("eliminarPorId lanza EntityNotFoundException si no existe")
        void shouldThrowWhenEliminarNonExistent() {
            when(habitacionRepository.existsById(999L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> habitacionService.eliminarPorId(999L));

            verify(habitacionRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("contarTodas delega al repositorio")
        void shouldDelegateCountToRepository() {
            when(habitacionRepository.count()).thenReturn(50L);

            long count = habitacionService.contarTodas();

            assertEquals(50L, count);
        }

        @Test
        @DisplayName("buscarDisponiblesPorHotelId delega al repositorio")
        void shouldDelegateDisponiblesToRepository() {
            List<Habitacion> disponibles = List.of(habitacion);
            when(habitacionRepository.findDisponiblesByHotelId(1L)).thenReturn(disponibles);

            List<Habitacion> result = habitacionService.buscarDisponiblesPorHotelId(1L);

            assertEquals(1, result.size());
        }
    }
}
