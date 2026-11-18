package cl.duoc.casasembrujadas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MitoResponse {

    private Long idMito;
    private String titulo;
    private String relato;
    private Integer anioOrigen;
    private String fuente;
}