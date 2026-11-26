package cl.duoc.casasembrujadas.repository;

import java.util.List;
import java.util.Optional;

import cl.duoc.casasembrujadas.model.FichaParanormal;
import cl.duoc.casasembrujadas.model.Imagen;
import cl.duoc.casasembrujadas.model.Mito;

public interface ContenidoRepository {

    List<Imagen> listarImagenes(Long idProducto);

    Optional<Imagen> buscarImagen(Long idImagen);

    Long insertarImagen(Imagen imagen);

    void actualizarImagen(Imagen imagen);

    boolean eliminarImagen(Long idImagen);

    List<Mito> listarMitos(Long idProducto);

    Optional<Mito> buscarMito(Long idMito);

    Long insertarMito(Mito mito);

    void actualizarMito(Mito mito);

    boolean eliminarMito(Long idMito);

    Optional<FichaParanormal> buscarFicha(Long idProducto);

    void guardarFicha(FichaParanormal ficha);

    boolean eliminarFicha(Long idProducto);
}
