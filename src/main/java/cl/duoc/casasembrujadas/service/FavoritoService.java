package cl.duoc.casasembrujadas.service;

import java.util.List;

import cl.duoc.casasembrujadas.dto.response.ProductoResponse;

public interface FavoritoService {

    List<ProductoResponse> listar(Long idUsuario);

    void agregar(Long idUsuario, Long idProducto);

    void quitar(Long idUsuario, Long idProducto);
}
