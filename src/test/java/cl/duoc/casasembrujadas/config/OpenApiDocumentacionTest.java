package cl.duoc.casasembrujadas.config;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "DB_URL=jdbc:postgresql://localhost:5432/casas_embrujadas",
        "DB_USERNAME=prueba",
        "DB_PASSWORD=prueba",
        "JWT_SECRET=c2VjcmV0by1zb2xvLXBhcmEtcHJ1ZWJhcy1hdXRvbWF0aWNhcy0xMjM0NTY3ODkw"
})
@AutoConfigureMockMvc
class OpenApiDocumentacionTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void elGrupoV1TieneLosDatosGeneralesYElEsquemaBearer() throws Exception {
        mvc.perform(get("/v3/api-docs/v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("API Casas Embrujadas"))
                .andExpect(jsonPath("$.info.contact.name").value("Dinko Ocampo"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.bearerFormat").value("JWT"))
                .andExpect(jsonPath("$.paths['/api/v2/productos']").doesNotExist());
    }

    @Test
    void lasLecturasDelCatalogoSonPublicasYLasEscriturasProtegidas() throws Exception {
        mvc.perform(get("/v3/api-docs/v1"))
                .andExpect(jsonPath("$.paths['/api/v1/productos'].get.security").doesNotExist())
                .andExpect(jsonPath("$.paths['/api/v1/productos'].post.security[0].bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/api/v1/productos'].post.responses['401']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/boletas/mis-boletas'].get.security[0].bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/api/v1/productos'].get.parameters[*].name").value(hasItem("precioMax")));
    }

    @Test
    void elGrupoV2SoloTieneLaVersion2() throws Exception {
        mvc.perform(get("/v3/api-docs/v2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v2/productos'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/productos']").doesNotExist());
    }
}
