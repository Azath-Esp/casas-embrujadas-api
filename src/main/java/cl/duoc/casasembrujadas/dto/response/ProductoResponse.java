package cl.duoc.casasembrujadas.dto.response;

import cl.duoc.casasembrujadas.model.EstadoProducto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {

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
    private String comuna;
    private String region;
    private String imagenPrincipal;
    private LocalDateTime fechaPublicacion;
}