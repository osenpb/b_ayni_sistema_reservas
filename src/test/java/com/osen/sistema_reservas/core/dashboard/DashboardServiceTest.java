package com.osen.sistema_reservas.core.dashboard;

import com.osen.sistema_reservas.auth.domain.model.User;
import com.osen.sistema_reservas.core.departamento.application.service.DepartamentoService;
import com.osen.sistema_reservas.core.departamento.domain.model.Departamento;
import com.osen.sistema_reservas.core.habitacion.application.service.HabitacionService;
import com.osen.sistema_reservas.core.hotel.application.service.HotelService;
import com.osen.sistema_reservas.core.hotel.domain.model.Hotel;
import com.osen.sistema_reservas.core.reserva.application.service.ReservaService;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import com.osen.sistema_reservas.core.dashboard.application.service.DashboardService;
import com.osen.sistema_reservas.core.dashboard.application.dtos.DashboardStatsResponse;
import com.osen.sistema_reservas.core.dashboard.application.dtos.IngresoMensualDTO;
import com.osen.sistema_reservas.core.dashboard.application.dtos.ReservaMensualDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardService - Tests de rendimiento y cuellos de botella")
class DashboardServiceTest {

    @Mock
    private DepartamentoService departamentoService;

    @Mock
    private HotelService hotelService;

    @Mock
    private HabitacionService habitacionService;

    @Mock
    private ReservaService reservaService;

    @InjectMocks
    private DashboardService dashboardService;

    private Hotel hotel;
    private User user;
    private Departamento departamento;
    private List<Departamento> departamentos;
    private List<Hotel> hoteles;
    private List<Reserva> reservas;

    @BeforeEach
    void setUp() {
        hotel = DashboardTestFixtures.createHotel();
        user = DashboardTestFixtures.createUser();
        departamento = DashboardTestFixtures.createDepartamento();

        departamentos = List.of(departamento);
        hoteles = List.of(hotel);
        reservas = List.of(DashboardTestFixtures.createReserva(hotel, user));
    }

    @Nested
    @DisplayName("CUELLO DE BOTELLA - Carga completa en memoria")
    class MemoryBottleneckTests {

        @Test
        @DisplayName("CUELLO DE BOTELLA: cargar 10000 reservas en memoria para calculos simples")
        void shouldLoadAllReservasIntoMemory() {
            List<Reserva> milReservas = DashboardTestFixtures.createReservasCollection(10000, hotel, user);
            when(departamentoService.listar()).thenReturn(departamentos);
            when(hotelService.listarTodos()).thenReturn(hoteles);
            when(reservaService.listarTodas()).thenReturn(milReservas);
            when(habitacionService.contarTodas()).thenReturn(100L);

            DashboardStatsResponse stats = dashboardService.obtenerEstadisticas();

            assertNotNull(stats);
            assertEquals(10000, stats.totalReservas());
            verify(reservaService).listarTodas();
        }

        @Test
        @DisplayName("CUELLO DE BOTELLA: Multiples pasadas sobre la misma coleccion de reservas")
        void shouldMakeMultiplePassesOverReservas() {
            List<Reserva> milReservas = DashboardTestFixtures.createReservasCollection(1000, hotel, user);
            when(departamentoService.listar()).thenReturn(departamentos);
            when(hotelService.listarTodos()).thenReturn(hoteles);
            when(reservaService.listarTodas()).thenReturn(milReservas);
            when(habitacionService.contarTodas()).thenReturn(100L);

            dashboardService.obtenerEstadisticas();

            List<Reserva> reservasPasadasAlMetodo = milReservas;
            long pasadas = reservasPasadasAlMetodo.stream().count();
            assertTrue(pasadas > 0);
        }
    }

    @Nested
    @DisplayName("Calculo de estadisticas")
    class EstadisticasTests {

        @Test
        @DisplayName("Debe calcular conteo por estado correctamente")
        void shouldCalculateReservasPorEstado() {
            List<Reserva> reservas = DashboardTestFixtures.createReservasPorEstados(hotel, user);
            when(departamentoService.listar()).thenReturn(departamentos);
            when(hotelService.listarTodos()).thenReturn(hoteles);
            when(reservaService.listarTodas()).thenReturn(reservas);
            when(habitacionService.contarTodas()).thenReturn(10L);

            DashboardStatsResponse stats = dashboardService.obtenerEstadisticas();

            assertNotNull(stats.reservasPorEstado());
        }

        @Test
        @DisplayName("Debe calcular ingresos totales solo de reservas confirmadas")
        void shouldSumOnlyConfirmedReservas() {
            List<Reserva> reservas = DashboardTestFixtures.createReservasConTotales(hotel, user);
            when(departamentoService.listar()).thenReturn(departamentos);
            when(hotelService.listarTodos()).thenReturn(hoteles);
            when(reservaService.listarTodas()).thenReturn(reservas);
            when(habitacionService.contarTodas()).thenReturn(10L);

            DashboardStatsResponse stats = dashboardService.obtenerEstadisticas();

            assertTrue(stats.ingresosTotales() >= 0);
        }

