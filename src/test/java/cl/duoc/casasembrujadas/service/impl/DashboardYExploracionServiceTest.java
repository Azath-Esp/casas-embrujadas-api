package cl.duoc.casasembrujadas.service.impl;

import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.RELOJ;
import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.casa;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import cl.duoc.casasembrujadas.dto.response.ComparacionResponse;
import cl.duoc.casasembrujadas.dto.response.DashboardResumenResponse;
import cl.duoc.casasembrujadas.dto.response.EtiquetaCantidadResponse;
import cl.duoc.casasembrujadas.dto.response.VentaMensualResponse;
import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.mapper.CategoriaMapper;
import cl.duoc.casasembrujadas.mapper.ContenidoMapper;
import cl.duoc.casasembrujadas.mapper.ProductoMapper;
import cl.duoc.casasembrujadas.mapper.UbicacionMapper;
import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.Producto;
import cl.duoc.casasembrujadas.repository.ContenidoRepository;
import cl.duoc.casasembrujadas.repository.DashboardRepository;
import cl.duoc.casasembrujadas.repository.ProductoRepository;
import cl.duoc.casasembrujadas.repository.UbicacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardYExploracionServiceTest {

    @Mock
    private DashboardRepository dashboardRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private ContenidoRepository contenidoRepository;
    @Mock
    private UbicacionRepository ubicacionRepository;

    @Test
    void elDashboardCompletaDoceMesesYLosCuatroEstados() {
        when(dashboardRepository.ventasPorMes(LocalDateTime.of(2025, 12, 1, 0, 0)))
                .thenReturn(List.of(new VentaMensualResponse("2026-07", 2, new BigDecimal("500"))));
        when(dashboardRepository.productosPorEstado()).thenReturn(List.of(new EtiquetaCantidadResponse("VENDIDA", 4)));
        when(dashboardRepository.ingresosTotales()).thenReturn(new BigDecimal("500"));

        DashboardResumenResponse resumen = new DashboardServiceImpl(dashboardRepository, RELOJ).resumen();

        assertEquals(12, resumen.getVentasPorMes().size());
        assertEquals("2025-12", resumen.getVentasPorMes().get(0).getMes());
        assertEquals("2026-11", resumen.getVentasPorMes().get(11).getMes());
        assertEquals(2, resumen.getVentasPorMes().get(7).getVentas());
        assertEquals(0, resumen.getVentasPorMes().get(8).getVentas());
        assertEquals(2, resumen.getVentasTotales());
        assertEquals(4, resumen.getCasasPorEstado().size());
        assertEquals(0, resumen.getCasasPorEstado().get(0).getCantidad());
        assertEquals(4, resumen.getCasasPorEstado().get(2).getCantidad());
    }

    private ExploracionServiceImpl exploracion() {
        return new ExploracionServiceImpl(productoRepository, contenidoRepository, ubicacionRepository,
                new ProductoMapper(new CategoriaMapper(), new ContenidoMapper()));
    }

    @Test
    void elComparadorMarcaElMenorPrecioYLosEmpates() {
        Producto barata = casa(1, EstadoProducto.DISPONIBLE, "100");
        barata.setDormitorios(5);
        barata.setMetrosCuadrados(80);
        barata.setBanos(1);
        barata.setNivelEmbrujo(5);
        Producto cara = casa(2, EstadoProducto.DISPONIBLE, "900");
        cara.setDormitorios(5);
        cara.setMetrosCuadrados(200);
        cara.setBanos(3);
        cara.setNivelEmbrujo(2);
        when(productoRepository.buscarPorIds(List.of(2L, 1L))).thenReturn(List.of(barata, cara));
        when(contenidoRepository.buscarFicha(1L)).thenReturn(Optional.empty());
        when(contenidoRepository.buscarFicha(2L)).thenReturn(Optional.empty());
        when(contenidoRepository.listarMitos(1L)).thenReturn(List.of());
        when(contenidoRepository.listarMitos(2L)).thenReturn(List.of());

        ComparacionResponse comparacion = exploracion().comparar(List.of(2L, 1L));

        assertEquals(2L, comparacion.getCasas().get(0).getIdProducto());
        assertEquals(List.of(1L), comparacion.getMejores().get("precio"));
        assertEquals(List.of(2L), comparacion.getMejores().get("metrosCuadrados"));
        assertEquals(List.of(2L, 1L), comparacion.getMejores().get("dormitorios"));
        assertEquals(List.of(2L, 1L), comparacion.getMejores().get("cantidadMitos"));
    }

    @Test
    void elComparadorNecesitaDosCasasDistintas() {
        assertThrows(ReglaNegocioException.class, () -> exploracion().comparar(List.of(1L, 1L)));
    }

    @Test
    void unaRegionInexistenteResponde404EnElMapaYEnLasComunas() {
        when(ubicacionRepository.existeRegion(99)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> exploracion().mapa(99));
        assertThrows(RecursoNoEncontradoException.class,
                () -> new UbicacionServiceImpl(ubicacionRepository, new UbicacionMapper()).listarComunas(99));
    }
}
