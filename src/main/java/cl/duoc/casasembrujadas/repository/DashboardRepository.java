package cl.duoc.casasembrujadas.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import cl.duoc.casasembrujadas.dto.response.EtiquetaCantidadResponse;
import cl.duoc.casasembrujadas.dto.response.VentaMensualResponse;

public interface DashboardRepository {

    List<VentaMensualResponse> ventasPorMes(LocalDateTime desde);

    BigDecimal ingresosTotales();

    List<EtiquetaCantidadResponse> productosPorEstado();

    List<EtiquetaCantidadResponse> categoriasMasVendidas(int limite);

    long contarReservasActivas();

    List<EtiquetaCantidadResponse> topFavoritos(int limite);
}
