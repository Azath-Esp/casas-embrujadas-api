package cl.duoc.casasembrujadas.mapper;

import cl.duoc.casasembrujadas.dto.response.ReservaResponse;
import cl.duoc.casasembrujadas.model.Reserva;
import org.springframework.stereotype.Component;


@Component
public class ReservaMapper {

    public ReservaResponse toResponse(Reserva reserva) {
        return ReservaResponse.builder()
                .idReserva(reserva.getIdReserva())
                .idProducto(reserva.getIdProducto())
                .productoTitulo(reserva.getProductoTitulo())
                .idUsuario(reserva.getIdUsuario())
                .usuarioNombre(reserva.getUsuarioNombre())
                .fechaInicio(reserva.getFechaInicio())
                .fechaExpiracion(reserva.getFechaExpiracion())
                .estado(reserva.getEstado())
                .build();
    }
}