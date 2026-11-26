package cl.duoc.casasembrujadas.model;

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
public class Producto {

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
    private BigDecimal latitud;
    private BigDecimal longitud;
    private LocalDateTime fechaPublicacion;
    private Integer idComuna;
    private String comunaNombre;
    private Integer idRegion;
    private String regionNombre;
    private String imagenPrincipal;
}