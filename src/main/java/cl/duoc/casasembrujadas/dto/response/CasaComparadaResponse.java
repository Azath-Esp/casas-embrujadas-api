package cl.duoc.casasembrujadas.dto.response;

import java.math.BigDecimal;

import cl.duoc.casasembrujadas.model.EstadoProducto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CasaComparadaResponse {

    private Long idProducto;
    private String titulo;
    private String imagenPrincipal;
    private EstadoProducto estado;
    private BigDecimal precio;
    private Integer metrosCuadrados;
    private Integer dormitorios;
    private Integer banos;
    private Integer nivelEmbrujo;
    private String tipoEntidad;
    private int cantidadMitos;
    private String region;
}
