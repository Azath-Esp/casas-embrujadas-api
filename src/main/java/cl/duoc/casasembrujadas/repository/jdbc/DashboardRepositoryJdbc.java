package cl.duoc.casasembrujadas.repository.jdbc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import cl.duoc.casasembrujadas.dto.response.EtiquetaCantidadResponse;
import cl.duoc.casasembrujadas.dto.response.VentaMensualResponse;
import cl.duoc.casasembrujadas.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DashboardRepositoryJdbc implements DashboardRepository {

    private static final RowMapper<EtiquetaCantidadResponse> ETIQUETA_CANTIDAD = (rs, fila) -> EtiquetaCantidadResponse.builder()
            .etiqueta(rs.getString("etiqueta"))
            .cantidad(rs.getLong("cantidad"))
            .build();

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<VentaMensualResponse> ventasPorMes(LocalDateTime desde) {
        String sql = """
                SELECT DATE_FORMAT(b.fecha, '%Y-%m') AS mes, COUNT(*) AS ventas, SUM(b.total) AS ingresos
                FROM boleta b
                WHERE b.estado = 'PAGADA' AND b.fecha >= :desde
                GROUP BY DATE_FORMAT(b.fecha, '%Y-%m')
                ORDER BY mes
                """;
        return jdbc.query(sql, Map.of("desde", desde), (rs, fila) -> VentaMensualResponse.builder()
                .mes(rs.getString("mes"))
                .ventas(rs.getLong("ventas"))
                .ingresos(rs.getBigDecimal("ingresos"))
                .build());
    }

    @Override
    public BigDecimal ingresosTotales() {
        return jdbc.queryForObject("SELECT COALESCE(SUM(total), 0) FROM boleta WHERE estado = 'PAGADA'", Map.of(), BigDecimal.class);
    }

    @Override
    public List<EtiquetaCantidadResponse> productosPorEstado() {
        return jdbc.query("SELECT estado AS etiqueta, COUNT(*) AS cantidad FROM producto GROUP BY estado ORDER BY estado", ETIQUETA_CANTIDAD);
    }

    @Override
    public List<EtiquetaCantidadResponse> categoriasMasVendidas(int limite) {
        String sql = """
                SELECT c.nombre AS etiqueta, COUNT(*) AS cantidad
                FROM detalle_boleta d
                JOIN boleta b ON b.id_boleta = d.id_boleta
                JOIN producto_categoria pc ON pc.id_producto = d.id_producto
                JOIN categoria c ON c.id_categoria = pc.id_categoria
                WHERE b.estado = 'PAGADA'
                GROUP BY c.id_categoria, c.nombre
                ORDER BY cantidad DESC, c.nombre
                LIMIT :limite
                """;
        return jdbc.query(sql, Map.of("limite", limite), ETIQUETA_CANTIDAD);
    }

    @Override
    public long contarReservasActivas() {
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM reserva WHERE estado = 'ACTIVA'", Map.of(), Long.class);
        return total == null ? 0 : total;
    }

    @Override
    public List<EtiquetaCantidadResponse> topFavoritos(int limite) {
        String sql = """
                SELECT p.titulo AS etiqueta, COUNT(*) AS cantidad
                FROM favorito f
                JOIN producto p ON p.id_producto = f.id_producto
                GROUP BY p.id_producto, p.titulo
                ORDER BY cantidad DESC, p.titulo
                LIMIT :limite
                """;
        return jdbc.query(sql, Map.of("limite", limite), ETIQUETA_CANTIDAD);
    }
}
