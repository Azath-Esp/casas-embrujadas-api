package cl.duoc.casasembrujadas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import cl.duoc.casasembrujadas.config.SecurityConfig;
import cl.duoc.casasembrujadas.dto.response.BoletaResponse;
import cl.duoc.casasembrujadas.dto.response.DashboardResumenResponse;
import cl.duoc.casasembrujadas.model.EstadoBoleta;
import cl.duoc.casasembrujadas.model.RolNombre;
import cl.duoc.casasembrujadas.security.JwtService;
import cl.duoc.casasembrujadas.security.RespuestaErrorJson;
import cl.duoc.casasembrujadas.security.UsuarioActual;
import cl.duoc.casasembrujadas.security.UsuarioAutenticado;
import cl.duoc.casasembrujadas.service.BoletaService;
import cl.duoc.casasembrujadas.service.DashboardService;
import cl.duoc.casasembrujadas.service.FavoritoService;
import cl.duoc.casasembrujadas.service.ProductoService;
import cl.duoc.casasembrujadas.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {ProductoController.class, BoletaController.class, DashboardController.class,
        UsuarioController.class, FavoritoController.class})
@Import({SecurityConfig.class, RespuestaErrorJson.class})
class PermisosPorRolWebMvcTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserDetailsService userDetailsService;
    @MockitoBean
    private UsuarioActual usuarioActual;
    @MockitoBean
    private ProductoService productoService;
    @MockitoBean
    private BoletaService boletaService;
    @MockitoBean
    private DashboardService dashboardService;
    @MockitoBean
    private UsuarioService usuarioService;
    @MockitoBean
    private FavoritoService favoritoService;

    @ParameterizedTest(name = "GET /api/v1/boletas como {0} responde {1}")
    @CsvSource({"ADMIN, 200", "VENDEDOR, 200", "CLIENTE, 403"})
    void listaDeBoletasSegunRol(String rol, int esperado) throws Exception {
        when(boletaService.listar()).thenReturn(List.of());

        mvc.perform(get("/api/v1/boletas").with(user("prueba").roles(rol)))
                .andExpect(status().is(esperado));
    }

    @ParameterizedTest(name = "DELETE /api/v1/productos/2 como {0} responde {1}")
    @CsvSource({"ADMIN, 204", "VENDEDOR, 403", "CLIENTE, 403"})
    void eliminarCasaSegunRol(String rol, int esperado) throws Exception {
        mvc.perform(delete("/api/v1/productos/2").with(user("prueba").roles(rol)))
                .andExpect(status().is(esperado));
    }

    @Test
    void sinSesionResponde401EnJsonYNoLlegaAlService() throws Exception {
        mvc.perform(delete("/api/v1/productos/2"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.ruta").value("/api/v1/productos/2"));
        verify(productoService, never()).eliminar(any());
    }

    @Test
    void elCatalogoEsPublico() throws Exception {
        mvc.perform(get("/api/v1/productos")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void elVendedorNoAnulaBoletas() throws Exception {
        mvc.perform(put("/api/v1/boletas/1/estado").contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"ANULADA\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje").value("No tienes permiso para realizar esta acción"));
        verify(boletaService, never()).cambiarEstado(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void elAdminAnulaBoletas() throws Exception {
        when(boletaService.cambiarEstado(1L, EstadoBoleta.ANULADA))
                .thenReturn(BoletaResponse.builder().idBoleta(1L).estado(EstadoBoleta.ANULADA).build());

        mvc.perform(put("/api/v1/boletas/1/estado").contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"ANULADA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ANULADA"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void elDashboardYLosUsuariosSonDelAdmin() throws Exception {
        when(dashboardService.resumen()).thenReturn(DashboardResumenResponse.builder().build());
        when(usuarioService.listar()).thenReturn(List.of());

        mvc.perform(get("/api/v1/dashboard/resumen")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/usuarios")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void elClienteNoVeElDashboardNiLosUsuarios() throws Exception {
        mvc.perform(get("/api/v1/dashboard/resumen")).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/usuarios")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void elClienteUsaSusFavoritos() throws Exception {
        when(usuarioActual.obtener()).thenReturn(new UsuarioAutenticado(3L, "cliente@casasembrujadas.cl", RolNombre.CLIENTE));
        when(favoritoService.listar(3L)).thenReturn(List.of());

        mvc.perform(get("/api/v1/favoritos")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void elAdminNoTieneFavoritos() throws Exception {
        mvc.perform(get("/api/v1/favoritos")).andExpect(status().isForbidden());
    }
}
