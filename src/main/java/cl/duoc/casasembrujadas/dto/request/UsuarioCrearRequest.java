package cl.duoc.casasembrujadas.dto.request;

import cl.duoc.casasembrujadas.dto.validation.RunValido;
import cl.duoc.casasembrujadas.model.RolNombre;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCrearRequest {

    @NotBlank(message = "El RUN es obligatorio")
    @RunValido
    @Schema(example = "12345678-5")
    private String run;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "El nombre admite hasta 60 caracteres")
    @Schema(example = "Camila")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 80, message = "Los apellidos admiten hasta 80 caracteres")
    @Schema(example = "Reyes Pinto")
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 120, message = "El correo admite hasta 120 caracteres")
    @Schema(example = "nuevo.cliente@casasembrujadas.cl")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La contraseña debe incluir al menos una letra y un número")
    @Schema(example = "Cliente123!")
    private String contrasena;

    @Size(max = 150, message = "La dirección admite hasta 150 caracteres")
    @Schema(example = "Av. Italia 1200")
    private String direccion;

    @Schema(example = "11")
    private Integer idComuna;

    @NotNull(message = "El rol es obligatorio")
    @Schema(example = "VENDEDOR")
    private RolNombre rol;
}