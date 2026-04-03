package com.hmlmb.rh.config;

import com.hmlmb.rh.model.Role;
import com.hmlmb.rh.model.User;
import com.hmlmb.rh.repository.RoleRepository;
import com.hmlmb.rh.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Criar roles se não existirem
        Role masterRole = roleRepository.findByName("ROLE_MASTER").orElseGet(() -> roleRepository.save(new Role("ROLE_MASTER")));
        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        // Podemos adicionar um ROLE_PENDING aqui se quisermos um papel explícito para usuários não aprovados
        // Role pendingRole = roleRepository.findByName("ROLE_PENDING").orElseGet(() -> roleRepository.save(new Role("ROLE_PENDING")));


        // Criar usuário admin (MASTER) se não existir
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setRoles(Set.of(masterRole, userRole)); // Admin terá ambos os papéis
            adminUser.setEnabled(true); // O usuário MASTER já começa ativado
            userRepository.save(adminUser);
        }
    }
}
