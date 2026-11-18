package cl.duoc.casasembrujadas.service.impl;

import java.util.List;

import cl.duoc.casasembrujadas.dto.response.ComunaResponse;
import cl.duoc.casasembrujadas.dto.response.RegionResponse;
import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.mapper.UbicacionMapper;
import cl.duoc.casasembrujadas.repository.UbicacionRepository;
import cl.duoc.casasembrujadas.service.UbicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UbicacionServiceImpl implements UbicacionService {

    private final UbicacionRepository ubicacionRepository;
    private final UbicacionMapper ubicacionMapper;

    @Override
    public List<RegionResponse> listarRegiones() {
        return ubicacionRepository.listarRegiones().stream().map(ubicacionMapper::toResponse).toList();
    }

    @Override
    public List<ComunaResponse> listarComunas(Integer idRegion) {
        if (!ubicacionRepository.existeRegion(idRegion)) {
            throw new RecursoNoEncontradoException("No existe la región con id " + idRegion);
        }
        return ubicacionRepository.listarComunasPorRegion(idRegion).stream().map(ubicacionMapper::toResponse).toList();
    }
}
