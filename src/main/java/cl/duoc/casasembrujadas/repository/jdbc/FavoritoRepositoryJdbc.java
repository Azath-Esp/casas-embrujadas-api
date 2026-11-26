package cl.duoc.casasembrujadas.repository.jdbc;

import java.util.List;
import java.util.Map;

import cl.duoc.casasembrujadas.model.Producto;
import cl.duoc.casasembrujadas.repository.FavoritoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FavoritoRepositoryJdbc implements FavoritoRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<Producto> listarProductos(Long idUsuario) {
        String sql = ProductoRepositoryJdbc.SELECT_PRODUCTO + """
                JOIN favorito f ON f.id_producto = p.id_producto
                WHERE f.id_usuario = :idUsuario
                ORDER BY f.fecha DESC
                """;
        return jdbc.query(sql, Map.of("idUsuario", idUsuario), RowMappers.PRODUCTO);
    }

    @Override
    public boolean existe(Long idUsuario, Long idProducto) {
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM favorito WHERE id_usuario = :idUsuario AND id_producto = :idProducto",
                Map.of("idUsuario", idUsuario, "idProducto", idProducto), Long.class);
        return total != null && total > 0;
    }

    @Override
    public void agregar(Long idUsuario, Long idProducto) {
        jdbc.update("INSERT INTO favorito (id_usuario, id_producto) VALUES (:idUsuario, :idProducto) ON CONFLICT DO NOTHING",
                Map.of("idUsuario", idUsuario, "idProducto", idProducto));
    }

    @Override
    public boolean quitar(Long idUsuario, Long idProducto) {
        return jdbc.update("DELETE FROM favorito WHERE id_usuario = :idUsuario AND id_producto = :idProducto",
                Map.of("idUsuario", idUsuario, "idProducto", idProducto)) > 0;
    }
}
