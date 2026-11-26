package cl.duoc.casasembrujadas.mapper;

import cl.duoc.casasembrujadas.dto.request.RegistroRequest;
import cl.duoc.casasembrujadas.dto.request.UsuarioCrearRequest;
import cl.duoc.casasembrujadas.dto.response.UsuarioResponse;
import cl.duoc.casasembrujadas.dto.validation.RunUtils;
import cl.duoc.casasembrujadas.model.RolNombre;
import cl.duoc.casasembrujadas.model.Usuario;
import org.springframework.stereotype.Component;


@Component
public class UsuarioMapper {

    public UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .run(usuario.getRun())
                .nombre(usuario.getNombre())
                .apellidos(usuario.getApellidos())
                .correo(usuario.getCorreo())
                .direccion(usuario.getDireccion())
                .idComuna(usuario.getIdComuna())
                .comuna(usuario.getComunaNombre())
                .region(usuario.getRegionNombre())
                .rol(usuario.getRol())
                .activo(usuario.isActivo())
                .fechaRegistro(usuario.getFechaRegistro())
                .build();
    }

    public Usuario toModel(RegistroRequest request) {
        return Usuario.builder()
                .run(RunUtils.normalizar(request.getRun()))
                .nombre(request.getNombre().trim())
                .apellidos(request.getApellidos().trim())
                .correo(request.getCorreo().trim().toLowerCase())
                .direccion(request.getDireccion())
                .idComuna(request.getIdComuna())
                .rol(RolNombre.CLIENTE)
                .activo(true)
                .build();
    }

    public Usuario toModel(UsuarioCrearRequest request) {
        return Usuario.builder()
                .run(RunUtils.normalizar(request.getRun()))
                .nombre(request.getNombre().trim())
                .apellidos(request.getApellidos().trim())
                .correo(request.getCorreo().trim().toLowerCase())
                .direccion(request.getDireccion())
                .idComuna(request.getIdComuna())
                .rol(request.getRol())
                .activo(true)
                .build();
    }
}