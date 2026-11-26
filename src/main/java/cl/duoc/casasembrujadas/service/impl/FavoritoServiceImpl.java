package cl.duoc.casasembrujadas.service.impl;

import java.util.List;

import cl.duoc.casasembrujadas.dto.response.ProductoResponse;
import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.mapper.ProductoMapper;
import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.Producto;
import cl.duoc.casasembrujadas.repository.FavoritoRepository;
import cl.duoc.casasembrujadas.repository.ProductoRepository;
import cl.duoc.casasembrujadas.service.FavoritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoritoServiceImpl implements FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Override
    public List<ProductoResponse> listar(Long idUsuario) {
        return favoritoRepository.listarProductos(idUsuario).stream().map(productoMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void agregar(Long idUsuario, Long idProducto) {
        Producto producto = productoRepository.buscarPorId(idProducto)
                .filter(casa -> casa.getEstado() != EstadoProducto.INACTIVA)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la casa con id " + idProducto));
        favoritoRepository.agregar(idUsuario, producto.getIdProducto());
    }

    @Override
    @Transactional
    public void quitar(Long idUsuario, Long idProducto) {
        if (!favoritoRepository.quitar(idUsuario, idProducto)) {
            throw new RecursoNoEncontradoException("La casa con id " + idProducto + " no está en tus favoritos");
        }
    }
}
