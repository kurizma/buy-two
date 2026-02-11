package com.buyone.orderservice.model.order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodTests {

    @Test
    void testPaymentMethodValues() {
        PaymentMethod[] methods = PaymentMethod.values();
        assertEquals(3, methods.length);
    }

    @Test
    void testPaymentMethodPayOnDelivery() {
        PaymentMethod method = PaymentMethod.PAY_ON_DELIVERY;
        assertEquals("PAY_ON_DELIVERY", method.name());
        assertEquals(0, method.ordinal());
    }

    @Test
    void testPaymentMethodCard() {
        PaymentMethod method = PaymentMethod.CARD;
        assertEquals("CARD", method.name());
        assertEquals(1, method.ordinal());
    }

    @Test
    void testPaymentMethodPaypal() {
        PaymentMethod method = PaymentMethod.PAYPAL;
        assertEquals("PAYPAL", method.name());
        assertEquals(2, method.ordinal());
    }

    @Test
    void testPaymentMethodValueOf() {
        assertEquals(PaymentMethod.PAY_ON_DELIVERY, PaymentMethod.valueOf("PAY_ON_DELIVERY"));
        assertEquals(PaymentMethod.CARD, PaymentMethod.valueOf("CARD"));
        assertEquals(PaymentMethod.PAYPAL, PaymentMethod.valueOf("PAYPAL"));
    }

    @Test
    void testPaymentMethodInvalidValueOf() {
        assertThrows(IllegalArgumentException.class, () -> PaymentMethod.valueOf("INVALID"));
    }
}
