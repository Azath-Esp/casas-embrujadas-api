package cl.duoc.casasembrujadas.service;

import java.util.List;

import cl.duoc.casasembrujadas.dto.request.PerfilRequest;
import cl.duoc.casasembrujadas.dto.request.RegistroRequest;
import cl.duoc.casasembrujadas.dto.request.UsuarioActualizarRequest;
import cl.duoc.casasembrujadas.dto.request.UsuarioCrearRequest;
import cl.duoc.casasembrujadas.dto.response.UsuarioResponse;
import cl.duoc.casasembrujadas.security.UsuarioAutenticado;

public interface UsuarioService {

    UsuarioResponse registrar(RegistroRequest request);

    List<UsuarioResponse> listar();

    UsuarioResponse obtener(Long idUsuario);

    UsuarioResponse crear(UsuarioCrearRequest request);

    UsuarioResponse actualizar(Long idUsuario, UsuarioActualizarRequest request, UsuarioAutenticado admin);

    void eliminar(Long idUsuario, UsuarioAutenticado admin);

    UsuarioResponse actualizarPerfil(Long idUsuario, PerfilRequest request);
}
