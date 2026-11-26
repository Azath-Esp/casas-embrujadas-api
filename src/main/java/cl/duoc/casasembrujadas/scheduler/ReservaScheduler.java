package cl.duoc.casasembrujadas.scheduler;

import cl.duoc.casasembrujadas.service.ReservaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaScheduler {

    private final ReservaService reservaService;

    @Scheduled(
            initialDelayString = "${app.reservas.espera-inicial-ms:30000}",
            fixedDelayString = "${app.reservas.intervalo-expiracion-ms:60000}")
    public void expirarReservasVencidas() {
        try {
            int expiradas = reservaService.expirarVencidas();
            if (expiradas > 0) {
                log.info("Reservas expiradas en esta pasada: {}", expiradas);
            }
        } catch (RuntimeException ex) {
            log.error("No se pudo revisar las reservas vencidas: {}", ex.getMessage());
        }
    }
}
