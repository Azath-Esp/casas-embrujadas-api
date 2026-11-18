package cl.duoc.casasembrujadas.dto.request;

import cl.duoc.casasembrujadas.model.EstadoProducto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 120, message = "El título admite hasta 120 caracteres")
    @Schema(example = "Casona de prueba")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Schema(example = "Casona antigua con historia propia.")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    @Schema(example = "950000000")
    private BigDecimal precio;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 150, message = "La dirección admite hasta 150 caracteres")
    @Schema(example = "Calle Falsa 123")
    private String direccion;

    @NotNull(message = "Los metros cuadrados son obligatorios")
    @Positive(message = "Los metros cuadrados deben ser mayores que cero")
    @Schema(example = "480")
    private Integer metrosCuadrados;

    @NotNull(message = "Los dormitorios son obligatorios")
    @PositiveOrZero(message = "Los dormitorios no pueden ser negativos")
    @Schema(example = "6")
    private Integer dormitorios;

    @NotNull(message = "Los baños son obligatorios")
    @PositiveOrZero(message = "Los baños no pueden ser negativos")
    @Schema(example = "3")
    private Integer banos;

    @NotNull(message = "El nivel de embrujo es obligatorio")
    @Min(value = 1, message = "El nivel de embrujo mínimo es 1")
    @Max(value = 5, message = "El nivel de embrujo máximo es 5")
    @Schema(example = "4")
    private Integer nivelEmbrujo;

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90", message = "La latitud mínima es -90")
    @DecimalMax(value = "90", message = "La latitud máxima es 90")
    @Schema(example = "-33.4489")
    private BigDecimal latitud;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180", message = "La longitud mínima es -180")
    @DecimalMax(value = "180", message = "La longitud máxima es 180")
    @Schema(example = "-70.6693")
    private BigDecimal longitud;

    @NotNull(message = "La comuna es obligatoria")
    @Schema(example = "10")
    private Integer idComuna;

    @NotEmpty(message = "Debe indicar al menos una categoría")
    @Schema(example = "[1, 5]")
    private List<Integer> idsCategorias;

    @Schema(description = "Opcional. Solo admite DISPONIBLE o INACTIVA; los demás estados los maneja el sistema.", example = "DISPONIBLE")
    private EstadoProducto estado;
}