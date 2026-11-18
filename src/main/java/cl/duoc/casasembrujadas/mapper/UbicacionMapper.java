package cl.duoc.casasembrujadas.mapper;

import cl.duoc.casasembrujadas.dto.response.ComunaResponse;
import cl.duoc.casasembrujadas.dto.response.RegionResponse;
import cl.duoc.casasembrujadas.model.Comuna;
import cl.duoc.casasembrujadas.model.Region;
import org.springframework.stereotype.Component;

@Component
public class UbicacionMapper {

    public RegionResponse toResponse(Region region) {
        return RegionResponse.builder()
                .idRegion(region.getIdRegion())
                .nombre(region.getNombre())
                .build();
    }

    public ComunaResponse toResponse(Comuna comuna) {
        return ComunaResponse.builder()
                .idComuna(comuna.getIdComuna())
                .nombre(comuna.getNombre())
                .idRegion(comuna.getIdRegion())
                .region(comuna.getRegionNombre())
                .build();
    }
}