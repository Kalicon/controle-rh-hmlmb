package com.hmlmb.rh.controller;

import com.hmlmb.rh.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // O Cadastro de novos usuarios publicos foi removido.
    // Agora o sistema eh fechado. Somente o Administrador cria usuarios no menu de configuracoes.
}
