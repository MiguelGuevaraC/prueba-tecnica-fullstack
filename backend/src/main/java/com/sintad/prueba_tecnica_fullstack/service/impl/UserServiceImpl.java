package com.sintad.prueba_tecnica_fullstack.service.impl;

import com.sintad.prueba_tecnica_fullstack.dto.user.UserRequest;
import com.sintad.prueba_tecnica_fullstack.dto.user.UserResponse;
import com.sintad.prueba_tecnica_fullstack.entity.User;
import com.sintad.prueba_tecnica_fullstack.repository.UserRepository;
import com.sintad.prueba_tecnica_fullstack.service.interfaces.IUserService;
import com.sintad.prueba_tecnica_fullstack.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse create(UserRequest request) {
        // Validar username único (solo usuarios activos)
        if (userRepository.existsByUsernameAndDeletedAtIsNull(request.getUsername())) {
            throw new IllegalArgumentException("El username ya está en uso: " + request.getUsername());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getById(Long id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));
        return toResponse(user);
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));

        Optional.ofNullable(request.getFullName()).ifPresent(user::setFullName);

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsernameAndDeletedAtIsNull(request.getUsername())) {
                throw new IllegalArgumentException("El username ya está en uso: " + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Optional.ofNullable(request.getRole()).ifPresent(user::setRole);
        user.setUpdatedAt(LocalDateTime.now());

        return toResponse(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));

        user.setDeletedAt(LocalDateTime.now()); // Soft delete
        userRepository.save(user);
    }

    @Override
    public Page<UserResponse> search(Specification<?> spec, Pageable pageable) {
        return userRepository.findAll((Specification<User>) spec, pageable)
                .map(this::toResponse);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
