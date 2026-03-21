package com.osen.sistema_reservas.api.admin;

import com.osen.sistema_reservas.core.dashboard.application.service.DashboardService;
import com.osen.sistema_reservas.core.dashboard.application.dtos.DashboardStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para el dashboard de administración.
 * Solo maneja requests/responses, toda la lógica está en DashboardService.
 */
@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    public AdminDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Obtiene las estadísticas del dashboard
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        DashboardStatsResponse stats = dashboardService.obtenerEstadisticas();
        return ResponseEntity.ok(stats);
    }
}
