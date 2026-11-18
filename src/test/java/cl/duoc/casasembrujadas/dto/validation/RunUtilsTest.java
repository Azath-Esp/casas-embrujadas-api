package cl.duoc.casasembrujadas.dto.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


class RunUtilsTest {

    @Test
    void calculaElDigitoVerificador() {
        assertEquals('5', RunUtils.calcularDigitoVerificador(12345678));
        assertEquals('1', RunUtils.calcularDigitoVerificador(11111111));
        assertEquals('K', RunUtils.calcularDigitoVerificador(6));
    }

    @Test
    void normalizaPuntosGuionYMinusculas() {
        assertEquals("12345678-5", RunUtils.normalizar("12.345.678-5"));
        assertEquals("12345678-5", RunUtils.normalizar("123456785"));
        assertEquals("1000005-K", RunUtils.normalizar("1.000.005-k"));
    }

    @Test
    void aceptaRunCorrectos() {
        assertTrue(RunUtils.esValido("12.345.678-5"));
        assertTrue(RunUtils.esValido("33333333-3"));
        assertTrue(RunUtils.esValido("1000005-k"));
    }

    @Test
    void rechazaRunIncorrectos() {
        assertFalse(RunUtils.esValido("12345678-9"));
        assertFalse(RunUtils.esValido("abc"));
        assertFalse(RunUtils.esValido("123-4"));
        assertFalse(RunUtils.esValido(null));
    }
}