package cl.duoc.casasembrujadas.service;

import java.util.List;

import cl.duoc.casasembrujadas.dto.request.FichaParanormalRequest;
import cl.duoc.casasembrujadas.dto.request.ImagenRequest;
import cl.duoc.casasembrujadas.dto.request.MitoRequest;
import cl.duoc.casasembrujadas.dto.response.FichaParanormalResponse;
import cl.duoc.casasembrujadas.dto.response.ImagenResponse;
import cl.duoc.casasembrujadas.dto.response.MitoResponse;

public interface ContenidoService {

    List<ImagenResponse> listarImagenes(Long idProducto);

    ImagenResponse agregarImagen(Long idProducto, ImagenRequest request);

    ImagenResponse actualizarImagen(Long idProducto, Long idImagen, ImagenRequest request);

    void eliminarImagen(Long idProducto, Long idImagen);

    List<MitoResponse> listarMitos(Long idProducto);

    MitoResponse agregarMito(Long idProducto, MitoRequest request);

    MitoResponse actualizarMito(Long idProducto, Long idMito, MitoRequest request);

    void eliminarMito(Long idProducto, Long idMito);

    FichaParanormalResponse obtenerFicha(Long idProducto);

    FichaParanormalResponse guardarFicha(Long idProducto, FichaParanormalRequest request);

    void eliminarFicha(Long idProducto);
}
