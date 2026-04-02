package com.hmlmb.rh.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true) // RS deve ser único
    private String rs;

    private Integer pv;

    private String cargo;

    private String regimeJuridico;
}
