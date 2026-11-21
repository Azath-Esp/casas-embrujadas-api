package cl.duoc.casasembrujadas.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoletaRequest {

    @NotEmpty(message = "El carrito no puede estar vacío")
    @Schema(example = "[1, 6]")
    private List<@NotNull(message = "El carrito contiene una casa sin identificador") Long> idsProductos;
}
