package cl.duoc.casasembrujadas.dto.request;

import java.math.BigDecimal;

import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.repository.ProductoFiltro;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class ProductoBusquedaRequest {

    @Schema(description = "Id de la categoría", example = "5")
    private Integer categoria;

    @Schema(description = "Id de la comuna", example = "10")
    private Integer comuna;

    @Schema(description = "Id de la región", example = "7")
    private Integer region;

    @Schema(description = "Precio neto máximo", example = "2000000000")
    private BigDecimal precioMax;

    @Schema(description = "Nivel de embrujo exacto, de 1 a 5", example = "5")
    private Integer nivelEmbrujo;

    @Schema(description = "Estado de la casa", example = "DISPONIBLE")
    private EstadoProducto estado;

    @Schema(description = "Texto a buscar en el título y la descripción", example = "palacio")
    private String q;

    @Schema(description = "Incluir las casas retiradas de la venta", example = "false")
    private boolean incluirInactivas;

    @Schema(description = "Número de página, desde 0", example = "0")
    private int pagina = 0;

    @Schema(description = "Casas por página", example = "12")
    private int tamano = 12;

    public ProductoFiltro aFiltro() {
        return ProductoFiltro.builder()
                .idCategoria(categoria)
                .idComuna(comuna)
                .idRegion(region)
                .precioMax(precioMax)
                .nivelEmbrujo(nivelEmbrujo)
                .estado(estado)
                .texto(q)
                .incluirInactivas(incluirInactivas)
                .build();
    }
}