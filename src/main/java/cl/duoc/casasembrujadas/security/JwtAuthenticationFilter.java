package cl.duoc.casasembrujadas.security;

import java.io.IOException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String ATRIBUTO_ERROR = "casasembrujadas.jwt.error";
    private static final String PREFIJO = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String encabezado = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (encabezado != null && encabezado.startsWith(PREFIJO)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            autenticar(encabezado.substring(PREFIJO.length()), request);
        }
        chain.doFilter(request, response);
    }

    private void autenticar(String token, HttpServletRequest request) {
        try {
            UserDetails usuario = userDetailsService.loadUserByUsername(jwtService.extraerCorreo(token));
            if (!usuario.isEnabled()) {
                request.setAttribute(ATRIBUTO_ERROR, "La cuenta está desactivada");
                return;
            }
            UsernamePasswordAuthenticationToken autenticacion =
                    UsernamePasswordAuthenticationToken.authenticated(usuario, null, usuario.getAuthorities());
            autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(autenticacion);
        } catch (ExpiredJwtException ex) {
            request.setAttribute(ATRIBUTO_ERROR, "El token expiró; inicia sesión nuevamente");
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException ex) {
            request.setAttribute(ATRIBUTO_ERROR, "El token no es válido");
        }
    }
}
