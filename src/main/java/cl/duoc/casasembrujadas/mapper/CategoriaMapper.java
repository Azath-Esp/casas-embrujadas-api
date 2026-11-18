package cl.duoc.casasembrujadas.mapper;

import cl.duoc.casasembrujadas.dto.request.CategoriaRequest;
import cl.duoc.casasembrujadas.dto.response.CategoriaResponse;
import cl.duoc.casasembrujadas.model.Categoria;
import org.springframework.stereotype.Component;


@Component
public class CategoriaMapper {

    public CategoriaResponse toResponse(Categoria categoria) {
        return CategoriaResponse.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .build();
    }

    public Categoria toModel(CategoriaRequest request) {
        return Categoria.builder()
                .nombre(request.getNombre().trim())
                .descripcion(request.getDescripcion())
                .build();
    }
}