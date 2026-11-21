package cl.duoc.casasembrujadas.controller;

import java.util.List;

import cl.duoc.casasembrujadas.dto.response.ComunaResponse;
import cl.duoc.casasembrujadas.dto.response.ErrorResponse;
import cl.duoc.casasembrujadas.dto.response.RegionResponse;
import cl.duoc.casasembrujadas.service.UbicacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/regiones")
@RequiredArgsConstructor
@Tag(name = "Ubicación", description = "Regiones y comunas de Chile")
public class UbicacionController {

    private final UbicacionService ubicacionService;

    @GetMapping
    @Operation(summary = "Lista las regiones")
    public List<RegionResponse> listarRegiones() {
        return ubicacionService.listarRegiones();
    }

    @GetMapping("/{idRegion}/comunas")
    @Operation(summary = "Lista las comunas de una región")
    @ApiResponse(responseCode = "200", description = "Comunas de la región")
    @ApiResponse(responseCode = "404", description = "No existe la región",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public List<ComunaResponse> listarComunas(@PathVariable Integer idRegion) {
        return ubicacionService.listarComunas(idRegion);
    }
}