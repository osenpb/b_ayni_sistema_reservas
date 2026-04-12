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

@Service
public class MercadoPagoService {

    @Value("${app.backend.webhook-url:http://localhost:8080/payments/webhook}")
    private String webhookUrl;

    public Payment crearPagoCheckoutApi(Reserva reserva, CheckoutApiRequest req, String idempotencyKey) {
        try {
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

            return new PaymentClient().create(paymentRequest, options);

        } catch (MPApiException e) {
            throw new BusinessException("Error MP: " + e.getApiResponse().getContent(), "MP_PAYMENT_CREATE_ERROR");
        } catch (MPException e) {
            throw new BusinessException("Error general MP: " + e.getMessage(), "MP_PAYMENT_CREATE_ERROR");
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
