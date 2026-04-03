package com.hmlmb.rh.controller;

import com.hmlmb.rh.model.User;
import com.hmlmb.rh.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ROLE_ADMIN')") // Apenas administradores podem acessar
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "user-management";
    }

    @PostMapping("/{id}/update")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String username,
                             @RequestParam(required = false) String password,
                             @RequestParam Set<String> roles,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(id, username, password, roles);
            redirectAttributes.addFlashAttribute("successMessage", "Usuário atualizado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar usuário: " + e.getMessage());
        }
        return "redirect:/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Usuário excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao excluir usuário.");
        }
        return "redirect:/users";
    }
}
