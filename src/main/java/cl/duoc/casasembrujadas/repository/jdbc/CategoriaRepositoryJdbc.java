package cl.duoc.casasembrujadas.repository.jdbc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import cl.duoc.casasembrujadas.model.Categoria;
import cl.duoc.casasembrujadas.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoriaRepositoryJdbc implements CategoriaRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<Categoria> listar() {
        return jdbc.query("SELECT id_categoria, nombre, descripcion FROM categoria ORDER BY nombre", RowMappers.CATEGORIA);
    }

    @Override
    public Optional<Categoria> buscarPorId(Integer idCategoria) {
        return jdbc.query("SELECT id_categoria, nombre, descripcion FROM categoria WHERE id_categoria = :id",
                Map.of("id", idCategoria), RowMappers.CATEGORIA).stream().findFirst();
    }

    @Override
    public boolean existePorNombre(String nombre, Integer idExcluido) {
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("nombre", nombre)
                .addValue("idExcluido", idExcluido == null ? -1 : idExcluido);
        Long total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM categoria WHERE lower(nombre) = lower(:nombre) AND id_categoria <> :idExcluido", parametros, Long.class);
        return total != null && total > 0;
    }

    @Override
    public Integer insertar(Categoria categoria) {
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("nombre", categoria.getNombre())
                .addValue("descripcion", categoria.getDescripcion());
        KeyHolder llave = new GeneratedKeyHolder();
        jdbc.update("INSERT INTO categoria (nombre, descripcion) VALUES (:nombre, :descripcion)", parametros, llave,
                new String[] {"id_categoria"});
        return llave.getKey().intValue();
    }

    @Override
    public void actualizar(Categoria categoria) {
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("id", categoria.getIdCategoria())
                .addValue("nombre", categoria.getNombre())
                .addValue("descripcion", categoria.getDescripcion());
        jdbc.update("UPDATE categoria SET nombre = :nombre, descripcion = :descripcion WHERE id_categoria = :id", parametros);
    }

    @Override
    public boolean eliminar(Integer idCategoria) {
        return jdbc.update("DELETE FROM categoria WHERE id_categoria = :id", Map.of("id", idCategoria)) > 0;
    }

    @Override
    public long contarProductos(Integer idCategoria) {
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM producto_categoria WHERE id_categoria = :id",
                Map.of("id", idCategoria), Long.class);
        return total == null ? 0 : total;
    }

    @Override
    public List<Categoria> listarPorProducto(Long idProducto) {
        String sql = """
                SELECT c.id_categoria, c.nombre, c.descripcion
                FROM categoria c
                JOIN producto_categoria pc ON pc.id_categoria = c.id_categoria
                WHERE pc.id_producto = :idProducto
                ORDER BY c.nombre
                """;
        return jdbc.query(sql, Map.of("idProducto", idProducto), RowMappers.CATEGORIA);
    }

    @Override
    public long contarPorIds(List<Integer> idsCategorias) {
        if (idsCategorias == null || idsCategorias.isEmpty()) {
            return 0;
        }
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM categoria WHERE id_categoria IN (:ids)",
                Map.of("ids", idsCategorias), Long.class);
        return total == null ? 0 : total;
    }
}
