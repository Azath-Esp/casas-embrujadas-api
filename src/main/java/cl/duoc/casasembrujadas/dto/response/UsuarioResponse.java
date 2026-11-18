package cl.duoc.casasembrujadas.dto.response;

import cl.duoc.casasembrujadas.model.RolNombre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Long idUsuario;
    private String run;
    private String nombre;
    private String apellidos;
    private String correo;
    private String direccion;
    private Integer idComuna;
    private String comuna;
    private String region;
    private RolNombre rol;
    private boolean activo;
    private LocalDateTime fechaRegistro;
}