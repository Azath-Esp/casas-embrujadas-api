package cl.duoc.casasembrujadas.service.impl;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import cl.duoc.casasembrujadas.dto.response.DashboardResumenResponse;
import cl.duoc.casasembrujadas.dto.response.EtiquetaCantidadResponse;
import cl.duoc.casasembrujadas.dto.response.VentaMensualResponse;
import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.repository.DashboardRepository;
import cl.duoc.casasembrujadas.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final int MESES = 12;
    private static final int TOP = 5;

    private final DashboardRepository dashboardRepository;
    private final Clock clock;

    @Override
    @Transactional(readOnly = true)
    public DashboardResumenResponse resumen() {
        List<VentaMensualResponse> ventas = completarMeses(YearMonth.now(clock));
        return DashboardResumenResponse.builder()
                .ventasPorMes(ventas)
                .ingresosTotales(dashboardRepository.ingresosTotales())
                .ventasTotales(ventas.stream().mapToLong(VentaMensualResponse::getVentas).sum())
                .casasPorEstado(completarEstados())
                .categoriasMasVendidas(dashboardRepository.categoriasMasVendidas(TOP))
                .reservasActivas(dashboardRepository.contarReservasActivas())
                .topFavoritos(dashboardRepository.topFavoritos(TOP))
                .build();
    }

    private List<VentaMensualResponse> completarMeses(YearMonth mesActual) {
        YearMonth primero = mesActual.minusMonths(MESES - 1);
        Map<String, VentaMensualResponse> conVentas = dashboardRepository
                .ventasPorMes(primero.atDay(1).atStartOfDay())
                .stream()
                .collect(Collectors.toMap(VentaMensualResponse::getMes, Function.identity()));
        return IntStream.range(0, MESES)
                .mapToObj(primero::plusMonths)
                .map(YearMonth::toString)
                .map(mes -> conVentas.getOrDefault(mes, new VentaMensualResponse(mes, 0, BigDecimal.ZERO)))
                .toList();
    }

    private List<EtiquetaCantidadResponse> completarEstados() {
        Map<String, Long> porEstado = dashboardRepository.productosPorEstado().stream()
                .collect(Collectors.toMap(EtiquetaCantidadResponse::getEtiqueta, EtiquetaCantidadResponse::getCantidad));
        return Arrays.stream(EstadoProducto.values())
                .map(estado -> new EtiquetaCantidadResponse(estado.name(), porEstado.getOrDefault(estado.name(), 0L)))
                .toList();
    }
}
