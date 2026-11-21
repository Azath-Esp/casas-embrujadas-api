package cl.duoc.casasembrujadas.repository.jdbc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import cl.duoc.casasembrujadas.model.Usuario;
import cl.duoc.casasembrujadas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UsuarioRepositoryJdbc implements UsuarioRepository {

    private static final String SELECT_USUARIO = """
            SELECT u.id_usuario, u.run, u.nombre, u.apellidos, u.correo, u.contrasena, u.direccion,
                   u.id_comuna, c.nombre AS comuna_nombre, rg.nombre AS region_nombre,
                   r.nombre AS rol, u.activo, u.fecha_registro
            FROM usuario u
            JOIN rol r ON r.id_rol = u.id_rol
            LEFT JOIN comuna c ON c.id_comuna = u.id_comuna
            LEFT JOIN region rg ON rg.id_region = c.id_region
            """;

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Optional<Usuario> buscarPorId(Long idUsuario) {
        return jdbc.query(SELECT_USUARIO + "WHERE u.id_usuario = :id", Map.of("id", idUsuario), RowMappers.USUARIO).stream().findFirst();
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return jdbc.query(SELECT_USUARIO + "WHERE u.correo = :correo", Map.of("correo", correo), RowMappers.USUARIO).stream().findFirst();
    }

    @Override
    public boolean existePorCorreo(String correo) {
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM usuario WHERE correo = :correo", Map.of("correo", correo), Long.class);
        return total != null && total > 0;
    }

    @Override
    public boolean existePorRun(String run) {
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM usuario WHERE run = :run", Map.of("run", run), Long.class);
        return total != null && total > 0;
    }

    @Override
    public List<Usuario> listar() {
        return jdbc.query(SELECT_USUARIO + "ORDER BY u.id_usuario", RowMappers.USUARIO);
    }

    @Override
    public Long insertar(Usuario usuario) {
        String sql = """
                INSERT INTO usuario (run, nombre, apellidos, correo, contrasena, direccion, id_comuna, id_rol, activo)
                VALUES (:run, :nombre, :apellidos, :correo, :contrasena, :direccion, :idComuna,
                        (SELECT id_rol FROM rol WHERE nombre = :rol), :activo)
                """;
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("run", usuario.getRun())
                .addValue("nombre", usuario.getNombre())
                .addValue("apellidos", usuario.getApellidos())
                .addValue("correo", usuario.getCorreo())
                .addValue("contrasena", usuario.getContrasena())
                .addValue("direccion", usuario.getDireccion())
                .addValue("idComuna", usuario.getIdComuna())
                .addValue("rol", usuario.getRol().name())
                .addValue("activo", usuario.isActivo());
        KeyHolder llave = new GeneratedKeyHolder();
        jdbc.update(sql, parametros, llave);
        return llave.getKey().longValue();
    }

    @Override
    public void actualizar(Usuario usuario) {
        String sql = """
                UPDATE usuario
                SET nombre = :nombre, apellidos = :apellidos, direccion = :direccion, id_comuna = :idComuna,
                    id_rol = (SELECT id_rol FROM rol WHERE nombre = :rol), activo = :activo
                WHERE id_usuario = :id
                """;
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("id", usuario.getIdUsuario())
                .addValue("nombre", usuario.getNombre())
                .addValue("apellidos", usuario.getApellidos())
                .addValue("direccion", usuario.getDireccion())
                .addValue("idComuna", usuario.getIdComuna())
                .addValue("rol", usuario.getRol().name())
                .addValue("activo", usuario.isActivo());
        jdbc.update(sql, parametros);
    }

    @Override
    public boolean eliminar(Long idUsuario) {
        return jdbc.update("DELETE FROM usuario WHERE id_usuario = :id", Map.of("id", idUsuario)) > 0;
    }

    @Override
    public boolean tieneBoletas(Long idUsuario) {
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM boleta WHERE id_usuario = :id", Map.of("id", idUsuario), Long.class);
        return total != null && total > 0;
    }
}
