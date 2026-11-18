package cl.duoc.casasembrujadas.repository;

import java.util.List;
import java.util.Optional;

import cl.duoc.casasembrujadas.model.Categoria;

public interface CategoriaRepository {

    List<Categoria> listar();

    Optional<Categoria> buscarPorId(Integer idCategoria);

    boolean existePorNombre(String nombre, Integer idExcluido);

    Integer insertar(Categoria categoria);

    void actualizar(Categoria categoria);

    boolean eliminar(Integer idCategoria);

    long contarProductos(Integer idCategoria);

    List<Categoria> listarPorProducto(Long idProducto);

    long contarPorIds(List<Integer> idsCategorias);
}
