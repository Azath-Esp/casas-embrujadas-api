package cl.duoc.casasembrujadas.controller;

import java.util.List;

import cl.duoc.casasembrujadas.dto.response.ComparacionResponse;
import cl.duoc.casasembrujadas.dto.response.ErrorResponse;
import cl.duoc.casasembrujadas.dto.response.ProductoMapaResponse;
import cl.duoc.casasembrujadas.service.ExploracionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Mapa y comparador", description = "Datos livianos para el mapa y comparación de hasta tres casas")
public class ExploracionController {

    private final ExploracionService exploracionService;

    @GetMapping("/mapa")
    @Operation(summary = "Casas para dibujar en el mapa", description = "Público. Excluye las casas inactivas.")
    @ApiResponse(responseCode = "200", description = "Un elemento por casa, con coordenadas")
    @ApiResponse(responseCode = "404", description = "No existe la región",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public List<ProductoMapaResponse> mapa(
            @Parameter(description = "Id de la región para filtrar", example = "7")
            @RequestParam(required = false) Integer region) {
        return exploracionService.mapa(region);
    }

    @GetMapping("/comparar")
    @Operation(summary = "Compara dos o tres casas", description = "Público. Indica el mejor valor de cada fila: menor precio y mayor en lo demás.")
    @ApiResponse(responseCode = "200", description = "Comparación")
    @ApiResponse(responseCode = "400", description = "Menos de dos o más de tres id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Alguna casa no existe o está inactiva",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Los id se repiten",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ComparacionResponse comparar(
            @Parameter(description = "Id de las casas separados por coma", example = "1,6,4")
            @RequestParam @Size(min = 2, max = 3, message = "Se pueden comparar entre 2 y 3 casas") List<Long> ids) {
        return exploracionService.comparar(ids);
    }
}
