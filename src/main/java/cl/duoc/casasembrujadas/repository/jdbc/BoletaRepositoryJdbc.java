package cl.duoc.casasembrujadas.repository.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import cl.duoc.casasembrujadas.model.Boleta;
import cl.duoc.casasembrujadas.model.DetalleBoleta;
import cl.duoc.casasembrujadas.model.EstadoBoleta;
import cl.duoc.casasembrujadas.repository.BoletaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoletaRepositoryJdbc implements BoletaRepository {

    private static final String SELECT_BOLETA = """
            SELECT b.id_boleta, b.numero, b.fecha, b.neto, b.iva, b.total, b.estado, b.id_usuario,
                   CONCAT(u.nombre, ' ', u.apellidos) AS cliente_nombre, u.run AS cliente_run, u.correo AS cliente_correo
            FROM boleta b
            JOIN usuario u ON u.id_usuario = b.id_usuario
            """;

    private static final String SELECT_DETALLE = """
            SELECT d.id_detalle, d.id_boleta, d.id_producto, p.titulo AS producto_titulo,
                   d.cantidad, d.precio_unitario, d.subtotal
            FROM detalle_boleta d
            JOIN producto p ON p.id_producto = d.id_producto
            """;

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public int siguienteNumero() {
        Integer numero = jdbc.queryForObject("SELECT COALESCE(MAX(numero), 0) + 1 FROM boleta FOR UPDATE", Map.of(), Integer.class);
        return numero == null ? 1 : numero;
    }

    @Override
    public Long insertar(Boleta boleta) {
        String sql = """
                INSERT INTO boleta (numero, fecha, neto, iva, total, estado, id_usuario)
                VALUES (:numero, :fecha, :neto, :iva, :total, :estado, :idUsuario)
                """;
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("numero", boleta.getNumero())
                .addValue("fecha", boleta.getFecha())
                .addValue("neto", boleta.getNeto())
                .addValue("iva", boleta.getIva())
                .addValue("total", boleta.getTotal())
                .addValue("estado", boleta.getEstado().name())
                .addValue("idUsuario", boleta.getIdUsuario());
        KeyHolder llave = new GeneratedKeyHolder();
        jdbc.update(sql, parametros, llave);
        return llave.getKey().longValue();
    }

    @Override
    public void insertarDetalles(Long idBoleta, List<DetalleBoleta> detalles) {
        String sql = """
                INSERT INTO detalle_boleta (id_boleta, id_producto, cantidad, precio_unitario, subtotal)
                VALUES (:idBoleta, :idProducto, :cantidad, :precioUnitario, :subtotal)
                """;
        MapSqlParameterSource[] lote = detalles.stream()
                .map(detalle -> new MapSqlParameterSource()
                        .addValue("idBoleta", idBoleta)
                        .addValue("idProducto", detalle.getIdProducto())
                        .addValue("cantidad", detalle.getCantidad())
                        .addValue("precioUnitario", detalle.getPrecioUnitario())
                        .addValue("subtotal", detalle.getSubtotal()))
                .toArray(MapSqlParameterSource[]::new);
        jdbc.batchUpdate(sql, lote);
    }

    @Override
    public Optional<Boleta> buscarPorId(Long idBoleta) {
        List<Boleta> boletas = jdbc.query(SELECT_BOLETA + "WHERE b.id_boleta = :id", Map.of("id", idBoleta), RowMappers.BOLETA);
        cargarDetalles(boletas);
        return boletas.stream().findFirst();
    }

    @Override
    public List<Boleta> listar() {
        List<Boleta> boletas = jdbc.query(SELECT_BOLETA + "ORDER BY b.numero DESC", RowMappers.BOLETA);
        cargarDetalles(boletas);
        return boletas;
    }

    @Override
    public List<Boleta> listarPorUsuario(Long idUsuario) {
        List<Boleta> boletas = jdbc.query(SELECT_BOLETA + "WHERE b.id_usuario = :id ORDER BY b.numero DESC", Map.of("id", idUsuario), RowMappers.BOLETA);
        cargarDetalles(boletas);
        return boletas;
    }

    private void cargarDetalles(List<Boleta> boletas) {
        if (boletas.isEmpty()) {
            return;
        }
        List<Long> ids = boletas.stream().map(Boleta::getIdBoleta).toList();
        Map<Long, List<DetalleBoleta>> porBoleta = jdbc
                .query(SELECT_DETALLE + "WHERE d.id_boleta IN (:ids) ORDER BY d.id_detalle", Map.of("ids", ids), RowMappers.DETALLE_BOLETA)
                .stream()
                .collect(Collectors.groupingBy(DetalleBoleta::getIdBoleta));
        boletas.forEach(boleta -> boleta.setDetalles(porBoleta.getOrDefault(boleta.getIdBoleta(), new ArrayList<>())));
    }

    @Override
    public void actualizarEstado(Long idBoleta, EstadoBoleta estado) {
        jdbc.update("UPDATE boleta SET estado = :estado WHERE id_boleta = :id", Map.of("estado", estado.name(), "id", idBoleta));
    }
}
