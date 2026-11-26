package cl.duoc.casasembrujadas.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;

import cl.duoc.casasembrujadas.dto.request.RegistroRequest;
import cl.duoc.casasembrujadas.dto.response.UsuarioResponse;
import cl.duoc.casasembrujadas.model.RolNombre;
import cl.duoc.casasembrujadas.model.Usuario;
import org.junit.jupiter.api.Test;


class UsuarioMapperTest {

    private final UsuarioMapper mapper = new UsuarioMapper();

    @Test
    void laRespuestaNoTieneNingunCampoDeContrasena() {
        boolean expone = Arrays.stream(UsuarioResponse.class.getDeclaredFields())
                .anyMatch(campo -> campo.getName().toLowerCase().contains("contrasena"));
        assertFalse(expone);
    }

    @Test
    void convierteElModeloEnRespuesta() {
        Usuario usuario = Usuario.builder()
                .idUsuario(3L)
                .run("33333333-3")
                .nombre("Camila")
                .correo("cliente@casasembrujadas.cl")
                .contrasena("hash-secreto")
                .comunaNombre("Providencia")
                .rol(RolNombre.CLIENTE)
                .activo(true)
                .build();

        UsuarioResponse respuesta = mapper.toResponse(usuario);

        assertEquals(3L, respuesta.getIdUsuario());
        assertEquals("Providencia", respuesta.getComuna());
        assertEquals(RolNombre.CLIENTE, respuesta.getRol());
    }

    @Test
    void elRegistroSiempreCreaUnClienteConDatosNormalizados() {
        RegistroRequest request = RegistroRequest.builder()
                .run("12.345.678-5")
                .nombre("  Camila ")
                .apellidos("Reyes Pinto")
                .correo(" Camila@CasasEmbrujadas.CL ")
                .contrasena("Cliente123!")
                .build();

        Usuario usuario = mapper.toModel(request);

        assertEquals("12345678-5", usuario.getRun());
        assertEquals("Camila", usuario.getNombre());
        assertEquals("camila@casasembrujadas.cl", usuario.getCorreo());
        assertEquals(RolNombre.CLIENTE, usuario.getRol());
        assertNull(usuario.getContrasena());
    }
}