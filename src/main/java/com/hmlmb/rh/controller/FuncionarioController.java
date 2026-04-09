package com.hmlmb.rh.controller;

import com.hmlmb.rh.model.Funcionario;
import com.hmlmb.rh.service.FuncionarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioService service;

    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    @GetMapping("/lista")
    public String listarFuncionarios(Model model) {
        model.addAttribute("funcionarios", service.listarTodos());
        model.addAttribute("pageTitle", "Lista de Funcionários");
        model.addAttribute("activePage", "func_lista");
        return "funcionarios/lista";
    }

    @GetMapping("/formulario")
    public String exibirFormulario(Model model) {
        model.addAttribute("funcionario", new Funcionario());
        model.addAttribute("pageTitle", "Novo Funcionário");
        model.addAttribute("activePage", "func_form");
        return "funcionarios/formulario";
    }

    @GetMapping("/editar/{id}")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        Optional<Funcionario> funcionario = service.buscarPorId(id);
        if (funcionario.isPresent()) {
            model.addAttribute("funcionario", funcionario.get());
            model.addAttribute("pageTitle", "Editar Funcionário");
            model.addAttribute("activePage", "func_form");
            return "funcionarios/formulario";
        }
        return "redirect:/funcionarios/lista";
    }

    @PostMapping
    public String salvarFuncionario(@ModelAttribute Funcionario funcionario) {
        service.salvar(funcionario);
        return "redirect:/funcionarios/lista";
    }

    @GetMapping("/deletar/{id}")
    public String deletarFuncionario(@PathVariable Long id) {
        service.deletar(id);
        return "redirect:/funcionarios/lista";
    }

    // Endpoint para o Autocomplete (API)
    @GetMapping("/api/buscar")
    @ResponseBody
    public List<Funcionario> buscarFuncionariosApi(@RequestParam String termo) {
        return service.buscarPorNome(termo);
    }

    // Novo endpoint para buscar funcionário por RS para pré-preenchimento
    @GetMapping("/api/buscarPorRs")
    @ResponseBody
    public ResponseEntity<Funcionario> buscarFuncionarioPorRs(@RequestParam String rs) {
        Optional<Funcionario> funcionario = service.buscarPorRs(rs);
        return funcionario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
