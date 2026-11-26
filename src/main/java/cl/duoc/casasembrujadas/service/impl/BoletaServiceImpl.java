package cl.duoc.casasembrujadas.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cl.duoc.casasembrujadas.dto.request.BoletaRequest;
import cl.duoc.casasembrujadas.dto.response.BoletaResponse;
import cl.duoc.casasembrujadas.exception.AccesoDenegadoException;
import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.mapper.BoletaMapper;
import cl.duoc.casasembrujadas.model.Boleta;
import cl.duoc.casasembrujadas.model.DetalleBoleta;
import cl.duoc.casasembrujadas.model.EstadoBoleta;
import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.EstadoReserva;
import cl.duoc.casasembrujadas.model.Producto;
import cl.duoc.casasembrujadas.model.Reserva;
import cl.duoc.casasembrujadas.repository.BoletaRepository;
import cl.duoc.casasembrujadas.repository.ProductoRepository;
import cl.duoc.casasembrujadas.repository.ReservaRepository;
import cl.duoc.casasembrujadas.security.UsuarioAutenticado;
import cl.duoc.casasembrujadas.service.BoletaService;
import cl.duoc.casasembrujadas.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoletaServiceImpl implements BoletaService {

    private static final BigDecimal TASA_IVA = new BigDecimal("0.19");

    private final BoletaRepository boletaRepository;
    private final ProductoRepository productoRepository;
    private final ReservaRepository reservaRepository;
    private final ReservaService reservaService;
    private final BoletaMapper boletaMapper;
    private final TransicionEstadoProducto transicionEstado;
    private final Clock clock;

    @Override
    @Transactional
    public BoletaResponse comprar(Long idUsuario, BoletaRequest request) {
        List<Long> ids = request.getIdsProductos();
        if (new HashSet<>(ids).size() != ids.size()) {
            throw new ReglaNegocioException("No se puede repetir una casa en el carrito");
        }
        reservaService.expirarVencidas();

        List<Producto> casas = new ArrayList<>();
        List<Reserva> reservasDelComprador = new ArrayList<>();
        for (Long idProducto : ids.stream().sorted().toList()) {
            Producto casa = productoRepository.buscarPorIdConBloqueo(idProducto)
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe la casa con id " + idProducto));
            validarQueSePuedeComprar(casa, idUsuario, reservasDelComprador);
            casas.add(casa);
        }

        BigDecimal neto = casas.stream().map(Producto::getPrecio).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal iva = neto.multiply(TASA_IVA).setScale(0, RoundingMode.HALF_UP);
        Boleta boleta = Boleta.builder()
                .numero(boletaRepository.siguienteNumero())
                .fecha(LocalDateTime.now(clock))
                .neto(neto)
                .iva(iva)
                .total(neto.add(iva))
                .estado(EstadoBoleta.PAGADA)
                .idUsuario(idUsuario)
                .build();
        Long idBoleta = boletaRepository.insertar(boleta);

        List<DetalleBoleta> detalles = casas.stream()
                .map(casa -> DetalleBoleta.builder()
                        .idProducto(casa.getIdProducto())
                        .cantidad(1)
                        .precioUnitario(casa.getPrecio())
                        .subtotal(casa.getPrecio())
                        .build())
                .toList();
        boletaRepository.insertarDetalles(idBoleta, detalles);

        casas.forEach(casa -> transicionEstado.cambiar(casa, EstadoProducto.VENDIDA));
        reservasDelComprador.forEach(reserva -> reservaRepository.actualizarEstado(reserva.getIdReserva(), EstadoReserva.CONVERTIDA));

        return boletaMapper.toResponse(buscar(idBoleta));
    }

    private void validarQueSePuedeComprar(Producto casa, Long idUsuario, List<Reserva> reservasDelComprador) {
        switch (casa.getEstado()) {
            case VENDIDA -> throw new ReglaNegocioException("La casa \"" + casa.getTitulo() + "\" ya fue vendida");
            case INACTIVA -> throw new ReglaNegocioException("La casa \"" + casa.getTitulo() + "\" no está a la venta");
            case RESERVADA -> {
                Reserva reserva = reservaRepository.buscarActivaPorProducto(casa.getIdProducto())
                        .orElseThrow(() -> new ReglaNegocioException("La casa \"" + casa.getTitulo() + "\" no está disponible"));
                if (!reserva.getIdUsuario().equals(idUsuario)) {
                    throw new ReglaNegocioException("La casa \"" + casa.getTitulo() + "\" está reservada por otro cliente");
                }
                reservasDelComprador.add(reserva);
            }
            case DISPONIBLE -> {
            }
        }
    }

    @Override
    public BoletaResponse obtener(Long idBoleta, UsuarioAutenticado usuario) {
        Boleta boleta = buscar(idBoleta);
        if (usuario.esCliente() && !boleta.getIdUsuario().equals(usuario.idUsuario())) {
            throw new AccesoDenegadoException("Esta boleta pertenece a otro cliente");
        }
        return boletaMapper.toResponse(boleta);
    }

    @Override
    public List<BoletaResponse> listar() {
        return boletaRepository.listar().stream().map(boletaMapper::toResponse).toList();
    }

    @Override
    public List<BoletaResponse> listarMias(Long idUsuario) {
        return boletaRepository.listarPorUsuario(idUsuario).stream().map(boletaMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public BoletaResponse cambiarEstado(Long idBoleta, EstadoBoleta nuevoEstado) {
        Boleta boleta = buscar(idBoleta);
        if (boleta.getEstado() == nuevoEstado) {
            throw new ReglaNegocioException("La boleta ya está " + nuevoEstado);
        }
        if (boleta.getEstado() == EstadoBoleta.ANULADA) {
            throw new ReglaNegocioException("Una boleta anulada no puede volver a pagarse: sus casas pudieron venderse de nuevo");
        }
        boletaRepository.actualizarEstado(idBoleta, nuevoEstado);
        for (DetalleBoleta detalle : boleta.getDetalles()) {
            productoRepository.buscarPorIdConBloqueo(detalle.getIdProducto())
                    .filter(casa -> casa.getEstado() == EstadoProducto.VENDIDA)
                    .ifPresent(casa -> transicionEstado.cambiar(casa, EstadoProducto.DISPONIBLE));
        }
        return boletaMapper.toResponse(buscar(idBoleta));
    }

    private Boleta buscar(Long idBoleta) {
        return boletaRepository.buscarPorId(idBoleta)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la boleta con id " + idBoleta));
    }
}
