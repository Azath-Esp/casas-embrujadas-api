package cl.duoc.casasembrujadas.dto.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import cl.duoc.casasembrujadas.dto.request.RegistroRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;


class RegistroRequestValidacionTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private RegistroRequest solicitudValida() {
        return RegistroRequest.builder()
                .run("12.345.678-5")
                .nombre("Camila")
                .apellidos("Reyes Pinto")
                .correo("camila@casasembrujadas.cl")
                .contrasena("Cliente123!")
                .build();
    }

    private Set<String> mensajes(RegistroRequest request) {
        return validator.validate(request).stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }

    @Test
    void unaSolicitudCorrectaNoTieneErrores() {
        assertTrue(mensajes(solicitudValida()).isEmpty());
    }

    @Test
    void rechazaRunConDigitoVerificadorIncorrecto() {
        RegistroRequest request = solicitudValida();
        request.setRun("12.345.678-9");
        assertEquals(Set.of("El RUN no es válido"), mensajes(request));
    }

    @Test
    void rechazaCorreoSinFormatoYContrasenaDebil() {
        RegistroRequest request = solicitudValida();
        request.setCorreo("no-es-un-correo");
        request.setContrasena("abcdefgh");
        assertEquals(
                Set.of("El correo no tiene un formato válido", "La contraseña debe incluir al menos una letra y un número"),
                mensajes(request));
    }
}