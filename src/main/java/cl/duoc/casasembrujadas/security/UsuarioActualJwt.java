package cl.duoc.casasembrujadas.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioActualJwt implements UsuarioActual {

    @Override
    public UsuarioAutenticado obtener() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !(autenticacion.getPrincipal() instanceof UsuarioDetalles detalles)) {
            throw new AuthenticationCredentialsNotFoundException("Debes iniciar sesión");
        }
        return detalles.aUsuarioAutenticado();
    }
}
