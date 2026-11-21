package cl.duoc.casasembrujadas.config;

import java.util.Arrays;
import java.util.List;

import cl.duoc.casasembrujadas.security.JwtAuthenticationFilter;
import cl.duoc.casasembrujadas.security.JwtService;
import cl.duoc.casasembrujadas.security.RespuestaErrorJson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String VENDEDOR = "VENDEDOR";
    private static final String CLIENTE = "CLIENTE";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RespuestaErrorJson respuestaErrorJson;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(errores -> errores
                        .authenticationEntryPoint((request, response, ex) -> respuestaErrorJson.escribir(request, response,
                                HttpStatus.UNAUTHORIZED, mensajeNoAutenticado(request)))
                        .accessDeniedHandler((request, response, ex) -> respuestaErrorJson.escribir(request, response,
                                HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta acción")))
                .authorizeHttpRequests(reglas -> reglas
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/registro").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**", "/api/v2/productos/**",
                                "/api/v1/categorias/**", "/api/v1/regiones/**").permitAll()
                        .requestMatchers("/api/v1/auth/me").authenticated()
                        .requestMatchers("/api/v1/usuarios/me").hasAnyRole(ADMIN, CLIENTE)
                        .requestMatchers("/api/v1/usuarios/**", "/api/v1/dashboard/**").hasRole(ADMIN)
                        .requestMatchers("/api/v1/favoritos/**").hasRole(CLIENTE)
                        .requestMatchers(HttpMethod.POST, "/api/v1/reservas").hasRole(CLIENTE)
                        .requestMatchers(HttpMethod.GET, "/api/v1/reservas/mis-reservas").hasRole(CLIENTE)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/reservas/*").hasRole(CLIENTE)
                        .requestMatchers(HttpMethod.GET, "/api/v1/reservas").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/v1/boletas").hasRole(CLIENTE)
                        .requestMatchers(HttpMethod.GET, "/api/v1/boletas/mis-boletas").hasRole(CLIENTE)
                        .requestMatchers(HttpMethod.GET, "/api/v1/boletas/*/pdf").hasAnyRole(ADMIN, VENDEDOR, CLIENTE)
                        .requestMatchers(HttpMethod.GET, "/api/v1/boletas", "/api/v1/boletas/*").hasAnyRole(ADMIN, VENDEDOR)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/boletas/*/estado").hasRole(ADMIN)
                        .requestMatchers("/api/v1/productos/**", "/api/v1/categorias/**").hasRole(ADMIN)
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private String mensajeNoAutenticado(jakarta.servlet.http.HttpServletRequest request) {
        Object motivo = request.getAttribute(JwtAuthenticationFilter.ATRIBUTO_ERROR);
        return motivo != null ? motivo.toString() : "Debes iniciar sesión para acceder a este recurso";
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider proveedor = new DaoAuthenticationProvider(userDetailsService);
        proveedor.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(proveedor);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origins}") String origenes) {
        CorsConfiguration configuracion = new CorsConfiguration();
        configuracion.setAllowedOrigins(Arrays.stream(origenes.split(","))
                .map(String::trim)
                .filter(origen -> !origen.isEmpty())
                .toList());
        configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuracion.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuracion.setExposedHeaders(List.of("Location", "Content-Disposition"));
        configuracion.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", configuracion);
        return fuente;
    }
}
