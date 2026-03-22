package com.osen.sistema_reservas.core.hotel;

import com.osen.sistema_reservas.core.departamento.application.service.DepartamentoService;
import com.osen.sistema_reservas.core.departamento.domain.model.Departamento;
import com.osen.sistema_reservas.core.habitacion.application.dtos.HabitacionResponse;
import com.osen.sistema_reservas.core.habitacion.application.service.HabitacionService;
import com.osen.sistema_reservas.core.habitacion.domain.model.Habitacion;
import com.osen.sistema_reservas.core.hotel.application.service.HotelService;
import com.osen.sistema_reservas.core.hotel.application.dtos.HotelRequest;
import com.osen.sistema_reservas.core.hotel.application.dtos.HotelResponse;
import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import com.osen.sistema_reservas.core.hotel.domain.port.out.HotelRepository;
import com.osen.sistema_reservas.core.tipoHabitacion.application.service.TipoHabitacionService;
import com.osen.sistema_reservas.core.tipoHabitacion.domain.model.TipoHabitacion;
import com.osen.sistema_reservas.shared.helpers.exceptions.EntityNotFoundException;
import com.osen.sistema_reservas.shared.helpers.exceptions.ValidationException;
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
@DisplayName("HotelService - Tests de rendimiento y N+1")
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private DepartamentoService departamentoService;

    @Mock
    private TipoHabitacionService tipoHabitacionService;

    @Mock
    private HabitacionService habitacionService;

    @InjectMocks
    private HotelService hotelService;

    private Hotel hotel;
    private Departamento departamento;
    private Habitacion habitacion;
    private TipoHabitacion tipoHabitacion;

    @BeforeEach
    void setUp() {
        departamento = new Departamento();
        departamento.setId(1L);
        departamento.setNombre("Lima");

        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setNombre("Hotel Test");
        hotel.setDireccion("Av. Test 123");
        hotel.setDepartamento(departamento);
        hotel.setHabitaciones(new ArrayList<>());

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
    @DisplayName("N+1 Query - Habitaciones disponibles")
    class N1QueryTests {

        @Test
        @DisplayName("CUELLO DE BOTELLA: obtenerHabitacionesDisponibles hace N queries para N habitaciones")
        void shouldCheckAvailabilityForEachHabitacion() {
            List<Habitacion> habitaciones = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Habitacion h = new Habitacion();
                h.setId((long) (i + 1));
                h.setNumero("10" + (i + 1));
                h.setEstado("DISPONIBLE");
                h.setPrecio(100.0);
                h.setHotel(hotel);
                h.setTipoHabitacion(tipoHabitacion);
                habitaciones.add(h);
            }

            LocalDate inicio = LocalDate.now().plusDays(1);
            LocalDate fin = LocalDate.now().plusDays(3);

            when(habitacionService.buscarDisponiblesPorHotelId(1L)).thenReturn(habitaciones);
            when(habitacionService.estaDisponible(anyLong(), eq(inicio), eq(fin))).thenReturn(true);

            List<HabitacionResponse> result = hotelService.obtenerHabitacionesDisponibles(1L, inicio, fin);

            verify(habitacionService, times(5)).estaDisponible(anyLong(), eq(inicio), eq(fin));
            assertEquals(5, result.size());
        }

        @Test
        @DisplayName("Debe filtrar habitaciones no disponibles en Java (rendimiento pobre)")
        void shouldFilterUnavailableInJava() {
            List<Habitacion> habitaciones = List.of(habitacion);
            LocalDate inicio = LocalDate.now().plusDays(1);
            LocalDate fin = LocalDate.now().plusDays(3);

            when(habitacionService.buscarDisponiblesPorHotelId(1L)).thenReturn(habitaciones);
            when(habitacionService.estaDisponible(eq(1L), eq(inicio), eq(fin))).thenReturn(false);

            List<HabitacionResponse> result = hotelService.obtenerHabitacionesDisponibles(1L, inicio, fin);

            assertEquals(0, result.size());
        }
    }

    @Nested
    @DisplayName("Metodos de consulta")
    class QueryTests {

        @Test
        @DisplayName("buscarPorId lanza EntityNotFoundException si no existe")
        void shouldThrowWhenNotFound() {
            when(hotelRepository.findByIdWithRelations(999L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> hotelService.buscarPorId(999L));

            assertTrue(ex.getMessage().contains("999"));
        }

        @Test
        @DisplayName("listarHoteles usa cache y retorna DTOs")
        void shouldReturnDTOsAndBeCacheable() {
            List<Hotel> hoteles = List.of(hotel);
            when(hotelRepository.findAllWithRelations()).thenReturn(hoteles);

            List<HotelResponse> result = hotelService.listarHoteles();

            assertFalse(result.isEmpty());
            assertEquals("Hotel Test", result.get(0).nombre());
        }

        @Test
        @DisplayName("listarPorDepartamentoId filtra por departamento")
        void shouldFilterByDepartamento() {
            when(hotelRepository.findByDepartamentoIdWithRelations(1L)).thenReturn(List.of(hotel));

            List<HotelResponse> result = hotelService.listarPorDepartamentoId(1L);

            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("CRUD operations")
    class CrudTests {

        @Test
        @DisplayName("guardar valida nombre requerido")
        void shouldValidateNombreRequerido() {
            HotelRequest request = new HotelRequest("", "Av. Direccion", 1L, List.of(), null);

            ValidationException ex = assertThrows(ValidationException.class,
                    () -> hotelService.guardar(request));

            assertTrue(ex.getFieldErrors().containsKey("nombre"));
        }

        @Test
        @DisplayName("guardar valida departamento requerido")
        void shouldValidateDepartamentoRequerido() {
            HotelRequest request = new HotelRequest("Hotel", "Av. Direccion", null, List.of(), null);

            ValidationException ex = assertThrows(ValidationException.class,
                    () -> hotelService.guardar(request));

            assertTrue(ex.getFieldErrors().containsKey("departamentoId"));
        }

        @Test
        @DisplayName("eliminar lanza EntityNotFoundException si no existe")
        void shouldThrowWhenEliminarNonExistent() {
            when(hotelRepository.existsById(999L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> hotelService.eliminar(999L));

            verify(hotelRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("buscarPorIdResponse retorna DTO")
        void shouldReturnDTOResponse() {
            when(hotelRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(hotel));

            HotelResponse result = hotelService.buscarPorIdResponse(1L);

            assertEquals("Hotel Test", result.nombre());
        }
    }
}
