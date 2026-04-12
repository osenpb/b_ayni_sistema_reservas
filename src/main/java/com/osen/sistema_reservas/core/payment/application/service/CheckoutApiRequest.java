package com.osen.sistema_reservas.core.payment.application.service;

public record CheckoutApiRequest(
        Long reservaId,
        String token,
        String paymentMethodId,
        Integer installments,
        String email,
        String docType,
        String docNumber
) {}
