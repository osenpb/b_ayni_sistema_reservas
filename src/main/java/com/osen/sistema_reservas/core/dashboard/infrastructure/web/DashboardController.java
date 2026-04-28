package com.osen.sistema_reservas.core.dashboard.infrastructure.web;

import com.osen.sistema_reservas.core.dashboard.application.service.DashboardService;
import com.osen.sistema_reservas.core.dashboard.application.dtos.DashboardStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/admin/dashboard", "/api/admin/dashboard"})
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        DashboardStatsResponse stats = dashboardService.obtenerEstadisticas();
        return ResponseEntity.ok(stats);
    }
}
