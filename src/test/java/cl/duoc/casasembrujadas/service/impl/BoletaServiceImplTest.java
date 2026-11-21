package cl.duoc.casasembrujadas.service.impl;

import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.ANTONIA;
import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.CAMILA;
import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.RELOJ;
import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.VENDEDOR;
import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.casa;
import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.reservaActiva;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import cl.duoc.casasembrujadas.dto.request.BoletaRequest;
import cl.duoc.casasembrujadas.exception.AccesoDenegadoException;
import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.mapper.BoletaMapper;
import cl.duoc.casasembrujadas.model.Boleta;
import cl.duoc.casasembrujadas.model.DetalleBoleta;
import cl.duoc.casasembrujadas.model.EstadoBoleta;
import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.EstadoReserva;
import cl.duoc.casasembrujadas.model.Producto;
import cl.duoc.casasembrujadas.repository.BoletaRepository;
import cl.duoc.casasembrujadas.repository.ProductoRepository;
import cl.duoc.casasembrujadas.repository.ReservaRepository;
import cl.duoc.casasembrujadas.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BoletaServiceImplTest {

    @Mock
    private BoletaRepository boletaRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private ReservaService reservaService;

    private BoletaServiceImpl servicio;

    @BeforeEach
    void preparar() {
        servicio = new BoletaServiceImpl(boletaRepository, productoRepository, reservaRepository, reservaService,
                new BoletaMapper(), new TransicionEstadoProducto(productoRepository), RELOJ);
    }

    private BoletaRequest carrito(Long... ids) {
        return BoletaRequest.builder().idsProductos(List.of(ids)).build();
    }

    private void existe(Producto producto) {
        when(productoRepository.buscarPorIdConBloqueo(producto.getIdProducto())).thenReturn(Optional.of(producto));
    }

    private void boletaGuardada() {
        when(boletaRepository.siguienteNumero()).thenReturn(5);
        when(boletaRepository.insertar(any())).thenReturn(50L);
        when(boletaRepository.buscarPorId(50L)).thenReturn(Optional.of(Boleta.builder().idBoleta(50L).numero(5)
                .estado(EstadoBoleta.PAGADA).idUsuario(5L).detalles(List.of()).build()));
    }

    @Test
    void calculaNetoIvaYTotalConLosPreciosDeLaBase() {
        existe(casa(1, EstadoProducto.DISPONIBLE, "1250000000"));
        existe(casa(2, EstadoProducto.DISPONIBLE, "24500000000"));
        boletaGuardada();

        servicio.comprar(5L, carrito(2L, 1L));

        ArgumentCaptor<Boleta> boleta = ArgumentCaptor.forClass(Boleta.class);
        verify(boletaRepository).insertar(boleta.capture());
        assertEquals(new BigDecimal("25750000000"), boleta.getValue().getNeto());
        assertEquals(new BigDecimal("4892500000"), boleta.getValue().getIva());
        assertEquals(new BigDecimal("30642500000"), boleta.getValue().getTotal());
        assertEquals(5, boleta.getValue().getNumero());
        assertEquals(EstadoBoleta.PAGADA, boleta.getValue().getEstado());
        verify(reservaService).expirarVencidas();
        verify(productoRepository).actualizarEstado(1L, EstadoProducto.VENDIDA);
        verify(productoRepository).actualizarEstado(2L, EstadoProducto.VENDIDA);
    }

    @Test
    void cadaDetalleTieneCantidadUnoYElPrecioDelMomento() {
        existe(casa(1, EstadoProducto.DISPONIBLE, "1250000000"));
        boletaGuardada();

        servicio.comprar(5L, carrito(1L));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<DetalleBoleta>> detalles = ArgumentCaptor.forClass(List.class);
        verify(boletaRepository).insertarDetalles(any(), detalles.capture());
        assertEquals(1, detalles.getValue().size());
        assertEquals(1, detalles.getValue().get(0).getCantidad());
        assertEquals(new BigDecimal("1250000000"), detalles.getValue().get(0).getSubtotal());
    }

    @Test
    void elIvaSeRedondeaAlPeso() {
        existe(casa(1, EstadoProducto.DISPONIBLE, "999"));
        boletaGuardada();

        servicio.comprar(5L, carrito(1L));

        ArgumentCaptor<Boleta> boleta = ArgumentCaptor.forClass(Boleta.class);
        verify(boletaRepository).insertar(boleta.capture());
        assertEquals(new BigDecimal("190"), boleta.getValue().getIva());
        assertEquals(new BigDecimal("1189"), boleta.getValue().getTotal());
    }

    @Test
    void noAceptaUnaCasaRepetidaYNoGuardaNada() {
        assertThrows(ReglaNegocioException.class, () -> servicio.comprar(5L, carrito(1L, 1L)));

        verify(boletaRepository, never()).insertar(any());
    }

    @Test
    void noVendeUnaCasaVendidaNiInactivaYNoGuardaNada() {
        existe(casa(1, EstadoProducto.DISPONIBLE, "100"));
        existe(casa(7, EstadoProducto.VENDIDA, "100"));

        assertThrows(ReglaNegocioException.class, () -> servicio.comprar(5L, carrito(1L, 7L)));

        existe(casa(12, EstadoProducto.INACTIVA, "100"));
        assertThrows(ReglaNegocioException.class, () -> servicio.comprar(5L, carrito(12L)));
        verify(boletaRepository, never()).insertar(any());
        verify(productoRepository, never()).actualizarEstado(anyLong(), any());
    }

    @Test
    void unaCasaInexistenteResponde404() {
        when(productoRepository.buscarPorIdConBloqueo(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> servicio.comprar(5L, carrito(99L)));
    }

    @Test
    void unaCasaReservadaPorOtroClienteNoSePuedeComprar() {
        existe(casa(8, EstadoProducto.RESERVADA, "100"));
        when(reservaRepository.buscarActivaPorProducto(8L)).thenReturn(Optional.of(reservaActiva(4, 8, 3)));

        assertThrows(ReglaNegocioException.class, () -> servicio.comprar(5L, carrito(8L)));
        verify(boletaRepository, never()).insertar(any());
    }

    @Test
    void quienReservoCompraYSuReservaQuedaConvertida() {
        existe(casa(8, EstadoProducto.RESERVADA, "100"));
        when(reservaRepository.buscarActivaPorProducto(8L)).thenReturn(Optional.of(reservaActiva(4, 8, 5)));
        boletaGuardada();

        servicio.comprar(5L, carrito(8L));

        verify(reservaRepository).actualizarEstado(4L, EstadoReserva.CONVERTIDA);
        verify(productoRepository).actualizarEstado(8L, EstadoProducto.VENDIDA);
    }

    @Test
    void unClienteNoVeLaBoletaDeOtro() {
        when(boletaRepository.buscarPorId(1L)).thenReturn(Optional.of(Boleta.builder().idBoleta(1L).idUsuario(3L)
                .estado(EstadoBoleta.PAGADA).detalles(List.of()).build()));

        assertThrows(AccesoDenegadoException.class, () -> servicio.obtener(1L, ANTONIA));
        assertEquals(1L, servicio.obtener(1L, CAMILA).getIdBoleta());
        assertEquals(1L, servicio.obtener(1L, VENDEDOR).getIdBoleta());
    }

    @Test
    void anularDevuelveLasCasasYNoSePuedeRevertir() {
        Boleta pagada = Boleta.builder().idBoleta(1L).estado(EstadoBoleta.PAGADA).idUsuario(3L)
                .detalles(List.of(DetalleBoleta.builder().idProducto(10L).build())).build();
        when(boletaRepository.buscarPorId(1L)).thenReturn(Optional.of(pagada));
        existe(casa(10, EstadoProducto.VENDIDA, "100"));

        servicio.cambiarEstado(1L, EstadoBoleta.ANULADA);

        verify(boletaRepository).actualizarEstado(1L, EstadoBoleta.ANULADA);
        verify(productoRepository).actualizarEstado(10L, EstadoProducto.DISPONIBLE);

        pagada.setEstado(EstadoBoleta.ANULADA);
        assertThrows(ReglaNegocioException.class, () -> servicio.cambiarEstado(1L, EstadoBoleta.PAGADA));
        verify(boletaRepository, never()).insertarDetalles(anyLong(), anyList());
    }
}
