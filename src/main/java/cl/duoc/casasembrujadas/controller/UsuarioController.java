package cl.duoc.casasembrujadas.controller;

import java.net.URI;
import java.util.List;

import cl.duoc.casasembrujadas.dto.request.PerfilRequest;
import cl.duoc.casasembrujadas.dto.request.UsuarioActualizarRequest;
import cl.duoc.casasembrujadas.dto.request.UsuarioCrearRequest;
import cl.duoc.casasembrujadas.dto.response.ErrorResponse;
import cl.duoc.casasembrujadas.dto.response.UsuarioResponse;
import cl.duoc.casasembrujadas.security.UsuarioActual;
import cl.duoc.casasembrujadas.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Usuarios", description = "Administración de usuarios y perfil propio")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioActual usuarioActual;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @Operation(summary = "Obtiene mi perfil", description = "ADMIN y CLIENTE.")
    public UsuarioResponse miPerfil() {
        return usuarioService.obtener(usuarioActual.obtener().idUsuario());
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @Operation(summary = "Modifica mi perfil", description = "ADMIN y CLIENTE. No permite cambiar correo, RUN, rol ni contraseña.")
    public UsuarioResponse actualizarMiPerfil(@Valid @RequestBody PerfilRequest request) {
        return usuarioService.actualizarPerfil(usuarioActual.obtener().idUsuario(), request);
    }

    @GetMapping
    @Operation(summary = "Lista los usuarios", description = "Solo ADMIN.")
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un usuario", description = "Solo ADMIN.")
    @ApiResponse(responseCode = "404", description = "No existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public UsuarioResponse obtener(@PathVariable Long id) {
        return usuarioService.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crea un usuario de cualquier rol", description = "Solo ADMIN.")
    @ApiResponse(responseCode = "201", description = "Usuario creado")
    @ApiResponse(responseCode = "409", description = "El correo o el RUN ya están registrados",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioCrearRequest request) {
        UsuarioResponse creado = usuarioService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getIdUsuario())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifica un usuario, su rol y si está activo", description = "Solo ADMIN. Un administrador no puede quitarse su propio acceso.")
    @ApiResponse(responseCode = "404", description = "No existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Intento de quitarse el propio acceso",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioActualizarRequest request) {
        return usuarioService.actualizar(id, request, usuarioActual.obtener());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un usuario", description = "Solo ADMIN. Si tiene boletas, se desactiva en vez de borrarse.")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado o desactivado")
    @ApiResponse(responseCode = "409", description = "Intento de eliminar la propia cuenta",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id, usuarioActual.obtener());
        return ResponseEntity.noContent().build();
    }
}
