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
public class Usuario {

    private Long idUsuario;
    private String run;
    private String nombre;
    private String apellidos;
    private String correo;
    private String contrasena;
    private String direccion;
    private Integer idComuna;
    private String comunaNombre;
    private String regionNombre;
    private RolNombre rol;
    private boolean activo;
    private LocalDateTime fechaRegistro;
}