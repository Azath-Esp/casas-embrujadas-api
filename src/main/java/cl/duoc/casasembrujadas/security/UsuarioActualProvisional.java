package cl.duoc.casasembrujadas.security;

import cl.duoc.casasembrujadas.exception.AccesoDenegadoException;
import cl.duoc.casasembrujadas.model.Usuario;
import cl.duoc.casasembrujadas.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UsuarioActualProvisional implements UsuarioActual {

    public static final String ENCABEZADO = "X-Usuario-Id";

    private final HttpServletRequest request;
    private final UsuarioRepository usuarioRepository;

    @Override
    public UsuarioAutenticado obtener() {
        String valor = request.getHeader(ENCABEZADO);
        if (valor == null || !valor.matches("\\d+")) {
            throw new AccesoDenegadoException("Falta el encabezado " + ENCABEZADO + " con el id del usuario");
        }
        Usuario usuario = usuarioRepository.buscarPorId(Long.valueOf(valor))
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new AccesoDenegadoException("El usuario " + valor + " no existe o está inactivo"));
        return new UsuarioAutenticado(usuario.getIdUsuario(), usuario.getCorreo(), usuario.getRol());
    }
}