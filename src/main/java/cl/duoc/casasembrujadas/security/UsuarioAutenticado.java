package cl.duoc.casasembrujadas.security;

import cl.duoc.casasembrujadas.model.RolNombre;

public record UsuarioAutenticado(Long idUsuario, String correo, RolNombre rol) {

    public boolean esCliente() {
        return rol == RolNombre.CLIENTE;
    }

    public boolean esAdmin() {
        return rol == RolNombre.ADMIN;
    }
}
