package cl.duoc.casasembrujadas.repository;

import java.util.List;

import cl.duoc.casasembrujadas.model.Producto;

public interface FavoritoRepository {

    List<Producto> listarProductos(Long idUsuario);

    boolean existe(Long idUsuario, Long idProducto);

    void agregar(Long idUsuario, Long idProducto);

    boolean quitar(Long idUsuario, Long idProducto);
}
