package cl.duoc.casasembrujadas.service;

import java.util.List;

import cl.duoc.casasembrujadas.dto.response.ComparacionResponse;
import cl.duoc.casasembrujadas.dto.response.ProductoMapaResponse;

public interface ExploracionService {

    List<ProductoMapaResponse> mapa(Integer idRegion);

    ComparacionResponse comparar(List<Long> idsProductos);
}
