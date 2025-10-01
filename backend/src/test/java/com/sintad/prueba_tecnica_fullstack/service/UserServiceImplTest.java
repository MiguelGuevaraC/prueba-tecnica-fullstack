package com.sintad.prueba_tecnica_fullstack.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.sintad.prueba_tecnica_fullstack.dto.user.UserRequest;
import com.sintad.prueba_tecnica_fullstack.dto.user.UserResponse;
import com.sintad.prueba_tecnica_fullstack.entity.User;
import com.sintad.prueba_tecnica_fullstack.repository.UserRepository;
import com.sintad.prueba_tecnica_fullstack.service.impl.UserServiceImpl;
import com.sintad.prueba_tecnica_fullstack.shared.exception.NotFoundException;

@SpringBootTest
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void whenCreateUser_thenReturnResponse() {
        UserRequest request = new UserRequest("Juan Pérez", "juan", "1234", "USER");

        User saved = User.builder()
                .id(1L)
                .fullName("Juan Pérez")
                .username("juan")
                .password("encodedPass")
                .role("USER")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponse response = userService.create(request);

        assertNotNull(response);
        assertEquals("juan", response.getUsername());
    }

    @Test
    void whenUserNotFound_thenThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(99L));
    }
}
