package org.koshiroanz.junit.ejemplo.models;

import org.junit.jupiter.api.Test;
import org.koshiroanz.junit.ejemplo.exceptions.NotEnoughMoneyException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Cristian", new BigDecimal("1000.123"));

        String esperadoPersona = "Cristian";
        String actualPersona = cuenta.getPersona();

        assertEquals(esperadoPersona, actualPersona);
        assertTrue(actualPersona.equals("Cristian"));
    }

    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Cristian", new BigDecimal("1.123"));
        assertEquals(1.123, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    void testSaldoCuentaLessThanZero() {
        Cuenta cuenta = new Cuenta("Cristian", new BigDecimal("-1.123"));
        assertEquals(-1.123, cuenta.getSaldo().doubleValue());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    void testSaldoCuentaGreaterThanZero() {
        Cuenta cuenta = new Cuenta("Cristian", new BigDecimal("1.123"));
        assertEquals(1.123, cuenta.getSaldo().doubleValue());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testSaldoCuentaEqualZero() {
        Cuenta cuenta = new Cuenta("Cristian", new BigDecimal("0.00"));
        assertEquals(0.00, cuenta.getSaldo().doubleValue());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.99"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.99"));
        assertEquals(cuenta2, cuenta);
    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().doubleValue());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000.123"));
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals(1100.123, cuenta.getSaldo().doubleValue());
    }

    @Test
    void testNotEnoughMoneyExceptionCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000.123"));
        Exception exception = assertThrows(NotEnoughMoneyException.class, () -> {
            cuenta.debito(new BigDecimal(1000.123));
        });

        String current = exception.getMessage();
        String expected = "Not enough money";
        assertEquals(expected, current);
    }
}