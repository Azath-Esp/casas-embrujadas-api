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
public class AuthResponse {

    private String token;
    private String tipo;
    private LocalDateTime expiraEn;
    private String nombre;
    private RolNombre rol;
}