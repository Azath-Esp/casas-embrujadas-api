package cl.duoc.casasembrujadas.controller;

import java.net.URI;
import java.util.List;

import cl.duoc.casasembrujadas.dto.request.ReservaRequest;
import cl.duoc.casasembrujadas.dto.response.ErrorResponse;
import cl.duoc.casasembrujadas.dto.response.ReservaResponse;
import cl.duoc.casasembrujadas.security.UsuarioActual;
import cl.duoc.casasembrujadas.service.ReservaService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Reserva de una casa por 24 horas")
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioActual usuarioActual;

    @PostMapping
    @Operation(summary = "Reserva una casa por 24 horas", description = "Solo CLIENTE. Máximo dos reservas activas por cliente.")
    @ApiResponse(responseCode = "201", description = "Reserva creada")
    @ApiResponse(responseCode = "404", description = "No existe la casa",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "La casa no está disponible o ya tienes dos reservas activas",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ReservaResponse> reservar(@Valid @RequestBody ReservaRequest request) {
        ReservaResponse reserva = reservaService.reservar(usuarioActual.obtener().idUsuario(), request.getIdProducto());
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reserva.getIdReserva())
                .toUri();
        return ResponseEntity.created(ubicacion).body(reserva);
    }

    @GetMapping("/mis-reservas")
    @Operation(summary = "Lista mis reservas", description = "Solo CLIENTE. Incluye las terminadas.")
    public List<ReservaResponse> listarMias() {
        return reservaService.listarMias(usuarioActual.obtener().idUsuario());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancela una reserva", description = "Solo el cliente dueño. La casa vuelve a estar disponible.")
    @ApiResponse(responseCode = "204", description = "Reserva cancelada")
    @ApiResponse(responseCode = "403", description = "La reserva es de otro cliente",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "No existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "La reserva ya no está activa",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        reservaService.cancelar(id, usuarioActual.obtener());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Lista todas las reservas", description = "Solo ADMIN.")
    public List<ReservaResponse> listarTodas() {
        return reservaService.listarTodas();
    }
}