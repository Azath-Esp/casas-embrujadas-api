package cl.duoc.casasembrujadas.repository;

import java.util.List;
import java.util.Optional;

import cl.duoc.casasembrujadas.model.Boleta;
import cl.duoc.casasembrujadas.model.DetalleBoleta;
import cl.duoc.casasembrujadas.model.EstadoBoleta;

public interface BoletaRepository {

    int siguienteNumero();

    Long insertar(Boleta boleta);

    void insertarDetalles(Long idBoleta, List<DetalleBoleta> detalles);

    Optional<Boleta> buscarPorId(Long idBoleta);

    List<Boleta> listar();

    List<Boleta> listarPorUsuario(Long idUsuario);

    void actualizarEstado(Long idBoleta, EstadoBoleta estado);
}
