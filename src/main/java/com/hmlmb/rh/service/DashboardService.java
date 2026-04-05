package com.hmlmb.rh.service;

import com.hmlmb.rh.repository.FuncionarioRepository;
import com.hmlmb.rh.repository.RequerimentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final FuncionarioRepository funcionarioRepository;
    private final RequerimentoRepository requerimentoRepository;

    public DashboardService(FuncionarioRepository funcionarioRepository, RequerimentoRepository requerimentoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.requerimentoRepository = requerimentoRepository;
    }

    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        long totalFuncionarios = funcionarioRepository.count();
        long totalRequerimentos = requerimentoRepository.count();
        
        // Count active vacations based on current date
        long totalFeriasMes = 0;
        try {
            totalFeriasMes = requerimentoRepository.countFeriasAtivas(LocalDate.now());
        } catch (Exception e) {
            System.err.println("Query falhou: " + e.getMessage());
        }

        // dummy for now, requires native queries or specific logic
        long requerimentosForaPrazo = 0;

        data.put("totalFuncionarios", totalFuncionarios);
        data.put("totalFeriasMes", totalFeriasMes); 
        data.put("totalRequerimentos", totalRequerimentos);
        data.put("requerimentosForaPrazo", requerimentosForaPrazo);
        data.put("departamentos", new String[]{"Geral"});
        data.put("feriasPorDepartamento", new int[]{0});

        return data;
    }
}
