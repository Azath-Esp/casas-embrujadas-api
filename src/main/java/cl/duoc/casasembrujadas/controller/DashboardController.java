package cl.duoc.casasembrujadas.controller;

import cl.duoc.casasembrujadas.dto.response.DashboardResumenResponse;
import cl.duoc.casasembrujadas.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Dashboard", description = "Métricas de ventas, casas, reservas y favoritos")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/resumen")
    @Operation(summary = "Resumen para el dashboard", description = "Solo ADMIN. Ventas e ingresos de los últimos 12 meses, "
            + "ingresos totales, casas por estado, categorías más vendidas, reservas activas y casas más guardadas.")
    public DashboardResumenResponse resumen() {
        return dashboardService.resumen();
    }
}
