package cl.duoc.casasembrujadas.service;

import java.util.List;

import cl.duoc.casasembrujadas.dto.request.CategoriaRequest;
import cl.duoc.casasembrujadas.dto.response.CategoriaResponse;

public interface CategoriaService {

    List<CategoriaResponse> listar();

    CategoriaResponse obtener(Integer idCategoria);

    CategoriaResponse crear(CategoriaRequest request);

    CategoriaResponse actualizar(Integer idCategoria, CategoriaRequest request);

    void eliminar(Integer idCategoria);
}
