package cl.duoc.casasembrujadas.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import cl.duoc.casasembrujadas.model.EstadoReserva;
import cl.duoc.casasembrujadas.model.Reserva;

public interface ReservaRepository {

    Long insertar(Reserva reserva);

    Optional<Reserva> buscarPorId(Long idReserva);

    Optional<Reserva> buscarActivaPorProducto(Long idProducto);

    long contarActivasPorUsuario(Long idUsuario);

    List<Reserva> listarPorUsuario(Long idUsuario);

    List<Reserva> listarTodas();

    void actualizarEstado(Long idReserva, EstadoReserva estado);

    List<Reserva> listarActivasVencidas(LocalDateTime ahora);
}
