package cl.duoc.casasembrujadas.repository;

import java.util.List;
import java.util.Optional;

import cl.duoc.casasembrujadas.model.Usuario;

public interface UsuarioRepository {

    Optional<Usuario> buscarPorId(Long idUsuario);

    Optional<Usuario> buscarPorCorreo(String correo);

    boolean existePorCorreo(String correo);

    boolean existePorRun(String run);

    List<Usuario> listar();

    Long insertar(Usuario usuario);

    void actualizar(Usuario usuario);

    boolean eliminar(Long idUsuario);

    boolean tieneBoletas(Long idUsuario);
}
