package cl.duoc.casasembrujadas.service;

import java.util.List;

import cl.duoc.casasembrujadas.dto.request.BoletaRequest;
import cl.duoc.casasembrujadas.dto.response.BoletaResponse;
import cl.duoc.casasembrujadas.model.EstadoBoleta;
import cl.duoc.casasembrujadas.security.UsuarioAutenticado;

public interface BoletaService {

    BoletaResponse comprar(Long idUsuario, BoletaRequest request);

    BoletaResponse obtener(Long idBoleta, UsuarioAutenticado usuario);

    List<BoletaResponse> listar();

    List<BoletaResponse> listarMias(Long idUsuario);

    BoletaResponse cambiarEstado(Long idBoleta, EstadoBoleta nuevoEstado);
}
