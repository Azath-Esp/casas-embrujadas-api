package cl.duoc.casasembrujadas.service.impl;

import java.util.List;

import cl.duoc.casasembrujadas.dto.request.PerfilRequest;
import cl.duoc.casasembrujadas.dto.request.RegistroRequest;
import cl.duoc.casasembrujadas.dto.request.UsuarioActualizarRequest;
import cl.duoc.casasembrujadas.dto.request.UsuarioCrearRequest;
import cl.duoc.casasembrujadas.dto.response.UsuarioResponse;
import cl.duoc.casasembrujadas.exception.RecursoDuplicadoException;
import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.mapper.UsuarioMapper;
import cl.duoc.casasembrujadas.model.RolNombre;
import cl.duoc.casasembrujadas.model.Usuario;
import cl.duoc.casasembrujadas.repository.UbicacionRepository;
import cl.duoc.casasembrujadas.repository.UsuarioRepository;
import cl.duoc.casasembrujadas.security.UsuarioAutenticado;
import cl.duoc.casasembrujadas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UbicacionRepository ubicacionRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        return guardarNuevo(usuarioMapper.toModel(request), request.getContrasena());
    }

    @Override
    public List<UsuarioResponse> listar() {
        return usuarioRepository.listar().stream().map(usuarioMapper::toResponse).toList();
    }

    @Override
    public UsuarioResponse obtener(Long idUsuario) {
        return usuarioMapper.toResponse(buscar(idUsuario));
    }

    @Override
    @Transactional
    public UsuarioResponse crear(UsuarioCrearRequest request) {
        return guardarNuevo(usuarioMapper.toModel(request), request.getContrasena());
    }

    @Override
    @Transactional
    public UsuarioResponse actualizar(Long idUsuario, UsuarioActualizarRequest request, UsuarioAutenticado admin) {
        Usuario usuario = buscar(idUsuario);
        boolean esSuPropiaCuenta = idUsuario.equals(admin.idUsuario());
        if (esSuPropiaCuenta && (request.getRol() != RolNombre.ADMIN || !request.getActivo())) {
            throw new ReglaNegocioException("No puedes quitarte el rol de administrador ni desactivar tu propia cuenta");
        }
        validarComuna(request.getIdComuna());
        usuario.setNombre(request.getNombre().trim());
        usuario.setApellidos(request.getApellidos().trim());
        usuario.setDireccion(request.getDireccion());
        usuario.setIdComuna(request.getIdComuna());
        usuario.setRol(request.getRol());
        usuario.setActivo(request.getActivo());
        usuarioRepository.actualizar(usuario);
        return usuarioMapper.toResponse(buscar(idUsuario));
    }

    @Override
    @Transactional
    public void eliminar(Long idUsuario, UsuarioAutenticado admin) {
        Usuario usuario = buscar(idUsuario);
        if (idUsuario.equals(admin.idUsuario())) {
            throw new ReglaNegocioException("No puedes eliminar tu propia cuenta");
        }
        if (usuarioRepository.tieneBoletas(idUsuario)) {
            usuario.setActivo(false);
            usuarioRepository.actualizar(usuario);
            return;
        }
        usuarioRepository.eliminar(idUsuario);
    }

    @Override
    @Transactional
    public UsuarioResponse actualizarPerfil(Long idUsuario, PerfilRequest request) {
        Usuario usuario = buscar(idUsuario);
        validarComuna(request.getIdComuna());
        usuario.setNombre(request.getNombre().trim());
        usuario.setApellidos(request.getApellidos().trim());
        usuario.setDireccion(request.getDireccion());
        usuario.setIdComuna(request.getIdComuna());
        usuarioRepository.actualizar(usuario);
        return usuarioMapper.toResponse(buscar(idUsuario));
    }

    private UsuarioResponse guardarNuevo(Usuario usuario, String contrasenaPlana) {
        if (usuarioRepository.existePorCorreo(usuario.getCorreo())) {
            throw new RecursoDuplicadoException("Ya existe una cuenta con el correo " + usuario.getCorreo());
        }
        if (usuarioRepository.existePorRun(usuario.getRun())) {
            throw new RecursoDuplicadoException("Ya existe una cuenta con el RUN " + usuario.getRun());
        }
        validarComuna(usuario.getIdComuna());
        usuario.setContrasena(passwordEncoder.encode(contrasenaPlana));
        Long idUsuario = usuarioRepository.insertar(usuario);
        return usuarioMapper.toResponse(buscar(idUsuario));
    }

    private void validarComuna(Integer idComuna) {
        if (idComuna != null && ubicacionRepository.buscarComunaPorId(idComuna).isEmpty()) {
            throw new RecursoNoEncontradoException("No existe la comuna con id " + idComuna);
        }
    }

    private Usuario buscar(Long idUsuario) {
        return usuarioRepository.buscarPorId(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario con id " + idUsuario));
    }
}
