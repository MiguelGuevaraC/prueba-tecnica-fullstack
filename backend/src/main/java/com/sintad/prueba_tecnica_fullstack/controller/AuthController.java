package com.sintad.prueba_tecnica_fullstack.controller;

import com.sintad.prueba_tecnica_fullstack.dto.auth.LoginRequest;
import com.sintad.prueba_tecnica_fullstack.dto.auth.LoginResponse;
import com.sintad.prueba_tecnica_fullstack.dto.user.UserRequest;
import com.sintad.prueba_tecnica_fullstack.dto.user.UserResponse;
import com.sintad.prueba_tecnica_fullstack.entity.User;
import com.sintad.prueba_tecnica_fullstack.repository.UserRepository;
import com.sintad.prueba_tecnica_fullstack.security.JwtUtil;
import com.sintad.prueba_tecnica_fullstack.service.interfaces.IUserService;
import com.sintad.prueba_tecnica_fullstack.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final IUserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario y obtener JWT")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

       
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.ok(ApiResponse.ok(new LoginResponse(token, request.getUsername(), user.getRole())));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody UserRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(userService.create(request)));
    }
}
