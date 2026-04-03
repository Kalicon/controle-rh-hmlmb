package com.hmlmb.rh.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ferias")
public class FeriasController {

    // Você injetaria um serviço aqui para lidar com a lógica de negócios
    // private final FeriasService feriasService;
    // public FeriasController(FeriasService feriasService) {
    //     this.feriasService = feriasService;
    // }

    @PostMapping
    public ResponseEntity<String> receberDadosFerias(@RequestBody FeriasDataDTO feriasData) {
        System.out.println("Dados de férias recebidos:");
        System.out.println("Funcionário: " + feriasData.getNomeFuncionario());
        System.out.println("Início: " + feriasData.getDataInicioFerias());
        System.out.println("Fim: " + feriasData.getDataFimFerias());
        System.out.println("Observações: " + feriasData.getObservacoes());
        System.out.println("Validado: " + feriasData.isValidado());

        // Aqui você chamaria um serviço para salvar os dados no banco de dados
        // feriasService.salvarFerias(feriasData);

        return ResponseEntity.ok("Dados de férias recebidos com sucesso!");
    }

    // Classe interna para DTO - pode ser movida para um arquivo separado mais tarde
    public static class FeriasDataDTO {
        private String nomeFuncionario;
        private String dataInicioFerias;
        private String dataFimFerias;
        private String observacoes;
        private boolean validado;

        // Getters e Setters
        public String getNomeFuncionario() {
            return nomeFuncionario;
        }

        public void setNomeFuncionario(String nomeFuncionario) {
            this.nomeFuncionario = nomeFuncionario;
        }

        public String getDataInicioFerias() {
            return dataInicioFerias;
        }

        public void setDataInicioFerias(String dataInicioFerias) {
            this.dataInicioFerias = dataInicioFerias;
        }

        public String getDataFimFerias() {
            return dataFimFerias;
        }

        public void setDataFimFerias(String dataFimFerias) {
            this.dataFimFerias = dataFimFerias;
        }

        public String getObservacoes() {
            return observacoes;
        }

        public void setObservacoes(String observacoes) {
            this.observacoes = observacoes;
        }

        public boolean isValidado() {
            return validado;
        }

        public void setValidado(boolean validado) {
            this.validado = validado;
        }
    }
}