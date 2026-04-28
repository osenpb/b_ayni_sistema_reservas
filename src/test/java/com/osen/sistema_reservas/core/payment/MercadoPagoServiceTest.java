package com.osen.sistema_reservas.core.payment;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.net.MPResponse;
import com.mercadopago.resources.payment.Payment;
import com.osen.sistema_reservas.core.payment.application.service.CheckoutApiRequest;
import com.osen.sistema_reservas.core.payment.application.service.MercadoPagoService;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import com.osen.sistema_reservas.shared.helpers.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("MercadoPagoService - Tests unitarios")
class MercadoPagoServiceTest {

    private MercadoPagoService mercadoPagoService;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        mercadoPagoService = new MercadoPagoService();

        reserva = new Reserva();
        reserva.setId(129L);
        reserva.setTotal(350.0);
        reserva.setEstado("PENDIENTE");
    }

    private void configurarRetry(int retries, long delayMs) {
        try {
            Field retriesField = MercadoPagoService.class.getDeclaredField("createRetries");
            retriesField.setAccessible(true);
            retriesField.set(mercadoPagoService, retries);

            Field delayField = MercadoPagoService.class.getDeclaredField("retryDelayMs");
            delayField.setAccessible(true);
            delayField.set(mercadoPagoService, delayMs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("Validaciones de request")
    class ValidacionRequestTests {

        @Test
        @DisplayName("Debe lanzar VALIDATION_ERROR cuando request es null")
        void shouldThrowWhenRequestIsNull() {
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> mercadoPagoService.crearPagoCheckoutApi(reserva, null, "idem-1"));

            assertEquals("VALIDATION_ERROR", ex.getErrorCode());
            assertTrue(ex.getMessage().contains("request de pago es obligatorio"));
        }

        @Test
        @DisplayName("Debe lanzar VALIDATION_ERROR cuando token es vacío")
        void shouldThrowWhenTokenBlank() {
            CheckoutApiRequest req = new CheckoutApiRequest(
                    129L, " ", "master", 1, "test@mail.com", "DNI", "12345678"
            );

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> mercadoPagoService.crearPagoCheckoutApi(reserva, req, "idem-2"));

            assertEquals("VALIDATION_ERROR", ex.getErrorCode());
            assertTrue(ex.getMessage().contains("token es obligatorio"));
        }

        @Test
        @DisplayName("Debe lanzar VALIDATION_ERROR cuando paymentMethodId es vacío")
        void shouldThrowWhenPaymentMethodIdBlank() {
            CheckoutApiRequest req = new CheckoutApiRequest(
                    129L, "tok_123", " ", 1, "test@mail.com", "DNI", "12345678"
            );

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> mercadoPagoService.crearPagoCheckoutApi(reserva, req, "idem-3"));

            assertEquals("VALIDATION_ERROR", ex.getErrorCode());
            assertTrue(ex.getMessage().contains("paymentMethodId es obligatorio"));
        }

        @Test
        @DisplayName("Debe lanzar VALIDATION_ERROR cuando installments <= 0")
        void shouldThrowWhenInstallmentsInvalid() {
            CheckoutApiRequest req = new CheckoutApiRequest(
                    129L, "tok_123", "master", 0, "test@mail.com", "DNI", "12345678"
            );

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> mercadoPagoService.crearPagoCheckoutApi(reserva, req, "idem-4"));

            assertEquals("VALIDATION_ERROR", ex.getErrorCode());
            assertTrue(ex.getMessage().contains("installments debe ser mayor a 0"));
        }

        @Test
        @DisplayName("Debe lanzar VALIDATION_ERROR cuando email es vacío")
        void shouldThrowWhenEmailBlank() {
            CheckoutApiRequest req = new CheckoutApiRequest(
                    129L, "tok_123", "master", 1, " ", "DNI", "12345678"
            );

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> mercadoPagoService.crearPagoCheckoutApi(reserva, req, "idem-5"));

            assertEquals("VALIDATION_ERROR", ex.getErrorCode());
            assertTrue(ex.getMessage().contains("email es obligatorio"));
        }

        @Test
        @DisplayName("Debe lanzar VALIDATION_ERROR cuando docType es vacío")
        void shouldThrowWhenDocTypeBlank() {
            CheckoutApiRequest req = new CheckoutApiRequest(
                    129L, "tok_123", "master", 1, "test@mail.com", " ", "12345678"
            );

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> mercadoPagoService.crearPagoCheckoutApi(reserva, req, "idem-6"));

            assertEquals("VALIDATION_ERROR", ex.getErrorCode());
            assertTrue(ex.getMessage().contains("docType es obligatorio"));
        }

        @Test
        @DisplayName("Debe lanzar VALIDATION_ERROR cuando docNumber es vacío")
        void shouldThrowWhenDocNumberBlank() {
            CheckoutApiRequest req = new CheckoutApiRequest(
                    129L, "tok_123", "master", 1, "test@mail.com", "DNI", " "
            );

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> mercadoPagoService.crearPagoCheckoutApi(reserva, req, "idem-7"));

            assertEquals("VALIDATION_ERROR", ex.getErrorCode());
            assertTrue(ex.getMessage().contains("docNumber es obligatorio"));
        }

        @Test
        @DisplayName("Debe lanzar VALIDATION_ERROR cuando reservaId no coincide con la reserva")
        void shouldThrowWhenReservaIdDoesNotMatch() {
            CheckoutApiRequest req = new CheckoutApiRequest(
                    999L, "tok_123", "master", 1, "test@mail.com", "DNI", "12345678"
            );

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> mercadoPagoService.crearPagoCheckoutApi(reserva, req, "idem-8"));

            assertEquals("VALIDATION_ERROR", ex.getErrorCode());
            assertTrue(ex.getMessage().contains("no coincide"));
        }
    }

    @Nested
    @DisplayName("crearPagoCheckoutApi")
    class CrearPagoTests {

        @Test
        @DisplayName("Debe crear pago cuando request es válido")
        void shouldCreatePaymentWhenRequestIsValid() throws Exception {
            CheckoutApiRequest req = new CheckoutApiRequest(
                    129L, "tok_123", "master", 1, "test@mail.com", "DNI", "12345678"
            );

            Payment paymentMock = mock(Payment.class);

            try (MockedConstruction<PaymentClient> mocked = mockConstruction(
                    PaymentClient.class,
                    (mock, context) -> when(mock.create(any(), any())).thenReturn(paymentMock)
            )) {
                Payment result = mercadoPagoService.crearPagoCheckoutApi(reserva, req, "idem-ok");

                assertSame(paymentMock, result);
                assertEquals(1, mocked.constructed().size());
                verify(mocked.constructed().getFirst(), times(1)).create(any(), any());
            }
        }

        @Test
        @DisplayName("Debe traducir MPApiException a BusinessException con detalle de API")
        void shouldTranslateMpApiExceptionOnCreate() throws Exception {
            CheckoutApiRequest req = new CheckoutApiRequest(
                    129L, "tok_123", "master", 1, "test@mail.com", "DNI", "12345678"
            );

            MPApiException apiException = mock(MPApiException.class);
            MPResponse apiResponse = mock(MPResponse.class);

            when(apiException.getApiResponse()).thenReturn(apiResponse);
            when(apiResponse.getContent()).thenReturn("{\"message\":\"bad_request\"}");
            when(apiResponse.getStatusCode()).thenReturn(400);

            try (MockedConstruction<PaymentClient> mocked = mockConstruction(
                    PaymentClient.class,
                    (mock, context) -> when(mock.create(any(), any())).thenThrow(apiException)
            )) {
                BusinessException ex = assertThrows(BusinessException.class,
                        () -> mercadoPagoService.crearPagoCheckoutApi(reserva, req, "idem-api-err"));

                assertEquals("MP_PAYMENT_CREATE_ERROR", ex.getErrorCode());
                assertTrue(ex.getMessage().contains("Error MP:"));
                assertTrue(ex.getMessage().contains("bad_request"));
                verify(mocked.constructed().getFirst(), times(1)).create(any(), any());
            }
        }

        @Test
        @DisplayName("Debe traducir MPException a BusinessException")
        void shouldTranslateMpExceptionOnCreate() throws Exception {
            CheckoutApiRequest req = new CheckoutApiRequest(
                    129L, "tok_123", "master", 1, "test@mail.com", "DNI", "12345678"
            );

            try (MockedConstruction<PaymentClient> mocked = mockConstruction(
                    PaymentClient.class,
                    (mock, context) -> when(mock.create(any(), any())).thenThrow(new MPException("fallo mp"))
            )) {
                BusinessException ex = assertThrows(BusinessException.class,
                        () -> mercadoPagoService.crearPagoCheckoutApi(reserva, req, "idem-err"));

                assertEquals("MP_PAYMENT_CREATE_ERROR", ex.getErrorCode());
                assertTrue(ex.getMessage().contains("Error general MP"));
                verify(mocked.constructed().getFirst(), times(1)).create(any(), any());
            }
        }

        @Test
        @DisplayName("Debe reintentar cuando MP responde internal_error y luego crear pago")
        void shouldRetryOnTransientApiErrorAndThenSucceed() throws Exception {
            CheckoutApiRequest req = new CheckoutApiRequest(
                    129L, "tok_123", "master", 1, "test@mail.com", "DNI", "12345678"
            );
            configurarRetry(1, 0L);

            MPApiException apiException = mock(MPApiException.class);
            MPResponse apiResponse = mock(MPResponse.class);
            when(apiException.getApiResponse()).thenReturn(apiResponse);
            when(apiResponse.getStatusCode()).thenReturn(500);
            when(apiResponse.getContent()).thenReturn("{\"message\":\"internal_error\"}");

            Payment paymentMock = mock(Payment.class);

            try (MockedConstruction<PaymentClient> mocked = mockConstruction(
                    PaymentClient.class,
                    (mock, context) -> when(mock.create(any(), any()))
                            .thenThrow(apiException)
                            .thenReturn(paymentMock)
            )) {
                Payment result = mercadoPagoService.crearPagoCheckoutApi(reserva, req, "idem-retry-ok");

                assertSame(paymentMock, result);
                verify(mocked.constructed().getFirst(), times(2)).create(any(), any());
            }
        }
    }

    @Nested
    @DisplayName("obtenerPago")
    class ObtenerPagoTests {

        @Test
        @DisplayName("Debe retornar pago cuando MP responde ok")
        void shouldReturnPaymentWhenFetchIsOk() throws Exception {
            Payment paymentMock = mock(Payment.class);

            try (MockedConstruction<PaymentClient> mocked = mockConstruction(
                    PaymentClient.class,
                    (mock, context) -> when(mock.get(1326958546L)).thenReturn(paymentMock)
            )) {
                Payment result = mercadoPagoService.obtenerPago(1326958546L);

                assertSame(paymentMock, result);
                verify(mocked.constructed().getFirst(), times(1)).get(1326958546L);
            }
        }

        @Test
        @DisplayName("Debe traducir error MP al consultar pago")
        void shouldTranslateMpExceptionOnFetch() throws Exception {
            try (MockedConstruction<PaymentClient> mocked = mockConstruction(
                    PaymentClient.class,
                    (mock, context) -> when(mock.get(1326958546L)).thenThrow(new MPException("no disponible"))
            )) {
                BusinessException ex = assertThrows(BusinessException.class,
                        () -> mercadoPagoService.obtenerPago(1326958546L));

                assertEquals("MP_PAYMENT_FETCH_ERROR", ex.getErrorCode());
                assertTrue(ex.getMessage().contains("No se pudo consultar el pago en Mercado Pago"));
                verify(mocked.constructed().getFirst(), times(1)).get(1326958546L);
            }
        }

        @Test
        @DisplayName("Debe traducir MPApiException al consultar pago")
        void shouldTranslateMpApiExceptionOnFetch() throws Exception {
            MPApiException apiException = mock(MPApiException.class);
            MPResponse apiResponse = mock(MPResponse.class);

            when(apiException.getApiResponse()).thenReturn(apiResponse);
            when(apiResponse.getContent()).thenReturn("{\"message\":\"not found\"}");
            when(apiResponse.getStatusCode()).thenReturn(404);

            try (MockedConstruction<PaymentClient> mocked = mockConstruction(
                    PaymentClient.class,
                    (mock, context) -> when(mock.get(1326958546L)).thenThrow(apiException)
            )) {
                BusinessException ex = assertThrows(BusinessException.class,
                        () -> mercadoPagoService.obtenerPago(1326958546L));

                assertEquals("MP_PAYMENT_FETCH_ERROR", ex.getErrorCode());
                assertTrue(ex.getMessage().contains("No se pudo consultar el pago en Mercado Pago"));
                verify(mocked.constructed().getFirst(), times(1)).get(1326958546L);
            }
        }
    }
}
