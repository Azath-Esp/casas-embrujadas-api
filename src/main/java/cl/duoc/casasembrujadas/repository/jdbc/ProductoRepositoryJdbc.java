package cl.duoc.casasembrujadas.repository.jdbc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.Producto;
import cl.duoc.casasembrujadas.repository.ProductoFiltro;
import cl.duoc.casasembrujadas.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductoRepositoryJdbc implements ProductoRepository {

    static final String SELECT_PRODUCTO = """
            SELECT p.id_producto, p.titulo, p.descripcion, p.precio, p.direccion, p.metros_cuadrados,
                   p.dormitorios, p.banos, p.nivel_embrujo, p.estado, p.latitud, p.longitud, p.fecha_publicacion,
                   p.id_comuna, c.nombre AS comuna_nombre, r.id_region, r.nombre AS region_nombre,
                   (SELECT i.url FROM imagen i WHERE i.id_producto = p.id_producto
                    ORDER BY i.orden, i.id_imagen LIMIT 1) AS imagen_principal
            FROM producto p
            JOIN comuna c ON c.id_comuna = p.id_comuna
            JOIN region r ON r.id_region = c.id_region
            """;

    private static final String FROM_CONTAR = """
            SELECT COUNT(*)
            FROM producto p
            JOIN comuna c ON c.id_comuna = p.id_comuna
            JOIN region r ON r.id_region = c.id_region
            """;

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<Producto> buscar(ProductoFiltro filtro, int pagina, int tamano) {
        MapSqlParameterSource parametros = new MapSqlParameterSource();
        String sql = SELECT_PRODUCTO + construirWhere(filtro, parametros)
                + " ORDER BY p.fecha_publicacion DESC, p.id_producto LIMIT :tamano OFFSET :desde";
        parametros.addValue("tamano", tamano);
        parametros.addValue("desde", pagina * tamano);
        return jdbc.query(sql, parametros, RowMappers.PRODUCTO);
    }

    @Override
    public long contar(ProductoFiltro filtro) {
        MapSqlParameterSource parametros = new MapSqlParameterSource();
        Long total = jdbc.queryForObject(FROM_CONTAR + construirWhere(filtro, parametros), parametros, Long.class);
        return total == null ? 0 : total;
    }

    private String construirWhere(ProductoFiltro filtro, MapSqlParameterSource parametros) {
        StringBuilder where = new StringBuilder("WHERE 1 = 1");
        if (filtro.getIdCategoria() != null) {
            where.append(" AND EXISTS (SELECT 1 FROM producto_categoria pc"
                    + " WHERE pc.id_producto = p.id_producto AND pc.id_categoria = :idCategoria)");
            parametros.addValue("idCategoria", filtro.getIdCategoria());
        }
        if (filtro.getIdComuna() != null) {
            where.append(" AND p.id_comuna = :idComuna");
            parametros.addValue("idComuna", filtro.getIdComuna());
        }
        if (filtro.getIdRegion() != null) {
            where.append(" AND r.id_region = :idRegion");
            parametros.addValue("idRegion", filtro.getIdRegion());
        }
        if (filtro.getPrecioMax() != null) {
            where.append(" AND p.precio <= :precioMax");
            parametros.addValue("precioMax", filtro.getPrecioMax());
        }
        if (filtro.getNivelEmbrujo() != null) {
            where.append(" AND p.nivel_embrujo = :nivelEmbrujo");
            parametros.addValue("nivelEmbrujo", filtro.getNivelEmbrujo());
        }
        if (filtro.getEstado() != null) {
            where.append(" AND p.estado = :estado");
            parametros.addValue("estado", filtro.getEstado().name());
        } else if (!filtro.isIncluirInactivas()) {
            where.append(" AND p.estado <> 'INACTIVA'");
        }
        if (filtro.getTexto() != null && !filtro.getTexto().isBlank()) {
            where.append(" AND (p.titulo LIKE :texto OR p.descripcion LIKE :texto)");
            parametros.addValue("texto", "%" + filtro.getTexto().trim() + "%");
        }
        return where.toString();
    }

    @Override
    public Optional<Producto> buscarPorId(Long idProducto) {
        return jdbc.query(SELECT_PRODUCTO + "WHERE p.id_producto = :id", Map.of("id", idProducto), RowMappers.PRODUCTO)
                .stream().findFirst();
    }

    @Override
    public Optional<Producto> buscarPorIdConBloqueo(Long idProducto) {
        return jdbc.query(SELECT_PRODUCTO + "WHERE p.id_producto = :id FOR UPDATE OF p", Map.of("id", idProducto), RowMappers.PRODUCTO)
                .stream().findFirst();
    }

    @Override
    public List<Producto> buscarPorIds(List<Long> idsProductos) {
        if (idsProductos == null || idsProductos.isEmpty()) {
            return List.of();
        }
        return jdbc.query(SELECT_PRODUCTO + "WHERE p.id_producto IN (:ids) ORDER BY p.id_producto",
                Map.of("ids", idsProductos), RowMappers.PRODUCTO);
    }

    @Override
    public List<Producto> listarParaMapa(Integer idRegion) {
        MapSqlParameterSource parametros = new MapSqlParameterSource();
        String sql = SELECT_PRODUCTO + "WHERE p.estado <> 'INACTIVA'";
        if (idRegion != null) {
            sql += " AND r.id_region = :idRegion";
            parametros.addValue("idRegion", idRegion);
        }
        return jdbc.query(sql + " ORDER BY p.id_producto", parametros, RowMappers.PRODUCTO);
    }

    @Override
    public Long insertar(Producto producto) {
        String sql = """
                INSERT INTO producto (titulo, descripcion, precio, direccion, metros_cuadrados, dormitorios, banos,
                                      nivel_embrujo, estado, latitud, longitud, id_comuna)
                VALUES (:titulo, :descripcion, :precio, :direccion, :metrosCuadrados, :dormitorios, :banos,
                        :nivelEmbrujo, :estado, :latitud, :longitud, :idComuna)
                """;
        KeyHolder llave = new GeneratedKeyHolder();
        jdbc.update(sql, parametrosDe(producto), llave);
        return llave.getKey().longValue();
    }

    @Override
    public void actualizar(Producto producto) {
        String sql = """
                UPDATE producto
                SET titulo = :titulo, descripcion = :descripcion, precio = :precio, direccion = :direccion,
                    metros_cuadrados = :metrosCuadrados, dormitorios = :dormitorios, banos = :banos,
                    nivel_embrujo = :nivelEmbrujo, estado = :estado, latitud = :latitud, longitud = :longitud,
                    id_comuna = :idComuna
                WHERE id_producto = :id
                """;
        jdbc.update(sql, parametrosDe(producto).addValue("id", producto.getIdProducto()));
    }

    private MapSqlParameterSource parametrosDe(Producto producto) {
        return new MapSqlParameterSource()
                .addValue("titulo", producto.getTitulo())
                .addValue("descripcion", producto.getDescripcion())
                .addValue("precio", producto.getPrecio())
                .addValue("direccion", producto.getDireccion())
                .addValue("metrosCuadrados", producto.getMetrosCuadrados())
                .addValue("dormitorios", producto.getDormitorios())
                .addValue("banos", producto.getBanos())
                .addValue("nivelEmbrujo", producto.getNivelEmbrujo())
                .addValue("estado", producto.getEstado().name())
                .addValue("latitud", producto.getLatitud())
                .addValue("longitud", producto.getLongitud())
                .addValue("idComuna", producto.getIdComuna());
    }

    @Override
    public void actualizarEstado(Long idProducto, EstadoProducto estado) {
        jdbc.update("UPDATE producto SET estado = :estado WHERE id_producto = :id",
                Map.of("estado", estado.name(), "id", idProducto));
    }

    @Override
    public boolean eliminar(Long idProducto) {
        return jdbc.update("DELETE FROM producto WHERE id_producto = :id", Map.of("id", idProducto)) > 0;
    }

    @Override
    public boolean tieneBoletas(Long idProducto) {
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM detalle_boleta WHERE id_producto = :id",
                Map.of("id", idProducto), Long.class);
        return total != null && total > 0;
    }

    @Override
    public void reemplazarCategorias(Long idProducto, List<Integer> idsCategorias) {
        jdbc.update("DELETE FROM producto_categoria WHERE id_producto = :id", Map.of("id", idProducto));
        MapSqlParameterSource[] lote = idsCategorias.stream()
                .distinct()
                .map(idCategoria -> new MapSqlParameterSource()
                        .addValue("idProducto", idProducto)
                        .addValue("idCategoria", idCategoria))
                .toArray(MapSqlParameterSource[]::new);
        jdbc.batchUpdate("INSERT INTO producto_categoria (id_producto, id_categoria) VALUES (:idProducto, :idCategoria)", lote);
    }
}
