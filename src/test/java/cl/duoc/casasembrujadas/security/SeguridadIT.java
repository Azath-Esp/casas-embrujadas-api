package cl.duoc.casasembrujadas.security;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class SeguridadIT {

    @Autowired
    private MockMvc mvc;

    private String login(String correo, String contrasena) throws Exception {
        String json = mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"" + correo + "\",\"contrasena\":\"" + contrasena + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(json, "$.token");
    }

    @Test
    void loginEntregaTokenYMeLoReconoce() throws Exception {
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"Admin@CasasEmbrujadas.cl\",\"contrasena\":\"Admin123!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.rol").value("ADMIN"))
                .andExpect(jsonPath("$.nombre").value("Isidora"))
                .andExpect(jsonPath("$.expiraEn").exists());

        mvc.perform(get("/api/v1/auth/me").header(HttpHeaders.AUTHORIZATION, login("vendedor@casasembrujadas.cl", "Vendedor123!")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("VENDEDOR"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());
    }

    @Test
    void credencialesIncorrectasResponden401() throws Exception {
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@casasembrujadas.cl\",\"contrasena\":\"otraClave1\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Correo o contraseña incorrectos"));
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"nadie@casasembrujadas.cl\",\"contrasena\":\"otraClave1\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Correo o contraseña incorrectos"));
    }

    @Test
    void elRegistroSiempreCreaUnClienteYGuardaLaContrasenaCifrada() throws Exception {
        mvc.perform(post("/api/v1/auth/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"run\":\"12.345.678-5\",\"nombre\":\"Nueva\",\"apellidos\":\"Clienta\","
                                + "\"correo\":\"nueva@casasembrujadas.cl\",\"contrasena\":\"Secreta123\",\"rol\":\"ADMIN\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("CLIENTE"))
                .andExpect(jsonPath("$.run").value("12345678-5"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());

        login("nueva@casasembrujadas.cl", "Secreta123");

        mvc.perform(post("/api/v1/auth/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"run\":\"9.876.543-3\",\"nombre\":\"Otra\",\"apellidos\":\"Persona\","
                                + "\"correo\":\"nueva@casasembrujadas.cl\",\"contrasena\":\"Secreta123\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void sinTokenResponde401EnJson() throws Exception {
        mvc.perform(get("/api/v1/boletas/mis-boletas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.mensaje").value("Debes iniciar sesión para acceder a este recurso"))
                .andExpect(jsonPath("$.ruta").value("/api/v1/boletas/mis-boletas"));
        mvc.perform(get("/api/v1/productos")).andExpect(status().isOk());
    }

    @Test
    void unTokenAlteradoOFalsoResponde401() throws Exception {
        String token = login("cliente@casasembrujadas.cl", "Cliente123!");
        String alterado = token.substring(0, token.length() - 4) + "abcd";
        mvc.perform(get("/api/v1/favoritos").header(HttpHeaders.AUTHORIZATION, alterado))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("El token no es válido"));
    }

    @Test
    void elVendedorSoloLee() throws Exception {
        String vendedor = login("vendedor@casasembrujadas.cl", "Vendedor123!");
        mvc.perform(get("/api/v1/boletas").header(HttpHeaders.AUTHORIZATION, vendedor)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/boletas/1").header(HttpHeaders.AUTHORIZATION, vendedor)).andExpect(status().isOk());
        mvc.perform(delete("/api/v1/productos/2").header(HttpHeaders.AUTHORIZATION, vendedor))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje").value("No tienes permiso para realizar esta acción"));
        mvc.perform(put("/api/v1/boletas/1/estado").header(HttpHeaders.AUTHORIZATION, vendedor)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"ANULADA\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/usuarios").header(HttpHeaders.AUTHORIZATION, vendedor)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/favoritos").header(HttpHeaders.AUTHORIZATION, vendedor)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/usuarios/me").header(HttpHeaders.AUTHORIZATION, vendedor)).andExpect(status().isForbidden());
    }

    @Test
    void elClienteNoEntraAlPanel() throws Exception {
        String cliente = login("cliente@casasembrujadas.cl", "Cliente123!");
        mvc.perform(get("/api/v1/boletas").header(HttpHeaders.AUTHORIZATION, cliente)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/reservas").header(HttpHeaders.AUTHORIZATION, cliente)).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/categorias").header(HttpHeaders.AUTHORIZATION, cliente)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Hackeo\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/boletas/mis-boletas").header(HttpHeaders.AUTHORIZATION, cliente)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/usuarios/me").header(HttpHeaders.AUTHORIZATION, cliente)).andExpect(status().isOk());
    }

    @Test
    void elAdminAdministraUsuariosPeroNoSeQuitaElAcceso() throws Exception {
        String admin = login("admin@casasembrujadas.cl", "Admin123!");
        mvc.perform(get("/api/v1/usuarios").header(HttpHeaders.AUTHORIZATION, admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6));
        mvc.perform(put("/api/v1/usuarios/1").header(HttpHeaders.AUTHORIZATION, admin).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Isidora\",\"apellidos\":\"Vega Rojas\",\"rol\":\"CLIENTE\",\"activo\":true}"))
                .andExpect(status().isConflict());
        mvc.perform(put("/api/v1/usuarios/6").header(HttpHeaders.AUTHORIZATION, admin).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Tomás\",\"apellidos\":\"Fuentealba Lagos\",\"rol\":\"CLIENTE\",\"activo\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"tomas.fuentealba@casasembrujadas.cl\",\"contrasena\":\"Cliente123!\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("La cuenta está desactivada"));
    }

    @Test
    void corsPermiteAlFrontendYExponeLosEncabezados() throws Exception {
        mvc.perform(options("/api/v1/boletas")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Authorization, Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"));
        mvc.perform(get("/api/v1/productos").header(HttpHeaders.ORIGIN, "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, containsString("Content-Disposition")));
        mvc.perform(options("/api/v1/boletas")
                        .header(HttpHeaders.ORIGIN, "http://sitio-malicioso.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isForbidden());
    }
}
