package cl.duoc.casasembrujadas.controller;

import java.util.List;

import cl.duoc.casasembrujadas.dto.response.ErrorResponse;
import cl.duoc.casasembrujadas.dto.response.ProductoResponse;
import cl.duoc.casasembrujadas.security.UsuarioActual;
import cl.duoc.casasembrujadas.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/favoritos")
@RequiredArgsConstructor
@Tag(name = "Favoritos", description = "Casas guardadas por el cliente")
public class FavoritoController {

    private final FavoritoService favoritoService;
    private final UsuarioActual usuarioActual;

    @GetMapping
    @Operation(summary = "Lista mis favoritos", description = "Solo CLIENTE.")
    public List<ProductoResponse> listar() {
        return favoritoService.listar(usuarioActual.obtener().idUsuario());
    }

    @PostMapping("/{idProducto}")
    @Operation(summary = "Agrega una casa a mis favoritos", description = "Solo CLIENTE. Repetir la operación no la duplica.")
    @ApiResponse(responseCode = "204", description = "La casa quedó en favoritos")
    @ApiResponse(responseCode = "404", description = "No existe la casa o está inactiva",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> agregar(@PathVariable Long idProducto) {
        favoritoService.agregar(usuarioActual.obtener().idUsuario(), idProducto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{idProducto}")
    @Operation(summary = "Quita una casa de mis favoritos", description = "Solo CLIENTE.")
    @ApiResponse(responseCode = "204", description = "La casa salió de favoritos")
    @ApiResponse(responseCode = "404", description = "La casa no estaba en favoritos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> quitar(@PathVariable Long idProducto) {
        favoritoService.quitar(usuarioActual.obtener().idUsuario(), idProducto);
        return ResponseEntity.noContent().build();
    }
}