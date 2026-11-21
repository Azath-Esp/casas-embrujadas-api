package cl.duoc.casasembrujadas.service.impl;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.EstadoReserva;
import cl.duoc.casasembrujadas.model.Producto;
import cl.duoc.casasembrujadas.model.Reserva;
import cl.duoc.casasembrujadas.model.RolNombre;
import cl.duoc.casasembrujadas.security.UsuarioAutenticado;

final class DatosDePrueba {

    static final ZoneId ZONA = ZoneId.of("America/Santiago");
    static final Clock RELOJ = Clock.fixed(Instant.parse("2026-11-17T15:00:00Z"), ZONA);
    static final LocalDateTime AHORA = LocalDateTime.now(RELOJ);

    static final UsuarioAutenticado CAMILA = new UsuarioAutenticado(3L, "cliente@casasembrujadas.cl", RolNombre.CLIENTE);
    static final UsuarioAutenticado ANTONIA = new UsuarioAutenticado(5L, "antonia.herrera@casasembrujadas.cl", RolNombre.CLIENTE);
    static final UsuarioAutenticado VENDEDOR = new UsuarioAutenticado(2L, "vendedor@casasembrujadas.cl", RolNombre.VENDEDOR);
    static final UsuarioAutenticado ADMIN = new UsuarioAutenticado(1L, "admin@casasembrujadas.cl", RolNombre.ADMIN);

    private DatosDePrueba() {
    }

    static Producto casa(long id, EstadoProducto estado, String precio) {
        return Producto.builder()
                .idProducto(id)
                .titulo("Casa " + id)
                .precio(new BigDecimal(precio))
                .estado(estado)
                .regionNombre("Metropolitana de Santiago")
                .build();
    }

    static Reserva reservaActiva(long idReserva, long idProducto, long idUsuario) {
        return Reserva.builder()
                .idReserva(idReserva)
                .idProducto(idProducto)
                .idUsuario(idUsuario)
                .fechaInicio(AHORA.minusHours(2))
                .fechaExpiracion(AHORA.plusHours(22))
                .estado(EstadoReserva.ACTIVA)
                .build();
    }
}
