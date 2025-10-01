package com.sintad.prueba_tecnica_fullstack.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de login con JWT")
public class LoginResponse {

    @Schema(description = "Token JWT de autenticación")
    private String token;

    @Schema(description = "Usuario autenticado", example = "admin")
    private String username;
}
