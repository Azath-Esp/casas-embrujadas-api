package cl.duoc.casasembrujadas.service.impl;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import cl.duoc.casasembrujadas.dto.response.CasaComparadaResponse;
import cl.duoc.casasembrujadas.dto.response.ComparacionResponse;
import cl.duoc.casasembrujadas.dto.response.ProductoMapaResponse;
import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.mapper.ProductoMapper;
import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.FichaParanormal;
import cl.duoc.casasembrujadas.model.Producto;
import cl.duoc.casasembrujadas.repository.ContenidoRepository;
import cl.duoc.casasembrujadas.repository.ProductoRepository;
import cl.duoc.casasembrujadas.repository.UbicacionRepository;
import cl.duoc.casasembrujadas.service.ExploracionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExploracionServiceImpl implements ExploracionService {

    private final ProductoRepository productoRepository;
    private final ContenidoRepository contenidoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final ProductoMapper productoMapper;

    @Override
    public List<ProductoMapaResponse> mapa(Integer idRegion) {
        if (idRegion != null && !ubicacionRepository.existeRegion(idRegion)) {
            throw new RecursoNoEncontradoException("No existe la región con id " + idRegion);
        }
        return productoRepository.listarParaMapa(idRegion).stream().map(productoMapper::toMapaResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ComparacionResponse comparar(List<Long> idsProductos) {
        List<Long> ids = idsProductos.stream().distinct().toList();
        if (ids.size() < 2) {
            throw new ReglaNegocioException("Elige al menos dos casas distintas para comparar");
        }
        Map<Long, Producto> encontrados = new LinkedHashMap<>();
        productoRepository.buscarPorIds(ids).stream()
                .filter(producto -> producto.getEstado() != EstadoProducto.INACTIVA)
                .forEach(producto -> encontrados.put(producto.getIdProducto(), producto));
        List<CasaComparadaResponse> casas = ids.stream()
                .map(id -> {
                    Producto producto = encontrados.get(id);
                    if (producto == null) {
                        throw new RecursoNoEncontradoException("No existe la casa con id " + id);
                    }
                    return aComparada(producto);
                })
                .toList();

        Map<String, List<Long>> mejores = new LinkedHashMap<>();
        mejores.put("precio", mejores(casas, casa -> casa.getPrecio().negate()));
        mejores.put("metrosCuadrados", mejores(casas, CasaComparadaResponse::getMetrosCuadrados));
        mejores.put("dormitorios", mejores(casas, CasaComparadaResponse::getDormitorios));
        mejores.put("banos", mejores(casas, CasaComparadaResponse::getBanos));
        mejores.put("nivelEmbrujo", mejores(casas, CasaComparadaResponse::getNivelEmbrujo));
        mejores.put("cantidadMitos", mejores(casas, CasaComparadaResponse::getCantidadMitos));
        return ComparacionResponse.builder().casas(casas).mejores(mejores).build();
    }

    private CasaComparadaResponse aComparada(Producto producto) {
        Long id = producto.getIdProducto();
        String tipoEntidad = contenidoRepository.buscarFicha(id).map(FichaParanormal::getTipoEntidad).orElse(null);
        return CasaComparadaResponse.builder()
                .idProducto(id)
                .titulo(producto.getTitulo())
                .imagenPrincipal(producto.getImagenPrincipal())
                .estado(producto.getEstado())
                .precio(producto.getPrecio())
                .metrosCuadrados(producto.getMetrosCuadrados())
                .dormitorios(producto.getDormitorios())
                .banos(producto.getBanos())
                .nivelEmbrujo(producto.getNivelEmbrujo())
                .tipoEntidad(tipoEntidad)
                .cantidadMitos(contenidoRepository.listarMitos(id).size())
                .region(producto.getRegionNombre())
                .build();
    }

    private <T extends Comparable<T>> List<Long> mejores(List<CasaComparadaResponse> casas,
                                                        Function<CasaComparadaResponse, T> valor) {
        T mejor = casas.stream().map(valor).max(Comparator.naturalOrder()).orElseThrow();
        return casas.stream()
                .filter(casa -> valor.apply(casa).compareTo(mejor) == 0)
                .map(CasaComparadaResponse::getIdProducto)
                .toList();
    }
}
