package com.hmlmb.rh.controller;

import com.hmlmb.rh.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String getDashboard(Model model) {
        Map<String, Object> dashboardData = dashboardService.getDashboardData();
        model.addAllAttributes(dashboardData);
        
        // Adiciona as variáveis para o novo layout
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("activePage", "dashboard");

        return "dashboard";
    }
}
