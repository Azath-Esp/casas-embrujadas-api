package cl.duoc.casasembrujadas.service.impl;

import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.ADMIN;
import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.AHORA;
import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.ANTONIA;
import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.CAMILA;
import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.RELOJ;
import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.casa;
import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.reservaActiva;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import cl.duoc.casasembrujadas.exception.AccesoDenegadoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.mapper.ReservaMapper;
import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.EstadoReserva;
import cl.duoc.casasembrujadas.model.Reserva;
import cl.duoc.casasembrujadas.repository.ProductoRepository;
import cl.duoc.casasembrujadas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private ProductoRepository productoRepository;

    private ReservaServiceImpl servicio;

    @BeforeEach
    void preparar() {
        servicio = new ReservaServiceImpl(reservaRepository, productoRepository, new ReservaMapper(),
                new TransicionEstadoProducto(productoRepository), RELOJ);
    }

    @Test
    void reservaPorVeinticuatroHorasYMarcaLaCasaComoReservada() {
        when(productoRepository.buscarPorIdConBloqueo(1L)).thenReturn(Optional.of(casa(1, EstadoProducto.DISPONIBLE, "100")));
        when(reservaRepository.insertar(any())).thenReturn(30L);
        when(reservaRepository.buscarPorId(30L)).thenReturn(Optional.of(reservaActiva(30, 1, 3)));

        servicio.reservar(3L, 1L);

        ArgumentCaptor<Reserva> reserva = ArgumentCaptor.forClass(Reserva.class);
        verify(reservaRepository).insertar(reserva.capture());
        assertEquals(AHORA, reserva.getValue().getFechaInicio());
        assertEquals(AHORA.plusHours(24), reserva.getValue().getFechaExpiracion());
        assertEquals(EstadoReserva.ACTIVA, reserva.getValue().getEstado());
        verify(productoRepository).actualizarEstado(1L, EstadoProducto.RESERVADA);
    }

    @Test
    void noReservaUnaCasaQueNoEstaDisponible() {
        when(productoRepository.buscarPorIdConBloqueo(7L)).thenReturn(Optional.of(casa(7, EstadoProducto.VENDIDA, "100")));

        assertThrows(ReglaNegocioException.class, () -> servicio.reservar(3L, 7L));
        verify(reservaRepository, never()).insertar(any());
    }

    @Test
    void unClienteNoPuedeTenerMasDeDosReservasActivas() {
        when(productoRepository.buscarPorIdConBloqueo(1L)).thenReturn(Optional.of(casa(1, EstadoProducto.DISPONIBLE, "100")));
        when(reservaRepository.contarActivasPorUsuario(3L)).thenReturn(2L);

        assertThrows(ReglaNegocioException.class, () -> servicio.reservar(3L, 1L));
        verify(reservaRepository, never()).insertar(any());
    }

    @Test
    void siLaBaseDetectaOtraReservaActivaSeTraduceAReglaDeNegocio() {
        when(productoRepository.buscarPorIdConBloqueo(1L)).thenReturn(Optional.of(casa(1, EstadoProducto.DISPONIBLE, "100")));
        when(reservaRepository.insertar(any())).thenThrow(new DuplicateKeyException("uq_reserva_activa_por_producto"));

        assertThrows(ReglaNegocioException.class, () -> servicio.reservar(3L, 1L));
        verify(productoRepository, never()).actualizarEstado(any(), any());
    }

    @Test
    void soloElDuenoOElAdminCancelan() {
        when(reservaRepository.buscarPorId(4L)).thenReturn(Optional.of(reservaActiva(4, 8, 3)));
        when(productoRepository.buscarPorIdConBloqueo(8L)).thenReturn(Optional.of(casa(8, EstadoProducto.RESERVADA, "100")));

        assertThrows(AccesoDenegadoException.class, () -> servicio.cancelar(4L, ANTONIA));
        servicio.cancelar(4L, CAMILA);

        verify(reservaRepository).actualizarEstado(4L, EstadoReserva.CANCELADA);
        verify(productoRepository).actualizarEstado(8L, EstadoProducto.DISPONIBLE);
    }

    @Test
    void noSeCancelaUnaReservaTerminada() {
        Reserva expirada = reservaActiva(4, 8, 3);
        expirada.setEstado(EstadoReserva.EXPIRADA);
        when(reservaRepository.buscarPorId(4L)).thenReturn(Optional.of(expirada));

        assertThrows(ReglaNegocioException.class, () -> servicio.cancelar(4L, ADMIN));
    }

    @Test
    void expirarMarcaLasVencidasYLiberaSusCasasSegunElReloj() {
        when(reservaRepository.listarActivasVencidas(AHORA)).thenReturn(List.of(reservaActiva(4, 8, 3)));
        when(productoRepository.buscarPorIdConBloqueo(8L)).thenReturn(Optional.of(casa(8, EstadoProducto.RESERVADA, "100")));

        assertEquals(1, servicio.expirarVencidas());

        verify(reservaRepository).actualizarEstado(4L, EstadoReserva.EXPIRADA);
        verify(productoRepository).actualizarEstado(8L, EstadoProducto.DISPONIBLE);
    }
}
