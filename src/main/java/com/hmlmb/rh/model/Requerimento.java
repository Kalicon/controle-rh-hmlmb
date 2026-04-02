package com.hmlmb.rh.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Requerimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Controle de Ano/Exercício ---
    @Column(nullable = false)
    private Integer exercicio; // Ex: 2023, 2024, 2025

    // --- Dados Identificadores ---
    private String nome;
    private String rs;
    private Integer pv;
    private String cargo;
    private String regimeJuridico; 

    // --- Controle de Datas ---
    private LocalDate dataEntrada; 
    private Integer quantidadeDias;
    private LocalDate dataInicio;
    private LocalDate dataTermino;

    @Column(length = 1000)
    private String observacoes;

    @PrePersist
    @PreUpdate
    public void calcularDatas() {
        if (this.dataInicio != null && this.quantidadeDias != null) {
            this.dataTermino = this.dataInicio.plusDays(this.quantidadeDias - 1);
        }

        if (this.dataEntrada == null) {
            this.dataEntrada = LocalDate.now(); 
        }

        // Se o exercício não for preenchido, assume o ano da data de início ou o ano atual
        if (this.exercicio == null) {
            if (this.dataInicio != null) {
                this.exercicio = this.dataInicio.getYear();
            } else {
                this.exercicio = LocalDate.now().getYear();
            }
        }
    }
}
