package cl.duoc.casasembrujadas.service.impl;

import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.casa;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import cl.duoc.casasembrujadas.dto.request.ProductoRequest;
import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.mapper.CategoriaMapper;
import cl.duoc.casasembrujadas.mapper.ContenidoMapper;
import cl.duoc.casasembrujadas.mapper.ProductoMapper;
import cl.duoc.casasembrujadas.model.Comuna;
import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.repository.CategoriaRepository;
import cl.duoc.casasembrujadas.repository.ContenidoRepository;
import cl.duoc.casasembrujadas.repository.ProductoFiltro;
import cl.duoc.casasembrujadas.repository.ProductoRepository;
import cl.duoc.casasembrujadas.repository.UbicacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private ContenidoRepository contenidoRepository;
    @Mock
    private UbicacionRepository ubicacionRepository;

    private ProductoServiceImpl servicio;

    @BeforeEach
    void preparar() {
        servicio = new ProductoServiceImpl(productoRepository, categoriaRepository, contenidoRepository, ubicacionRepository,
                new ProductoMapper(new CategoriaMapper(), new ContenidoMapper()), new TransicionEstadoProducto(productoRepository));
    }

    private ProductoRequest solicitud(EstadoProducto estado) {
        return ProductoRequest.builder()
                .titulo("Casona").descripcion("Descripción").precio(new BigDecimal("100")).direccion("Calle 1")
                .metrosCuadrados(100).dormitorios(1).banos(1).nivelEmbrujo(3)
                .latitud(new BigDecimal("-33.4")).longitud(new BigDecimal("-70.6")).idComuna(10)
                .idsCategorias(List.of(1, 5)).estado(estado).build();
    }

    private void comunaYCategoriasExisten() {
        when(ubicacionRepository.buscarComunaPorId(10)).thenReturn(Optional.of(Comuna.builder().idComuna(10).build()));
        when(categoriaRepository.contarPorIds(List.of(1, 5))).thenReturn(2L);
    }

    @Test
    void elTamanoDePaginaTieneUnTopeDeCincuenta() {
        ProductoFiltro filtro = ProductoFiltro.builder().build();
        when(productoRepository.buscar(eq(filtro), anyInt(), anyInt())).thenReturn(List.of());
        when(productoRepository.contar(filtro)).thenReturn(120L);

        var pagina = servicio.buscar(filtro, -3, 1000);

        verify(productoRepository).buscar(filtro, 0, 50);
        assertEquals(3, pagina.getTotalPaginas());
    }

    @Test
    void unaCasaNuevaNoPuedeNacerVendida() {
        comunaYCategoriasExisten();

        assertThrows(ReglaNegocioException.class, () -> servicio.crear(solicitud(EstadoProducto.VENDIDA)));
        verify(productoRepository, never()).insertar(any());
    }

    @Test
    void rechazaCategoriasInexistentes() {
        when(ubicacionRepository.buscarComunaPorId(10)).thenReturn(Optional.of(Comuna.builder().idComuna(10).build()));
        when(categoriaRepository.contarPorIds(List.of(1, 5))).thenReturn(1L);

        assertThrows(RecursoNoEncontradoException.class, () -> servicio.crear(solicitud(null)));
    }

    @Test
    void elAdministradorNoPuedeMarcarUnaCasaComoReservada() {
        when(productoRepository.buscarPorIdConBloqueo(1L)).thenReturn(Optional.of(casa(1, EstadoProducto.DISPONIBLE, "100")));
        comunaYCategoriasExisten();

        assertThrows(ReglaNegocioException.class, () -> servicio.actualizar(1L, solicitud(EstadoProducto.RESERVADA)));
        verify(productoRepository, never()).actualizar(any());
    }

    @Test
    void noSeReactivaUnaCasaConBoletaPagada() {
        when(productoRepository.buscarPorIdConBloqueo(7L)).thenReturn(Optional.of(casa(7, EstadoProducto.VENDIDA, "100")));
        comunaYCategoriasExisten();
        when(productoRepository.tieneBoletaPagada(7L)).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> servicio.actualizar(7L, solicitud(EstadoProducto.DISPONIBLE)));
    }

    @Test
    void unaCasaConBoletasSeDesactivaEnVezDeBorrarse() {
        when(productoRepository.buscarPorIdConBloqueo(7L)).thenReturn(Optional.of(casa(7, EstadoProducto.VENDIDA, "100")));
        when(productoRepository.tieneBoletas(7L)).thenReturn(true);

        servicio.eliminar(7L);

        verify(productoRepository).actualizarEstado(7L, EstadoProducto.INACTIVA);
        verify(productoRepository, never()).eliminar(anyLong());
    }

    @Test
    void unaCasaSinHistoriaSeBorraYUnaReservadaNo() {
        when(productoRepository.buscarPorIdConBloqueo(2L)).thenReturn(Optional.of(casa(2, EstadoProducto.DISPONIBLE, "100")));
        when(productoRepository.tieneBoletas(2L)).thenReturn(false);
        servicio.eliminar(2L);
        verify(productoRepository).eliminar(2L);

        when(productoRepository.buscarPorIdConBloqueo(8L)).thenReturn(Optional.of(casa(8, EstadoProducto.RESERVADA, "100")));
        assertThrows(ReglaNegocioException.class, () -> servicio.eliminar(8L));
    }

    @Test
    void laMaquinaDeEstadosRechazaTransicionesSinFlecha() {
        TransicionEstadoProducto transicion = new TransicionEstadoProducto(productoRepository);

        assertThrows(ReglaNegocioException.class,
                () -> transicion.cambiar(casa(12, EstadoProducto.INACTIVA, "100"), EstadoProducto.RESERVADA));
        assertThrows(ReglaNegocioException.class,
                () -> transicion.cambiar(casa(8, EstadoProducto.RESERVADA, "100"), EstadoProducto.INACTIVA));
        verify(productoRepository, never()).actualizarEstado(anyLong(), any());
    }
}
