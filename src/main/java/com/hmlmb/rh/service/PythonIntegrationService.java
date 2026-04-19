package com.hmlmb.rh.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hmlmb.rh.dto.RequerimentoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.logging.Logger;

@Service
public class PythonIntegrationService {

    private static final Logger log = Logger.getLogger(PythonIntegrationService.class.getName());
    private final ObjectMapper objectMapper;

    @Value("${app.python.executable:python3}")
    private String pythonExecutable;

    @Value("${app.python.script.path:scripts/extrair_dados.py}")
    private String pythonScriptPath;

    public PythonIntegrationService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public RequerimentoDTO extrairDadosDoPdf(String pdfPath) {
        try {
            // Resolve o caminho absoluto de forma segura para producao
            String scriptPath = Paths.get(pythonScriptPath).toAbsolutePath().toString();
            
            log.info("Iniciando extracao de PDF: Executavel [" + pythonExecutable + "] Script [" + scriptPath + "]");

            ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, scriptPath, pdfPath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                 log.severe("Falha na execucao do Python. Exit code: " + exitCode + " Output: " + output.toString());
                 RequerimentoDTO erroDto = new RequerimentoDTO();
                 erroDto.setErro("Erro interno ao chamar processo de extracao. Verifique os logs do servidor.");
                 return erroDto;
            }

            return objectMapper.readValue(output.toString(), RequerimentoDTO.class);

        } catch (Exception e) {
            log.severe("Erro catastrofico ao integrar com Python: " + e.getMessage());
            RequerimentoDTO erroDto = new RequerimentoDTO();
            erroDto.setErro("Falha catastrofica de comunicacao interna. Tente novamente mais tarde.");
            return erroDto;
        }
    }
}
