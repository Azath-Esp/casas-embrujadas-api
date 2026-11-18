package cl.duoc.casasembrujadas.repository;

import java.util.List;
import java.util.Optional;

import cl.duoc.casasembrujadas.model.Comuna;
import cl.duoc.casasembrujadas.model.Region;

public interface UbicacionRepository {

    List<Region> listarRegiones();

    boolean existeRegion(Integer idRegion);

    List<Comuna> listarComunasPorRegion(Integer idRegion);

    Optional<Comuna> buscarComunaPorId(Integer idComuna);
}
