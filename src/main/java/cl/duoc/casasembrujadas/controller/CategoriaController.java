package cl.duoc.casasembrujadas.controller;

import java.net.URI;
import java.util.List;

import cl.duoc.casasembrujadas.dto.request.CategoriaRequest;
import cl.duoc.casasembrujadas.dto.response.CategoriaResponse;
import cl.duoc.casasembrujadas.dto.response.ErrorResponse;
import cl.duoc.casasembrujadas.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Agrupaciones de casas")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Lista las categorías")
    public List<CategoriaResponse> listar() {
        return categoriaService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una categoría")
    @ApiResponse(responseCode = "200", description = "La categoría")
    @ApiResponse(responseCode = "404", description = "No existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public CategoriaResponse obtener(@PathVariable Integer id) {
        return categoriaService.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crea una categoría", description = "Solo ADMIN.")
    @ApiResponse(responseCode = "201", description = "Categoría creada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Ya existe una categoría con ese nombre",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<CategoriaResponse> crear(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse creada = categoriaService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getIdCategoria())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifica una categoría", description = "Solo ADMIN.")
    @ApiResponse(responseCode = "200", description = "Categoría modificada")
    @ApiResponse(responseCode = "404", description = "No existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Ya existe otra categoría con ese nombre",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public CategoriaResponse actualizar(@PathVariable Integer id, @Valid @RequestBody CategoriaRequest request) {
        return categoriaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una categoría", description = "Solo ADMIN. No se puede eliminar si tiene casas asociadas.")
    @ApiResponse(responseCode = "204", description = "Categoría eliminada")
    @ApiResponse(responseCode = "404", description = "No existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Tiene casas asociadas",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}