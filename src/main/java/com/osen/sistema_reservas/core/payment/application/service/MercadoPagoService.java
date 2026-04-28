package com.osen.sistema_reservas.core.payment.application.service;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import com.osen.sistema_reservas.shared.helpers.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import java.math.BigDecimal;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MercadoPagoService {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoService.class);

    @Value("${app.backend.webhook-url:http://localhost:8080/payments/webhook}")
    private String webhookUrl;

    @Value("${app.payment.mp.create-retries:2}")
    private int createRetries;

    @Value("${app.payment.mp.retry-delay-ms:350}")
    private long retryDelayMs;

    public Payment crearPagoCheckoutApi(Reserva reserva, CheckoutApiRequest req, String idempotencyKey) {
        validarReserva(reserva, req);
        validarRequest(req);

        String contexto = String.format(
                "reservaId=%s, paymentMethodId=%s, installments=%s, email=%s, docType=%s, docNumber=%s, amount=%s, webhookUrl=%s, idemKey=%s",
                reserva.getId(),
                req.paymentMethodId(),
                req.installments(),
                ocultarEmail(req.email()),
                req.docType(),
                ocultarDocumento(req.docNumber()),
                reserva.getTotal(),
                webhookUrl,
                idempotencyKey
        );

        PaymentCreateRequest paymentRequest = PaymentCreateRequest.builder()
                .transactionAmount(BigDecimal.valueOf(reserva.getTotal()))
                .token(req.token())
                .description("Pago reserva hotel #" + reserva.getId())
                .installments(req.installments())
                .paymentMethodId(req.paymentMethodId())
                .payer(PaymentPayerRequest.builder()
                        .email(req.email())
                        .identification(IdentificationRequest.builder()
                                .type(req.docType())
                                .number(req.docNumber())
                                .build())
                        .build())
                .externalReference(String.valueOf(reserva.getId()))
                .notificationUrl(webhookUrl)
                .build();

        MPRequestOptions options = MPRequestOptions.builder()
                .customHeaders(java.util.Map.of("X-Idempotency-Key", idempotencyKey))
                .build();

        PaymentClient paymentClient = new PaymentClient();
        int maxAttempts = Math.max(1, createRetries + 1);
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return paymentClient.create(paymentRequest, options);
            } catch (MPApiException e) {
                String mpBody = e.getApiResponse() != null ? e.getApiResponse().getContent() : "sin cuerpo";
                int status = e.getApiResponse() != null ? e.getApiResponse().getStatusCode() : -1;
                boolean transitorio = esErrorTransitorio(status, mpBody);

                if (transitorio && attempt < maxAttempts) {
                    log.warn("Error transitorio Mercado Pago al crear pago. intento={}/{}, status={}, {}, mpResponse={}", attempt, maxAttempts, status, contexto, mpBody);
                    dormirBackoff();
                    continue;
                }

                log.error("Error Mercado Pago al crear pago. intento={}/{}, status={}, {}, mpResponse={}", attempt, maxAttempts, status, contexto, mpBody);
                throw new BusinessException("Error MP: " + mpBody + " | contexto: " + contexto, "MP_PAYMENT_CREATE_ERROR");
            } catch (MPException e) {
                if (attempt < maxAttempts) {
                    log.warn("Error general MP en intento {}/{}. {}, detalle={}", attempt, maxAttempts, contexto, e.getMessage());
                    dormirBackoff();
                    continue;
                }
                log.error("Error general Mercado Pago al crear pago. intento={}/{}, {}, detalle={}", attempt, maxAttempts, contexto, e.getMessage());
                throw new BusinessException("Error general MP: " + e.getMessage(), "MP_PAYMENT_CREATE_ERROR");
            }
        }

        throw new BusinessException("No se pudo crear el pago en Mercado Pago", "MP_PAYMENT_CREATE_ERROR");
    }

    private void validarReserva(Reserva reserva, CheckoutApiRequest req) {
        if (reserva == null) {
            throw new BusinessException("La reserva es obligatoria para crear el pago", "VALIDATION_ERROR");
        }
        if (reserva.getId() == null) {
            throw new BusinessException("La reserva no tiene ID", "VALIDATION_ERROR");
        }
        if (reserva.getTotal() <= 0) {
            throw new BusinessException("El monto de la reserva debe ser mayor a 0", "VALIDATION_ERROR");
        }
        if (req != null && req.reservaId() != null && !reserva.getId().equals(req.reservaId())) {
            throw new BusinessException("La reserva del pago no coincide con la reserva seleccionada", "VALIDATION_ERROR");
        }
    }

    private void validarRequest(CheckoutApiRequest req) {
        if (req == null) {
            throw new BusinessException("El request de pago es obligatorio", "VALIDATION_ERROR");
        }
        if (req.token() == null || req.token().isBlank()) {
            throw new BusinessException("token es obligatorio", "VALIDATION_ERROR");
        }
        if (req.paymentMethodId() == null || req.paymentMethodId().isBlank()) {
            throw new BusinessException("paymentMethodId es obligatorio", "VALIDATION_ERROR");
        }
        if (req.installments() == null || req.installments() <= 0) {
            throw new BusinessException("installments debe ser mayor a 0", "VALIDATION_ERROR");
        }
        if (req.email() == null || req.email().isBlank()) {
            throw new BusinessException("email es obligatorio", "VALIDATION_ERROR");
        }
        if (req.docType() == null || req.docType().isBlank()) {
            throw new BusinessException("docType es obligatorio", "VALIDATION_ERROR");
        }
        if (req.docNumber() == null || req.docNumber().isBlank()) {
            throw new BusinessException("docNumber es obligatorio", "VALIDATION_ERROR");
        }
    }

    private String ocultarEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            return "***";
        }
        String[] parts = email.split("@", 2);
        String usuario = parts[0];
        String dominio = parts[1];
        if (usuario.length() <= 2) {
            return "***@" + dominio;
        }
        return usuario.substring(0, 2) + "***@" + dominio;
    }

    private String ocultarDocumento(String doc) {
        if (doc == null || doc.isBlank()) {
            return "***";
        }
        int len = doc.length();
        if (len <= 4) {
            return "***" + doc;
        }
        return "***" + doc.substring(len - 4);
    }

    private boolean esErrorTransitorio(int status, String mpBody) {
        if (status == 429 || status == 500 || status == 502 || status == 503 || status == 504) {
            return true;
        }
        if (mpBody == null) {
            return false;
        }
        return mpBody.toLowerCase(Locale.ROOT).contains("internal_error");
    }

    private void dormirBackoff() {
        if (retryDelayMs <= 0) {
            return;
        }
        try {
            Thread.sleep(retryDelayMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }


    public Payment obtenerPago(Long paymentId) {
        try {
            return new PaymentClient().get(paymentId);
        } catch (MPApiException | MPException e) {
            throw new BusinessException("No se pudo consultar el pago en Mercado Pago: " + e.getMessage(), "MP_PAYMENT_FETCH_ERROR");
        }
    }

}
