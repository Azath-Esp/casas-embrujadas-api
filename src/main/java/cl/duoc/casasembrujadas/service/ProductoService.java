package cl.duoc.casasembrujadas.service;

import cl.duoc.casasembrujadas.dto.request.ProductoRequest;
import cl.duoc.casasembrujadas.dto.response.PaginaResponse;
import cl.duoc.casasembrujadas.dto.response.ProductoDetalleResponse;
import cl.duoc.casasembrujadas.dto.response.ProductoResponse;
import cl.duoc.casasembrujadas.repository.ProductoFiltro;

public interface ProductoService {

    PaginaResponse<ProductoResponse> buscar(ProductoFiltro filtro, int pagina, int tamano);

    PaginaResponse<ProductoDetalleResponse> buscarConDetalle(ProductoFiltro filtro, int pagina, int tamano);

    ProductoResponse obtener(Long idProducto);

    ProductoDetalleResponse obtenerDetalle(Long idProducto);

    ProductoDetalleResponse crear(ProductoRequest request);

    ProductoDetalleResponse actualizar(Long idProducto, ProductoRequest request);

    void eliminar(Long idProducto);
}
