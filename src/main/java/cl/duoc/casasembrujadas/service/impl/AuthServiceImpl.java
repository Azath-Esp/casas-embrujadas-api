package cl.duoc.casasembrujadas.service.impl;

import cl.duoc.casasembrujadas.dto.request.LoginRequest;
import cl.duoc.casasembrujadas.dto.request.RegistroRequest;
import cl.duoc.casasembrujadas.dto.response.AuthResponse;
import cl.duoc.casasembrujadas.dto.response.UsuarioResponse;
import cl.duoc.casasembrujadas.security.JwtService;
import cl.duoc.casasembrujadas.security.TokenGenerado;
import cl.duoc.casasembrujadas.security.UsuarioDetalles;
import cl.duoc.casasembrujadas.service.AuthService;
import cl.duoc.casasembrujadas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    @Override
    public UsuarioResponse registrar(RegistroRequest request) {
        return usuarioService.registrar(request);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication autenticacion = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.getCorreo().trim().toLowerCase(), request.getContrasena()));
        UsuarioDetalles usuario = (UsuarioDetalles) autenticacion.getPrincipal();
        TokenGenerado token = jwtService.generar(usuario);
        return AuthResponse.builder()
                .token(token.token())
                .tipo("Bearer")
                .expiraEn(token.expiraEn())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol())
                .build();
    }
}
