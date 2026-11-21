package cl.duoc.casasembrujadas.repository.jdbc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import cl.duoc.casasembrujadas.model.Comuna;
import cl.duoc.casasembrujadas.model.Region;
import cl.duoc.casasembrujadas.repository.UbicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UbicacionRepositoryJdbc implements UbicacionRepository {

    private static final String SELECT_COMUNA = """
            SELECT c.id_comuna, c.nombre, c.id_region, r.nombre AS region_nombre
            FROM comuna c
            JOIN region r ON r.id_region = c.id_region
            """;

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<Region> listarRegiones() {
        return jdbc.query("SELECT id_region, nombre FROM region ORDER BY id_region", RowMappers.REGION);
    }

    @Override
    public boolean existeRegion(Integer idRegion) {
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM region WHERE id_region = :id", Map.of("id", idRegion), Long.class);
        return total != null && total > 0;
    }

    @Override
    public List<Comuna> listarComunasPorRegion(Integer idRegion) {
        return jdbc.query(SELECT_COMUNA + "WHERE c.id_region = :idRegion ORDER BY c.nombre", Map.of("idRegion", idRegion), RowMappers.COMUNA);
    }

    @Override
    public Optional<Comuna> buscarComunaPorId(Integer idComuna) {
        return jdbc.query(SELECT_COMUNA + "WHERE c.id_comuna = :id", Map.of("id", idComuna), RowMappers.COMUNA).stream().findFirst();
    }
}
