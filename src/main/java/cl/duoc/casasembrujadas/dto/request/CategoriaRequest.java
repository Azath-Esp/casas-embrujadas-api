package cl.duoc.casasembrujadas.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class CategoriaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "El nombre admite hasta 60 caracteres")
    @Schema(example = "Faros y fuertes")
    private String nombre;

    @Size(max = 255, message = "La descripción admite hasta 255 caracteres")
    @Schema(example = "Construcciones costeras con historias de naufragios.")
    private String descripcion;
}
