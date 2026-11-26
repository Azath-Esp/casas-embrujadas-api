package cl.duoc.casasembrujadas.mapper;

import java.util.List;

import cl.duoc.casasembrujadas.dto.request.ProductoRequest;
import cl.duoc.casasembrujadas.dto.response.ProductoDetalleResponse;
import cl.duoc.casasembrujadas.dto.response.ProductoMapaResponse;
import cl.duoc.casasembrujadas.dto.response.ProductoResponse;
import cl.duoc.casasembrujadas.model.Categoria;
import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.FichaParanormal;
import cl.duoc.casasembrujadas.model.Imagen;
import cl.duoc.casasembrujadas.model.Mito;
import cl.duoc.casasembrujadas.model.Producto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ProductoMapper {

    private final CategoriaMapper categoriaMapper;
    private final ContenidoMapper contenidoMapper;

    public ProductoResponse toResponse(Producto producto) {
        return ProductoResponse.builder()
                .idProducto(producto.getIdProducto())
                .titulo(producto.getTitulo())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .direccion(producto.getDireccion())
                .metrosCuadrados(producto.getMetrosCuadrados())
                .dormitorios(producto.getDormitorios())
                .banos(producto.getBanos())
                .nivelEmbrujo(producto.getNivelEmbrujo())
                .estado(producto.getEstado())
                .comuna(producto.getComunaNombre())
                .region(producto.getRegionNombre())
                .imagenPrincipal(producto.getImagenPrincipal())
                .fechaPublicacion(producto.getFechaPublicacion())
                .build();
    }

    public ProductoDetalleResponse toDetalleResponse(Producto producto, List<Imagen> imagenes, List<Categoria> categorias,
                                                     List<Mito> mitos, FichaParanormal ficha) {
        return ProductoDetalleResponse.builder()
                .idProducto(producto.getIdProducto())
                .titulo(producto.getTitulo())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .direccion(producto.getDireccion())
                .metrosCuadrados(producto.getMetrosCuadrados())
                .dormitorios(producto.getDormitorios())
                .banos(producto.getBanos())
                .nivelEmbrujo(producto.getNivelEmbrujo())
                .estado(producto.getEstado())
                .idComuna(producto.getIdComuna())
                .comuna(producto.getComunaNombre())
                .idRegion(producto.getIdRegion())
                .region(producto.getRegionNombre())
                .latitud(producto.getLatitud())
                .longitud(producto.getLongitud())
                .fechaPublicacion(producto.getFechaPublicacion())
                .imagenes(imagenes.stream().map(contenidoMapper::toResponse).toList())
                .categorias(categorias.stream().map(categoriaMapper::toResponse).toList())
                .mitos(mitos.stream().map(contenidoMapper::toResponse).toList())
                .fichaParanormal(contenidoMapper.toResponse(ficha))
                .build();
    }

    public ProductoMapaResponse toMapaResponse(Producto producto) {
        return ProductoMapaResponse.builder()
                .idProducto(producto.getIdProducto())
                .titulo(producto.getTitulo())
                .latitud(producto.getLatitud())
                .longitud(producto.getLongitud())
                .precio(producto.getPrecio())
                .estado(producto.getEstado())
                .imagenPrincipal(producto.getImagenPrincipal())
                .build();
    }

    public Producto toModel(ProductoRequest request) {
        return Producto.builder()
                .titulo(request.getTitulo().trim())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .direccion(request.getDireccion())
                .metrosCuadrados(request.getMetrosCuadrados())
                .dormitorios(request.getDormitorios())
                .banos(request.getBanos())
                .nivelEmbrujo(request.getNivelEmbrujo())
                .estado(request.getEstado() != null ? request.getEstado() : EstadoProducto.DISPONIBLE)
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .idComuna(request.getIdComuna())
                .build();
    }
}