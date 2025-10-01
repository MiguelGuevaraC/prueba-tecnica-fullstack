package com.sintad.prueba_tecnica_fullstack.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Petición de login")
public class LoginRequest {

    @Schema(description = "Usuario para login", example = "admin")
    @NotBlank
    private String username;

    @Schema(description = "Contraseña en texto plano", example = "12345678")
    @NotBlank
    private String password;
}
