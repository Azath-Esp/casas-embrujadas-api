package cl.duoc.casasembrujadas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import cl.duoc.casasembrujadas.dto.request.BoletaRequest;
import cl.duoc.casasembrujadas.dto.request.CategoriaRequest;
import cl.duoc.casasembrujadas.dto.request.ProductoRequest;
import cl.duoc.casasembrujadas.dto.response.BoletaResponse;
import cl.duoc.casasembrujadas.dto.response.ProductoDetalleResponse;
import cl.duoc.casasembrujadas.dto.response.ReservaResponse;
import cl.duoc.casasembrujadas.exception.AccesoDenegadoException;
import cl.duoc.casasembrujadas.exception.RecursoDuplicadoException;
import cl.duoc.casasembrujadas.exception.RecursoNoEncontradoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.model.EstadoBoleta;
import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.EstadoReserva;
import cl.duoc.casasembrujadas.model.RolNombre;
import cl.duoc.casasembrujadas.repository.ProductoFiltro;
import cl.duoc.casasembrujadas.security.UsuarioAutenticado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ServiciosIT {

    private static final UsuarioAutenticado CAMILA = new UsuarioAutenticado(3L, "cliente@casasembrujadas.cl", RolNombre.CLIENTE);
    private static final UsuarioAutenticado ANTONIA = new UsuarioAutenticado(5L, "antonia.herrera@casasembrujadas.cl", RolNombre.CLIENTE);
    private static final UsuarioAutenticado VENDEDOR = new UsuarioAutenticado(2L, "vendedor@casasembrujadas.cl", RolNombre.VENDEDOR);

    @Autowired
    private ProductoService productoService;
    @Autowired
    private CategoriaService categoriaService;
    @Autowired
    private FavoritoService favoritoService;
    @Autowired
    private ReservaService reservaService;
    @Autowired
    private BoletaService boletaService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private BoletaRequest carrito(Long... ids) {
        return BoletaRequest.builder().idsProductos(List.of(ids)).build();
    }

    private EstadoProducto estadoDe(Long idProducto) {
        return productoService.obtener(idProducto).getEstado();
    }

    @Test
    void laCompraCalculaLosMontosYVendeLasCasas() {
        BoletaResponse boleta = boletaService.comprar(5L, carrito(1L, 2L));

        assertEquals(5, boleta.getNumero());
        assertEquals(new BigDecimal("25750000000"), boleta.getNeto());
        assertEquals(new BigDecimal("4892500000"), boleta.getIva());
        assertEquals(new BigDecimal("30642500000"), boleta.getTotal());
        assertEquals(2, boleta.getDetalles().size());
        assertEquals(EstadoProducto.VENDIDA, estadoDe(1L));
        assertEquals(EstadoProducto.VENDIDA, estadoDe(2L));
    }

    @Test
    void laCompraRechazaCarritosInvalidos() {
        assertThrows(ReglaNegocioException.class, () -> boletaService.comprar(5L, carrito(1L, 1L)));
        assertThrows(ReglaNegocioException.class, () -> boletaService.comprar(5L, carrito(7L)));
        assertThrows(ReglaNegocioException.class, () -> boletaService.comprar(5L, carrito(12L)));
        assertThrows(ReglaNegocioException.class, () -> boletaService.comprar(5L, carrito(8L)));
        assertThrows(RecursoNoEncontradoException.class, () -> boletaService.comprar(5L, carrito(999L)));
        assertEquals(EstadoProducto.DISPONIBLE, estadoDe(1L));
    }

    @Test
    void quienReservoPuedeComprarYSuReservaQuedaConvertida() {
        BoletaResponse boleta = boletaService.comprar(3L, carrito(8L));

        assertEquals(EstadoProducto.VENDIDA, estadoDe(8L));
        assertEquals(1, boleta.getDetalles().size());
        ReservaResponse reserva = reservaService.listarMias(3L).stream()
                .filter(r -> r.getIdProducto().equals(8L)).findFirst().orElseThrow();
        assertEquals(EstadoReserva.CONVERTIDA, reserva.getEstado());
    }

    @Test
    void reservarRespetaElMaximoDeDosActivas() {
        ReservaResponse segunda = reservaService.reservar(3L, 1L);

        assertEquals(EstadoReserva.ACTIVA, segunda.getEstado());
        assertEquals(segunda.getFechaInicio().plusHours(24), segunda.getFechaExpiracion());
        assertEquals(EstadoProducto.RESERVADA, estadoDe(1L));
        assertThrows(ReglaNegocioException.class, () -> reservaService.reservar(3L, 2L));
        assertThrows(ReglaNegocioException.class, () -> reservaService.reservar(5L, 1L));
        assertThrows(ReglaNegocioException.class, () -> reservaService.reservar(5L, 7L));
    }

    @Test
    void soloElDuenoCancelaSuReserva() {
        assertThrows(AccesoDenegadoException.class, () -> reservaService.cancelar(4L, ANTONIA));
        assertEquals(EstadoProducto.RESERVADA, estadoDe(8L));

        reservaService.cancelar(4L, CAMILA);

        assertEquals(EstadoProducto.DISPONIBLE, estadoDe(8L));
        assertThrows(ReglaNegocioException.class, () -> reservaService.cancelar(4L, CAMILA));
    }

    @Test
    void unaReservaVencidaExpiraYLiberaLaCasa() {
        assertEquals(0, reservaService.expirarVencidas());
        jdbcTemplate.update("UPDATE reserva SET fecha_inicio = LOCALTIMESTAMP - INTERVAL '2 days', fecha_expiracion = LOCALTIMESTAMP - INTERVAL '1 day' WHERE id_reserva = 4");

        assertEquals(1, reservaService.expirarVencidas());

        assertEquals(EstadoProducto.DISPONIBLE, estadoDe(8L));
        assertEquals(0, reservaService.expirarVencidas());
    }

    @Test
    void comprarRevisaLaExpiracionAunqueElSchedulerNoHayaPasado() {
        jdbcTemplate.update("UPDATE reserva SET fecha_inicio = LOCALTIMESTAMP - INTERVAL '2 days', fecha_expiracion = LOCALTIMESTAMP - INTERVAL '1 day' WHERE id_reserva = 4");

        BoletaResponse boleta = boletaService.comprar(5L, carrito(8L));

        assertEquals(EstadoBoleta.PAGADA, boleta.getEstado());
        assertEquals(EstadoProducto.VENDIDA, estadoDe(8L));
    }

    @Test
    void anularUnaBoletaDevuelveLaCasaYNoSePuedeRevertir() {
        BoletaResponse anulada = boletaService.cambiarEstado(1L, EstadoBoleta.ANULADA);

        assertEquals(EstadoBoleta.ANULADA, anulada.getEstado());
        assertEquals(EstadoProducto.DISPONIBLE, estadoDe(10L));
        assertThrows(ReglaNegocioException.class, () -> boletaService.cambiarEstado(1L, EstadoBoleta.ANULADA));
        assertThrows(ReglaNegocioException.class, () -> boletaService.cambiarEstado(1L, EstadoBoleta.PAGADA));
    }

    @Test
    void unClienteSoloVeSusBoletas() {
        assertEquals(1, boletaService.obtener(1L, CAMILA).getNumero());
        assertEquals(1, boletaService.obtener(1L, VENDEDOR).getNumero());
        assertThrows(AccesoDenegadoException.class, () -> boletaService.obtener(1L, ANTONIA));
        assertEquals(1, boletaService.listarMias(3L).size());
        assertEquals(4, boletaService.listar().size());
    }

    @Test
    void reglasDeCategorias() {
        assertThrows(ReglaNegocioException.class, () -> categoriaService.eliminar(1));
        categoriaService.eliminar(8);
        assertThrows(RecursoNoEncontradoException.class, () -> categoriaService.obtener(8));
        assertThrows(RecursoDuplicadoException.class,
                () -> categoriaService.crear(CategoriaRequest.builder().nombre("apariciones").build()));
    }

    @Test
    void eliminarUnaCasaLaBorraOLaDesactivaSegunSuHistoria() {
        productoService.eliminar(7L);
        assertEquals(EstadoProducto.INACTIVA, estadoDe(7L));

        assertThrows(ReglaNegocioException.class, () -> productoService.eliminar(8L));

        productoService.eliminar(2L);
        assertThrows(RecursoNoEncontradoException.class, () -> productoService.obtener(2L));
    }

    @Test
    void crearYEditarUnaCasa() {
        ProductoRequest request = ProductoRequest.builder()
                .titulo("Casona de prueba").descripcion("Descripción").precio(new BigDecimal("100000000"))
                .direccion("Calle 1").metrosCuadrados(100).dormitorios(2).banos(1).nivelEmbrujo(3)
                .latitud(new BigDecimal("-33.45")).longitud(new BigDecimal("-70.66")).idComuna(10)
                .idsCategorias(List.of(1, 5)).build();

        ProductoDetalleResponse creada = productoService.crear(request);

        assertEquals(EstadoProducto.DISPONIBLE, creada.getEstado());
        assertEquals(2, creada.getCategorias().size());
        assertEquals("Metropolitana de Santiago", creada.getRegion());

        request.setEstado(EstadoProducto.VENDIDA);
        assertThrows(ReglaNegocioException.class, () -> productoService.actualizar(creada.getIdProducto(), request));
        request.setEstado(EstadoProducto.INACTIVA);
        assertEquals(EstadoProducto.INACTIVA, productoService.actualizar(creada.getIdProducto(), request).getEstado());

        request.setIdsCategorias(List.of(1, 999));
        assertThrows(RecursoNoEncontradoException.class, () -> productoService.crear(request));

        request.setIdsCategorias(List.of(1));
        request.setEstado(EstadoProducto.DISPONIBLE);
        assertThrows(ReglaNegocioException.class, () -> productoService.actualizar(7L, request));
    }

    @Test
    void lecturaDelCatalogoEnSusDosVersiones() {
        assertEquals(12, productoService.buscar(ProductoFiltro.builder().build(), 0, 100).getTotalElementos());
        assertEquals(50, productoService.buscar(ProductoFiltro.builder().build(), 0, 100).getTamano());
        assertEquals(3, productoService.buscar(ProductoFiltro.builder().build(), 0, 5).getTotalPaginas());

        ProductoDetalleResponse cousino = productoService.obtenerDetalle(6L);
        assertEquals(2, cousino.getImagenes().size());
        assertEquals(1, cousino.getMitos().size());
        assertEquals("Espíritu residente", cousino.getFichaParanormal().getTipoEntidad());
        assertTrue(cousino.getLatitud().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    void favoritosSoloDeCasasVisibles() {
        favoritoService.agregar(3L, 2L);
        assertEquals(5, favoritoService.listar(3L).size());
        assertThrows(RecursoNoEncontradoException.class, () -> favoritoService.agregar(3L, 999L));
        assertThrows(RecursoNoEncontradoException.class, () -> favoritoService.agregar(3L, 12L));
        assertThrows(RecursoNoEncontradoException.class, () -> favoritoService.quitar(3L, 9L));
    }
}
