package cl.duoc.casasembrujadas.repository;

import java.util.List;
import java.util.Optional;

import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.Producto;

public interface ProductoRepository {

    List<Producto> buscar(ProductoFiltro filtro, int pagina, int tamano);

    long contar(ProductoFiltro filtro);

    Optional<Producto> buscarPorId(Long idProducto);

    Optional<Producto> buscarPorIdConBloqueo(Long idProducto);

    List<Producto> buscarPorIds(List<Long> idsProductos);

    List<Producto> listarParaMapa(Integer idRegion);

    Long insertar(Producto producto);

    void actualizar(Producto producto);

    void actualizarEstado(Long idProducto, EstadoProducto estado);

    boolean eliminar(Long idProducto);

    boolean tieneBoletas(Long idProducto);

    void reemplazarCategorias(Long idProducto, List<Integer> idsCategorias);
}
