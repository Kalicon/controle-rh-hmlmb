package com.hmlmb.rh.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hmlmb.rh.dto.RequerimentoDTO;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;

@Service
public class PythonIntegrationService {

    private final ObjectMapper objectMapper;

    public PythonIntegrationService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public RequerimentoDTO extrairDadosDoPdf(String pdfPath) {
        try {
            String scriptPath = Paths.get("scripts", "extrair_dados.py").toAbsolutePath().toString();

            ProcessBuilder processBuilder = new ProcessBuilder("python3", scriptPath, pdfPath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            int exitCode = process.waitFor();
            
            // Tratamento: Se o Python não conseguir rodar ou explodir, devolvemos um DTO com a mensagem
            if (exitCode != 0) {
                 RequerimentoDTO erroDto = new RequerimentoDTO();
                 erroDto.setErro("Erro interno ao chamar Python. Código: " + exitCode + ". Detalhes: " + output.toString());
                 return erroDto;
            }

            // Mapeia o resultado (sucesso ou mensagem tratada de erro vinda do print do python) para o DTO
            return objectMapper.readValue(output.toString(), RequerimentoDTO.class);

        } catch (Exception e) {
            RequerimentoDTO erroDto = new RequerimentoDTO();
            erroDto.setErro("Falha catastrófica de comunicação com script Python: " + e.getMessage());
            return erroDto;
        }
    }
}
