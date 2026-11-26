package cl.duoc.casasembrujadas.service;

import java.util.List;

import cl.duoc.casasembrujadas.dto.response.ComunaResponse;
import cl.duoc.casasembrujadas.dto.response.RegionResponse;

public interface UbicacionService {

    List<RegionResponse> listarRegiones();

    List<ComunaResponse> listarComunas(Integer idRegion);
}
