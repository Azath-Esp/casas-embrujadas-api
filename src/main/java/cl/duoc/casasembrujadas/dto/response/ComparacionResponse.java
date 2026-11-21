package cl.duoc.casasembrujadas.dto.response;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparacionResponse {

    private List<CasaComparadaResponse> casas;

    @Schema(description = "Por cada campo, los id de las casas con el mejor valor. Hay más de uno si empatan.",
            example = "{\"precio\": [1], \"metrosCuadrados\": [4]}")
    private Map<String, List<Long>> mejores;
}
