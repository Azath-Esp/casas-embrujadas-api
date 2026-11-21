package cl.duoc.casasembrujadas.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.casasembrujadas.security.UsuarioActualProvisional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ControllersIT {

    private static final String USUARIO = UsuarioActualProvisional.ENCABEZADO;

    @Autowired
    private MockMvc mvc;

    @Test
    void catalogoConFiltrosYPaginacion() throws Exception {
        mvc.perform(get("/api/v1/productos").param("region", "7").param("tamano", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido.length()").value(5))
                .andExpect(jsonPath("$.totalElementos").value(6))
                .andExpect(jsonPath("$.totalPaginas").value(2));
    }

    @Test
    void detalleEnLasDosVersiones() throws Exception {
        mvc.perform(get("/api/v1/productos/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Palacio Cousiño"))
                .andExpect(jsonPath("$.imagenes").doesNotExist());
        mvc.perform(get("/api/v2/productos/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imagenes.length()").value(2))
                .andExpect(jsonPath("$.fichaParanormal.tipoEntidad").value("Espíritu residente"));
    }

    @Test
    void erroresConFormatoUnico() throws Exception {
        mvc.perform(get("/api/v1/productos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.mensaje").value("No existe la casa con id 999"))
                .andExpect(jsonPath("$.ruta").value("/api/v1/productos/999"))
                .andExpect(jsonPath("$.timestamp").exists());
        mvc.perform(get("/api/v1/productos/abc"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/productos").param("estado", "FANTASMA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores[0]").value("estado: el valor 'FANTASMA' no tiene un formato válido"));
        mvc.perform(get("/api/v1/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe la ruta /api/v1/no-existe"));
        mvc.perform(post("/api/v1/categorias").contentType(MediaType.APPLICATION_JSON).content("{ esto no es json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validacionDelCuerpo() throws Exception {
        mvc.perform(post("/api/v1/categorias").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("La solicitud tiene datos inválidos"))
                .andExpect(jsonPath("$.errores").value(hasItem("nombre: El nombre es obligatorio")));
    }

    @Test
    void crudDeCategoriasConCodigosCorrectos() throws Exception {
        mvc.perform(post("/api/v1/categorias").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Faros y fuertes 2\",\"descripcion\":\"Prueba\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern(".*/api/v1/categorias/\\d+")))
                .andExpect(jsonPath("$.nombre").value("Faros y fuertes 2"));
        mvc.perform(post("/api/v1/categorias").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Apariciones\"}"))
                .andExpect(status().isConflict());
        mvc.perform(delete("/api/v1/categorias/1"))
                .andExpect(status().isConflict());
        mvc.perform(delete("/api/v1/categorias/8"))
                .andExpect(status().isNoContent());
    }

    @Test
    void compraDeUnCliente() throws Exception {
        mvc.perform(post("/api/v1/boletas").header(USUARIO, "5").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idsProductos\":[1,2]}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern(".*/api/v1/boletas/\\d+")))
                .andExpect(jsonPath("$.numero").value(5))
                .andExpect(jsonPath("$.total").value(30642500000L))
                .andExpect(jsonPath("$.detalles.length()").value(2));
        mvc.perform(post("/api/v1/boletas").header(USUARIO, "5").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idsProductos\":[1]}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("La casa \"Casona Dubois\" ya fue vendida"));
        mvc.perform(post("/api/v1/boletas").header(USUARIO, "5").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idsProductos\":[]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void duenoDeLaBoleta() throws Exception {
        mvc.perform(get("/api/v1/boletas/1").header(USUARIO, "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteRun").value("33333333-3"));
        mvc.perform(get("/api/v1/boletas/1").header(USUARIO, "5"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje").value("Esta boleta pertenece a otro cliente"));
        mvc.perform(get("/api/v1/boletas/1"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/boletas/mis-boletas").header(USUARIO, "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void reservasYFavoritos() throws Exception {
        mvc.perform(post("/api/v1/reservas").header(USUARIO, "3").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idProducto\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
        mvc.perform(post("/api/v1/reservas").header(USUARIO, "3").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idProducto\":2}"))
                .andExpect(status().isConflict());
        mvc.perform(delete("/api/v1/reservas/4").header(USUARIO, "5"))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/v1/reservas/4").header(USUARIO, "3"))
                .andExpect(status().isNoContent());

        mvc.perform(post("/api/v1/favoritos/2").header(USUARIO, "3"))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/favoritos").header(USUARIO, "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void ubicacionYContenido() throws Exception {
        mvc.perform(get("/api/v1/regiones")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(16));
        mvc.perform(get("/api/v1/regiones/7/comunas")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(9));
        mvc.perform(get("/api/v1/regiones/99/comunas")).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/productos/5/ficha-paranormal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoEntidad").value("Brujos de la Recta Provincia"));
        mvc.perform(post("/api/v1/productos/5/mitos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Prueba\",\"relato\":\"Relato\",\"fuente\":\"https://ejemplo.cl\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
        mvc.perform(delete("/api/v1/productos/5/imagenes/9"))
                .andExpect(status().isConflict());
        mvc.perform(put("/api/v1/boletas/1/estado").contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"ANULADA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ANULADA"));
    }
}