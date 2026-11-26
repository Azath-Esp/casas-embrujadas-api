package cl.duoc.casasembrujadas.dto.request;

import cl.duoc.casasembrujadas.model.RolNombre;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class UsuarioActualizarRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "El nombre admite hasta 60 caracteres")
    @Schema(example = "Camila")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 80, message = "Los apellidos admiten hasta 80 caracteres")
    @Schema(example = "Reyes Pinto")
    private String apellidos;

    @Size(max = 150, message = "La dirección admite hasta 150 caracteres")
    @Schema(example = "Av. Italia 1200")
    private String direccion;

    @Schema(example = "11")
    private Integer idComuna;

    @NotNull(message = "El rol es obligatorio")
    @Schema(example = "CLIENTE")
    private RolNombre rol;

    @NotNull(message = "Debe indicar si el usuario está activo")
    @Schema(example = "true")
    private Boolean activo;
}