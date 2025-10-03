package com.sintad.prueba_tecnica_fullstack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sintad.prueba_tecnica_fullstack.dto.auth.LoginRequest;
import com.sintad.prueba_tecnica_fullstack.dto.user.UserRequest;
import com.sintad.prueba_tecnica_fullstack.dto.user.UserResponse;
import com.sintad.prueba_tecnica_fullstack.entity.User;
import com.sintad.prueba_tecnica_fullstack.repository.UserRepository;
import com.sintad.prueba_tecnica_fullstack.security.CustomUserDetailsService;
import com.sintad.prueba_tecnica_fullstack.security.JwtAuthenticationFilter;
import com.sintad.prueba_tecnica_fullstack.security.JwtUtil;
import com.sintad.prueba_tecnica_fullstack.service.interfaces.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private IUserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("admin", "admin123");

        User fakeUser = User.builder()
                .id(1L)
                .username("admin")
                .password("encodedPassword")
                .role("ADMIN")
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "admin123");

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(fakeUser));
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    void testLoginUserNotFound() throws Exception {
        LoginRequest request = new LoginRequest("ghost", "1234");

        Authentication auth = new UsernamePasswordAuthenticationToken("ghost", "1234");
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegisterSuccess() throws Exception {
        UserRequest request = new UserRequest("newuser", "User Nuevo", "password", "USER");
        UserResponse response = UserResponse.builder()
                .id(1L)
                .username("newuser")
                .fullName("User Nuevo")
                .role("USER")
                .build();

        when(userService.create(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("newuser"))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }
}
