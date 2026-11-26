package cl.duoc.casasembrujadas.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import cl.duoc.casasembrujadas.dto.request.CategoriaRequest;
import cl.duoc.casasembrujadas.exception.RecursoDuplicadoException;
import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.mapper.CategoriaMapper;
import cl.duoc.casasembrujadas.model.Categoria;
import cl.duoc.casasembrujadas.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    private CategoriaServiceImpl servicio;

    @BeforeEach
    void preparar() {
        servicio = new CategoriaServiceImpl(categoriaRepository, new CategoriaMapper());
    }

    @Test
    void creaUnaCategoriaConNombreNuevo() {
        when(categoriaRepository.existePorNombre("Faros", null)).thenReturn(false);
        when(categoriaRepository.insertar(any())).thenReturn(9);

        assertEquals(9, servicio.crear(CategoriaRequest.builder().nombre(" Faros ").build()).getIdCategoria());
    }

    @Test
    void rechazaUnNombreRepetido() {
        when(categoriaRepository.existePorNombre("Apariciones", null)).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class,
                () -> servicio.crear(CategoriaRequest.builder().nombre("Apariciones").build()));
        verify(categoriaRepository, never()).insertar(any());
    }

    @Test
    void noEliminaUnaCategoriaConCasas() {
        when(categoriaRepository.buscarPorId(1)).thenReturn(Optional.of(Categoria.builder().idCategoria(1).nombre("Palacios").build()));
        when(categoriaRepository.contarProductos(1)).thenReturn(5L);

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class, () -> servicio.eliminar(1));

        assertEquals("No se puede eliminar la categoría \"Palacios\" porque tiene 5 casas asociadas", error.getMessage());
        verify(categoriaRepository, never()).eliminar(any());
    }

    @Test
    void eliminaUnaCategoriaSinCasasYAvisaSiNoExiste() {
        when(categoriaRepository.buscarPorId(8)).thenReturn(Optional.of(Categoria.builder().idCategoria(8).nombre("Faros").build()));
        when(categoriaRepository.contarProductos(8)).thenReturn(0L);
        servicio.eliminar(8);
        verify(categoriaRepository).eliminar(8);

        when(categoriaRepository.buscarPorId(99)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.obtener(99));
    }
}
