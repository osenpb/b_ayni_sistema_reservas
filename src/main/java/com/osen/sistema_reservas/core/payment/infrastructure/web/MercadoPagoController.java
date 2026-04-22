package com.osen.sistema_reservas.core.payment.infrastructure.web;

import com.mercadopago.resources.payment.Payment;
import com.osen.sistema_reservas.auth.domain.model.User;
import com.osen.sistema_reservas.core.payment.application.service.CheckoutApiRequest;
import com.osen.sistema_reservas.core.payment.application.service.MercadoPagoService;
import com.osen.sistema_reservas.core.reserva.application.service.ReservaService;
import com.osen.sistema_reservas.core.reserva.domain.model.Reserva;
import com.osen.sistema_reservas.shared.helpers.dtos.MessageResponse;
import com.osen.sistema_reservas.shared.helpers.exceptions.BusinessException;
import com.osen.sistema_reservas.shared.helpers.exceptions.ForbiddenException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/public/payments")
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService;
    private final ReservaService reservaService;

    public MercadoPagoController(MercadoPagoService mercadoPagoService, ReservaService reservaService) {
        this.mercadoPagoService = mercadoPagoService;
        this.reservaService = reservaService;
    }

    @PostMapping("/checkout-api")
    public ResponseEntity<CheckoutApiResponse> pagarCheckoutApi(
            @RequestBody CheckoutApiRequest request,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey,
            @AuthenticationPrincipal User user) {

        CheckoutApiRequest requestFinal = normalizarRequest(request, user);

        if (requestFinal.reservaId() == null) {
            throw new BusinessException("reservaId es obligatorio", "VALIDATION_ERROR");
        }

        Reserva reserva = reservaService.buscarPorId(requestFinal.reservaId());
        validarPropietario(reserva, user);

        if (!"PENDIENTE".equalsIgnoreCase(reserva.getEstado())) {
            throw new BusinessException("Solo se puede pagar una reserva en estado PENDIENTE", "ESTADO_INVALIDO");
        }

        String idemKey = (idempotencyKey == null || idempotencyKey.isBlank())
                ? UUID.randomUUID().toString()
                : idempotencyKey;

        Payment payment = mercadoPagoService.crearPagoCheckoutApi(reserva, requestFinal, idemKey);

        return ResponseEntity.ok(new CheckoutApiResponse(
                payment.getId(),
                payment.getStatus(),
                payment.getStatusDetail(),
                idemKey
        ));
    }

    @PostMapping("/webhook")
    public ResponseEntity<MessageResponse> webhook(@RequestBody(required = false) Map<String, Object> body) {
        Long paymentId = extraerPaymentId(body);
        if (paymentId == null) {
            return ResponseEntity.ok(MessageResponse.of("Webhook recibido sin payment_id"));
        }

        Payment payment = mercadoPagoService.obtenerPago(paymentId);
        if (!"approved".equalsIgnoreCase(payment.getStatus())) {
            return ResponseEntity.ok(MessageResponse.of("Pago no aprobado, estado: " + payment.getStatus()));
        }

        String externalReference = payment.getExternalReference();
        if (externalReference == null || externalReference.isBlank()) {
            throw new BusinessException("El pago no contiene external_reference", "MP_EXTERNAL_REFERENCE_MISSING");
        }

        Long reservaId = Long.parseLong(externalReference);
        reservaService.confirmarPago(reservaId);

        return ResponseEntity.ok(MessageResponse.of("Pago aprobado. Reserva confirmada: " + reservaId));
    }

    private void validarPropietario(Reserva reserva, User user) {
        if (reserva.getUser() == null || user == null || !reserva.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("No tiene permiso sobre esta reserva");
        }
    }

    private CheckoutApiRequest normalizarRequest(CheckoutApiRequest request, User user) {
        if (request == null) {
            return null;
        }

        String email = request.email();
        if ((email == null || email.isBlank()) && user != null) {
            email = user.getEmail();
        }

        return new CheckoutApiRequest(
                request.reservaId(),
                request.token(),
                request.paymentMethodId(),
                request.installments(),
                email,
                request.docType(),
                request.docNumber()
        );
    }

    private Long extraerPaymentId(Map<String, Object> body) {
        if (body == null) return null;

        Object data = body.get("data");
        if (!(data instanceof Map<?, ?> dataMap)) return null;

        Object id = dataMap.get("id");
        if (id == null) return null;

        try {
            return Long.valueOf(String.valueOf(id));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public record CheckoutApiResponse(
            Long paymentId,
            String status,
            String statusDetail,
            String idempotencyKey
    ) {}
}
