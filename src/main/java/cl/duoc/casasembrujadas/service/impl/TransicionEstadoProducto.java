package cl.duoc.casasembrujadas.service.impl;

import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.Producto;
import cl.duoc.casasembrujadas.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransicionEstadoProducto {

    private final ProductoRepository productoRepository;

    public void cambiar(Producto producto, EstadoProducto destino) {
        EstadoProducto actual = producto.getEstado();
        if (actual == destino) {
            return;
        }
        if (!actual.puedeCambiarA(destino)) {
            throw new ReglaNegocioException("La casa \"" + producto.getTitulo() + "\" no puede pasar de " + actual + " a " + destino);
        }
        productoRepository.actualizarEstado(producto.getIdProducto(), destino);
        producto.setEstado(destino);
    }
}
