package com.hmlmb.rh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class GeminiService {

    private static final Logger log = Logger.getLogger(GeminiService.class.getName());

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String perguntarGemini(String prompt) {
        try {
            // Monta a URL completa com a chave da API
            String fullUrl = apiUrl + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // O Gemini (modelo gemini-1.5-flash) exige esta estrutura JSON exata:
            // { "contents": [{ "parts": [{"text": "Sua pergunta aqui"}] }] }
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", prompt);

            Map<String, Object> parts = new HashMap<>();
            parts.put("parts", List.of(textPart));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(parts));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Enviando pergunta para o Gemini AI...");
            ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                // Navega pelo JSON de resposta para extrair o texto puro
                JsonNode textNode = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text");
                return textNode.asText();
            } else {
                log.severe("Erro do Gemini: " + response.getBody());
                return "Desculpe, o servidor da IA retornou um erro (" + response.getStatusCode() + ").";
            }
        } catch (Exception e) {
            log.severe("Erro critico na integracao com Gemini: " + e.getMessage());
            return "Desculpe, ocorreu uma falha grave de comunicação com a Inteligência Artificial. Detalhe técnico: " + e.getMessage();
        }
    }
}
