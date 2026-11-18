package cl.duoc.casasembrujadas.service.impl;

import java.util.List;

import cl.duoc.casasembrujadas.dto.request.ProductoRequest;
import cl.duoc.casasembrujadas.dto.response.PaginaResponse;
import cl.duoc.casasembrujadas.dto.response.ProductoDetalleResponse;
import cl.duoc.casasembrujadas.dto.response.ProductoResponse;
import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.mapper.ProductoMapper;
import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.Producto;
import cl.duoc.casasembrujadas.repository.CategoriaRepository;
import cl.duoc.casasembrujadas.repository.ContenidoRepository;
import cl.duoc.casasembrujadas.repository.ProductoFiltro;
import cl.duoc.casasembrujadas.repository.ProductoRepository;
import cl.duoc.casasembrujadas.repository.UbicacionRepository;
import cl.duoc.casasembrujadas.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private static final int TAMANO_MAXIMO = 50;
    private static final int TAMANO_MAXIMO_DETALLE = 20;

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ContenidoRepository contenidoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final ProductoMapper productoMapper;
    private final TransicionEstadoProducto transicionEstado;

    @Override
    @Transactional(readOnly = true)
    public PaginaResponse<ProductoResponse> buscar(ProductoFiltro filtro, int pagina, int tamano) {
        int paginaSegura = Math.max(pagina, 0);
        int tamanoSeguro = Math.min(Math.max(tamano, 1), TAMANO_MAXIMO);
        List<ProductoResponse> contenido = productoRepository.buscar(filtro, paginaSegura, tamanoSeguro).stream()
                .map(productoMapper::toResponse)
                .toList();
        return armarPagina(contenido, paginaSegura, tamanoSeguro, productoRepository.contar(filtro));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResponse<ProductoDetalleResponse> buscarConDetalle(ProductoFiltro filtro, int pagina, int tamano) {
        int paginaSegura = Math.max(pagina, 0);
        int tamanoSeguro = Math.min(Math.max(tamano, 1), TAMANO_MAXIMO_DETALLE);
        List<ProductoDetalleResponse> contenido = productoRepository.buscar(filtro, paginaSegura, tamanoSeguro).stream()
                .map(this::armarDetalle)
                .toList();
        return armarPagina(contenido, paginaSegura, tamanoSeguro, productoRepository.contar(filtro));
    }

    @Override
    public ProductoResponse obtener(Long idProducto) {
        return productoMapper.toResponse(buscar(idProducto));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDetalleResponse obtenerDetalle(Long idProducto) {
        return armarDetalle(buscar(idProducto));
    }

    @Override
    @Transactional
    public ProductoDetalleResponse crear(ProductoRequest request) {
        validarComunaYCategorias(request);
        if (request.getEstado() != null && request.getEstado() != EstadoProducto.DISPONIBLE
                && request.getEstado() != EstadoProducto.INACTIVA) {
            throw new ReglaNegocioException("Una casa nueva solo puede crearse DISPONIBLE o INACTIVA");
        }
        Producto producto = productoMapper.toModel(request);
        Long idProducto = productoRepository.insertar(producto);
        productoRepository.reemplazarCategorias(idProducto, request.getIdsCategorias());
        return armarDetalle(buscar(idProducto));
    }

    @Override
    @Transactional
    public ProductoDetalleResponse actualizar(Long idProducto, ProductoRequest request) {
        Producto actual = productoRepository.buscarPorIdConBloqueo(idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la casa con id " + idProducto));
        validarComunaYCategorias(request);
        EstadoProducto destino = request.getEstado() != null ? request.getEstado() : actual.getEstado();
        validarCambioDeEstadoManual(actual, destino);

        Producto cambios = productoMapper.toModel(request);
        cambios.setIdProducto(idProducto);
        cambios.setEstado(actual.getEstado());
        productoRepository.actualizar(cambios);
        productoRepository.reemplazarCategorias(idProducto, request.getIdsCategorias());
        transicionEstado.cambiar(actual, destino);
        return armarDetalle(buscar(idProducto));
    }

    @Override
    @Transactional
    public void eliminar(Long idProducto) {
        Producto producto = productoRepository.buscarPorIdConBloqueo(idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la casa con id " + idProducto));
        if (producto.getEstado() == EstadoProducto.RESERVADA) {
            throw new ReglaNegocioException("No se puede eliminar una casa con una reserva activa");
        }
        if (productoRepository.tieneBoletas(idProducto)) {
            transicionEstado.cambiar(producto, EstadoProducto.INACTIVA);
            return;
        }
        productoRepository.eliminar(idProducto);
    }

    private void validarCambioDeEstadoManual(Producto actual, EstadoProducto destino) {
        if (destino == actual.getEstado()) {
            return;
        }
        if (destino != EstadoProducto.DISPONIBLE && destino != EstadoProducto.INACTIVA) {
            throw new ReglaNegocioException("El estado " + destino + " lo asigna el sistema al reservar o vender");
        }
        if (!actual.getEstado().puedeCambiarA(destino)) {
            throw new ReglaNegocioException("La casa no puede pasar de " + actual.getEstado() + " a " + destino);
        }
        if (destino == EstadoProducto.DISPONIBLE && productoRepository.tieneBoletaPagada(actual.getIdProducto())) {
            throw new ReglaNegocioException("La casa tiene una boleta pagada: para volver a ofrecerla hay que anular esa boleta");
        }
    }

    private void validarComunaYCategorias(ProductoRequest request) {
        if (ubicacionRepository.buscarComunaPorId(request.getIdComuna()).isEmpty()) {
            throw new RecursoNoEncontradoException("No existe la comuna con id " + request.getIdComuna());
        }
        long distintas = request.getIdsCategorias().stream().distinct().count();
        if (categoriaRepository.contarPorIds(request.getIdsCategorias()) != distintas) {
            throw new RecursoNoEncontradoException("Alguna de las categorías indicadas no existe");
        }
    }

    private Producto buscar(Long idProducto) {
        return productoRepository.buscarPorId(idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la casa con id " + idProducto));
    }

    private ProductoDetalleResponse armarDetalle(Producto producto) {
        Long id = producto.getIdProducto();
        return productoMapper.toDetalleResponse(
                producto,
                contenidoRepository.listarImagenes(id),
                categoriaRepository.listarPorProducto(id),
                contenidoRepository.listarMitos(id),
                contenidoRepository.buscarFicha(id).orElse(null));
    }

    private <T> PaginaResponse<T> armarPagina(List<T> contenido, int pagina, int tamano, long total) {
        return PaginaResponse.<T>builder()
                .contenido(contenido)
                .pagina(pagina)
                .tamano(tamano)
                .totalElementos(total)
                .totalPaginas((int) Math.ceil((double) total / tamano))
                .build();
    }
}
