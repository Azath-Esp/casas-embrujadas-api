package cl.duoc.casasembrujadas.repository;

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
public class ProductoFiltro {

    private Integer idCategoria;
    private Integer idComuna;
    private Integer idRegion;
    private BigDecimal precioMax;
    private Integer nivelEmbrujo;
    private EstadoProducto estado;
    private String texto;
    private boolean incluirInactivas;
}
