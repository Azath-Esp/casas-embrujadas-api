package cl.duoc.casasembrujadas.controller;

import cl.duoc.casasembrujadas.dto.request.ProductoBusquedaRequest;
import cl.duoc.casasembrujadas.dto.response.ErrorResponse;
import cl.duoc.casasembrujadas.dto.response.PaginaResponse;
import cl.duoc.casasembrujadas.dto.response.ProductoDetalleResponse;
import cl.duoc.casasembrujadas.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v2/productos")
@RequiredArgsConstructor
@Tag(name = "Productos v2", description = "Casas con imágenes, categorías, mitos, ficha paranormal y coordenadas")
public class ProductoV2Controller {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Lista las casas con su detalle completo", description = "Mismos filtros que la versión 1. Máximo 20 casas por página.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de casas con detalle"),
            @ApiResponse(responseCode = "400", description = "Algún parámetro tiene un formato inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PaginaResponse<ProductoDetalleResponse> listar(@ParameterObject ProductoBusquedaRequest busqueda) {
        return productoService.buscarConDetalle(busqueda.aFiltro(), busqueda.getPagina(), busqueda.getTamano());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una casa con su detalle completo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "La casa con su detalle"),
            @ApiResponse(responseCode = "404", description = "No existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ProductoDetalleResponse obtener(@PathVariable Long id) {
        return productoService.obtenerDetalle(id);
    }
}