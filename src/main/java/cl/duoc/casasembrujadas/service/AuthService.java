package cl.duoc.casasembrujadas.service;

import cl.duoc.casasembrujadas.dto.request.LoginRequest;
import cl.duoc.casasembrujadas.dto.request.RegistroRequest;
import cl.duoc.casasembrujadas.dto.response.AuthResponse;
import cl.duoc.casasembrujadas.dto.response.UsuarioResponse;

public interface AuthService {

    UsuarioResponse registrar(RegistroRequest request);

    AuthResponse login(LoginRequest request);
}
