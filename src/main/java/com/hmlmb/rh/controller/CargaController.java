package com.hmlmb.rh.controller;

import com.hmlmb.rh.service.CargaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CargaController {

    private final CargaService cargaService;

    public CargaController(CargaService cargaService) {
        this.cargaService = cargaService;
    }

    @PostMapping("/carga/funcionarios")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            cargaService.carregarFuncionarios(file);
            redirectAttributes.addFlashAttribute("message", "Arquivo carregado com sucesso: " + file.getOriginalFilename());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao carregar o arquivo: " + e.getMessage());
        }
        return "redirect:/";
    }
}
