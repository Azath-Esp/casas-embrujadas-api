package cl.duoc.casasembrujadas.service.impl;

import java.util.List;

import cl.duoc.casasembrujadas.dto.request.CategoriaRequest;
import cl.duoc.casasembrujadas.dto.response.CategoriaResponse;
import cl.duoc.casasembrujadas.exception.RecursoDuplicadoException;
import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.mapper.CategoriaMapper;
import cl.duoc.casasembrujadas.model.Categoria;
import cl.duoc.casasembrujadas.repository.CategoriaRepository;
import cl.duoc.casasembrujadas.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    public List<CategoriaResponse> listar() {
        return categoriaRepository.listar().stream().map(categoriaMapper::toResponse).toList();
    }

    @Override
    public CategoriaResponse obtener(Integer idCategoria) {
        return categoriaMapper.toResponse(buscar(idCategoria));
    }

    @Override
    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        Categoria categoria = categoriaMapper.toModel(request);
        if (categoriaRepository.existePorNombre(categoria.getNombre(), null)) {
            throw new RecursoDuplicadoException("Ya existe una categoría llamada \"" + categoria.getNombre() + "\"");
        }
        categoria.setIdCategoria(categoriaRepository.insertar(categoria));
        return categoriaMapper.toResponse(categoria);
    }

    @Override
    @Transactional
    public CategoriaResponse actualizar(Integer idCategoria, CategoriaRequest request) {
        buscar(idCategoria);
        Categoria categoria = categoriaMapper.toModel(request);
        categoria.setIdCategoria(idCategoria);
        if (categoriaRepository.existePorNombre(categoria.getNombre(), idCategoria)) {
            throw new RecursoDuplicadoException("Ya existe una categoría llamada \"" + categoria.getNombre() + "\"");
        }
        categoriaRepository.actualizar(categoria);
        return categoriaMapper.toResponse(categoria);
    }

    @Override
    @Transactional
    public void eliminar(Integer idCategoria) {
        Categoria categoria = buscar(idCategoria);
        long casas = categoriaRepository.contarProductos(idCategoria);
        if (casas > 0) {
            throw new ReglaNegocioException("No se puede eliminar la categoría \"" + categoria.getNombre()
                    + "\" porque tiene " + casas + " casas asociadas");
        }
        categoriaRepository.eliminar(idCategoria);
    }

    private Categoria buscar(Integer idCategoria) {
        return categoriaRepository.buscarPorId(idCategoria)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la categoría con id " + idCategoria));
    }
}
