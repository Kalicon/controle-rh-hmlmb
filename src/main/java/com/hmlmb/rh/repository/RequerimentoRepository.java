package com.hmlmb.rh.repository;

import com.hmlmb.rh.model.Requerimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RequerimentoRepository extends JpaRepository<Requerimento, Long> {
    
    // Método mágico do Spring Data: Busca todos ordenados por data de início (mais recentes primeiro)
    List<Requerimento> findByExercicioOrderByDataInicioDesc(Integer exercicio);

    // Consulta para o AutoComplete: Buscar todos os RGs, Nomes, Cargos e PVs cadastrados 
    // Usamos um Set Map ou DTO para não precisar carregar o requerimento inteiro, mas o JPQL facilita.
    // Retorna uma lista de Arrays de Object onde [0] é nome, [1] é cargo, [2] é rs, [3] é pv
    @Query("SELECT DISTINCT r.nome, r.cargo, r.rs, r.pv, r.regimeJuridico FROM Requerimento r WHERE r.nome IS NOT NULL")
    List<Object[]> findDistinctServidores();
}
