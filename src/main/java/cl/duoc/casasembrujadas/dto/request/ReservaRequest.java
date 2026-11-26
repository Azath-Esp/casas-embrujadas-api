package cl.duoc.casasembrujadas.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequest {

    @NotNull(message = "La casa es obligatoria")
    @Schema(example = "1")
    private Long idProducto;
}