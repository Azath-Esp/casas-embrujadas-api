package cl.duoc.casasembrujadas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FichaParanormalResponse {

    private String tipoEntidad;
    private String horarioActividad;
    private String fenomenosReportados;
    private LocalDate ultimoAvistamiento;
}