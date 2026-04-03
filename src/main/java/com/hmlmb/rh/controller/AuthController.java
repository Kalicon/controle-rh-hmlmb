package com.hmlmb.rh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        // Futuramente, podemos adicionar um objeto ao modelo para o formulário
        return "register";
    }
}
