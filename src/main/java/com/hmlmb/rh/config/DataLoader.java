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
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        // Criar usuário admin se não existir
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setRoles(Set.of(adminRole, userRole));
            userRepository.save(adminUser);
        }
    }
}
