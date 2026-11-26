package cl.duoc.casasembrujadas.mapper;

import java.util.List;

import cl.duoc.casasembrujadas.dto.response.BoletaResponse;
import cl.duoc.casasembrujadas.dto.response.DetalleBoletaResponse;
import cl.duoc.casasembrujadas.model.Boleta;
import cl.duoc.casasembrujadas.model.DetalleBoleta;
import org.springframework.stereotype.Component;


@Component
public class BoletaMapper {

    public BoletaResponse toResponse(Boleta boleta) {
        List<DetalleBoleta> detalles = boleta.getDetalles() != null ? boleta.getDetalles() : List.of();
        return BoletaResponse.builder()
                .idBoleta(boleta.getIdBoleta())
                .numero(boleta.getNumero())
                .fecha(boleta.getFecha())
                .neto(boleta.getNeto())
                .iva(boleta.getIva())
                .total(boleta.getTotal())
                .estado(boleta.getEstado())
                .idUsuario(boleta.getIdUsuario())
                .clienteNombre(boleta.getClienteNombre())
                .clienteRun(boleta.getClienteRun())
                .clienteCorreo(boleta.getClienteCorreo())
                .detalles(detalles.stream().map(this::toResponse).toList())
                .build();
    }

    public DetalleBoletaResponse toResponse(DetalleBoleta detalle) {
        return DetalleBoletaResponse.builder()
                .idProducto(detalle.getIdProducto())
                .productoTitulo(detalle.getProductoTitulo())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .build();
    }
}