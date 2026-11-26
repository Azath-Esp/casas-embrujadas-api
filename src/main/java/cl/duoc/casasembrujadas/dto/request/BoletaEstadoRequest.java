package cl.duoc.casasembrujadas.dto.request;

import cl.duoc.casasembrujadas.model.EstadoBoleta;
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
public class BoletaEstadoRequest {

    @NotNull(message = "El estado es obligatorio")
    @Schema(example = "ANULADA")
    private EstadoBoleta estado;
}