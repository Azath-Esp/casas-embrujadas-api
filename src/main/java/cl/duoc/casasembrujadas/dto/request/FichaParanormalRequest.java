



package cl.duoc.casasembrujadas.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FichaParanormalRequest {

    @NotBlank(message = "El tipo de entidad es obligatorio")
    @Size(max = 80, message = "El tipo de entidad admite hasta 80 caracteres")
    @Schema(example = "Apariciones")
    private String tipoEntidad;

    @NotBlank(message = "El horario de actividad es obligatorio")
    @Size(max = 80, message = "El horario de actividad admite hasta 80 caracteres")
    @Schema(example = "De noche")
    private String horarioActividad;

    @NotBlank(message = "Los fenómenos reportados son obligatorios")
    @Schema(example = "Pasos en el segundo piso y puertas que se cierran.")
    private String fenomenosReportados;

    @PastOrPresent(message = "El último avistamiento no puede ser una fecha futura")
    @Schema(example = "2026-10-31")
    private LocalDate ultimoAvistamiento;
}