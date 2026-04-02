package com.hmlmb.rh.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * Data Transfer Object: Usado apenas para transitar dados entre o Python e o Java.
 * Impede que erros sujem a nossa entidade principal.
 */
@Data
public class RequerimentoDTO {
    
    // Campo exclusivo de erro. O Python envia isso caso algo quebre.
    private String erro;
    
    // Os campos que o Python extrai
    private Integer exercicio; 
    private String nome;
    private String rs;
    private Integer pv;
    private String cargo;
    private String regimeJuridico; 
    private LocalDate dataInicio;
    private Integer quantidadeDias;
    private String observacoes;
}
