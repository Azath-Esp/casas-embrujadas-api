package cl.duoc.casasembrujadas.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Casa guardada como favorita por un usuario.
 *
 * @author Dinko Ocampo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Favorito {

    private Long idUsuario;
    private Long idProducto;
    private LocalDateTime fecha;
}