package cl.duoc.casasembrujadas.controller;

import java.net.URI;
import java.util.List;

import cl.duoc.casasembrujadas.dto.request.BoletaEstadoRequest;
import cl.duoc.casasembrujadas.dto.request.BoletaRequest;
import cl.duoc.casasembrujadas.dto.response.BoletaResponse;
import cl.duoc.casasembrujadas.dto.response.ErrorResponse;
import cl.duoc.casasembrujadas.security.UsuarioActual;
import cl.duoc.casasembrujadas.service.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("/api/v1/boletas")
@RequiredArgsConstructor
@Tag(name = "Boletas", description = "Compra de casas y consulta de boletas")
public class BoletaController {

    private final BoletaService boletaService;
    private final UsuarioActual usuarioActual;

    @PostMapping
    @Operation(summary = "Compra las casas del carrito", description = "Solo CLIENTE. El backend calcula neto, IVA y total.")
    @ApiResponse(responseCode = "201", description = "Boleta creada")
    @ApiResponse(responseCode = "400", description = "Carrito vacío o con datos inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Alguna casa no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Alguna casa está vendida, inactiva, repetida o reservada por otro cliente",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<BoletaResponse> comprar(@Valid @RequestBody BoletaRequest request) {
        BoletaResponse boleta = boletaService.comprar(usuarioActual.obtener().idUsuario(), request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(boleta.getIdBoleta())
                .toUri();
        return ResponseEntity.created(ubicacion).body(boleta);
    }

    @GetMapping
    @Operation(summary = "Lista todas las boletas", description = "ADMIN y VENDEDOR.")
    public List<BoletaResponse> listar() {
        return boletaService.listar();
    }

    @GetMapping("/mis-boletas")
    @Operation(summary = "Lista mis boletas", description = "Solo CLIENTE.")
    public List<BoletaResponse> listarMias() {
        return boletaService.listarMias(usuarioActual.obtener().idUsuario());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una boleta", description = "ADMIN y VENDEDOR ven cualquiera; un CLIENTE solo las suyas.")
    @ApiResponse(responseCode = "200", description = "La boleta con sus detalles")
    @ApiResponse(responseCode = "403", description = "La boleta es de otro cliente",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "No existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public BoletaResponse obtener(@PathVariable Long id) {
        return boletaService.obtener(id, usuarioActual.obtener());
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambia el estado de una boleta", description = "Solo ADMIN. Anular devuelve sus casas a DISPONIBLE.")
    @ApiResponse(responseCode = "200", description = "Estado cambiado")
    @ApiResponse(responseCode = "404", description = "No existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Cambio de estado no permitido",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public BoletaResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody BoletaEstadoRequest request) {
        return boletaService.cambiarEstado(id, request.getEstado());
    }
}