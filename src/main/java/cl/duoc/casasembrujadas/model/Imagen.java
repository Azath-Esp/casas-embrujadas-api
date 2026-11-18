package cl.duoc.casasembrujadas.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**Imagen de una casa, con los datos de atribución de su licencia.*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Imagen {

    private Long idImagen;
    private String url;
    private Integer orden;
    private String autor;
    private String licencia;
    private String urlFuente;
    private Long idProducto;
}