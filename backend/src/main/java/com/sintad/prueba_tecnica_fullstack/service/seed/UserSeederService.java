package com.sintad.prueba_tecnica_fullstack.service.seed;

import com.sintad.prueba_tecnica_fullstack.entity.User;
import com.sintad.prueba_tecnica_fullstack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSeederService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void seed(boolean force) {
        if (force) {
            userRepository.deleteAll();
        }

        if (userRepository.count() == 0) {
            List<User> users = List.of(
                    User.builder()
                            .fullName("Administrador General")
                            .username("admin")
                            .password(passwordEncoder.encode("admin123")) 
                            .role("ADMIN")
                            .createdAt(LocalDateTime.now())
                            .build(),
                    User.builder()
                            .fullName("Usuario Demo")
                            .username("usuario")
                            .password(passwordEncoder.encode("user123")) 
                            .role("OTRO")
                            .createdAt(LocalDateTime.now())
                            .build());
            userRepository.saveAll(users);
        }
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }
}
