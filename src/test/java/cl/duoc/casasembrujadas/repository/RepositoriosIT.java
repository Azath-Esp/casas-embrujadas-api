package cl.duoc.casasembrujadas.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import cl.duoc.casasembrujadas.dto.response.EtiquetaCantidadResponse;
import cl.duoc.casasembrujadas.model.Boleta;
import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.EstadoReserva;
import cl.duoc.casasembrujadas.model.Producto;
import cl.duoc.casasembrujadas.model.Reserva;
import cl.duoc.casasembrujadas.model.RolNombre;
import cl.duoc.casasembrujadas.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class RepositoriosIT {

    @Autowired
    private UbicacionRepository ubicacionRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private ContenidoRepository contenidoRepository;
    @Autowired
    private FavoritoRepository favoritoRepository;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private BoletaRepository boletaRepository;
    @Autowired
    private DashboardRepository dashboardRepository;

    @Test
    void ubicacionYCategorias() {
        assertEquals(16, ubicacionRepository.listarRegiones().size());
        assertEquals(9, ubicacionRepository.listarComunasPorRegion(7).size());
        assertEquals("Los Lagos", ubicacionRepository.buscarComunaPorId(27).orElseThrow().getRegionNombre());
        assertEquals(8, categoriaRepository.listar().size());
        assertEquals(0, categoriaRepository.contarProductos(8));
        assertTrue(categoriaRepository.existePorNombre("apariciones", null));
        assertFalse(categoriaRepository.existePorNombre("Apariciones", 5));
        assertEquals(2, categoriaRepository.contarPorIds(List.of(1, 5, 999)));
    }

    @Test
    void usuarioSeLeeConSuRolYSeInserta() {
        Usuario admin = usuarioRepository.buscarPorCorreo("admin@casasembrujadas.cl").orElseThrow();
        assertEquals(RolNombre.ADMIN, admin.getRol());
        assertEquals("Santiago", admin.getComunaNombre());
        assertTrue(admin.getContrasena().startsWith("$2a$"));

        Long id = usuarioRepository.insertar(Usuario.builder()
                .run("12345678-5").nombre("Prueba").apellidos("Temporal").correo("prueba@casasembrujadas.cl")
                .contrasena("hash").rol(RolNombre.CLIENTE).activo(true).build());
        Usuario nuevo = usuarioRepository.buscarPorId(id).orElseThrow();
        assertEquals(RolNombre.CLIENTE, nuevo.getRol());
        assertNotNull(nuevo.getFechaRegistro());
        assertTrue(usuarioRepository.existePorRun("12345678-5"));
    }

    @Test
    void elRunDuplicadoLanzaDuplicateKeyException() {
        assertThrows(DuplicateKeyException.class, () -> usuarioRepository.insertar(Usuario.builder()
                .run("33333333-3").nombre("Otra").apellidos("Persona").correo("otra@casasembrujadas.cl")
                .contrasena("hash").rol(RolNombre.CLIENTE).activo(true).build()));
    }

    @Test
    void catalogoConFiltrosYPaginacion() {
        ProductoFiltro sinFiltro = ProductoFiltro.builder().build();
        assertEquals(12, productoRepository.contar(sinFiltro));
        assertEquals(13, productoRepository.contar(ProductoFiltro.builder().incluirInactivas(true).build()));
        assertEquals(5, productoRepository.buscar(sinFiltro, 0, 5).size());
        assertEquals(2, productoRepository.buscar(sinFiltro, 2, 5).size());
        assertEquals(7, productoRepository.contar(ProductoFiltro.builder().estado(EstadoProducto.DISPONIBLE).build()));
        assertEquals(6, productoRepository.contar(ProductoFiltro.builder().idRegion(7).build()));
        assertEquals(2, productoRepository.contar(ProductoFiltro.builder().idCategoria(3).build()));
        assertEquals(2, productoRepository.contar(ProductoFiltro.builder().precioMax(new BigDecimal("500000000")).build()));

        List<Producto> porTexto = productoRepository.buscar(ProductoFiltro.builder().texto("quicavi").build(), 0, 10);
        assertEquals(1, porTexto.size());
        assertEquals("Cueva de Quicaví", porTexto.get(0).getTitulo());
        assertEquals("Los Lagos", porTexto.get(0).getRegionNombre());
        assertEquals("/img/placeholder-casa.svg", porTexto.get(0).getImagenPrincipal());
    }

    @Test
    void contenidoDeUnaCasa() {
        assertEquals(2, contenidoRepository.listarImagenes(6L).size());
        assertEquals(2, contenidoRepository.listarMitos(1L).size());
        assertEquals("Espíritu residente", contenidoRepository.buscarFicha(6L).orElseThrow().getTipoEntidad());
        assertEquals(2, categoriaRepository.listarPorProducto(6L).size());
        assertEquals(12, productoRepository.listarParaMapa(null).size());
        assertEquals(2, productoRepository.listarParaMapa(2).size());
    }

    @Test
    void laBaseImpideBorrarUnaCasaConBoleta() {
        assertTrue(productoRepository.tieneBoletas(7L));
        assertThrows(DataIntegrityViolationException.class, () -> productoRepository.eliminar(7L));
    }

    @Test
    void favoritosYReservas() {
        assertEquals(4, favoritoRepository.listarProductos(3L).size());
        favoritoRepository.agregar(3L, 2L);
        favoritoRepository.agregar(3L, 2L);
        assertEquals(5, favoritoRepository.listarProductos(3L).size());
        assertTrue(favoritoRepository.quitar(3L, 2L));

        assertEquals(1, reservaRepository.contarActivasPorUsuario(3L));
        Reserva activa = reservaRepository.buscarActivaPorProducto(8L).orElseThrow();
        assertEquals("Teatro Municipal de Santiago", activa.getProductoTitulo());
        assertEquals(0, reservaRepository.listarActivasVencidas(LocalDateTime.now()).size());
        assertEquals(1, reservaRepository.listarActivasVencidas(LocalDateTime.now().plusDays(2)).size());

        assertThrows(DuplicateKeyException.class, () -> reservaRepository.insertar(Reserva.builder()
                .idProducto(8L).idUsuario(5L).fechaInicio(LocalDateTime.now())
                .fechaExpiracion(LocalDateTime.now().plusHours(24)).estado(EstadoReserva.ACTIVA).build()));
    }

    @Test
    void boletasConSusDetalles() {
        assertEquals(5, boletaRepository.siguienteNumero());
        List<Boleta> todas = boletaRepository.listar();
        assertEquals(4, todas.size());
        Boleta segunda = boletaRepository.buscarPorId(2L).orElseThrow();
        assertEquals(2, segunda.getDetalles().size());
        assertEquals(new BigDecimal("4855200000"), segunda.getTotal());
        assertEquals("Tomás Fuentealba Lagos", segunda.getClienteNombre());
        assertEquals(1, boletaRepository.listarPorUsuario(3L).size());
    }

    @Test
    void consultasDelDashboard() {
        assertEquals(3, dashboardRepository.ventasPorMes(LocalDateTime.now().minusMonths(12)).size());
        assertEquals(new BigDecimal("12358150000"), dashboardRepository.ingresosTotales());
        assertEquals(4, dashboardRepository.productosPorEstado().size());
        assertEquals(1, dashboardRepository.contarReservasActivas());
        List<EtiquetaCantidadResponse> top = dashboardRepository.topFavoritos(5);
        assertEquals("Palacio Cousiño", top.get(0).getEtiqueta());
        assertEquals(4, top.get(0).getCantidad());
        assertFalse(dashboardRepository.categoriasMasVendidas(5).isEmpty());
    }
}
