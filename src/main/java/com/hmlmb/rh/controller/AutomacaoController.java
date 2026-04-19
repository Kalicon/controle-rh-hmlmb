package com.hmlmb.rh.controller;

import com.hmlmb.rh.dto.RequerimentoDTO;
import com.hmlmb.rh.model.Requerimento;
import com.hmlmb.rh.service.CargaRequerimentoService;
import com.hmlmb.rh.service.GeminiService;
import com.hmlmb.rh.service.PythonIntegrationService;
import com.hmlmb.rh.service.RequerimentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/automacao")
public class AutomacaoController {

    private final CargaRequerimentoService cargaRequerimentoService;
    private final PythonIntegrationService pythonIntegrationService;
    private final RequerimentoService requerimentoService;
    private final GeminiService geminiService;

    private static final String UPLOAD_DIR = "temp_uploads/";

    public AutomacaoController(CargaRequerimentoService cargaRequerimentoService, 
                               PythonIntegrationService pythonIntegrationService,
                               RequerimentoService requerimentoService,
                               GeminiService geminiService) {
        this.cargaRequerimentoService = cargaRequerimentoService;
        this.pythonIntegrationService = pythonIntegrationService;
        this.requerimentoService = requerimentoService;
        this.geminiService = geminiService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("pageTitle", "Central de Automação");
        model.addAttribute("activePage", "automacao");
        return "automacao/index";
    }

    @PostMapping("/carga-excel")
    public String processarCargaExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Por favor, selecione um arquivo Excel.");
            return "redirect:/automacao";
        }
        try {
            int processados = cargaRequerimentoService.carregarRequerimentos(file);
            redirectAttributes.addFlashAttribute("successMessage", "Carga concluída com sucesso! " + processados + " requerimentos processados.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao processar planilha: " + e.getMessage());
        }
        return "redirect:/automacao";
    }

    @PostMapping("/upload-pdf-lote")
    @ResponseBody
    public ResponseEntity<List<RequerimentoDTO>> processarLotePdf(@RequestParam("pdfFiles") MultipartFile[] files) {
        List<RequerimentoDTO> resultados = new ArrayList<>();

        if (files == null || files.length == 0 || files.length > 10) {
            RequerimentoDTO erro = new RequerimentoDTO();
            erro.setErro("Selecione de 1 a 10 arquivos PDF.");
            resultados.add(erro);
            return ResponseEntity.badRequest().body(resultados);
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            Path tempFile = null;
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);

                String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file.pdf";
                String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
                tempFile = uploadPath.resolve(uniqueFilename);

                Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

                RequerimentoDTO dto = pythonIntegrationService.extrairDadosDoPdf(tempFile.toAbsolutePath().toString());
                
                if (dto != null) {
                    dto.setObservacoes(originalFilename);
                    resultados.add(dto);
                }

            } catch (Exception e) {
                RequerimentoDTO erroDto = new RequerimentoDTO();
                erroDto.setErro("Falha no arquivo " + file.getOriginalFilename() + ": " + e.getMessage());
                resultados.add(erroDto);
            } finally {
                if (tempFile != null) {
                    try {
                        Files.deleteIfExists(tempFile);
                    } catch (IOException ignored) {}
                }
            }
        }
        return ResponseEntity.ok(resultados);
    }

    @PostMapping("/salvar-lote")
    @ResponseBody
    public ResponseEntity<?> salvarLote(@RequestBody List<Requerimento> requerimentos) {
        try {
            for (Requerimento req : requerimentos) {
                requerimentoService.salvar(req);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao salvar lote: " + e.getMessage());
        }
    }

    @PostMapping("/chat-gemini")
    @ResponseBody
    public ResponseEntity<?> perguntarGemini(@RequestBody Map<String, String> payload) {
        try {
            String pergunta = payload.get("pergunta");
            if (pergunta == null || pergunta.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("resposta", "A pergunta não pode estar vazia."));
            }

            // Da um contexto pro Gemini agir como um assistente de RH
            String contexto = "Você é um assistente virtual especialista em Recursos Humanos (RH), leis trabalhistas, licenças e requerimentos. Seja educado, objetivo e claro na resposta. A pergunta do funcionário do RH é: ";
            
            String resposta = geminiService.perguntarGemini(contexto + pergunta);
            
            return ResponseEntity.ok(Map.of("resposta", resposta));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("resposta", "Desculpe, ocorreu um erro interno: " + e.getMessage()));
        }
    }
}
