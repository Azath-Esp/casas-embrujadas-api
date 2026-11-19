package cl.duoc.casasembrujadas.security;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey clave;
    private final Duration duracion;
    private final Clock clock;

    public JwtService(@Value("${app.jwt.secret}") String secretoBase64,
                      @Value("${app.jwt.expiration-minutes}") long minutos,
                      Clock clock) {
        this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretoBase64));
        this.duracion = Duration.ofMinutes(minutos);
        this.clock = clock;
    }

    public TokenGenerado generar(UsuarioDetalles usuario) {
        Instant ahora = clock.instant().truncatedTo(ChronoUnit.SECONDS);
        Instant vence = ahora.plus(duracion);
        String token = Jwts.builder()
                .subject(usuario.getUsername())
                .claim("id", usuario.getIdUsuario())
                .claim("nombre", usuario.getNombre())
                .claim("rol", usuario.getRol().name())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(vence))
                .signWith(clave)
                .compact();
        return new TokenGenerado(token, LocalDateTime.ofInstant(vence, clock.getZone()));
    }

    public Claims validar(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .clock(() -> Date.from(clock.instant()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extraerCorreo(String token) {
        return validar(token).getSubject();
    }
}
