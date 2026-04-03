package com.hmlmb.rh.repository;

import com.hmlmb.rh.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    Optional<Funcionario> findByRs(String rs);

    List<Funcionario> findByNomeContainingIgnoreCase(String nome);
}
