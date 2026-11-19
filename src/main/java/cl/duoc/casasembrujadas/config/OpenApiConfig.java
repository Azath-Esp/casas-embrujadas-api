package cl.duoc.casasembrujadas.config;

import java.util.List;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String ESQUEMA_JWT = "bearerAuth";

    private static final List<String> LECTURAS_PUBLICAS = List.of(
            "/api/v1/productos",
            "/api/v2/productos",
            "/api/v1/categorias",
            "/api/v1/regiones");

    private static final List<String> ESCRITURAS_PUBLICAS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/registro");

    @Bean
    public OpenAPI apiCasasEmbrujadas() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Casas Embrujadas")
                        .version("1.0.0")
                        .description("API REST de una tienda ficticia que \"vende\" lugares embrujados reales de Chile. "
                                + "Proyecto académico de DSY1104 Desarrollo Full Stack II, Duoc UC. "
                                + "Los lugares y leyendas son reales; las ventas no.")
                        .contact(new Contact().name("Dinko Ocampo"))
                        .license(new License().name("Uso académico")))
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA_JWT, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Pega aquí el token que entrega POST /api/v1/auth/login, sin la palabra Bearer.")));
    }

    @Bean
    public GroupedOpenApi grupoV1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .displayName("API v1")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public GroupedOpenApi grupoV2() {
        return GroupedOpenApi.builder()
                .group("v2")
                .displayName("API v2")
                .pathsToMatch("/api/v2/**")
                .build();
    }

    @Bean
    public GlobalOpenApiCustomizer marcarOperacionesProtegidas() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }
            openApi.getPaths().forEach((ruta, item) -> item.readOperationsMap().forEach((metodo, operacion) -> {
                if (!esPublica(ruta, metodo)) {
                    proteger(operacion);
                }
            }));
        };
    }

    private boolean esPublica(String ruta, PathItem.HttpMethod metodo) {
        if (metodo == PathItem.HttpMethod.GET) {
            return LECTURAS_PUBLICAS.stream().anyMatch(ruta::startsWith);
        }
        return ESCRITURAS_PUBLICAS.contains(ruta);
    }

    private void proteger(Operation operacion) {
        operacion.addSecurityItem(new SecurityRequirement().addList(ESQUEMA_JWT));
        operacion.getResponses().putIfAbsent("401", respuestaDeError("Falta el token o no es válido"));
        operacion.getResponses().putIfAbsent("403", respuestaDeError("El rol del usuario no tiene permiso"));
    }

    private ApiResponse respuestaDeError(String descripcion) {
        return new ApiResponse()
                .description(descripcion)
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))));
    }
}
