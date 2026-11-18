package cl.duoc.casasembrujadas.service;

import java.util.List;

import cl.duoc.casasembrujadas.dto.response.ReservaResponse;
import cl.duoc.casasembrujadas.security.UsuarioAutenticado;

public interface ReservaService {

    ReservaResponse reservar(Long idUsuario, Long idProducto);

    void cancelar(Long idReserva, UsuarioAutenticado usuario);

    List<ReservaResponse> listarMias(Long idUsuario);

    List<ReservaResponse> listarTodas();

    int expirarVencidas();
}
