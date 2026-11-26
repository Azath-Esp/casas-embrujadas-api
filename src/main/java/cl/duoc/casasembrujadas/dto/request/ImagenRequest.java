package cl.duoc.casasembrujadas.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagenRequest {

    @NotBlank(message = "La URL es obligatoria")
    @Size(max = 500, message = "La URL admite hasta 500 caracteres")
    @Schema(example = "/img/placeholder-casa.svg")
    private String url;

    @NotNull(message = "El orden es obligatorio")
    @Min(value = 1, message = "El orden mínimo es 1")
    @Schema(example = "1")
    private Integer orden;

    @NotBlank(message = "El autor es obligatorio")
    @Size(max = 150, message = "El autor admite hasta 150 caracteres")
    @Schema(example = "Dinko Ocampo")
    private String autor;

    @NotBlank(message = "La licencia es obligatoria")
    @Size(max = 80, message = "La licencia admite hasta 80 caracteres")
    @Schema(example = "CC BY-SA 4.0")
    private String licencia;

    @Size(max = 500, message = "La URL de la fuente admite hasta 500 caracteres")
    @Schema(example = "https://commons.wikimedia.org/wiki/File:Ejemplo.jpg")
    private String urlFuente;
}