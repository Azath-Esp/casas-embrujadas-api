package cl.duoc.casasembrujadas.repository.jdbc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import cl.duoc.casasembrujadas.model.EstadoReserva;
import cl.duoc.casasembrujadas.model.Reserva;
import cl.duoc.casasembrujadas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservaRepositoryJdbc implements ReservaRepository {

    private static final String SELECT_RESERVA = """
            SELECT r.id_reserva, r.id_producto, p.titulo AS producto_titulo, r.id_usuario,
                   CONCAT(u.nombre, ' ', u.apellidos) AS usuario_nombre,
                   r.fecha_inicio, r.fecha_expiracion, r.estado
            FROM reserva r
            JOIN producto p ON p.id_producto = r.id_producto
            JOIN usuario u ON u.id_usuario = r.id_usuario
            """;

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Long insertar(Reserva reserva) {
        String sql = """
                INSERT INTO reserva (id_producto, id_usuario, fecha_inicio, fecha_expiracion, estado)
                VALUES (:idProducto, :idUsuario, :fechaInicio, :fechaExpiracion, :estado)
                """;
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("idProducto", reserva.getIdProducto())
                .addValue("idUsuario", reserva.getIdUsuario())
                .addValue("fechaInicio", reserva.getFechaInicio())
                .addValue("fechaExpiracion", reserva.getFechaExpiracion())
                .addValue("estado", reserva.getEstado().name());
        KeyHolder llave = new GeneratedKeyHolder();
        jdbc.update(sql, parametros, llave);
        return llave.getKey().longValue();
    }

    @Override
    public Optional<Reserva> buscarPorId(Long idReserva) {
        return jdbc.query(SELECT_RESERVA + "WHERE r.id_reserva = :id", Map.of("id", idReserva), RowMappers.RESERVA).stream().findFirst();
    }

    @Override
    public Optional<Reserva> buscarActivaPorProducto(Long idProducto) {
        return jdbc.query(SELECT_RESERVA + "WHERE r.id_producto = :id AND r.estado = 'ACTIVA'", Map.of("id", idProducto), RowMappers.RESERVA)
                .stream().findFirst();
    }

    @Override
    public long contarActivasPorUsuario(Long idUsuario) {
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM reserva WHERE id_usuario = :id AND estado = 'ACTIVA'",
                Map.of("id", idUsuario), Long.class);
        return total == null ? 0 : total;
    }

    @Override
    public List<Reserva> listarPorUsuario(Long idUsuario) {
        return jdbc.query(SELECT_RESERVA + "WHERE r.id_usuario = :id ORDER BY r.fecha_inicio DESC", Map.of("id", idUsuario), RowMappers.RESERVA);
    }

    @Override
    public List<Reserva> listarTodas() {
        return jdbc.query(SELECT_RESERVA + "ORDER BY r.fecha_inicio DESC", RowMappers.RESERVA);
    }

    @Override
    public void actualizarEstado(Long idReserva, EstadoReserva estado) {
        jdbc.update("UPDATE reserva SET estado = :estado WHERE id_reserva = :id", Map.of("estado", estado.name(), "id", idReserva));
    }

    @Override
    public List<Reserva> listarActivasVencidas(LocalDateTime ahora) {
        return jdbc.query(SELECT_RESERVA + "WHERE r.estado = 'ACTIVA' AND r.fecha_expiracion <= :ahora ORDER BY r.fecha_expiracion",
                Map.of("ahora", ahora), RowMappers.RESERVA);
    }
}
