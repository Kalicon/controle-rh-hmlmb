package com.hmlmb.rh.service;

import com.hmlmb.rh.model.Role;
import com.hmlmb.rh.model.User;
import com.hmlmb.rh.repository.RoleRepository;
import com.hmlmb.rh.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerNewUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username já existe: " + username);
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Role 'ROLE_USER' não encontrada."));

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRoles(Set.of(userRole));
        newUser.setEnabled(true); // Default to true when registering

        return userRepository.save(newUser);
    }
    
    public User createUser(String username, String password, Set<String> roleNames, boolean enabled) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username já existe: " + username);
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        
        Set<Role> roles = roleRepository.findByNameIn(roleNames);
        if (roles.size() != roleNames.size()) {
            throw new IllegalArgumentException("Uma ou mais roles especificadas não foram encontradas.");
        }
        newUser.setRoles(roles);
        newUser.setEnabled(enabled);

        return userRepository.save(newUser);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(Long id, String username, String password, Set<String> roleNames, boolean enabled) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + id));

        // Verifica se o novo username já existe e não é o do próprio usuário
        if (!existingUser.getUsername().equals(username) && userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username já existe: " + username);
        }
        existingUser.setUsername(username);

        if (password != null && !password.isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(password));
        }

        Set<Role> roles = roleRepository.findByNameIn(roleNames);
        if (roles.size() != roleNames.size()) {
            throw new IllegalArgumentException("Uma ou mais roles especificadas não foram encontradas.");
        }
        existingUser.setRoles(roles);
        existingUser.setEnabled(enabled);

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
