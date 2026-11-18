package cl.duoc.casasembrujadas.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaMensualResponse {

    private String mes;
    private long ventas;
    private BigDecimal ingresos;
}
