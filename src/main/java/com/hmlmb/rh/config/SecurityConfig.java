package com.hmlmb.rh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .anyRequest().authenticated() // Todas as rotas precisam de login
            )
            .formLogin((form) -> form
                .loginPage("/login") // Nossa tela customizada
                .defaultSuccessUrl("/requerimentos", true) // Vai pra lista após logar
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // Desabilitado para facilitar os uploads via JS no momento

        return http.build();
    }

    // Login hardcoded na memória por enquanto (pode ser trocado por banco de dados depois)
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin =
             User.withDefaultPasswordEncoder()
                .username("admin")
                .password("hmlmb2024")
                .roles("ADMIN")
                .build();

        UserDetails rh =
             User.withDefaultPasswordEncoder()
                .username("rh")
                .password("123456")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, rh);
    }
}
