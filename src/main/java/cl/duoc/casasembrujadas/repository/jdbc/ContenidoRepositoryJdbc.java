package cl.duoc.casasembrujadas.repository.jdbc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import cl.duoc.casasembrujadas.model.FichaParanormal;
import cl.duoc.casasembrujadas.model.Imagen;
import cl.duoc.casasembrujadas.model.Mito;
import cl.duoc.casasembrujadas.repository.ContenidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContenidoRepositoryJdbc implements ContenidoRepository {

    private static final String SELECT_IMAGEN = "SELECT id_imagen, url, orden, autor, licencia, url_fuente, id_producto FROM imagen ";
    private static final String SELECT_MITO = "SELECT id_mito, titulo, relato, anio_origen, fuente, id_producto FROM mito ";

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<Imagen> listarImagenes(Long idProducto) {
        return jdbc.query(SELECT_IMAGEN + "WHERE id_producto = :id ORDER BY orden, id_imagen", Map.of("id", idProducto), RowMappers.IMAGEN);
    }

    @Override
    public Optional<Imagen> buscarImagen(Long idImagen) {
        return jdbc.query(SELECT_IMAGEN + "WHERE id_imagen = :id", Map.of("id", idImagen), RowMappers.IMAGEN).stream().findFirst();
    }

    @Override
    public Long insertarImagen(Imagen imagen) {
        String sql = """
                INSERT INTO imagen (url, orden, autor, licencia, url_fuente, id_producto)
                VALUES (:url, :orden, :autor, :licencia, :urlFuente, :idProducto)
                """;
        KeyHolder llave = new GeneratedKeyHolder();
        jdbc.update(sql, parametrosDe(imagen), llave, new String[] {"id_imagen"});
        return llave.getKey().longValue();
    }

    @Override
    public void actualizarImagen(Imagen imagen) {
        String sql = """
                UPDATE imagen
                SET url = :url, orden = :orden, autor = :autor, licencia = :licencia, url_fuente = :urlFuente
                WHERE id_imagen = :id
                """;
        jdbc.update(sql, parametrosDe(imagen).addValue("id", imagen.getIdImagen()));
    }

    private MapSqlParameterSource parametrosDe(Imagen imagen) {
        return new MapSqlParameterSource()
                .addValue("url", imagen.getUrl())
                .addValue("orden", imagen.getOrden())
                .addValue("autor", imagen.getAutor())
                .addValue("licencia", imagen.getLicencia())
                .addValue("urlFuente", imagen.getUrlFuente())
                .addValue("idProducto", imagen.getIdProducto());
    }

    @Override
    public boolean eliminarImagen(Long idImagen) {
        return jdbc.update("DELETE FROM imagen WHERE id_imagen = :id", Map.of("id", idImagen)) > 0;
    }

    @Override
    public List<Mito> listarMitos(Long idProducto) {
        return jdbc.query(SELECT_MITO + "WHERE id_producto = :id ORDER BY id_mito", Map.of("id", idProducto), RowMappers.MITO);
    }

    @Override
    public Optional<Mito> buscarMito(Long idMito) {
        return jdbc.query(SELECT_MITO + "WHERE id_mito = :id", Map.of("id", idMito), RowMappers.MITO).stream().findFirst();
    }

    @Override
    public Long insertarMito(Mito mito) {
        String sql = """
                INSERT INTO mito (titulo, relato, anio_origen, fuente, id_producto)
                VALUES (:titulo, :relato, :anioOrigen, :fuente, :idProducto)
                """;
        KeyHolder llave = new GeneratedKeyHolder();
        jdbc.update(sql, parametrosDe(mito), llave, new String[] {"id_mito"});
        return llave.getKey().longValue();
    }

    @Override
    public void actualizarMito(Mito mito) {
        String sql = """
                UPDATE mito
                SET titulo = :titulo, relato = :relato, anio_origen = :anioOrigen, fuente = :fuente
                WHERE id_mito = :id
                """;
        jdbc.update(sql, parametrosDe(mito).addValue("id", mito.getIdMito()));
    }

    private MapSqlParameterSource parametrosDe(Mito mito) {
        return new MapSqlParameterSource()
                .addValue("titulo", mito.getTitulo())
                .addValue("relato", mito.getRelato())
                .addValue("anioOrigen", mito.getAnioOrigen())
                .addValue("fuente", mito.getFuente())
                .addValue("idProducto", mito.getIdProducto());
    }

    @Override
    public boolean eliminarMito(Long idMito) {
        return jdbc.update("DELETE FROM mito WHERE id_mito = :id", Map.of("id", idMito)) > 0;
    }

    @Override
    public Optional<FichaParanormal> buscarFicha(Long idProducto) {
        String sql = """
                SELECT id_producto, tipo_entidad, horario_actividad, fenomenos_reportados, ultimo_avistamiento
                FROM ficha_paranormal
                WHERE id_producto = :id
                """;
        return jdbc.query(sql, Map.of("id", idProducto), RowMappers.FICHA).stream().findFirst();
    }

    @Override
    public void guardarFicha(FichaParanormal ficha) {
        String sql = """
                INSERT INTO ficha_paranormal (id_producto, tipo_entidad, horario_actividad, fenomenos_reportados, ultimo_avistamiento)
                VALUES (:idProducto, :tipoEntidad, :horarioActividad, :fenomenos, :ultimoAvistamiento)
                ON CONFLICT (id_producto) DO UPDATE SET
                    tipo_entidad = EXCLUDED.tipo_entidad,
                    horario_actividad = EXCLUDED.horario_actividad,
                    fenomenos_reportados = EXCLUDED.fenomenos_reportados,
                    ultimo_avistamiento = EXCLUDED.ultimo_avistamiento
                """;
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("idProducto", ficha.getIdProducto())
                .addValue("tipoEntidad", ficha.getTipoEntidad())
                .addValue("horarioActividad", ficha.getHorarioActividad())
                .addValue("fenomenos", ficha.getFenomenosReportados())
                .addValue("ultimoAvistamiento", ficha.getUltimoAvistamiento());
        jdbc.update(sql, parametros);
    }

    @Override
    public boolean eliminarFicha(Long idProducto) {
        return jdbc.update("DELETE FROM ficha_paranormal WHERE id_producto = :id", Map.of("id", idProducto)) > 0;
    }
}
