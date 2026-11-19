package cl.duoc.casasembrujadas.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import cl.duoc.casasembrujadas.model.RolNombre;
import cl.duoc.casasembrujadas.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRETO = "c2VjcmV0by1zb2xvLXBhcmEtcHJ1ZWJhcy1hdXRvbWF0aWNhcy0xMjM0NTY3ODkw";
    private static final String OTRO_SECRETO = "b3Ryby1zZWNyZXRvLWRpc3RpbnRvLXBhcmEtZmlybWFyLXRva2Vucy1mYWxzb3MtMTIz";
    private static final ZoneId ZONA = ZoneId.of("America/Santiago");
    private static final Instant INICIO = Instant.parse("2026-11-17T15:00:00Z");

    private final UsuarioDetalles camila = new UsuarioDetalles(Usuario.builder()
            .idUsuario(3L).correo("cliente@casasembrujadas.cl").nombre("Camila").rol(RolNombre.CLIENTE).activo(true).build());

    private JwtService servicioEn(Instant instante, String secreto) {
        return new JwtService(secreto, 60, Clock.fixed(instante, ZONA));
    }

    @Test
    void generaUnTokenConSusDatosYSuVencimiento() {
        TokenGenerado generado = servicioEn(INICIO, SECRETO).generar(camila);

        Claims datos = servicioEn(INICIO, SECRETO).validar(generado.token());

        assertEquals("cliente@casasembrujadas.cl", datos.getSubject());
        assertEquals("CLIENTE", datos.get("rol", String.class));
        assertEquals(3L, datos.get("id", Long.class));
        assertEquals(LocalDateTime.of(2026, 11, 17, 13, 0), generado.expiraEn());
        assertEquals(3, generado.token().split("\\.").length);
    }

    @Test
    void rechazaUnTokenVencido() {
        String token = servicioEn(INICIO, SECRETO).generar(camila).token();

        JwtService unaHoraYUnMinutoDespues = servicioEn(INICIO.plusSeconds(61 * 60), SECRETO);

        assertThrows(ExpiredJwtException.class, () -> unaHoraYUnMinutoDespues.validar(token));
    }

    @Test
    void rechazaUnTokenFirmadoConOtraClave() {
        String falso = servicioEn(INICIO, OTRO_SECRETO).generar(camila).token();

        assertThrows(JwtException.class, () -> servicioEn(INICIO, SECRETO).validar(falso));
    }

    @Test
    void rechazaUnTokenModificado() {
        String token = servicioEn(INICIO, SECRETO).generar(camila).token();
        String[] partes = token.split("\\.");
        String alterado = partes[0] + "." + partes[1].substring(0, partes[1].length() - 2) + "AA." + partes[2];

        assertThrows(JwtException.class, () -> servicioEn(INICIO, SECRETO).validar(alterado));
    }

    @Test
    void noAceptaUnSecretoDemasiadoCorto() {
        assertThrows(WeakKeyException.class, () -> servicioEn(INICIO, "Y29ydG8="));
    }
}
