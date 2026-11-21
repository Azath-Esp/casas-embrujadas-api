package cl.duoc.casasembrujadas.controller;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ExtrasIT {

    @Autowired
    private MockMvc mvc;

    private String token(String correo, String contrasena) throws Exception {
        String json = mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"" + correo + "\",\"contrasena\":\"" + contrasena + "\"}"))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(json, "$.token");
    }

    @Test
    void dashboardCompletoParaElAdmin() throws Exception {
        mvc.perform(get("/api/v1/dashboard/resumen").header(HttpHeaders.AUTHORIZATION, token("admin@casasembrujadas.cl", "Admin123!")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ventasPorMes.length()").value(12))
                .andExpect(jsonPath("$.ventasTotales").value(3))
                .andExpect(jsonPath("$.ingresosTotales").value(12358150000L))
                .andExpect(jsonPath("$.casasPorEstado[*].etiqueta").value(contains("DISPONIBLE", "RESERVADA", "VENDIDA", "INACTIVA")))
                .andExpect(jsonPath("$.casasPorEstado[*].cantidad").value(contains(7, 1, 4, 1)))
                .andExpect(jsonPath("$.reservasActivas").value(1))
                .andExpect(jsonPath("$.topFavoritos[0].etiqueta").value("Palacio Cousiño"))
                .andExpect(jsonPath("$.topFavoritos[0].cantidad").value(4))
                .andExpect(jsonPath("$.categoriasMasVendidas[0].etiqueta").value("Apariciones"));
    }

    @Test
    void elDashboardEsSoloDelAdmin() throws Exception {
        mvc.perform(get("/api/v1/dashboard/resumen").header(HttpHeaders.AUTHORIZATION, token("vendedor@casasembrujadas.cl", "Vendedor123!")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/dashboard/resumen")).andExpect(status().isUnauthorized());
    }

    @Test
    void mapaPublicoConFiltroPorRegion() throws Exception {
        mvc.perform(get("/api/v1/productos/mapa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(12))
                .andExpect(jsonPath("$[0].latitud").exists());
        mvc.perform(get("/api/v1/productos/mapa").param("region", "2"))
                .andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/v1/productos/mapa").param("region", "99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void comparadorConMejoresValores() throws Exception {
        mvc.perform(get("/api/v1/productos/comparar").param("ids", "6,1,4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.casas[*].idProducto").value(contains(6, 1, 4)))
                .andExpect(jsonPath("$.casas[0].tipoEntidad").value("Espíritu residente"))
                .andExpect(jsonPath("$.casas[1].cantidadMitos").value(2))
                .andExpect(jsonPath("$.mejores.precio").value(contains(1)))
                .andExpect(jsonPath("$.mejores.metrosCuadrados").value(contains(4)))
                .andExpect(jsonPath("$.mejores.nivelEmbrujo").value(contains(1)))
                .andExpect(jsonPath("$.mejores.cantidadMitos").value(contains(1)));
        mvc.perform(get("/api/v1/productos/comparar").param("ids", "1")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/productos/comparar").param("ids", "1,2,3,4"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores[0]").value("ids: Se pueden comparar entre 2 y 3 casas"));
        mvc.perform(get("/api/v1/productos/comparar").param("ids", "1,1")).andExpect(status().isConflict());
        mvc.perform(get("/api/v1/productos/comparar").param("ids", "1,12")).andExpect(status().isNotFound());
    }

    @Test
    void pdfDeLaBoletaSegunQuienLaPide() throws Exception {
        mvc.perform(get("/api/v1/boletas/1/pdf").header(HttpHeaders.AUTHORIZATION, token("cliente@casasembrujadas.cl", "Cliente123!")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"boleta-000001.pdf\""));
        mvc.perform(get("/api/v1/boletas/1/pdf").header(HttpHeaders.AUTHORIZATION, token("antonia.herrera@casasembrujadas.cl", "Cliente123!")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje").value("Esta boleta pertenece a otro cliente"));
        mvc.perform(get("/api/v1/boletas/1/pdf").header(HttpHeaders.AUTHORIZATION, token("vendedor@casasembrujadas.cl", "Vendedor123!")))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/boletas/1/pdf")).andExpect(status().isUnauthorized());
    }
}
