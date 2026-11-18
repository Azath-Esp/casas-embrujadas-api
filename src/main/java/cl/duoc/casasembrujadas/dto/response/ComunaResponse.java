package cl.duoc.casasembrujadas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComunaResponse {

    private Integer idComuna;
    private String nombre;
    private Integer idRegion;
    private String region;
}