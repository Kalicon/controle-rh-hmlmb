package com.hmlmb.rh.controller;

import com.hmlmb.rh.dto.RequerimentoDTO;
import com.hmlmb.rh.model.Requerimento;
import com.hmlmb.rh.repository.RequerimentoRepository;
import com.hmlmb.rh.service.PythonIntegrationService;
import com.hmlmb.rh.service.RequerimentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/requerimentos")
public class RequerimentoController {

    private final RequerimentoService service;
    private final PythonIntegrationService pythonIntegrationService;
    private final RequerimentoRepository repository; // Injetado para o AutoComplete rápido

    private static final String UPLOAD_DIR = "temp_uploads/";

    public RequerimentoController(RequerimentoService service, PythonIntegrationService pythonIntegrationService, RequerimentoRepository repository) {
        this.service = service;
        this.pythonIntegrationService = pythonIntegrationService;
        this.repository = repository;
    }

    /**
     * Lista requerimentos com base no ano do Exercício e no Mês.
     */
    @GetMapping("/lista") // <-- CORREÇÃO: Mapeamento explícito para /lista
    public String listarRequerimentos(
            @RequestParam(name = "ano", required = false) Integer ano, 
            @RequestParam(name = "mes", required = false) Integer mes,
            Model model) {
        
        Integer exercicioAtual = (ano != null) ? ano : LocalDate.now().getYear();
        
        // Passa para o Service filtrar a Lista por Ano E Mês
        model.addAttribute("requerimentos", service.listarPorExercicioE(exercicioAtual, mes));
        
        // Passa as variaveis de filtro pra tela nao perder o estado
        model.addAttribute("exercicioAtual", exercicioAtual);
        model.addAttribute("mesAtual", mes);
        
        // Adiciona as variáveis para o novo layout
        model.addAttribute("pageTitle", "Lista de Requerimentos");
        model.addAttribute("activePage", "req_lista");

        return "requerimentos/lista";
    }

    @GetMapping("/formulario") // <-- CORREÇÃO: Mapeamento explícito para /formulario
    public String exibirFormulario(Model model) {
        model.addAttribute("requerimento", new Requerimento());
        
        // Adiciona as variáveis para o novo layout
        model.addAttribute("pageTitle", "Novo Requerimento");
        model.addAttribute("activePage", "req_form");

        return "requerimentos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        Optional<Requerimento> requerimento = repository.findById(id);
        if (requerimento.isPresent()) {
            model.addAttribute("requerimento", requerimento.get());
            
            // Adiciona as variáveis para o novo layout
            model.addAttribute("pageTitle", "Editar Requerimento");
            model.addAttribute("activePage", "req_form"); // Mesma aba ativa no menu

            return "requerimentos/formulario";
        }
        return "redirect:/requerimentos/lista";
    }

    @PostMapping
    public String salvarRequerimento(@ModelAttribute Requerimento requerimento) {
        service.salvar(requerimento);
        // Pega o mês que ele acabou de salvar para redirecionar certinho
        Integer mesSalvo = requerimento.getDataInicio() != null ? requerimento.getDataInicio().getMonthValue() : null;
        
        String redirectUrl = "redirect:/requerimentos/lista?ano=" + requerimento.getExercicio(); // <-- CORREÇÃO: Redireciona para /lista
        if(mesSalvo != null){
            redirectUrl += "&mes=" + mesSalvo;
        }
        return redirectUrl;
    }

    @GetMapping("/deletar/{id}")
    public String deletarRequerimento(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/requerimentos/lista";
    }

    /**
     * Endpoint API (JSON) para a tela consultar os nomes e dados (AutoComplete Datalist).
     */
    @GetMapping("/api/servidores")
    @ResponseBody
    public List<Map<String, String>> buscarServidoresCadastrados() {
        List<Object[]> rawList = repository.findDistinctServidores();
        List<Map<String, String>> result = new ArrayList<>();
        
        for (Object[] obj : rawList) {
            Map<String, String> map = new HashMap<>();
            map.put("nome", (String) obj[0]);
            map.put("cargo", (String) obj[1]);
            map.put("rs", (String) obj[2]);
            map.put("pv", obj[3] != null ? obj[3].toString() : "");
            map.put("regimeJuridico", (String) obj[4]);
            
            // Só adiciona se tiver nome
            if(map.get("nome") != null && !map.get("nome").isEmpty()) {
                result.add(map);
            }
        }
        return result;
    }

    /**
     * Endpoint do Upload.
     */
    @PostMapping("/upload-pdf")
    @ResponseBody 
    public ResponseEntity<RequerimentoDTO> processarPdf(@RequestParam("pdfFile") MultipartFile file) {
        if (file.isEmpty()) {
            RequerimentoDTO erroDto = new RequerimentoDTO();
            erroDto.setErro("O arquivo enviado está vazio.");
            return ResponseEntity.badRequest().body(erroDto);
        }

        Path tempFile = null;
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);

            String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file.pdf";
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            tempFile = uploadPath.resolve(uniqueFilename);

            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            RequerimentoDTO dto = pythonIntegrationService.extrairDadosDoPdf(tempFile.toAbsolutePath().toString());

            if (dto != null && dto.getErro() != null && !dto.getErro().isEmpty()) {
                return ResponseEntity.status(400).body(dto); 
            }

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            RequerimentoDTO erroDto = new RequerimentoDTO();
            erroDto.setErro("Falha inesperada no servidor ao processar arquivo.");
            return ResponseEntity.status(500).body(erroDto);
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    System.err.println("Aviso: Falha ao apagar temp file: " + tempFile.toString());
                }
            }
        }
    }
}