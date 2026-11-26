package cl.duoc.casasembrujadas.scheduler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.casasembrujadas.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

@ExtendWith(MockitoExtension.class)
class ReservaSchedulerTest {

    @Mock
    private ReservaService reservaService;

    @InjectMocks
    private ReservaScheduler scheduler;

    @Test
    void cadaPasadaExpiraLasReservasVencidas() {
        when(reservaService.expirarVencidas()).thenReturn(2);

        scheduler.expirarReservasVencidas();

        verify(reservaService).expirarVencidas();
    }

    @Test
    void siLaBaseFallaLaTareaNoSeCae() {
        when(reservaService.expirarVencidas()).thenThrow(new DataAccessResourceFailureException("Base de datos apagada"));

        assertDoesNotThrow(() -> scheduler.expirarReservasVencidas());
    }
}
