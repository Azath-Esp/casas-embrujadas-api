package cl.duoc.casasembrujadas.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResumenResponse {

    private List<VentaMensualResponse> ventasPorMes;
    private BigDecimal ingresosTotales;
    private long ventasTotales;
    private List<EtiquetaCantidadResponse> casasPorEstado;
    private List<EtiquetaCantidadResponse> categoriasMasVendidas;
    private long reservasActivas;
    private List<EtiquetaCantidadResponse> topFavoritos;
}
