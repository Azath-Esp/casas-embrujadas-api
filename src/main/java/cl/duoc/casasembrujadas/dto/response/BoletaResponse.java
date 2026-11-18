package cl.duoc.casasembrujadas.dto.response;

import cl.duoc.casasembrujadas.model.EstadoBoleta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoletaResponse {

    private Long idBoleta;
    private Integer numero;
    private LocalDateTime fecha;
    private BigDecimal neto;
    private BigDecimal iva;
    private BigDecimal total;
    private EstadoBoleta estado;
    private Long idUsuario;
    private String clienteNombre;
    private String clienteRun;
    private String clienteCorreo;
    private List<DetalleBoletaResponse> detalles;
}