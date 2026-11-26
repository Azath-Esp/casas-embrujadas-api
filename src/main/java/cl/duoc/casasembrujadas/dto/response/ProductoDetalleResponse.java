package cl.duoc.casasembrujadas.dto.response;

import cl.duoc.casasembrujadas.model.EstadoProducto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDetalleResponse {

    private Long idProducto;
    private String titulo;
    private String descripcion;
    private BigDecimal precio;
    private String direccion;
    private Integer metrosCuadrados;
    private Integer dormitorios;
    private Integer banos;
    private Integer nivelEmbrujo;
    private EstadoProducto estado;
    private Integer idComuna;
    private String comuna;
    private Integer idRegion;
    private String region;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private LocalDateTime fechaPublicacion;
    private List<ImagenResponse> imagenes;
    private List<CategoriaResponse> categorias;
    private List<MitoResponse> mitos;
    private FichaParanormalResponse fichaParanormal;
}