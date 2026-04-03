package com.hmlmb.rh.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        // Mock data for now
        data.put("totalFuncionarios", 150);
        data.put("totalFeriasMes", 12);
        data.put("requerimentosForaPrazo", 5);
        data.put("departamentos", new String[]{"TI", "RH", "Financeiro", "Marketing"});
        data.put("feriasPorDepartamento", new int[]{5, 2, 3, 2});

        return data;
    }
}
