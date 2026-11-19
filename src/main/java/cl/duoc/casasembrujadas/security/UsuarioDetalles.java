package cl.duoc.casasembrujadas.security;

import java.util.Collection;
import java.util.List;

import cl.duoc.casasembrujadas.model.RolNombre;
import cl.duoc.casasembrujadas.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UsuarioDetalles implements UserDetails {

    private final Usuario usuario;

    public UsuarioDetalles(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));
    }

    @Override
    public String getPassword() {
        return usuario.getContrasena();
    }

    @Override
    public String getUsername() {
        return usuario.getCorreo();
    }

    @Override
    public boolean isEnabled() {
        return usuario.isActivo();
    }

    public Long getIdUsuario() {
        return usuario.getIdUsuario();
    }

    public String getNombre() {
        return usuario.getNombre();
    }

    public RolNombre getRol() {
        return usuario.getRol();
    }

    public UsuarioAutenticado aUsuarioAutenticado() {
        return new UsuarioAutenticado(usuario.getIdUsuario(), usuario.getCorreo(), usuario.getRol());
    }
}
