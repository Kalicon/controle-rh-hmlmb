package com.hmlmb.rh.service;

import com.hmlmb.rh.model.Requerimento;
import com.hmlmb.rh.repository.RequerimentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequerimentoService {

    private final RequerimentoRepository repository;

    public RequerimentoService(RequerimentoRepository repository) {
        this.repository = repository;
    }

    public List<Requerimento> listarTodos() {
        return repository.findAll();
    }

    public List<Requerimento> listarPorExercicioE(Integer exercicio, Integer mes) {
        if (exercicio == null) {
            exercicio = LocalDate.now().getYear(); // Por padrão, lista o ano atual
        }
        
        List<Requerimento> todosDoAno = repository.findByExercicioOrderByDataInicioDesc(exercicio);

        // Se passar um mês, filtra no Java
        if (mes != null && mes >= 1 && mes <= 12) {
             return todosDoAno.stream()
                .filter(req -> req.getDataInicio() != null && req.getDataInicio().getMonthValue() == mes)
                .collect(Collectors.toList());
        }

        return todosDoAno;
    }

    public Requerimento salvar(Requerimento requerimento) {
        if (requerimento.getDataInicio() != null && requerimento.getQuantidadeDias() != null) {
            requerimento.setDataTermino(requerimento.getDataInicio().plusDays(requerimento.getQuantidadeDias() - 1));
        }
        return repository.save(requerimento);
    }
}
