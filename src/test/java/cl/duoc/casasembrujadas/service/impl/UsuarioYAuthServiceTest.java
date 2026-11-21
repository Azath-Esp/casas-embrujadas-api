package cl.duoc.casasembrujadas.service.impl;

import static cl.duoc.casasembrujadas.service.impl.DatosDePrueba.ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import cl.duoc.casasembrujadas.dto.request.LoginRequest;
import cl.duoc.casasembrujadas.dto.request.RegistroRequest;
import cl.duoc.casasembrujadas.dto.request.UsuarioActualizarRequest;
import cl.duoc.casasembrujadas.dto.response.AuthResponse;
import cl.duoc.casasembrujadas.exception.RecursoDuplicadoException;
import cl.duoc.casasembrujadas.exception.ReglaNegocioException;
import cl.duoc.casasembrujadas.mapper.UsuarioMapper;
import cl.duoc.casasembrujadas.model.RolNombre;
import cl.duoc.casasembrujadas.model.Usuario;
import cl.duoc.casasembrujadas.repository.UbicacionRepository;
import cl.duoc.casasembrujadas.repository.UsuarioRepository;
import cl.duoc.casasembrujadas.security.JwtService;
import cl.duoc.casasembrujadas.security.TokenGenerado;
import cl.duoc.casasembrujadas.security.UsuarioDetalles;
import cl.duoc.casasembrujadas.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioYAuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UbicacionRepository ubicacionRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private UsuarioService usuarioServiceSimulado;

    private UsuarioServiceImpl usuarios;
    private AuthServiceImpl auth;

    @BeforeEach
    void preparar() {
        usuarios = new UsuarioServiceImpl(usuarioRepository, ubicacionRepository, new UsuarioMapper(), passwordEncoder);
        auth = new AuthServiceImpl(authenticationManager, jwtService, usuarioServiceSimulado);
    }

    private RegistroRequest registro() {
        return RegistroRequest.builder().run("12.345.678-5").nombre("Nueva").apellidos("Clienta")
                .correo("Nueva@CasasEmbrujadas.cl").contrasena("Secreta123").build();
    }

    @Test
    void elRegistroGuardaElHashYNuncaLaContrasenaPlana() {
        when(passwordEncoder.encode("Secreta123")).thenReturn("$2a$10$hash");
        when(usuarioRepository.insertar(any())).thenReturn(7L);
        when(usuarioRepository.buscarPorId(7L)).thenReturn(Optional.of(Usuario.builder().idUsuario(7L).rol(RolNombre.CLIENTE).build()));

        usuarios.registrar(registro());

        ArgumentCaptor<Usuario> guardado = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).insertar(guardado.capture());
        assertEquals("$2a$10$hash", guardado.getValue().getContrasena());
        assertEquals("nueva@casasembrujadas.cl", guardado.getValue().getCorreo());
        assertEquals("12345678-5", guardado.getValue().getRun());
        assertEquals(RolNombre.CLIENTE, guardado.getValue().getRol());
    }

    @Test
    void noRegistraUnCorreoRepetido() {
        when(usuarioRepository.existePorCorreo("nueva@casasembrujadas.cl")).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class, () -> usuarios.registrar(registro()));
        verify(usuarioRepository, never()).insertar(any());
    }

    @Test
    void elAdministradorNoPuedeQuitarseElRol() {
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(Usuario.builder().idUsuario(1L).rol(RolNombre.ADMIN).build()));
        UsuarioActualizarRequest cambio = UsuarioActualizarRequest.builder().nombre("Isidora").apellidos("Vega")
                .rol(RolNombre.CLIENTE).activo(true).build();

        assertThrows(ReglaNegocioException.class, () -> usuarios.actualizar(1L, cambio, ADMIN));
        verify(usuarioRepository, never()).actualizar(any());
    }

    @Test
    void eliminarUnUsuarioConBoletasLoDesactiva() {
        Usuario camila = Usuario.builder().idUsuario(3L).rol(RolNombre.CLIENTE).activo(true).build();
        when(usuarioRepository.buscarPorId(3L)).thenReturn(Optional.of(camila));
        when(usuarioRepository.tieneBoletas(3L)).thenReturn(true);

        usuarios.eliminar(3L, ADMIN);

        assertFalse(camila.isActivo());
        verify(usuarioRepository).actualizar(camila);
        verify(usuarioRepository, never()).eliminar(any());
    }

    @Test
    void elLoginEntregaUnTokenBearerConElRol() {
        UsuarioDetalles camila = new UsuarioDetalles(Usuario.builder().idUsuario(3L).correo("cliente@casasembrujadas.cl")
                .nombre("Camila").rol(RolNombre.CLIENTE).activo(true).build());
        when(authenticationManager.authenticate(any()))
                .thenReturn(UsernamePasswordAuthenticationToken.authenticated(camila, null, camila.getAuthorities()));
        when(jwtService.generar(camila)).thenReturn(new TokenGenerado("eyJ.token.firma", LocalDateTime.of(2026, 11, 17, 13, 0)));

        AuthResponse respuesta = auth.login(LoginRequest.builder().correo(" Cliente@CasasEmbrujadas.CL ").contrasena("Cliente123!").build());

        assertEquals("eyJ.token.firma", respuesta.getToken());
        assertEquals("Bearer", respuesta.getTipo());
        assertEquals(RolNombre.CLIENTE, respuesta.getRol());
        ArgumentCaptor<UsernamePasswordAuthenticationToken> intento = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(intento.capture());
        assertEquals("cliente@casasembrujadas.cl", intento.getValue().getName());
    }

    @Test
    void unaContrasenaIncorrectaNoEntregaToken() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class,
                () -> auth.login(LoginRequest.builder().correo("cliente@casasembrujadas.cl").contrasena("otra").build()));
        verify(jwtService, never()).generar(any());
    }
}
