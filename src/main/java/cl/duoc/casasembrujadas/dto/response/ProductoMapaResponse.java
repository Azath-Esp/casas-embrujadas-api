package cl.duoc.casasembrujadas.dto.response;

import cl.duoc.casasembrujadas.model.EstadoProducto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoMapaResponse {

    private Long idProducto;
    private String titulo;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private BigDecimal precio;
    private EstadoProducto estado;
    private String imagenPrincipal;
}