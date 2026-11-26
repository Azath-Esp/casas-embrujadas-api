package cl.duoc.casasembrujadas.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MitoRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título admite hasta 150 caracteres")
    @Schema(example = "La dama del balcón")
    private String titulo;

    @NotBlank(message = "El relato es obligatorio")
    @Schema(example = "Los vecinos cuentan que una figura se asoma al balcón cada invierno.")
    private String relato;

    @Min(value = 1000, message = "El año de origen mínimo es 1000")
    @Max(value = 2100, message = "El año de origen máximo es 2100")
    @Schema(example = "1920")
    private Integer anioOrigen;

    @NotBlank(message = "La fuente es obligatoria")
    @Size(max = 500, message = "La fuente admite hasta 500 caracteres")
    @Schema(example = "https://www.memoriachilena.gob.cl")
    private String fuente;
}