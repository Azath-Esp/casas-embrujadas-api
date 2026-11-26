package cl.duoc.casasembrujadas.service.impl;

import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.casa;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.mapper.CategoriaMapper;
import cl.duoc.casasembrujadas.mapper.ContenidoMapper;
import cl.duoc.casasembrujadas.mapper.ProductoMapper;
import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.Imagen;
import cl.duoc.casasembrujadas.repository.ContenidoRepository;
import cl.duoc.casasembrujadas.repository.FavoritoRepository;
import cl.duoc.casasembrujadas.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContenidoYFavoritoServiceTest {

    @Mock
    private ContenidoRepository contenidoRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private FavoritoRepository favoritoRepository;

    private ContenidoServiceImpl contenido;
    private FavoritoServiceImpl favoritos;

    @BeforeEach
    void preparar() {
        contenido = new ContenidoServiceImpl(contenidoRepository, productoRepository, new ContenidoMapper());
        favoritos = new FavoritoServiceImpl(favoritoRepository, productoRepository,
                new ProductoMapper(new CategoriaMapper(), new ContenidoMapper()));
    }

    @Test
    void unaCasaNoSeQuedaSinImagenes() {
        Imagen unica = Imagen.builder().idImagen(9L).idProducto(5L).build();
        when(contenidoRepository.buscarImagen(9L)).thenReturn(Optional.of(unica));
        when(contenidoRepository.listarImagenes(5L)).thenReturn(List.of(unica));

        assertThrows(ReglaNegocioException.class, () -> contenido.eliminarImagen(5L, 9L));
        verify(contenidoRepository, never()).eliminarImagen(anyLong());
    }

    @Test
    void unaImagenDeOtraCasaNoSeEncuentra() {
        when(contenidoRepository.buscarImagen(1L)).thenReturn(Optional.of(Imagen.builder().idImagen(1L).idProducto(1L).build()));

        assertThrows(RecursoNoEncontradoException.class, () -> contenido.eliminarImagen(9L, 1L));
    }

    @Test
    void agregaUnFavoritoSoloSiLaCasaEstaVisible() {
        when(productoRepository.buscarPorId(2L)).thenReturn(Optional.of(casa(2, EstadoProducto.DISPONIBLE, "100")));
        favoritos.agregar(3L, 2L);
        verify(favoritoRepository).agregar(3L, 2L);

        when(productoRepository.buscarPorId(12L)).thenReturn(Optional.of(casa(12, EstadoProducto.INACTIVA, "100")));
        assertThrows(RecursoNoEncontradoException.class, () -> favoritos.agregar(3L, 12L));
        verify(favoritoRepository, never()).agregar(3L, 12L);
    }

    @Test
    void quitarUnFavoritoQueNoEstaResponde404() {
        when(favoritoRepository.quitar(3L, 9L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> favoritos.quitar(3L, 9L));
    }
}
