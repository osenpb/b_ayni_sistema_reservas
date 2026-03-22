package com.osen.sistema_reservas.core.reserva;

import com.osen.sistema_reservas.auth.domain.model.User;
import com.osen.sistema_reservas.core.habitacion.application.service.HabitacionService;
import com.osen.sistema_reservas.core.habitacion.domain.model.Habitacion;
import com.osen.sistema_reservas.core.hotel.application.service.HotelService;
import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import com.osen.sistema_reservas.core.reserva.application.dtos.ReservaAdminUpdateDTO;
import com.osen.sistema_reservas.core.reserva.application.dtos.ReservaRequest;
import com.osen.sistema_reservas.core.reserva.application.service.ReservaService;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import com.osen.sistema_reservas.core.reserva.domain.port.out.ReservaRepository;
import com.osen.sistema_reservas.shared.helpers.exceptions.BusinessException;
import com.osen.sistema_reservas.shared.helpers.exceptions.ConflictException;
import com.osen.sistema_reservas.shared.helpers.exceptions.EntityNotFoundException;
import com.osen.sistema_reservas.shared.helpers.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservaService - Tests de rendimiento y logicade negocio")
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private HotelService hotelService;

    @Mock
    private HabitacionService habitacionService;

    @InjectMocks
    private ReservaService reservaService;

    private Hotel hotel;
    private User user;
    private Habitacion habitacion;

    @BeforeEach
    void setUp() {
        hotel = TestFixtures.createHotel();
        user = TestFixtures.createUser();
        habitacion = TestFixtures.createHabitacion(hotel);
    }

    @Nested
    @DisplayName("N+1 Query - Verificacion de disponibilidad de habitaciones")
    class AvailabilityN1Tests {

        @Test
        @DisplayName("Debe hacer N queries de disponibilidad para N habitaciones - CUELLO DE BOTELLA")
        void shouldQueryAvailabilityForEachHabitacion() {
            List<Long> habitacionesIds = List.of(1L, 2L, 3L, 4L, 5L);
            List<Habitacion> habitaciones = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                Habitacion h = TestFixtures.createHabitacion(hotel);
                h.setId((long) (i + 1));
                h.setNumero("10" + (i + 1));
                habitaciones.add(h);
            }

            when(hotelService.buscarPorId(1L)).thenReturn(hotel);
            when(habitacionService.buscarPorIds(habitacionesIds)).thenReturn(habitaciones);
            when(habitacionService.estaDisponible(anyLong(), any(), any())).thenReturn(true);
            when(reservaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            LocalDate inicio = LocalDate.now().plusDays(2);
            LocalDate fin = LocalDate.now().plusDays(4);
            ReservaRequest request = new ReservaRequest(inicio, fin, habitacionesIds);

            reservaService.reservarHabitaciones(1L, request, user);

            verify(habitacionService, times(5)).estaDisponible(anyLong(), eq(inicio), eq(fin));
        }

        @Test
        @DisplayName("CONFLICT: Habitacion no disponible lanza ConflictException")
        void shouldThrowConflictWhenHabitacionNoDisponible() {
            when(hotelService.buscarPorId(1L)).thenReturn(hotel);
            when(habitacionService.buscarPorIds(List.of(1L))).thenReturn(List.of(habitacion));
            when(habitacionService.estaDisponible(eq(1L), any(), any())).thenReturn(false);

            LocalDate inicio = LocalDate.now().plusDays(2);
            LocalDate fin = LocalDate.now().plusDays(4);
            ReservaRequest request = new ReservaRequest(inicio, fin, List.of(1L));

            ConflictException ex = assertThrows(ConflictException.class,
                    () -> reservaService.reservarHabitaciones(1L, request, user));

            assertTrue(ex.getMessage().contains("no está disponible"));
            assertEquals("HABITACION_NO_DISPONIBLE", ex.getConflictType());
        }

        @Test
        @DisplayName("CONFLICT: Habitacion no pertenece al hotel lanza BusinessException")
        void shouldThrowWhenHabitacionNoPerteneceAlHotel() {
            Hotel otroHotel = TestFixtures.createHotel();
            otroHotel.setId(99L);
            otroHotel.setNombre("Otro Hotel");

            Habitacion habOtroHotel = TestFixtures.createHabitacion(otroHotel);

            when(hotelService.buscarPorId(1L)).thenReturn(hotel);
            when(habitacionService.buscarPorIds(List.of(1L))).thenReturn(List.of(habOtroHotel));

            LocalDate inicio = LocalDate.now().plusDays(2);
            LocalDate fin = LocalDate.now().plusDays(4);
            ReservaRequest request = new ReservaRequest(inicio, fin, List.of(1L));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> reservaService.reservarHabitaciones(1L, request, user));

            assertEquals("HABITACION_HOTEL_INCORRECTO", ex.getErrorCode());
        }

        @Test
        @DisplayName("NOT_FOUND: Alguna habitacion no existe lanza EntityNotFoundException")
        void shouldThrowWhenHabitacionNoExiste() {
            when(hotelService.buscarPorId(1L)).thenReturn(hotel);
            when(habitacionService.buscarPorIds(List.of(1L, 2L))).thenReturn(List.of(habitacion));

            LocalDate inicio = LocalDate.now().plusDays(2);
            LocalDate fin = LocalDate.now().plusDays(4);
            ReservaRequest request = new ReservaRequest(inicio, fin, List.of(1L, 2L));

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> reservaService.reservarHabitaciones(1L, request, user));

            assertTrue(ex.getMessage().contains("no fueron encontradas"));
        }
    }

    @Nested
    @DisplayName("Validacion de fechas")
    class FechaValidacionTests {

        @Test
        @DisplayName("VALIDATION: Fecha inicio nula lanza ValidationException")
        void shouldThrowWhenFechaInicioNull() {
            LocalDate fin = LocalDate.now().plusDays(4);
            ReservaRequest request = new ReservaRequest(null, fin, List.of(1L));

            ValidationException ex = assertThrows(ValidationException.class,
                    () -> reservaService.reservarHabitaciones(1L, request, user));

            assertTrue(ex.getFieldErrors().containsKey("fechaInicio"));
        }

        @Test
        @DisplayName("VALIDATION: Lista de habitaciones vacia lanza ValidationException")
        void shouldThrowWhenHabitacionesEmpty() {
            LocalDate inicio = LocalDate.now().plusDays(2);
            LocalDate fin = LocalDate.now().plusDays(4);
            ReservaRequest request = new ReservaRequest(inicio, fin, List.of());

            ValidationException ex = assertThrows(ValidationException.class,
                    () -> reservaService.reservarHabitaciones(1L, request, user));

            assertTrue(ex.getFieldErrors().containsKey("habitacionesIds"));
        }

        @Test
        @DisplayName("BUSINESS: Reserva con menos de 24h de anticipacion lanza BusinessException")
        void shouldThrowWhenLessThan24HoursAnticipation() {
            when(hotelService.buscarPorId(1L)).thenReturn(hotel);

            LocalDate hoy = LocalDate.now();
            LocalDate manana = LocalDate.now().plusDays(1);
            ReservaRequest request = new ReservaRequest(hoy, manana, List.of(1L));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> reservaService.reservarHabitaciones(1L, request, user));

            assertEquals("FECHA_MINIMA_24_HORAS", ex.getErrorCode());
        }

        @Test
        @DisplayName("BUSINESS: Fecha fin antes o igual a fecha inicio lanza BusinessException")
        void shouldThrowWhenFechaFinBeforeFechaInicio() {
            when(hotelService.buscarPorId(1L)).thenReturn(hotel);

            LocalDate inicio = LocalDate.now().plusDays(5);
            LocalDate fin = LocalDate.now().plusDays(3);
            ReservaRequest request = new ReservaRequest(inicio, fin, List.of(1L));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> reservaService.reservarHabitaciones(1L, request, user));

            assertEquals("FECHA_FIN_INVALIDA", ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Calculo de totales")
    class CalculoTotalTests {

        @Test
        @DisplayName("Debe calcular correctamente el total: precio * noches * habitaciones")
        void shouldCalculateTotalCorrectly() {
            Habitacion h1 = TestFixtures.createHabitacion(hotel);
            h1.setPrecio(100.0);
            Habitacion h2 = TestFixtures.createHabitacion(hotel);
            h2.setId(2L);
            h2.setNumero("102");
            h2.setPrecio(150.0);

            when(hotelService.buscarPorId(1L)).thenReturn(hotel);
            when(habitacionService.buscarPorIds(any())).thenReturn(List.of(h1, h2));
            when(habitacionService.estaDisponible(anyLong(), any(), any())).thenReturn(true);
            when(reservaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            LocalDate inicio = LocalDate.now().plusDays(2);
            LocalDate fin = LocalDate.now().plusDays(5);
            ReservaRequest request = new ReservaRequest(inicio, fin, List.of(1L, 2L));

            Reserva result = reservaService.reservarHabitaciones(1L, request, user);

            assertEquals(750.0, result.getTotal());
        }

        @Test
        @DisplayName("Debe guardar reserva con estado PENDIENTE por defecto")
        void shouldSaveWithPendingState() {
            when(hotelService.buscarPorId(1L)).thenReturn(hotel);
            when(habitacionService.buscarPorIds(any())).thenReturn(List.of(habitacion));
            when(habitacionService.estaDisponible(anyLong(), any(), any())).thenReturn(true);
            when(reservaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            LocalDate inicio = LocalDate.now().plusDays(2);
            LocalDate fin = LocalDate.now().plusDays(5);
            ReservaRequest request = new ReservaRequest(inicio, fin, List.of(1L));

            Reserva result = reservaService.reservarHabitaciones(1L, request, user);

            assertEquals("PENDIENTE", result.getEstado());
        }
    }

    @Nested
    @DisplayName("Confirmacion de pago")
    class ConfirmarPagoTests {

        @Test
        @DisplayName("Debe confirmar reserva PENDIENTE exitosamente")
        void shouldConfirmPendingReserva() {
            Reserva reserva = TestFixtures.createReservaPendiente(hotel, user);
            when(reservaRepository.findByIdWithRelations(2L)).thenReturn(Optional.of(reserva));
            when(reservaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Reserva result = reservaService.confirmarPago(2L);

            assertEquals("CONFIRMADA", result.getEstado());
            verify(reservaRepository).save(reserva);
        }

        @Test
        @DisplayName("BUSINESS: Confirmar reserva ya confirmada lanza BusinessException")
        void shouldThrowWhenReservaAlreadyConfirmed() {
            Reserva reserva = TestFixtures.createReserva(hotel, user);
            when(reservaRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(reserva));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> reservaService.confirmarPago(1L));

            assertEquals("ESTADO_INVALIDO", ex.getErrorCode());
        }

        @Test
        @DisplayName("NOT_FOUND: Confirmar reserva inexistente lanza EntityNotFoundException")
        void shouldThrowWhenReservaNotFound() {
            when(reservaRepository.findByIdWithRelations(999L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> reservaService.confirmarPago(999L));

            assertTrue(ex.getMessage().contains("999"));
        }
    }

    @Nested
    @DisplayName("Metodos de consulta")
    class QueryMethodTests {

        @Test
        @DisplayName("buscarPorId debe lanzar EntityNotFoundException si no existe")
        void shouldThrowWhenReservaNotFoundById() {
            when(reservaRepository.findByIdWithRelations(999L)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> reservaService.buscarPorId(999L));

            assertTrue(ex.getMessage().contains("999"));
        }

        @Test
        @DisplayName("eliminar debe lanzar EntityNotFoundException si no existe")
        void shouldThrowWhenEliminarNonExistent() {
            when(reservaRepository.existsById(999L)).thenReturn(false);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> reservaService.eliminar(999L));

            verify(reservaRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("VALIDATION: Usuario null en reservarHabitaciones lanza ValidationException")
        void shouldThrowWhenUserIsNull() {
            LocalDate inicio = LocalDate.now().plusDays(2);
            LocalDate fin = LocalDate.now().plusDays(4);
            ReservaRequest request = new ReservaRequest(inicio, fin, List.of(1L));

            ValidationException ex = assertThrows(ValidationException.class,
                    () -> reservaService.reservarHabitaciones(1L, request, null));

            assertTrue(ex.getFieldErrors().containsKey("usuario"));
        }
    }
}
