package cl.duoc.casasembrujadas.controller;

import java.net.URI;

import cl.duoc.casasembrujadas.dto.request.ProductoBusquedaRequest;
import cl.duoc.casasembrujadas.dto.request.ProductoRequest;
import cl.duoc.casasembrujadas.dto.response.ErrorResponse;
import cl.duoc.casasembrujadas.dto.response.PaginaResponse;
import cl.duoc.casasembrujadas.dto.response.ProductoDetalleResponse;
import cl.duoc.casasembrujadas.dto.response.ProductoResponse;
import cl.duoc.casasembrujadas.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos v1", description = "Catálogo de casas embrujadas y su administración")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Lista las casas", description = "Filtros opcionales combinables y paginación. Por defecto oculta las casas inactivas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de casas"),
            @ApiResponse(responseCode = "400", description = "Algún parámetro tiene un formato inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PaginaResponse<ProductoResponse> listar(@ParameterObject ProductoBusquedaRequest busqueda) {
        return productoService.buscar(busqueda.aFiltro(), busqueda.getPagina(), busqueda.getTamano());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una casa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "La casa"),
            @ApiResponse(responseCode = "404", description = "No existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ProductoResponse obtener(@PathVariable Long id) {
        return productoService.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crea una casa", description = "Solo ADMIN. Responde la casa creada con sus categorías.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Casa creada; el encabezado Location indica su URL"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "La comuna o alguna categoría no existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Estado inicial no permitido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductoDetalleResponse> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoDetalleResponse creada = productoService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getIdProducto())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifica una casa", description = "Solo ADMIN. El estado solo admite DISPONIBLE o INACTIVA.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Casa modificada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No existe la casa, la comuna o una categoría",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Cambio de estado no permitido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ProductoDetalleResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return productoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una casa", description = "Solo ADMIN. Si la casa tiene boletas, se desactiva en vez de borrarse.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Casa eliminada o desactivada"),
            @ApiResponse(responseCode = "404", description = "No existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "La casa tiene una reserva activa",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}