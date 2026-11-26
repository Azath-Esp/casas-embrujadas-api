package cl.duoc.casasembrujadas.service.impl;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import cl.duoc.casasembrujadas.dto.response.ReservaResponse;
import cl.duoc.casasembrujadas.exception.AccesoDenegadoException;
import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.mapper.ReservaMapper;
import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.EstadoReserva;
import cl.duoc.casasembrujadas.model.Producto;
import cl.duoc.casasembrujadas.model.Reserva;
import cl.duoc.casasembrujadas.repository.ProductoRepository;
import cl.duoc.casasembrujadas.repository.ReservaRepository;
import cl.duoc.casasembrujadas.security.UsuarioAutenticado;
import cl.duoc.casasembrujadas.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private static final int HORAS_RESERVA = 24;
    private static final int MAXIMO_ACTIVAS_POR_CLIENTE = 2;

    private final ReservaRepository reservaRepository;
    private final ProductoRepository productoRepository;
    private final ReservaMapper reservaMapper;
    private final TransicionEstadoProducto transicionEstado;
    private final Clock clock;

    @Override
    @Transactional
    public ReservaResponse reservar(Long idUsuario, Long idProducto) {
        expirarVencidas();
        Producto producto = productoRepository.buscarPorIdConBloqueo(idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la casa con id " + idProducto));
        if (producto.getEstado() != EstadoProducto.DISPONIBLE) {
            throw new ReglaNegocioException("La casa \"" + producto.getTitulo() + "\" no está disponible para reservar");
        }
        if (reservaRepository.contarActivasPorUsuario(idUsuario) >= MAXIMO_ACTIVAS_POR_CLIENTE) {
            throw new ReglaNegocioException("Ya tienes " + MAXIMO_ACTIVAS_POR_CLIENTE + " reservas activas, que es el máximo permitido");
        }
        LocalDateTime ahora = LocalDateTime.now(clock);
        Reserva reserva = Reserva.builder()
                .idProducto(idProducto)
                .idUsuario(idUsuario)
                .fechaInicio(ahora)
                .fechaExpiracion(ahora.plusHours(HORAS_RESERVA))
                .estado(EstadoReserva.ACTIVA)
                .build();
        Long idReserva;
        try {
            idReserva = reservaRepository.insertar(reserva);
        } catch (DuplicateKeyException ex) {
            throw new ReglaNegocioException("La casa \"" + producto.getTitulo() + "\" ya tiene una reserva activa");
        }
        transicionEstado.cambiar(producto, EstadoProducto.RESERVADA);
        return reservaMapper.toResponse(buscar(idReserva));
    }

    @Override
    @Transactional
    public void cancelar(Long idReserva, UsuarioAutenticado usuario) {
        Reserva reserva = buscar(idReserva);
        if (!usuario.esAdmin() && !reserva.getIdUsuario().equals(usuario.idUsuario())) {
            throw new AccesoDenegadoException("Esta reserva pertenece a otro cliente");
        }
        if (reserva.getEstado() != EstadoReserva.ACTIVA) {
            throw new ReglaNegocioException("Solo se puede cancelar una reserva activa; esta está " + reserva.getEstado());
        }
        reservaRepository.actualizarEstado(idReserva, EstadoReserva.CANCELADA);
        liberarCasa(reserva.getIdProducto());
    }

    @Override
    public List<ReservaResponse> listarMias(Long idUsuario) {
        return reservaRepository.listarPorUsuario(idUsuario).stream().map(reservaMapper::toResponse).toList();
    }

    @Override
    public List<ReservaResponse> listarTodas() {
        return reservaRepository.listarTodas().stream().map(reservaMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public int expirarVencidas() {
        List<Reserva> vencidas = reservaRepository.listarActivasVencidas(LocalDateTime.now(clock));
        for (Reserva reserva : vencidas) {
            reservaRepository.actualizarEstado(reserva.getIdReserva(), EstadoReserva.EXPIRADA);
            liberarCasa(reserva.getIdProducto());
        }
        return vencidas.size();
    }

    private void liberarCasa(Long idProducto) {
        productoRepository.buscarPorIdConBloqueo(idProducto)
                .filter(producto -> producto.getEstado() == EstadoProducto.RESERVADA)
                .ifPresent(producto -> transicionEstado.cambiar(producto, EstadoProducto.DISPONIBLE));
    }

    private Reserva buscar(Long idReserva) {
        return reservaRepository.buscarPorId(idReserva)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la reserva con id " + idReserva));
    }
}
