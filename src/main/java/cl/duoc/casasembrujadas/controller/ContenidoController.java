package cl.duoc.casasembrujadas.controller;

import java.net.URI;
import java.util.List;

import cl.duoc.casasembrujadas.dto.request.FichaParanormalRequest;
import cl.duoc.casasembrujadas.dto.request.ImagenRequest;
import cl.duoc.casasembrujadas.dto.request.MitoRequest;
import cl.duoc.casasembrujadas.dto.response.ErrorResponse;
import cl.duoc.casasembrujadas.dto.response.FichaParanormalResponse;
import cl.duoc.casasembrujadas.dto.response.ImagenResponse;
import cl.duoc.casasembrujadas.dto.response.MitoResponse;
import cl.duoc.casasembrujadas.service.ContenidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/v1/productos/{idProducto}")
@RequiredArgsConstructor
@Tag(name = "Contenido de las casas", description = "Imágenes con atribución, mitos y ficha paranormal")
@ApiResponses({
        @ApiResponse(responseCode = "404", description = "No existe la casa o el elemento indicado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
public class ContenidoController {

    private final ContenidoService contenidoService;

    @GetMapping("/imagenes")
    @Operation(summary = "Lista las imágenes de una casa")
    public List<ImagenResponse> listarImagenes(@PathVariable Long idProducto) {
        return contenidoService.listarImagenes(idProducto);
    }

    @PostMapping("/imagenes")
    @Operation(summary = "Agrega una imagen", description = "Solo ADMIN.")
    @ApiResponse(responseCode = "201", description = "Imagen agregada")
    public ResponseEntity<ImagenResponse> agregarImagen(@PathVariable Long idProducto, @Valid @RequestBody ImagenRequest request) {
        ImagenResponse imagen = contenidoService.agregarImagen(idProducto, request);
        return ResponseEntity.created(ubicacion(imagen.getIdImagen())).body(imagen);
    }

    @PutMapping("/imagenes/{idImagen}")
    @Operation(summary = "Modifica una imagen", description = "Solo ADMIN.")
    public ImagenResponse actualizarImagen(@PathVariable Long idProducto, @PathVariable Long idImagen,
                                           @Valid @RequestBody ImagenRequest request) {
        return contenidoService.actualizarImagen(idProducto, idImagen, request);
    }

    @DeleteMapping("/imagenes/{idImagen}")
    @Operation(summary = "Elimina una imagen", description = "Solo ADMIN. Una casa debe conservar al menos una imagen.")
    @ApiResponse(responseCode = "204", description = "Imagen eliminada")
    @ApiResponse(responseCode = "409", description = "Es la última imagen de la casa",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> eliminarImagen(@PathVariable Long idProducto, @PathVariable Long idImagen) {
        contenidoService.eliminarImagen(idProducto, idImagen);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mitos")
    @Operation(summary = "Lista los mitos de una casa")
    public List<MitoResponse> listarMitos(@PathVariable Long idProducto) {
        return contenidoService.listarMitos(idProducto);
    }

    @PostMapping("/mitos")
    @Operation(summary = "Agrega un mito", description = "Solo ADMIN.")
    @ApiResponse(responseCode = "201", description = "Mito agregado")
    public ResponseEntity<MitoResponse> agregarMito(@PathVariable Long idProducto, @Valid @RequestBody MitoRequest request) {
        MitoResponse mito = contenidoService.agregarMito(idProducto, request);
        return ResponseEntity.created(ubicacion(mito.getIdMito())).body(mito);
    }

    @PutMapping("/mitos/{idMito}")
    @Operation(summary = "Modifica un mito", description = "Solo ADMIN.")
    public MitoResponse actualizarMito(@PathVariable Long idProducto, @PathVariable Long idMito,
                                       @Valid @RequestBody MitoRequest request) {
        return contenidoService.actualizarMito(idProducto, idMito, request);
    }

    @DeleteMapping("/mitos/{idMito}")
    @Operation(summary = "Elimina un mito", description = "Solo ADMIN.")
    @ApiResponse(responseCode = "204", description = "Mito eliminado")
    public ResponseEntity<Void> eliminarMito(@PathVariable Long idProducto, @PathVariable Long idMito) {
        contenidoService.eliminarMito(idProducto, idMito);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ficha-paranormal")
    @Operation(summary = "Obtiene la ficha paranormal de una casa")
    public FichaParanormalResponse obtenerFicha(@PathVariable Long idProducto) {
        return contenidoService.obtenerFicha(idProducto);
    }

    @PutMapping("/ficha-paranormal")
    @Operation(summary = "Crea o reemplaza la ficha paranormal", description = "Solo ADMIN. Cada casa tiene a lo más una ficha.")
    public FichaParanormalResponse guardarFicha(@PathVariable Long idProducto, @Valid @RequestBody FichaParanormalRequest request) {
        return contenidoService.guardarFicha(idProducto, request);
    }

    @DeleteMapping("/ficha-paranormal")
    @Operation(summary = "Elimina la ficha paranormal", description = "Solo ADMIN.")
    @ApiResponse(responseCode = "204", description = "Ficha eliminada")
    public ResponseEntity<Void> eliminarFicha(@PathVariable Long idProducto) {
        contenidoService.eliminarFicha(idProducto);
        return ResponseEntity.noContent().build();
    }

    private URI ubicacion(Long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    }
}