package cl.duoc.casasembrujadas.security;

import cl.duoc.casasembrujadas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioDetallesService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) {
        return usuarioRepository.buscarPorCorreo(correo.trim().toLowerCase())
                .map(UsuarioDetalles::new)
                .orElseThrow(() -> new UsernameNotFoundException("No existe un usuario con ese correo"));
    }
}
