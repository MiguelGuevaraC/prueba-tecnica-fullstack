package com.sintad.prueba_tecnica_fullstack.service.impl;

import com.sintad.prueba_tecnica_fullstack.dto.user.UserRequest;
import com.sintad.prueba_tecnica_fullstack.dto.user.UserResponse;
import com.sintad.prueba_tecnica_fullstack.entity.User;
import com.sintad.prueba_tecnica_fullstack.repository.UserRepository;
import com.sintad.prueba_tecnica_fullstack.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl service;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void create_success() {
        UserRequest req = new UserRequest();
        req.setFullName("Miguel Guevara");
        req.setUsername("miguel");
        req.setPassword("123456");
        req.setRole("ADMIN");

        when(userRepository.existsByUsernameAndDeletedAtIsNull("miguel")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("ENC_123456");

        User saved = User.builder()
                .id(1L).fullName("Miguel Guevara").username("miguel")
                .password("ENC_123456").role("ADMIN")
                .createdAt(LocalDateTime.now()).build();
        when(userRepository.save(Mockito.<User>any())).thenReturn(saved);

        UserResponse resp = service.create(req);

        assertThat(resp.getId(), is(1L));
        assertThat(resp.getUsername(), is("miguel"));
        assertThat(resp.getRole(), is("ADMIN"));
    }

    @Test
    void create_fails_nullRequest() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(null));
        assertThat(ex.getMessage(), containsString("no puede ser nulo"));
        verify(userRepository, never()).save(Mockito.<User>any());
    }

    @Test
    void create_fails_usernameExists() {
        UserRequest req = new UserRequest();
        req.setFullName("X");
        req.setUsername("repetido");
        req.setPassword("p");
        req.setRole("USER");

        when(userRepository.existsByUsernameAndDeletedAtIsNull("repetido")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(req));
        assertThat(ex.getMessage(), containsString("ya está en uso"));
        verify(userRepository, never()).save(Mockito.<User>any());
    }

    @Test
    void getById_success() {
        User u = User.builder()
                .id(10L).fullName("Admin").username("admin")
                .role("ADMIN").createdAt(LocalDateTime.now()).build();
        when(userRepository.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.of(u));

        UserResponse resp = service.getById(10L);

        assertThat(resp.getId(), is(10L));
        assertThat(resp.getUsername(), is("admin"));
        assertThat(resp.getRole(), is("ADMIN"));
    }

    @Test
    void getById_notFound() {
        when(userRepository.findByIdAndDeletedAtIsNull(404L)).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getById(404L));
        assertThat(ex.getMessage(), containsString("Usuario no encontrado con ID: 404"));
    }

    @Test
    void update_success_changeUsernameAndPassword() {
        User existing = User.builder()
                .id(5L).fullName("User A").username("userA")
                .password("OLD").role("USER")
                .createdAt(LocalDateTime.now().minusDays(2)).build();
        when(userRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(Optional.of(existing));

        UserRequest req = new UserRequest();
        req.setFullName("User A Prime");
        req.setUsername("userA2");
        req.setPassword("newpass");
        req.setRole("ADMIN");

        when(userRepository.existsByUsernameAndDeletedAtIsNull("userA2")).thenReturn(false);
        when(passwordEncoder.encode("newpass")).thenReturn("ENC_newpass");

        User updated = User.builder()
                .id(5L).fullName("User A Prime").username("userA2")
                .password("ENC_newpass").role("ADMIN")
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now()).build();
        when(userRepository.save(Mockito.<User>any())).thenReturn(updated);

        UserResponse resp = service.update(5L, req);

        assertThat(resp.getId(), is(5L));
        assertThat(resp.getUsername(), is("userA2"));
        assertThat(resp.getRole(), is("ADMIN"));
    }

    @Test
    void update_success_partial_noUsernameChange_noPassword() {
        User existing = User.builder()
                .id(6L).fullName("User B").username("userB")
                .password("HASH").role("USER")
                .createdAt(LocalDateTime.now().minusDays(2)).build();
        when(userRepository.findByIdAndDeletedAtIsNull(6L)).thenReturn(Optional.of(existing));

        UserRequest req = new UserRequest();
        req.setFullName("User B Edit");
        req.setRole("ADMIN");

        User updated = User.builder()
                .id(6L).fullName("User B Edit").username("userB")
                .password("HASH").role("ADMIN")
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now()).build();
        when(userRepository.save(Mockito.<User>any())).thenReturn(updated);

        UserResponse resp = service.update(6L, req);

        assertThat(resp.getId(), is(6L));
        assertThat(resp.getUsername(), is("userB"));
        assertThat(resp.getRole(), is("ADMIN"));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void update_fails_nullRequest() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(1L, null));
        assertThat(ex.getMessage(), containsString("no puede ser nulo"));
        verify(userRepository, never()).findByIdAndDeletedAtIsNull(anyLong());
        verify(userRepository, never()).save(Mockito.<User>any());
    }

    @Test
    void update_fails_userNotFound() {
        when(userRepository.findByIdAndDeletedAtIsNull(7L)).thenReturn(Optional.empty());
        UserRequest req = new UserRequest();
        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.update(7L, req));
        assertThat(ex.getMessage(), containsString("Usuario no encontrado con ID: 7"));
    }

    @Test
    void update_fails_usernameAlreadyUsed() {
        User existing = User.builder()
                .id(8L).fullName("User C").username("userC")
                .password("HASH").role("USER").build();
        when(userRepository.findByIdAndDeletedAtIsNull(8L)).thenReturn(Optional.of(existing));

        UserRequest req = new UserRequest();
        req.setUsername("taken");

        when(userRepository.existsByUsernameAndDeletedAtIsNull("taken")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(8L, req));
        assertThat(ex.getMessage(), containsString("ya está en uso"));
        verify(userRepository, never()).save(Mockito.<User>any());
    }

    @Test
    void delete_success_softDelete() {
        User existing = User.builder().id(9L).username("userD").build();
        when(userRepository.findByIdAndDeletedAtIsNull(9L)).thenReturn(Optional.of(existing));

        service.delete(9L);

        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertThat(saved.getId(), is(9L));
        assertThat(saved.getDeletedAt(), notNullValue());
    }

    @Test
    void delete_notFound() {
        when(userRepository.findByIdAndDeletedAtIsNull(9L)).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.delete(9L));
        assertThat(ex.getMessage(), containsString("Usuario no encontrado con ID: 9"));
        verify(userRepository, never()).save(Mockito.<User>any());
    }

    @Test
    void search_success_withSpecAndNotDeleted() {
        User u = User.builder().id(1L).fullName("Admin").username("admin").role("ADMIN").build();
        Page<User> page = new PageImpl<>(List.of(u), PageRequest.of(0, 10), 1);

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<UserResponse> result = service.search(
                (Specification<?>) (root, query, cb) -> cb.equal(root.get("role"), "ADMIN"),
                PageRequest.of(0, 10)
        );

        assertThat(result.getTotalElements(), is(1L));
        assertThat(result.getContent(), hasSize(1));
        assertThat(result.getContent().get(0).getUsername(), is("admin"));
    }

    @Test
    void search_success_nullSpec_appliesNotDeleted() {
        User u = User.builder().id(2L).fullName("User").username("user").role("USER").build();
        Page<User> page = new PageImpl<>(List.of(u), PageRequest.of(0, 10), 1);

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<UserResponse> result = service.search(null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements(), is(1L));
        assertThat(result.getContent(), hasSize(1));
        assertThat(result.getContent().get(0).getUsername(), is("user"));
    }
}