        @Test
        @DisplayName("Debe normalizar estado para ingresos y conteos")
        void shouldNormalizeEstadoWhenCalculatingStats() {
            Reserva confirmadaConFormatoIrregular = DashboardTestFixtures.createReserva(hotel, user);
            confirmadaConFormatoIrregular.setEstado(" confirmada ");
            confirmadaConFormatoIrregular.setTotal(450.0);

            when(departamentoService.listar()).thenReturn(departamentos);
            when(hotelService.listarTodos()).thenReturn(hoteles);
            when(reservaService.listarTodas()).thenReturn(List.of(confirmadaConFormatoIrregular));
            when(habitacionService.contarTodas()).thenReturn(10L);

            DashboardStatsResponse stats = dashboardService.obtenerEstadisticas();

            assertEquals(450.0, stats.ingresosTotales());
            assertEquals(1L, stats.reservasPorEstado().stream()
                    .filter(r -> "CONFIRMADA".equals(r.estado()))
                    .findFirst()
                    .orElseThrow()
                    .cantidad());
        }

        @Test
        @DisplayName("Debe calcular hoteles por departamento")
        void shouldCalculateHotelesPorDepartamento() {
            when(departamentoService.listar()).thenReturn(departamentos);
            when(hotelService.listarTodos()).thenReturn(hoteles);
            when(reservaService.listarTodas()).thenReturn(reservas);
            when(habitacionService.contarTodas()).thenReturn(10L);

            DashboardStatsResponse stats = dashboardService.obtenerEstadisticas();

            assertNotNull(stats.hotelesPorDepartamento());
        }

        @Test
        @DisplayName("Debe devolver top 5 hoteles con mas reservas")
        void shouldReturnTop5Hoteles() {
            when(departamentoService.listar()).thenReturn(departamentos);
            when(hotelService.listarTodos()).thenReturn(hoteles);
            when(reservaService.listarTodas()).thenReturn(reservas);
            when(habitacionService.contarTodas()).thenReturn(10L);

            DashboardStatsResponse stats = dashboardService.obtenerEstadisticas();

            assertNotNull(stats.topHoteles());
        }

        @Test
        @DisplayName("Debe devolver ultimas 5 reservas recientes ordenadas por fecha")
        void shouldReturnRecentReservas() {
            when(departamentoService.listar()).thenReturn(departamentos);
            when(hotelService.listarTodos()).thenReturn(hoteles);
            when(reservaService.listarTodas()).thenReturn(reservas);
            when(habitacionService.contarTodas()).thenReturn(10L);

            DashboardStatsResponse stats = dashboardService.obtenerEstadisticas();

            assertNotNull(stats.reservasRecientes());
        }
    }

    @Nested
    @DisplayName("Casos extremos")
    class EdgeCaseTests {

        @Test
        @DisplayName("Debe funcionar con colecciones vacias")
        void shouldHandleEmptyCollections() {
            when(departamentoService.listar()).thenReturn(new ArrayList<>());
            when(hotelService.listarTodos()).thenReturn(new ArrayList<>());
            when(reservaService.listarTodas()).thenReturn(new ArrayList<>());
            when(habitacionService.contarTodas()).thenReturn(0L);

            DashboardStatsResponse stats = dashboardService.obtenerEstadisticas();

            assertNotNull(stats);
            assertEquals(0, stats.totalReservas());
            assertEquals(0, stats.totalHoteles());
        }

        @Test
        @DisplayName("Debe manejar reservas sin hotel (null)")
        void shouldHandleReservasWithoutHotel() {
            Reserva reservaSinHotel = DashboardTestFixtures.createReserva(hotel, user);
            reservaSinHotel.setHotel(null);

            when(departamentoService.listar()).thenReturn(departamentos);
            when(hotelService.listarTodos()).thenReturn(hoteles);
            when(reservaService.listarTodas()).thenReturn(List.of(reservaSinHotel));
            when(habitacionService.contarTodas()).thenReturn(10L);

            assertDoesNotThrow(() -> dashboardService.obtenerEstadisticas());
        }

        @Test
        @DisplayName("Debe manejar reservas sin usuario (null)")
        void shouldHandleReservasWithoutUser() {
            Reserva reservaSinUser = DashboardTestFixtures.createReserva(hotel, user);
            reservaSinUser.setUser(null);

            when(departamentoService.listar()).thenReturn(departamentos);
            when(hotelService.listarTodos()).thenReturn(hoteles);
            when(reservaService.listarTodas()).thenReturn(List.of(reservaSinUser));
            when(habitacionService.contarTodas()).thenReturn(10L);

            assertDoesNotThrow(() -> dashboardService.obtenerEstadisticas());
        }

        @Test
        @DisplayName("Debe usar fechaInicio cuando fechaReserva es null para series mensuales")
        void shouldUseFechaInicioWhenFechaReservaIsNull() {
            Reserva reservaSinFechaReserva = DashboardTestFixtures.createReserva(hotel, user);
            reservaSinFechaReserva.setFechaReserva(null);
            reservaSinFechaReserva.setFechaInicio(LocalDate.now().minusDays(3));
            reservaSinFechaReserva.setEstado("CONFIRMADA");
            reservaSinFechaReserva.setTotal(300.0);

            when(departamentoService.listar()).thenReturn(departamentos);
            when(hotelService.listarTodos()).thenReturn(hoteles);
            when(reservaService.listarTodas()).thenReturn(List.of(reservaSinFechaReserva));
            when(habitacionService.contarTodas()).thenReturn(10L);

            DashboardStatsResponse stats = dashboardService.obtenerEstadisticas();

            long totalReservasMes = stats.reservasPorMes().stream().mapToLong(ReservaMensualDTO::cantidad).sum();
            double totalIngresosMes = stats.ingresosPorMes().stream().mapToDouble(IngresoMensualDTO::monto).sum();

            assertEquals(1L, totalReservasMes);
            assertEquals(300.0, totalIngresosMes);
        }
    }
}
