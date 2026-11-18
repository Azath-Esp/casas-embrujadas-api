package cl.duoc.casasembrujadas.service.impl;

import java.util.List;

import cl.duoc.casasembrujadas.dto.request.FichaParanormalRequest;
import cl.duoc.casasembrujadas.dto.request.ImagenRequest;
import cl.duoc.casasembrujadas.dto.request.MitoRequest;
import cl.duoc.casasembrujadas.dto.response.FichaParanormalResponse;
import cl.duoc.casasembrujadas.dto.response.ImagenResponse;
import cl.duoc.casasembrujadas.dto.response.MitoResponse;
import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.mapper.ContenidoMapper;
import cl.duoc.casasembrujadas.model.FichaParanormal;
import cl.duoc.casasembrujadas.model.Imagen;
import cl.duoc.casasembrujadas.model.Mito;
import cl.duoc.casasembrujadas.repository.ContenidoRepository;
import cl.duoc.casasembrujadas.repository.ProductoRepository;
import cl.duoc.casasembrujadas.service.ContenidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContenidoServiceImpl implements ContenidoService {

    private final ContenidoRepository contenidoRepository;
    private final ProductoRepository productoRepository;
    private final ContenidoMapper contenidoMapper;

    @Override
    public List<ImagenResponse> listarImagenes(Long idProducto) {
        exigirCasa(idProducto);
        return contenidoRepository.listarImagenes(idProducto).stream().map(contenidoMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ImagenResponse agregarImagen(Long idProducto, ImagenRequest request) {
        exigirCasa(idProducto);
        Imagen imagen = contenidoMapper.toModel(request, idProducto);
        imagen.setIdImagen(contenidoRepository.insertarImagen(imagen));
        return contenidoMapper.toResponse(imagen);
    }

    @Override
    @Transactional
    public ImagenResponse actualizarImagen(Long idProducto, Long idImagen, ImagenRequest request) {
        buscarImagen(idProducto, idImagen);
        Imagen imagen = contenidoMapper.toModel(request, idProducto);
        imagen.setIdImagen(idImagen);
        contenidoRepository.actualizarImagen(imagen);
        return contenidoMapper.toResponse(imagen);
    }

    @Override
    @Transactional
    public void eliminarImagen(Long idProducto, Long idImagen) {
        buscarImagen(idProducto, idImagen);
        if (contenidoRepository.listarImagenes(idProducto).size() <= 1) {
            throw new ReglaNegocioException("Una casa debe conservar al menos una imagen");
        }
        contenidoRepository.eliminarImagen(idImagen);
    }

    @Override
    public List<MitoResponse> listarMitos(Long idProducto) {
        exigirCasa(idProducto);
        return contenidoRepository.listarMitos(idProducto).stream().map(contenidoMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public MitoResponse agregarMito(Long idProducto, MitoRequest request) {
        exigirCasa(idProducto);
        Mito mito = contenidoMapper.toModel(request, idProducto);
        mito.setIdMito(contenidoRepository.insertarMito(mito));
        return contenidoMapper.toResponse(mito);
    }

    @Override
    @Transactional
    public MitoResponse actualizarMito(Long idProducto, Long idMito, MitoRequest request) {
        buscarMito(idProducto, idMito);
        Mito mito = contenidoMapper.toModel(request, idProducto);
        mito.setIdMito(idMito);
        contenidoRepository.actualizarMito(mito);
        return contenidoMapper.toResponse(mito);
    }

    @Override
    @Transactional
    public void eliminarMito(Long idProducto, Long idMito) {
        buscarMito(idProducto, idMito);
        contenidoRepository.eliminarMito(idMito);
    }

    @Override
    public FichaParanormalResponse obtenerFicha(Long idProducto) {
        exigirCasa(idProducto);
        FichaParanormal ficha = contenidoRepository.buscarFicha(idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException("La casa con id " + idProducto + " no tiene ficha paranormal"));
        return contenidoMapper.toResponse(ficha);
    }

    @Override
    @Transactional
    public FichaParanormalResponse guardarFicha(Long idProducto, FichaParanormalRequest request) {
        exigirCasa(idProducto);
        FichaParanormal ficha = contenidoMapper.toModel(request, idProducto);
        contenidoRepository.guardarFicha(ficha);
        return contenidoMapper.toResponse(ficha);
    }

    @Override
    @Transactional
    public void eliminarFicha(Long idProducto) {
        exigirCasa(idProducto);
        if (!contenidoRepository.eliminarFicha(idProducto)) {
            throw new RecursoNoEncontradoException("La casa con id " + idProducto + " no tiene ficha paranormal");
        }
    }

    private void exigirCasa(Long idProducto) {
        if (productoRepository.buscarPorId(idProducto).isEmpty()) {
            throw new RecursoNoEncontradoException("No existe la casa con id " + idProducto);
        }
    }

    private Imagen buscarImagen(Long idProducto, Long idImagen) {
        return contenidoRepository.buscarImagen(idImagen)
                .filter(imagen -> imagen.getIdProducto().equals(idProducto))
                .orElseThrow(() -> new RecursoNoEncontradoException("La casa con id " + idProducto + " no tiene la imagen con id " + idImagen));
    }

    private Mito buscarMito(Long idProducto, Long idMito) {
        return contenidoRepository.buscarMito(idMito)
                .filter(mito -> mito.getIdProducto().equals(idProducto))
                .orElseThrow(() -> new RecursoNoEncontradoException("La casa con id " + idProducto + " no tiene el mito con id " + idMito));
    }
}
