package cl.duoc.casasembrujadas.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    private Long idReserva;
    private Long idProducto;
    private String productoTitulo;
    private Long idUsuario;
    private String usuarioNombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaExpiracion;
    private EstadoReserva estado;
}