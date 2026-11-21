package cl.duoc.casasembrujadas.security;

import java.time.LocalDateTime;

public record TokenGenerado(String token, LocalDateTime expiraEn) {
}
