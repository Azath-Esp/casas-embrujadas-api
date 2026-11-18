package cl.duoc.casasembrujadas.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FichaParanormal {

    private Long idProducto;
    private String tipoEntidad;
    private String horarioActividad;
    private String fenomenosReportados;
    private LocalDate ultimoAvistamiento;
}