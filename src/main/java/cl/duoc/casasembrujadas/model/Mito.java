package cl.duoc.casasembrujadas.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mito {

    private Long idMito;
    private String titulo;
    private String relato;
    private Integer anioOrigen;
    private String fuente;
    private Long idProducto;
}